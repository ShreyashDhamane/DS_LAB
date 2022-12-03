import java.rmi.*;

// Remote interface for our application
public interface check_course extends Remote {

    public double check_courseance(String acc_no, String password) throws RemoteException;

    public void receiveRequest(int serverNo, int no_of_requests) throws RemoteException;

    public boolean transfer(String acc_no, String cred_acc_no, String password, double amt) throws RemoteException;

}
