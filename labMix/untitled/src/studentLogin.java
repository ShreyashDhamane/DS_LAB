import java.rmi.*;

public interface studentLogin extends Remote {
    public boolean authenticateStudent(String username, String password) throws RemoteException;
    public boolean createStudent(String username, String password) throws RemoteException;
}
