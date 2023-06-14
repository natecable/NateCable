import java.io.*;
import java.sql.*;
import java.util.*;

public class Hurts {
    public static void main(String[] args) throws InterruptedException, SQLException, IOException, java.lang.ClassNotFoundException{
        Connection con = null;
        Scanner in = new Scanner(System.in);
        do {
            try {
                System.out.println("Please input your Oracle username on Edgar1:");
                String user_name = in.nextLine();
                System.out.println("Please input your Oracle password on Edgar1:");
                // designed for inputting password without displaying the password:
                Console console = System.console();
                char[] pwd = console.readPassword();
                con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user_name,
                        new String(pwd));
                break;
            } catch (Exception e) {
                System.out.println("Incorrect username/password; try again.");
            }
        } while (true);


        int sel = 0;
        do{
            try{
            System.out.println("Welcome to Hurt's Rent-A-Lemon:\n[1]: Customer Page\n[2]: Staff Page\n[3]: Manager Page\n[4]: Exit");
            if(in.hasNextInt()){
                sel = in.nextInt();
                if(sel == 4){
                    return;
                }else if(sel == 3){
                    ManagerInterface.Manager(con);
                    break;
                }else if(sel == 2){
                    StaffInterface.Staff(con);
                    break;
                }else if (sel == 1){
                    CustomerInterface.Customer(con);
                    break;
                }else{
                    System.out.println(sel + " is not a valid option");
                }
            }else{
                String t = in.next();
                System.out.println(t + " is not a valid option");
            }
        }catch(SQLException p){
            System.out.println("Your session has timed out, you have been sent back to the main menu");
            System.out.println(p.getMessage());
        }
        }while(true);
        System.out.println("Closing down...");
    }
}
