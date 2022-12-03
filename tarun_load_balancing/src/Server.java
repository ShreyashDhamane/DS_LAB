import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements check_course {
    public Server(int serverNo) throws RemoteException {
        super();
        RN = new int[3];
        no_of_requests = 0;
        critical = false;
        this.serverNo = serverNo;
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 8082);
            TokenInterface token = (TokenInterface) reg.lookup("tokenServer");
            this.token = token;
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e.getMessage());
        }
    }

    static ArrayList<Account> a = new ArrayList<Account>() {
        {
            add(new Account("stud111", "studAtman", 6));
            add(new Account("stud222", "studTarun", 5));
            add(new Account("tech333", "techShreyash", 7));
            add(new Account("tech444", "techVatsal", 3));
        }
    };
    int RN[];
    boolean critical;
    int no_of_requests;
    TokenInterface token;
    int serverNo;

    public double check_courseance(String acc_no, String password) throws RemoteException {
        System.out.println("Course updation received from student ID number "
                + acc_no);
        for (int i = 0; i < a.size(); i++) {
            double bal = a.get(i).check_courseance(acc_no, password);
            if (bal != -1)
                return bal;
        }
        return -1.0;
    }

    public boolean transfer(String d_acc_no, String cred_acc_no, String password, double amt) throws RemoteException {
        System.out.println("Course updation received to teacher ID number "
                + d_acc_no);
        System.out.println("Course transfer to teacher ID number " +
                cred_acc_no);
        boolean isValid = false;
        for (int i = 0; i < a.size(); i++) {
            isValid = a.get(i).checkValid(d_acc_no, password);
            if (isValid) {
                break;
            }
        }
        if (!isValid) {
            return false;
        } else {
            if (token.getOwner() == -1) {
                token.setOwner(serverNo);
                System.out.println("No owner");
                no_of_requests++;
                RN[serverNo]++;
            } else {
                sendRequest();
            }
            while (token.getOwner() != serverNo)
                ;
            System.out.println("Got token");
            critical = true;
            boolean b = critical_section(d_acc_no, cred_acc_no, password,
                    amt);
            critical = false;
            releaseToken();
            return b;
        }
    }

    public void sendRequest() throws RemoteException {
        no_of_requests++;
        for (int i = 0; i < 1; i++) {
            try {
                Registry reg = LocateRegistry.getRegistry("localhost",
                        8000 + i);
                check_course server = (check_course) reg.lookup("studentServer" + i);
                server.receiveRequest(serverNo, no_of_requests);
            } catch (Exception e) {
                System.out.println("Exception occurred : " +
                        e.getMessage());
            }
        }
    }

    public boolean critical_section(String d_acc_no, String cred_acc_no,
            String password, double amt) {
        int deb_ind = 0;
        int cred_ind = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).acc_no.equals(d_acc_no) &&
                    a.get(i).password.equals(password)) {
                deb_ind = i;
            }
            if (a.get(i).acc_no.equals(cred_acc_no)) {
                cred_ind = i;
            }
        }
        if (a.get(deb_ind).balance < amt)
            return false;
        else {
            a.get(deb_ind).balance -= amt;
            a.get(cred_ind).balance += amt;
            return true;
        }
    }

    public void receiveRequest(int i, int n) throws RemoteException {
        System.out.println("Received request from " + i);
        if (RN[i] <= n) {
            RN[i] = n;
            if (token.getToken()[i] + 1 == RN[i]) {
                if (token.getOwner() == serverNo) {
                    if (critical) {
                        System.out.println("Add to queue");
                        token.getQueue()[token.getTail()] = i;
                        token.setTail(token.getTail() + 1);
                    } else {
                        System.out.println("Queue empty, setting owner");
                        token.setOwner(i);
                    }
                }
            }
        }
    }

    public void releaseToken() throws RemoteException {
        token.setToken(serverNo, RN[serverNo]);
        if (token.getHead() != token.getTail()) {
            System.out.println("Release token");
            token.setOwner(token.getQueue()[token.getHead()]);
            System.out.println("New owner" + token.getOwner());
            token.setHead(token.getHead() + 1);
        }
    }

    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.createRegistry(8000);
            reg.rebind("studentServer0", new Server(0));
            Registry reg1 = LocateRegistry.createRegistry(8001);
            reg1.rebind("studentServer1", new Server(1));
            Registry reg2 = LocateRegistry.createRegistry(8002);
            reg2.rebind("studentServer2", new Server(2));
            Registry reg3 = LocateRegistry.createRegistry(8003);
//            reg.rebind("studentServer3", new Server(3));
//            Registry reg4 = LocateRegistry.createRegistry(8004);
//            reg1.rebind("studentServer4", new Server(4));
//            Registry reg5 = LocateRegistry.createRegistry(8005);
//            reg2.rebind("studentServer5", new Server(5));
            System.out.println("3 servers are running now ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Account {
    String acc_no;
    String password;
    double balance;

    Account(String acc_no, String password, double balance) {
        this.acc_no = acc_no;
        this.password = password;
        this.balance = balance;
    }

    public double check_courseance(String acc_no, String password) {
        if (this.acc_no.equals(acc_no) && this.password.equals(password))
            return this.balance;
        else
            return -1.0;
    }

    public boolean checkValid(String acc_no, String password) {
        if (this.acc_no.equals(acc_no) && this.password.equals(password))
            return true;
        else
            return false;
    }
}
