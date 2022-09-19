package ClockServers;

import common.AppConstants;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.util.ArrayList;

///**
// * Implementation of {@link Server Time}.
// */
@SuppressWarnings("serial")
public class ServerTimeImpl extends UnicastRemoteObject implements ServerTime {

    private LocalTime localTime;
    public static ArrayList<Student> students = new ArrayList<Student>();

    public ServerTimeImpl() throws RemoteException {
        super();
    }
    public ServerTimeImpl(LocalTime localTime) throws RemoteException {
        this.localTime = localTime;
    }
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


    @Override
    public LocalTime getLocalTime() throws RemoteException {
        return localTime;
    }

    @Override
    public void adjustTime(LocalTime localTime, long avgDiff) throws RemoteException {
        long localTimeNanos = localTime.toNanoOfDay();
        long thisNanos = getLocalTime().toNanoOfDay();
        var newNanos = thisNanos - localTimeNanos;
        newNanos = newNanos * -1 + avgDiff + thisNanos;
        LocalTime newLocalTime = LocalTime.ofNanoOfDay(newNanos);
        this.localTime = newLocalTime;
        System.out.println("Updated time: " + AppConstants.formatter.format(newLocalTime));
    }

}

