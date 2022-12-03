import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Balance_Load extends UnicastRemoteObject implements
        Balance_LoadInterface {
    int ServerNo;
    int RequestNo;

    public Balance_Load() throws RemoteException {
        ServerNo = 4;
        RequestNo = 0;
    }

    public int getServerName() throws RemoteException {
        int serverNo = RequestNo % ServerNo;
        return serverNo;
    }

    public check_course getServer() throws RemoteException {
        int serverNo = RequestNo % ServerNo;
        RequestNo++;
        check_course server = null;
        // return "server"+serverNo;
        try {
            String path = "studentServer" + serverNo;
            System.out.println("Redirecting request to server "
                    + serverNo);
            Registry reg = LocateRegistry.getRegistry("localhost",
                    8000 + serverNo);
            server = (check_course) reg.lookup(path);
        } catch (NotBoundException e) {
            System.out.println("Unable to connect to server, trying nextone " + e);
            server = this.getServer();
        }
        return server;
    }

    public static void main(String[] args) throws RemoteException {
        Balance_Load token = new Balance_Load();

        try {
            String objPath = "Balance_Load";
            Registry reg = LocateRegistry.createRegistry(8081);
            reg.rebind(objPath, new Balance_Load());
            System.out.println("The Load Balancer is running now...");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}