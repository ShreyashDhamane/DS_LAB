package ClockServers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalTime;

/**
 * Interface for the client-side to access the server.
 */
public interface ServerTime extends Remote {

    /**
     * @return the local time
     */
    LocalTime getLocalTime() throws RemoteException;

    /**
     * Adjusts local time based on server time with average hours.
     *
     * @param localTime server local time
     * @param avgDiff average hours
     */
    void adjustTime(LocalTime localTime, long avgDiff) throws RemoteException;

    public boolean authenticateStudent(String username, String password) throws RemoteException;
    public boolean createStudent(String username, String password) throws RemoteException;
}