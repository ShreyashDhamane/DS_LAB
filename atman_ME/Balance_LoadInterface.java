import java.rmi.*;

// Remote interface for our getting time in application
public interface Balance_LoadInterface extends Remote {

    public check_course getServer() throws RemoteException;

    public int getServerName() throws RemoteException;

}
