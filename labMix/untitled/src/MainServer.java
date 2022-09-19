import ClockServers.ServerTime;
import ClockServers.ServerTimeImpl;
import ClockServers.Student;
import common.AppConstants;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.util.ArrayList;

import static common.AppConstants.formatter;

// unicast remote object is a remote object that can only be accessed by one client at a time
public class MainServer extends UnicastRemoteObject implements studentLogin {
    public MainServer() throws RemoteException {
        super();
    }

    //can use hashmaps,
    public static ArrayList<Student> students = new ArrayList<Student>();

    @Override
    public boolean authenticateStudent(String username, String password) throws RemoteException {
        System.out.println("Authenticating The User.");
        boolean isValidUser = false;
        for(int i = 0 ; i < students.size() ; i++){
            System.out.println("Inside the Loop.");
            if(students.get(i).authenticateUser(username, password)){
                System.out.println("Inside the Authenticator.");
                System.out.println(students.get(i));
                isValidUser = true;
                break;
            }
        }
        return isValidUser;
    }

    @Override
    public boolean createStudent(String username, String password) throws RemoteException {
        boolean success = false;
        try{
            Student newStudent = new Student(username, password);
            students.add(newStudent);
            success = true;
        }catch (Exception e){
            System.out.println(e);
        }
        return success;
    }

    public static void main(String[] args) {
        String serverName = "StudentTeacherPortal";
        try {
            Registry reg = LocateRegistry.createRegistry(8000);
            reg.rebind(serverName, new MainServer());
            System.out.println("Server is running...");

            Student student = new Student("shreyash", "password");
            students.add(student);

        } catch (Exception e) {

            e.printStackTrace();
        }

        //Berkely Server
        try {
            LocalTime localTime = LocalTime.parse(AppConstants.LOCAL_HOUR, formatter);
            System.out.println("Time local: " + formatter.format(localTime));

            // creation of servers (machines)
            ServerTime machine1Server = createMachineServer(1);
            ServerTime machine2Server = createMachineServer(2);
            ServerTime machine3Server = createMachineServer(3);

            // calculate the average of the hours
            var avgDiff = generateAverageTime(localTime,
                    machine1Server.getLocalTime(),
                    machine2Server.getLocalTime(),
                    machine3Server.getLocalTime());

            // adjust servers time
            machine1Server.adjustTime(localTime, avgDiff);
            machine2Server.adjustTime(localTime, avgDiff);
            machine3Server.adjustTime(localTime, avgDiff);
            localTime = localTime.plusNanos(avgDiff);

            System.out.println("\nUpdated schedules!");
            System.out.println("Local time: " + formatter.format(localTime));
            System.out.println("Server 1 time: " + formatter.format(machine1Server.getLocalTime()));
            System.out.println("Server 2 time: " + formatter.format(machine2Server.getLocalTime()));
            System.out.println("Server 3 time: " + formatter.format(machine3Server.getLocalTime()));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    //	/**
//* Creates a {@link ServerTime} that is associated with a machine to have its time
//* adjusted.
//*
//* @param machineNumber machine number
//* @return machine server with its time
//* @throws Exception when trying to create server or record
//*/
    private static ServerTime createMachineServer(int machineNumber) throws Exception {
        String serverName = AppConstants.SERVER_NAME;
        int serverPort = 0;
        if(machineNumber == 1){
            serverPort = AppConstants.SERVER_PORT_1;
        }else if(machineNumber == 2){
            serverPort = AppConstants.SERVER_PORT_2;
        }else if(machineNumber == 3){
            serverPort = AppConstants.SERVER_PORT_3;
        }else{
            serverPort = -1;
        }
        Registry machineRegistry = LocateRegistry.getRegistry(serverName, serverPort);
        ServerTime machineServerTime = (ServerTime) machineRegistry.lookup(ServerTimeImpl.class.getSimpleName());
        LocalTime machineTime = machineServerTime.getLocalTime();
        System.out.println("Connection with the machine " + machineNumber + " successfully established. Hour: "
                + formatter.format(machineTime));
        return machineServerTime;
    }

    //	/**
//* Calculates the average of the hour that must be adjusted.<br>
//* Added time of the machines (each subtracted by the local time) divided by the
//* total of machines.
//*
//* @param localTime local time
//* @param times machine time
//* @return calculated average hour
//*/
    private static long generateAverageTime(LocalTime localTime, LocalTime... times) {
        long nanoLocal = localTime.toNanoOfDay();
        long difServer = 0;
        for (LocalTime t : times) {
            difServer += t.toNanoOfDay() - nanoLocal;
        }
        return difServer / (times.length + 1);
    }

}
