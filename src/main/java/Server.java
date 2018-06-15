import business.Task;
import business.TaskerImpl;
import com.AddTaskRep;
import com.AddTaskReq;
import com.GetTaskRep;
import com.GetTaskReq;
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
                this.spread.open().thenRun(() -> {
                    System.out.println("Starting server " + this.serverID);
                    this.spread.join("servers");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    private void registerHandlers() {

        this.spread.handler(AddTaskReq.class, (m, v) -> {
            int id = reqID.incrementAndGet();
            //System.out.println("Pedido de adicionar Task: " + reqID + "\nMensagem: " + v.url);
            Task task = new Task(id, v.url);
            boolean result = this.tasker.addTask(task);
            AddTaskRep reply = new AddTaskRep(v.reqID, task, result);

            sendMsg(m.getSender().toString(), reply);
        });


        this.spread.handler(GetTaskReq.class, (m, v) -> {
            int id = reqID.incrementAndGet();
            //System.out.println("Pedido de atribuição de Task: " + reqID + "\nMensagem: " + v.url);
            Task task = this.tasker.getNextTask();
            GetTaskRep reply;
            if (task != null)
                reply = new GetTaskRep(v.reqID, task, true);
            else
                reply = new GetTaskRep(v.reqID, task, false);

            sendMsg(m.getSender().toString(), reply);
        });

    }

    public void sendMsg(String group, Object obj) {
        SpreadMessage m = new SpreadMessage();
        m.addGroup(group);
        m.setAgreed();
        this.spread.multicast(m,obj);
    }


    private void registerMessages() {
        tc.serializer().register(AddTaskRep.class);
        tc.serializer().register(AddTaskReq.class);
        tc.serializer().register(GetTaskReq.class);
        tc.serializer().register(GetTaskRep.class);
    }
}
