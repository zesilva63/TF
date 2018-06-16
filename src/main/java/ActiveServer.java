import com.StateRep;
import com.StateReq;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import pt.haslab.ekit.Spread;
import spread.MembershipInfo;
import spread.SpreadException;
import spread.SpreadMessage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ActiveServer {
    private final Map<Class<? extends Object>, BiConsumer<SpreadMessage, Object>> handlers;
    private Spread spread;
    private int serverID;
    private SingleThreadContext tc;
    private List<SpreadMessage> bufferedMessages;
    private boolean active = false, buffering = false;

    private final String serversGroup = "servers";

    public ActiveServer(int id) {
        handlers = new HashMap<>();
        serverID = id;

        tc = new SingleThreadContext("srv-%d", new Serializer());
        Serializer s = tc.serializer();

        s.register(StateRep.class);
        s.register(StateReq.class);
        registerMessages(s);

        try {
            spread = new Spread("server" + id,true);
        }catch (SpreadException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        tc.execute(() -> {
            registerServerHandlers();
            registerHandlers();

            for(Class<? extends Object> cls: handlers.keySet()) {
                spread.handler(cls, (m,v) -> {
                    BiConsumer<SpreadMessage, Object> handler = handlers.get(v.getClass());

                    if (handler == null)
                        return;

                    if (active)
                        handler.accept(m, v);

                    if (buffering)
                        bufferedMessages.add(m);
                });
            }

            try {
                spread.open().thenRun(() -> {
                    System.out.println("Starting server " + serverID);
                    spread.join(serversGroup);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void registerServerHandlers() {
        spread.handler(MembershipInfo.class, (m, v) -> {
            if (active)
                return;

            if (v.getMembers().length == 1) {
                active = true;
                System.out.println("Server started");
            } else {
                bufferedMessages = new ArrayList<>();
                multicast(serversGroup, new StateReq());
                System.out.println("Asking for state...");
            }
        });

        spread.handler(StateReq.class, (m, v) -> {
            if (active) {
                System.out.println("State shared");
                StateRep rep = saveState();
                multicast(m.getSender().toString(), rep);
            }

            if (m.getSender().equals(spread.getPrivateGroup()))
                buffering = true;
        });

        spread.handler(StateRep.class, (m, v) -> {
            if (active)
                return;

            System.out.println("Recovering state...");
            recoverState(v);

            for(SpreadMessage msg: bufferedMessages) {
                Object obj = tc.serializer().readObject(new ByteArrayInputStream(msg.getData()));
                BiConsumer<SpreadMessage, Object> handler = handlers.get(m);

                handler.accept(msg, obj);
            }

            bufferedMessages = null;

            buffering = false;
            active = true;
            System.out.println("State recovered");
        });
    }

    public <T> void handler(Class<T> type, BiConsumer<SpreadMessage, T> rh) {
        handlers.put(type, (i, r) ->  rh.accept(i, type.cast(r)) );
    }

    public void multicast(String group, Object obj) {
        SpreadMessage m = new SpreadMessage();
        m.addGroup(group);
        m.setAgreed();
        spread.multicast(m, obj);
    }

    public abstract void registerMessages(Serializer s);

    public abstract void registerHandlers();

    public abstract StateRep saveState();
    public abstract void recoverState(StateRep rep);
}
