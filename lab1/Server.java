import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// unicast remote object is a remote object that can only be accessed by one client at a time
public class Server extends UnicastRemoteObject implements studentLogin {
    public Server() throws RemoteException {
        super();
    }

    //can use hashmaps,
    public static ArrayList<Student> students = new ArrayList<Student>();

    @Override
    public boolean authenticateStudent(String username, String password) throws RemoteException {
        System.out.println("Authenticating The User.");
        boolean isValidUser = false;
        for(int i = 0 ; i < students.size() ; i++){
            System.out.println("Inside the Loop.");
            if(students.get(i).authenticateUser(username, password)){
                System.out.println("Inside the Authenticator.");
                System.out.println(students.get(i));
                isValidUser = true;
                break;
            }
        }
        return isValidUser;
    }

    public static void main(String[] args) {
        String serverName = "StudentTeacherPortal";
        try {
            Registry reg = LocateRegistry.createRegistry(8000);
            reg.rebind(serverName, new Server());
            System.out.println("Server is running...");

            Student student = new Student("shreyash", "password");
            students.add(student);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


}

class Student{
    private String username;
    private String password;

    Student(String username, String password){
        this.password = password;
        this.username = username;
    }

    public boolean authenticateUser(String username, String password){

        return this.username.equals(username) && this.password.equals(password);
    }
}