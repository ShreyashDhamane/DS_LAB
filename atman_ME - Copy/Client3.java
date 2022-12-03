import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.time.*;

public class Client3 {
    public static void main(String args[]) throws RemoteException {
        try {
            Scanner sc = new Scanner(System.in);
            Registry reg = LocateRegistry.getRegistry("localhost", 8081);
            Balance_LoadInterface lb = (Balance_LoadInterface) reg.lookup("Balance_Load");
            System.out.println("Connected to server " +
                    lb.getServerName());
            check_course obj_bal = lb.getServer();
            Clock client_time;
            Registry reg_time;
            getTime obj;
            long start;
            long serverTime;
            long end;
            long rtt;
            long updatedTime;
            System.out.print("\nEnter student account ID:");
            String acc_no = sc.nextLine();
            System.out.print("Enter password:");
            String password = sc.nextLine();
            System.out.println("Choices:\n1.Check existing courses\n2.Enroll into new course\nEnter choice:");
            int ch = sc.nextInt();
            switch (ch) {
                case 1:
                    client_time = Clock.systemUTC();
                    reg_time = LocateRegistry.getRegistry("localhost", 8080);
                    obj = (getTime) reg_time.lookup("Server_Time");
                    start = Instant.now().toEpochMilli();
                    serverTime = obj.getSystemTime();
                    System.out.println("Server time " + serverTime);
                    end = Instant.now().toEpochMilli();
                    rtt = (end - start) / 2;
                    System.out.println("Round Trip Time " + rtt);
                    updatedTime = serverTime + rtt;
                    client_time = Clock.offset(client_time,
                            Duration.ofMillis(updatedTime -
                                    client_time.instant().toEpochMilli()));
                    System.out.println("New Client time " +
                            client_time.instant().toEpochMilli());
                    double bal = obj_bal.check_courseance(acc_no, password);
                    if (bal == -1) {
                        System.out.println("\nInvalid credentials");
                        return;
                    } else {
                        System.out.println("\nNumber of existing courses:" + bal + "\n");
                    }
                    break;
                case 2:
                    sc.nextLine();
                    System.out.print("Enter Teacher ID: ");
                    String cred_acc_no = sc.nextLine();
                    System.out.print("Enter course admission amount: ");
                    double amt = sc.nextDouble();
                    client_time = Clock.systemUTC();
                    reg_time = LocateRegistry.getRegistry("localhost", 8080);
                    obj = (getTime) reg_time.lookup("Server_Time");
                    start = Instant.now().toEpochMilli();
                    serverTime = obj.getSystemTime();
                    System.out.println("Server time " + serverTime);
                    end = Instant.now().toEpochMilli();
                    rtt = (end - start) / 2;
                    System.out.println("Round Trip Time " + rtt);
                    updatedTime = serverTime + rtt;
                    client_time = Clock.offset(client_time,
                            Duration.ofMillis(updatedTime -
                                    client_time.instant().toEpochMilli()));
                    System.out.println("New Client time " +
                            client_time.instant().toEpochMilli());
                    boolean status = obj_bal.transfer(acc_no, cred_acc_no, password, amt);
                    if (status) {
                        System.out.println("\nCourse added");
                        //System.out.println("New Balance:" + obj_bal.check_courseance(acc_no, password));
                    } else {
                        System.out.println("\nError occured");
                    }
                    break;
                default:
                    System.out.println("Wrong choice entered");
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}