import business.Task;
import business.TaskerImpl;
import com.*;
import interfaces.Tasker;
import io.atomix.catalyst.serializer.Serializer;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends ActiveServer {
    private Tasker tasker;
    private AtomicInteger reqID;

    public static void main(String[] args) {
        int id = Integer.parseInt(args[0]);
        Server server = new Server(id);
        server.start();
    }

    public Server(int id) {
        super(id);

        tasker = new TaskerImpl();
        reqID = new AtomicInteger(0);
    }

    public void registerMessages(Serializer s) {
        s.register(AddTaskRep.class);
        s.register(AddTaskReq.class);
        s.register(GetTaskReq.class);
        s.register(GetTaskRep.class);
        s.register(FinishTaskReq.class);
        s.register(FinishTaskRep.class);
        s.register(ReallocateTasksReq.class);
        s.register(ReallocateTasksRep.class);
    }

    public void registerHandlers() {
        handler(AddTaskReq.class, (m, v) -> {
            int id = reqID.incrementAndGet();
            Task task = new Task(id, v.url);
            boolean result = this.tasker.addTask(task);
            AddTaskRep reply = new AddTaskRep(v.reqID, task, result);

            multicast(m.getSender().toString(), reply);
        });


        handler(GetTaskReq.class, (m, v) -> {
            Task task = this.tasker.getNextTask();
            GetTaskRep reply;
            task.setClient(m.getSender().toString());
            reply = new GetTaskRep(v.reqID, task);

            multicast(m.getSender().toString(), reply);
        });


        handler(FinishTaskReq.class, (m, v) -> {
            System.out.println(tasker.print());

            Boolean result = this.tasker.finishTask(v.task);
            System.out.println(tasker.print());

            FinishTaskRep reply = new FinishTaskRep(v.reqID, result);


            multicast(m.getSender().toString(), reply);
        });


        handler(ReallocateTasksReq.class, (m, v) -> {
            Boolean result = this.tasker.reallocateTasks(v.client);
            ReallocateTasksRep reply = new ReallocateTasksRep(v.reqID, result);

            multicast(m.getSender().toString(), reply);
        });
    }

    @Override
    public StateRep saveState() {
        return new StateRep((TaskerImpl) tasker, reqID.get());
    }

    @Override
    public void recoverState(StateRep rep) {
        tasker = rep.getTasker();
        reqID = new AtomicInteger(rep.getReqId());
    }
}
