import java.rmi.*;

// Remote interface for our getting time in application
public interface loadBalancerInterface extends Remote {

    public checkBal getServer() throws RemoteException;

    public int get_name_server() throws RemoteException;

}
