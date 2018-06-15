import business.Task;
import business.TaskerImpl;
import com.AddTaskRep;
import com.AddTaskReq;
import interfaces.Tasker;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import pt.haslab.ekit.Spread;
import spread.SpreadException;
import spread.SpreadMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private SingleThreadContext tc;
    private CompletableFuture<String> stateTransfer;
    private Spread spread;
    private io.atomix.catalyst.transport.Address address;
    private Tasker tasker;
    private AtomicInteger reqID;
    private int serverID;


    public static void main(String[] args) {
        int id = Integer.parseInt(args[0]);
        Server server = new Server(id);
        server.start();
    }



    public Server(int id) {
        this.tc = new SingleThreadContext("srv-%d", new Serializer());
        this.stateTransfer = null;
        this.tasker = new TaskerImpl();
        this.serverID = id;
        this.reqID = new AtomicInteger(0);

        registerMessages();

        try {
            spread = new Spread("server" + id,false);
        }catch (SpreadException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        this.tc.execute(() -> {
            registerHandlers();

            try {
                this.spread.open().thenRun(() -> System.out.println("Starting server " + this.serverID));
                this.spread.join("servers");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    private void registerHandlers() {

        this.spread.handler(AddTaskReq.class, (m, v) -> {
            int id = reqID.incrementAndGet();
            //System.out.println("Chegou o pedido: " + reqID + "\nMensagem: " + v.url);
            Task task = new Task(id, v.url);
            boolean result = this.tasker.addTask(task);
            AddTaskRep reply = new AddTaskRep(task, result);

            SpreadMessage m2 = new SpreadMessage();
            m2.addGroup(m.getSender());
            m2.setAgreed();
            this.spread.multicast(m2, reply);
        });

    }


    private void registerMessages() {
        tc.serializer().register(AddTaskRep.class);
        tc.serializer().register(AddTaskReq.class);
    }
}
