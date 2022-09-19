import java.rmi.*;

public interface studentLogin extends Remote {
    public boolean authenticateStudent(String username, String password) throws RemoteException;
}
