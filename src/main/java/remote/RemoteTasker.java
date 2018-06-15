package remote;

import business.Task;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteTasker implements Tasker {

    private AtomicInteger reqID;
    private SingleThreadContext tc;
    private Spread spread;
    private CompletableFuture<Object> complete;


    public RemoteTasker(int id) {
        this.tc = new SingleThreadContext("cl-%d", new Serializer());
        this.complete = null;
        this.reqID = new AtomicInteger(0);

        registerMessages();

        try {
            spread = new Spread("client" + id,false);
        }catch (SpreadException e) {
            e.printStackTrace();
        }

        registerHandlers();

        tc.execute(() -> {
            spread.open();
            spread.join("clients");
        });
    }


    @Override
    public boolean addTask(Task t) {
        this.complete = new CompletableFuture<>();

        SpreadMessage m = new SpreadMessage();
        m.addGroup("servers");
        m.setAgreed();
        spread.multicast(m, new AddTaskReq(reqID.incrementAndGet(), t));

        try {
            AddTaskRep reply = (AddTaskRep) this.complete.get();
            return reply.result;
        } catch (Exception e) {
            System.out.println("Error getting reply from Add Task operation");
        }

        return false;
    }


    @Override
    public Task getNextTask() {
        return null;
    }


    @Override
    public boolean finishTask(Task t) {
        return false;
    }


    public void registerMessages() {
        tc.serializer().register(AddTaskReq.class);
        tc.serializer().register(AddTaskRep.class);
        tc.serializer().register(GetTaskReq.class);
        tc.serializer().register(GetTaskRep.class);
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

    }

}
