package ClockServers.servers;

import ClockServers.ServerTime;
import ClockServers.ServerTimeImpl;
import common.AppConstants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;

import static common.AppConstants.formatter;

/**
 * Representation of machine 3 to have its time set.
 */
public class Machine3 {

    public static void main(String[] args) {
        try {
            LocalTime hour = LocalTime.parse(AppConstants.MACHINE_3_HOUR, formatter);
            ServerTime machineServer = new ServerTimeImpl(hour);
            Registry registry = LocateRegistry.createRegistry(AppConstants.SERVER_PORT_3);
            registry.rebind(ServerTimeImpl.class.getSimpleName(), machineServer);
            System.out.println(String.format("Machine 3 started on port %d [local time: %s].",
                    AppConstants.SERVER_PORT_3,
                    AppConstants.formatter.format(hour)));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
