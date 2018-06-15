package business;

import interfaces.Tasker;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.*;

public class TaskerImpl implements Tasker, CatalystSerializable {

    // waiting tasks to be atributted
    private Queue<Task> waitingTasks;
    // tasks waiting to finish
    private List<Task> pendingTasks;

    public TaskerImpl() {
        this.waitingTasks = new LinkedList<>();
        this.pendingTasks = new ArrayList<>();
    }

    @Override
    public synchronized boolean addTask(Task t) {
        return this.waitingTasks.add(t);
    }


    @Override
    public synchronized Task getNextTask() {
        Task t = waitingTasks.poll();

        if(t != null)
            pendingTasks.add(t);

        return t;
    }


    @Override
    public synchronized boolean finishTask(Task t) {
        int taskID = t.getID();
        Task task = pendingTasks.remove(taskID);

        return task != null;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {

    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {

    }
}
