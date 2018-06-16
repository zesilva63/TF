package interfaces;

import business.Task;
import io.atomix.catalyst.serializer.CatalystSerializable;

/*
* Represents the task manager.
* */
public interface Tasker {

    /* Adds a task to the system */
    boolean addTask(Task t);

    /* Returns the next task for a client */
    Task getNextTask();

    /* Add tasks from client disconencted */
    boolean reallocateTasks(String client);

    /* Finishes a task executed attributed to a client */
    boolean finishTask(Task t);

    String print();
}
