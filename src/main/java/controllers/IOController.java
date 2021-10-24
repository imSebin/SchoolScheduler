package controllers;
import java.util.List;
import java.util.Scanner;

public class IOController {
    /**
     *
     */
    private static Scanner scanner = new Scanner(System.in);
    //TODO - add exceptions and input error catching to this class

    /**
     *
     * @return the title of an event as input by user
     */
    public static String getTitle(){
        System.out.println("enter title for event: ");
        return scanner.nextLine();
    }

    /**
     *
     * @return the type of event as input by user
     */
    public static String getEventType(){
        System.out.println("Enter 'test' to add test, 'assignment' to add assignment, 'lecture' to add lecture " +
                "[please enter test]"); //TODO after phase 0, more than just tests
        return scanner.nextLine();
    }

    /**
     *
     * @return a course title as input by User
     */
    public static String getCourse(){
        System.out.println("enter course name");
        return scanner.nextLine();
    }

    /**
     *
     * @return date in form of list of integers
     */
    public static List<Integer> getDate(String request){
        System.out.println(request + " (YYYY-MM-DD-HH-MM)");
        String date = scanner.nextLine();
        String[] dateParts = date.split("-");
        return List.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[3]),  Integer.parseInt(dateParts[4]));
    }

    /**
     *
     * @return time in [HH, MM]
     */
    public static List<Integer> getTime(String request){
        System.out.println(request + " (HH-MM)");
        String time = scanner.nextLine();
        String[] timeParts = time.split("-");
        return List.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
    }

    public static String getAnswer(String request) {
        System.out.println(request);
        return scanner.nextLine();
    }
}
