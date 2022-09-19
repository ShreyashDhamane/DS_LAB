import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) throws RemoteException {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 8000);

            studentLogin authenticator = (studentLogin) reg.lookup("StudentTeacherPortal");

            System.out.print("\nEnter Username:");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            boolean isValidStudent = authenticator.authenticateStudent(username, password);
            if (!isValidStudent) {
                System.out.println("\nInvalid login Credentials.");
            } else {
                System.out.println("\nStudent Logged In Successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
