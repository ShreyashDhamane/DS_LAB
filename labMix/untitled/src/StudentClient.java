import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class StudentClient {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) throws RemoteException {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 8000);

            studentLogin authenticator = (studentLogin) reg.lookup("StudentTeacherPortal");

            boolean ahead = true;
            int choice;
            String username, password;
            while (ahead){
                printList();

                System.out.print("Your choice: ");
                choice = scanner.nextInt();scanner.nextLine();

                switch (choice){
                    case 0:
                        ahead = false;
                        break;
                    case 1:
                        System.out.print("\nEnter Username:");
                        username = scanner.nextLine();

                        System.out.print("Enter Password: ");
                        password = scanner.nextLine();

                        boolean isValidStudent = authenticator.authenticateStudent(username, password);

                        if (!isValidStudent) {
                            System.out.println("\nInvalid login Credentials.");
                        } else {
                            System.out.println("\nStudent Logged In Successfully.");
                        }
                        break;
                    case 2:
                        System.out.print("\nEnter Username:");
                        username = scanner.nextLine();

                        System.out.print("Enter Password: ");
                        password = scanner.nextLine();

                        boolean isUserCreated = authenticator.createStudent(username, password);

                        if (!isUserCreated) {
                            System.out.println("\nUnable to create new user..");
                        } else {
                            System.out.println("\nNew user created successfully.");
                        }
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printList(){
        System.out.println("Options:");
        System.out.println("1.Login to your student account.");
        System.out.println("2.Create new student account.");
        System.out.println("0.exit");
    }
}
