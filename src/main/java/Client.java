import business.Menu;
import business.Task;
import interfaces.Tasker;
import remote.RemoteTasker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client {

    public static void main(String[] args) {

        int id = Integer.parseInt(args[0]);
        Tasker tasker = new RemoteTasker(id);
        List pendingTasks = new ArrayList<>();
        int running = 1;


        String[] menu0 = {"Add Task",
                          "Get next Task",
                          "Finish Task"
        };

        Menu main_menu = new Menu(menu0);


        do {
            main_menu.execute();
            switch (main_menu.getOption()) {
                case 1: AddTask(tasker);
                        break;
                case 2: GetNextTask(tasker, pendingTasks);
                        break;
                case 3: FinishTask(tasker, pendingTasks);
                        break;
                case 0: running = 0;
                }
        } while (running != 0);

        System.out.println("Thanks, Come Again!");

    }

    private static void AddTask(Tasker tasker) {
        Scanner is = new Scanner(System.in);
        String url = null;

        System.out.print("Task url: ");
        url = is.nextLine();

        Task t = new Task(url);
        Boolean res = tasker.addTask(t);

        if(res)
            System.out.println("Added Task!");
        else
            System.out.println("Error adding Task!");
    }


    private static void GetNextTask(Tasker tasker, List<Task> pendingTasks) {
        Scanner is = new Scanner(System.in);

        Task t = tasker.getNextTask();

        if(t != null) {
            pendingTasks.add(t);
            System.out.println("New Task: " + t.getUrl());
        } else
            System.out.println("Error getting Task!");
    }


    private static void FinishTask(Tasker tasker, List<Task> pendingTasks) {
        int size = pendingTasks.size();
        int i = 0;

        String[] tags = new String[size];
        for(Task t : pendingTasks) {
            tags[i]= t.getUrl();
            i++;
        }

        Menu m1 = new Menu(tags);
        m1.execute();
        if(m1.getOption() != 0) {
            Task t = pendingTasks.get(m1.getOption() - 1);

            Boolean res = tasker.addTask(t);

            if(res) {
                System.out.println("Added Task!");
                pendingTasks.remove(m1.getOption() - 1);
            } else
                System.out.println("Error adding Task");
        }
        
    }
}
