import business.Task;
import interfaces.Tasker;
import remote.RemoteTasker;

import static java.lang.Thread.sleep;

public class Client {

    private Tasker tasker;
    private int id;

    public Client(int id) {
        this.tasker = new RemoteTasker(id);
        this.id = id;
    }

    public static void main(String[] args) throws InterruptedException {
        //int id = Integer.parseInt(args[0]);
        int id = 1;
        Tasker tasker = new RemoteTasker(id);

        Task t = new Task("Tarefa 1");
        boolean b  = tasker.addTask(t);
        System.out.println("Resposta ao pedido " + b);



        //Task t4 = new Task("Tarefa 2");
        //boolean b2  = tasker.addTask(t4);
        //System.out.println("Resposta ao pedido " + b2);

        //Task t2 = tasker.getNextTask();
        //System.out.println("TASK atribuida " + t2.getUrl());

        //boolean b2 = tasker.finishTask(t2);
        //System.out.println("Finalizou pedido " + b);

    }
}
