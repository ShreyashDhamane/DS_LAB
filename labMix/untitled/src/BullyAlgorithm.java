import ClockServers.ServerTime;
import ClockServers.ServerTimeImpl;
import common.AppConstants;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

import static common.AppConstants.formatter;

public class BullyAlgorithm extends UnicastRemoteObject {

    public static ServerTime leader;

    protected BullyAlgorithm() throws RemoteException {
    }

    //Main function
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        //store the servers in a list
        ArrayList<ServerTime> sl = new ArrayList();
        ServerTime mc1 = createMachineServer(1);
        ServerTime mc2 = createMachineServer(2);
        ServerTime mc3 = createMachineServer(3);
        ServerTime mc4 = createMachineServer(4);

        sl.add(mc1);
        sl.add(mc2);
        sl.add(mc3);
        sl.add(mc4);
        System.out.println("=====");
        //initially select a leader
        System.out.println("Initial Election Started...");
        leader = initialElection(sl, sl.size());
        System.out.println("Coordinator is : P"+ leader.getPid());
        System.out.println("=====");

        //processes communicate with the leader. If leader is down, then election process is executed.
        pingLeader(sl, leader);
        while(ProcessElection.isElectionFlag()) {
            election(sl, leader);
        }
        ProcessElection.setPingLeaderFlag(true);

    }

    public static ServerTime createMachineServer(int machineNumber) throws Exception {
        String serverName = AppConstants.SERVER_NAME;
        int serverPort = 0;
        if(machineNumber == 1){
            serverPort = AppConstants.SERVER_PORT_1;
        }else if(machineNumber == 2){
            serverPort = AppConstants.SERVER_PORT_2;
        }else if(machineNumber == 3){
            serverPort = AppConstants.SERVER_PORT_3;
        }else if(machineNumber == 4){
            serverPort = AppConstants.MAIN_SERVER_PORT;
        }
        else{
            serverPort = -1;
        }
        Registry machineRegistry = LocateRegistry.getRegistry(serverName, serverPort);
        ServerTime machineServerTime = (ServerTime) machineRegistry.lookup(ServerTimeImpl.class.getSimpleName());
        LocalTime machineTime = machineServerTime.getLocalTime();
        System.out.println("Connection with the machine " + machineNumber + " successfully established. Hour: "
                + formatter.format(machineTime));
        return machineServerTime;
    }
    //initially select a leader
    public static ServerTime initialElection(ArrayList sl, int no_of_processes) throws RemoteException {
        LocalTime localTime = LocalTime.parse(AppConstants.LOCAL_HOUR, formatter);
        ServerTime temp = new ServerTimeImpl(localTime, 0);
        for (int i = 0; i < sl.size(); i++) {
            ServerTime p = (ServerTime) sl.get(i);
            if (temp.getPid() < p.getPid())
                temp = p;
        }
        temp.setCoordinatorFlag(true);
        return temp;
    }

    //elect new leader
    public static void election(ArrayList<ServerTime> pl, ServerTime leader) throws RemoteException {
        ServerTime ed = ProcessElection.getElectionInitiator();
        if((ed.getPid()) == leader.getPid()) {
            ServerTime oldLeader = leader;
            (oldLeader).setDownflag(false);
//            leader = pl.get(ed.getPid()-1);
            leader = pl.get(ed.getPid()-2);
            ProcessElection.setElectionFlag(false);
            leader.setCoordinatorFlag(true);
            System.out.println("\nNew Coordinator is : P" + leader.getPid());
        }
        else {
            System.out.print("\n");
            for(int i = ed.getPid() + 1; i<=pl.size();i++) {
                System.out.println("P" + ed.getPid() + ": Sending message to P" + i);
            }
            //get next higher node
            ServerTime a = pl.get(ed.getPid());
            ProcessElection.setElectionInitiator(a);
        }


    }

    //ping leader to check if its active
    public static void pingLeader(ArrayList<ServerTime> pl, ServerTime leader) throws RemoteException {
        Random random = new Random();
        int r = random.nextInt(4) + 1;
        System.out.println('r');
        System.out.println(r);
        int j = 0;
        while(ProcessElection.isPingLeaderFlag()) {
            for(int i = 0; i<pl.size(); i++) {
                ServerTime p = pl.get(i);
                System.out.println("pid");
                System.out.println(p.getPid());
                System.out.println(p.isCoordinatorFlag());
                if(!(p.isCoordinatorFlag())) {
                    System.out.println("P" + p.getPid() + ": Coordinator, are you there?");
                    j++;
                    if(j==r)
                        leader.setDownflag(true);
                    if(!(leader.isDownflag()))
                        System.out.println("P" +leader.getPid() + ": Yes");
                    else {
                        ProcessElection.setElectionFlag(true);
                        ProcessElection.setElectionInitiator(p);
                        System.out.println("P" + p.getPid() + ": Coordinator is down. Initiating election..");
                        ProcessElection.setPingLeaderFlag(false);
                        break;
                    }
                }
            }
        }
    }
}
