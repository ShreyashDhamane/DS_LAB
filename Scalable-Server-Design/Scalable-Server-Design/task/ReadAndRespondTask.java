package cs455.scaling.task;

import cs455.scaling.server.ServerStatistics;
import cs455.scaling.threadpool.Task;
import cs455.scaling.util.LOGGER;
import cs455.scaling.util.Util;
import cs455.scaling.wireformats.Protocol;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class ReadAndRespondTask implements Task {

  // for logging
  private static final transient LOGGER log = new LOGGER(ReadAndRespondTask.class.getSimpleName(), false);

  private final SelectionKey selectionKey;
  private final SocketChannel client;
  private final ConcurrentHashMap<String, ServerStatistics> registeredClients;

  public ReadAndRespondTask(SelectionKey selectionKey, ConcurrentHashMap<String, ServerStatistics> registeredClients) {
    this.selectionKey = selectionKey;
    this.client = (SocketChannel) selectionKey.channel();
    this.registeredClients = registeredClients;
  }

  @Override
  public void execute() {
    try {
      // Create a buffer to read into
      ByteBuffer buffer = ByteBuffer.allocate(Protocol.MESSAGE_SIZE.getValue());

      int bytesRead = Util.readFromChannel(buffer, client);
      if (bytesRead == -1) {
        log.info(log.RED("bytesRead: "+bytesRead)+" closing connection");
        log.info("Removing "+Util.getRemoteAddressPortPair(client.socket())+" from registered clients list.");
        throw new Exception("Client disconnected.");

      } else {

        byte[] receivedMessage = buffer.array();
        String response = String.format("%40s", Util.SHA1FromBytes(receivedMessage)).replace(" ", "-");

        log.info("Sending "+response+" to "+Util.getRemoteAddressPortPair(client.socket()));
        log.info("Length of response message: "+response.length());

        if (!client.isOpen())
          throw new Exception("Client Disconnected");

        int bytesWritten = Util.writeToChannel(client, response.getBytes());
        if (bytesWritten < 0) {
          throw new Exception("Client disconnected.");
        }

        log.info("bytes written to " + Util.getRemoteAddressPortPair(client.socket()) + ": " + bytesWritten + " bytes");
        log.info("buffer has " + buffer.remaining() + " bytes remaining after writing.");
        log.info("Sent " + response + " to " + Util.getRemoteAddressPortPair(client.socket()));

        if (!client.isOpen())
          throw new Exception("Client Disconnected");

        registeredClients.get(Util.getRemoteAddressPortPair(client.socket())).incrementMessageCount();

        if (!client.isOpen())
          throw new Exception("Client Disconnected");

      }

    } catch (Exception e) {
      log.printStackTrace(e);
      registeredClients.remove(Util.getRemoteAddressPortPair(client.socket()));
      try {
        client.close();
      } catch (Exception ce){}
      selectionKey.cancel();

    } finally {
      selectionKey.attach(null);
    }
  }
}
