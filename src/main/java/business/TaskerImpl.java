package business;

import interfaces.Tasker;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.*;
import java.util.function.Predicate;

public class TaskerImpl implements Tasker, CatalystSerializable {

    private Deque<Task> waitingTasks;    // waiting tasks to be atributted
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
    public synchronized String print() {
        return "Waiting " + waitingTasks.toString() + "\n" + "Pending " + pendingTasks.toString();
    }

    @Override
    public synchronized Task getNextTask() {
        Task t = waitingTasks.pollFirst();

        if(t != null) {
            pendingTasks.add(t);
        }

        return t;
    }


    @Override
    public synchronized boolean reallocateTasks(String cli) {
        Predicate<Task> taskPredicate = t -> t.getClient().equals(cli);
        this.pendingTasks.stream().filter(taskPredicate).forEach(t -> waitingTasks.addFirst(t));
        return pendingTasks.removeIf(taskPredicate);
    }


    @Override
    public synchronized boolean finishTask(Task task) {
        Predicate<Task> taskPredicate = t -> t.getID() == task.getID();
        return pendingTasks.removeIf(taskPredicate);
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(pendingTasks, bufferOutput);

        bufferOutput.writeInt(waitingTasks.size());
        for(Task t: waitingTasks)
            serializer.writeObject(t, bufferOutput);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        pendingTasks = serializer.readObject(bufferInput);

        int size = bufferInput.readInt();
        waitingTasks = new LinkedList<>();
        for (int i = 0; i < size; i++)
            waitingTasks.add(serializer.readObject(bufferInput));
    }
}
