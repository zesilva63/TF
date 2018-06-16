import business.Task;
import interfaces.Tasker;
import remote.RemoteTasker;

public class Client {

    private Tasker tasker;
    private int id;

    public Client(int id) {
        this.tasker = new RemoteTasker(id);
        this.id = id;
    }

    public static void main(String[] args) {
        int id = Integer.parseInt(args[0]);
        Tasker tasker = new RemoteTasker(id);

        Task t = new Task("Tarefa 1");
        boolean b  = tasker.addTask(t);
        System.out.println("Resposta ao pedido " + b);

        Task t2 = tasker.getNextTask();
        System.out.println("TASK atribuida " + t2.getUrl());
        //Task t3 = new Task(1,"");
        boolean b2 = tasker.finishTask(t2);
        System.out.println("Finalizou pedido " + b);

    }
}
