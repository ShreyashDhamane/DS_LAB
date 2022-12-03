import java.rmi.*;

// Remote interface for our getting time in application
public interface timeGetter extends Remote {
    long getSystemTime() throws RemoteException;
}
