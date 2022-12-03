import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Load_Balance extends UnicastRemoteObject implements
        loadBalancerInterface {
    int serverNo;
    int RequestNo;

    public loadBalancer() throws RemoteException {
        serverNo = 5;
        RequestNo = 0;
    }

    public int get_name_server() throws RemoteException {
        int serverNo = RequestNo % serverNo;
        return serverNo;
    }

    public checkBal getServer() throws RemoteException {
        int serverNo = RequestNo % serverNo;
        RequestNo++;
        checkBal server = null;
        // return "server"+serverNo;
        try {
            String path = "Student Server" + serverNo;
            System.out.println("Redirecting request to server "
                    + serverNo);
            Registry reg = LocateRegistry.getRegistry("localhost",
                    8000 + serverNo);
            server = (checkBal) reg.lookup(path);
        } catch (NotBoundException e) {
            System.out.println("Unable to connect to server, trying nextone " + e);
            server = this.getServer();
        }
        return server;
    }

    public static void main(String[] args) throws RemoteException {
        loadBalancer token = new loadBalancer();

        try {
            String objPath = "loadBalancer";
            Registry reg = LocateRegistry.createRegistry(8081);
            reg.rebind(objPath, new loadBalancer());
            System.out.println("Load Balancer is running successfully...");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}