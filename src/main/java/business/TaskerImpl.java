package business;

import interfaces.Tasker;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.*;
import java.util.function.Predicate;

public class TaskerImpl implements Tasker, CatalystSerializable {

    private Queue<Task> waitingTasks;    // waiting tasks to be atributted
    private List<Task> pendingTasks;    // tasks waiting to finish


    public TaskerImpl() {
        waitingTasks = new LinkedList<>();
        pendingTasks = new ArrayList<>();
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
    public synchronized boolean finishTask(Task task) {
        Predicate<Task> taskPredicate = t -> t.getID() == task.getID();
        boolean result = pendingTasks.removeIf(taskPredicate);

        return result;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {

    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {

    }
}
