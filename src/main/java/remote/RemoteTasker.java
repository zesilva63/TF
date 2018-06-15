package remote;

import business.Task;
import com.*;
import interfaces.Tasker;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import pt.haslab.ekit.Spread;
import spread.SpreadException;
import spread.SpreadMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteTasker implements Tasker {

    private AtomicInteger reqID;
    private SingleThreadContext tc;
    private Spread spread;
    private CompletableFuture<Object> complete;
    private final String serversGroup = "servers";
    private final String clientsGroup = "clients";



    public RemoteTasker(int id) {
        tc = new SingleThreadContext("cl-%d", new Serializer());
        complete = null;
        reqID = new AtomicInteger(0);

        registerMessages();

        try {
            spread = new Spread("client" + id,false);
        }catch (SpreadException e) {
            e.printStackTrace();
        }

        registerHandlers();

        tc.execute(() -> {
            spread.open().thenRun(() -> {
                System.out.println("Starting client " + id);
                this.spread.join(clientsGroup);
            });
        });
    }


    @Override
    public boolean addTask(Task t) {
        complete = new CompletableFuture<>();
        AddTaskReq request = new AddTaskReq(reqID.incrementAndGet(), t);

        sendMsg(serversGroup, request);

        try {
            AddTaskRep reply = (AddTaskRep) complete.get();
            return reply.result;
        } catch (Exception e) {
            System.out.println("Error getting reply from Add Task operation");
        }

        return false;
    }


    @Override
    public Task getNextTask() {
        complete = new CompletableFuture<>();
        GetTaskReq request = new GetTaskReq(reqID.incrementAndGet());

        sendMsg(serversGroup, request);

        try {
            GetTaskRep reply = (GetTaskRep) complete.get();
            if(reply.result)
                return reply.task;
        } catch (Exception e) {
            System.out.println("Error getting reply from Get Task operation");
        }

        return null;
    }


    @Override
    public boolean finishTask(Task t) {
        complete = new CompletableFuture<>();
        FinishTaskReq request = new FinishTaskReq(reqID.incrementAndGet(), t);

        sendMsg(serversGroup, request);

        try {
            FinishTaskRep reply = (FinishTaskRep) complete.get();
            return reply.result;
        } catch (Exception e) {
            System.out.println("Error getting reply from Finish Task operation");
        }

        return false;
    }


    public void sendMsg(String group, Object obj) {
        SpreadMessage m = new SpreadMessage();
        m.addGroup(group);
        m.setAgreed();
        spread.multicast(m,obj);
    }


    public void registerMessages() {
        tc.serializer().register(AddTaskReq.class);
        tc.serializer().register(AddTaskRep.class);
        tc.serializer().register(GetTaskReq.class);
        tc.serializer().register(GetTaskRep.class);
        tc.serializer().register(FinishTaskReq.class);
        tc.serializer().register(FinishTaskRep.class);
    }


    public void registerHandlers() {

        this.spread.handler(AddTaskRep.class, (m, v) -> {
            if(complete != null && v.reqID == reqID.intValue())
                complete.complete(v);
        });

        this.spread.handler(GetTaskRep.class, (m, v) -> {
            if(complete != null && v.reqID == reqID.intValue())
                complete.complete(v);
        });

        this.spread.handler(FinishTaskRep.class, (m, v) -> {
            if(complete != null && v.reqID == reqID.intValue())
                complete.complete(v);
        });

    }

}
