package interfaces;

import business.Task;

/*
* Represents the task manager.
* */
public interface Tasker {

    /* Adds a task to the system */
    boolean addTask(Task t);

    /* Returns the next task for a client */
    Task getNextTask();

    /* Finishes a task executed attributed to a client */
    boolean finishTask(Task t);
}
