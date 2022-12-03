package cs455.scaling.client;

import cs455.scaling.util.LOGGER;
import cs455.scaling.util.Util;
import cs455.scaling.wireformats.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;


public class ClientNode {

  private static final LOGGER log = new LOGGER(ClientNode.class.getSimpleName(), false);
  private final String server_host;
  private final int server_port;
  private final int message_rate;
  private final LinkedList<String> messageDigests;
  private final ByteBuffer receiveBuffer;
  private final ClientStatistics counter;
  private SocketChannel serverConnection = null;
  private Selector selector = null;
  public ClientNode(String server_host, int server_port, int message_rate) {
    this.server_host = server_host;
    this.server_port = server_port;
    this.message_rate = message_rate;
    this.messageDigests = new LinkedList<>();
    this.receiveBuffer = ByteBuffer.allocate(Protocol.SERVER_RESPONSE_MESSAGE_SIZE.getValue());
    this.counter = new ClientStatistics();
  }

  public int getMessageRate() {
    return this.message_rate;
  }
  public SocketChannel getServerConnection() {
    return this.serverConnection;
  }
  public void startPrinter() {
    Thread t = new Thread(() -> {
      while (this.serverConnection.isOpen()) {
        synchronized (this.counter) {
          System.out.println(
              String.format("[%d] Total Sent Count: %d, Total Received Count: %d",
                  Util.getTimestamp(), this.counter.getTotalSentCount(),
                  this.counter.getTotalReceiveCount())
          );
          this.counter.resetCounters();
          if (log.getLogStatus())
            System.out.println(log.PURPLE("size of linked list: " + this.messageDigests.size()));
        }
        System.out.flush();

        try {
          Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
          log.printStackTrace(e);
        }

      }
    });
    t.start();
  }

  public void connectToServer() {
    log.info("Connecting to server at " + server_host + ":" + server_port);
    try {
      selector = Selector.open(); // open a selector to listen for activity on SocketChannel

      serverConnection = SocketChannel.open(new InetSocketAddress(server_host, server_port));
      serverConnection.configureBlocking(false);
      log.info((serverConnection == null) ? "serverConnection is null" : "serverConnection is not null");
      log.info("Connected to server " + Util.getRemoteAddressPortPair(serverConnection.socket()));

      serverConnection.register(selector, SelectionKey.OP_READ);

    } catch (Exception e) {
      e.printStackTrace();
    }
    log.info("Done with method connectToServer");
  }

  public void closeServerConnection() throws IOException {
    if (serverConnection.isOpen())
      serverConnection.close();
  }

  public void startReceiverThread() {
    log.info("Starting receiver thread");
    Thread t = new Thread(() -> {
      while (this.serverConnection.isOpen()) {
        this.receiveMessage();
      }
    });
    t.start();
    log.info("Receiver thread started");
  }

  public void receiveMessage() {
    try {
      this.receiveBuffer.clear(); 
      if (this.selector.select() == 0) {
        return;
      }

      Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator();
      while (iter.hasNext()) {
        SelectionKey key = iter.next(); 
        iter.remove();

        if (!key.isValid()) {
          log.error("Invalid key.");
          continue;
        }

        if (key.isReadable()) {
          int bytesRead = Util.readFromChannel(this.receiveBuffer, this.serverConnection);
          if (bytesRead == -1) {
            log.error("Bytes Read: " + bytesRead);
            continue;
          }
          String response = new String(this.receiveBuffer.array()).trim();
          log.info("Received response: " + response);
          this.removeFromMessageDigest(response);
          this.counter.incrementReceiveCount();

        }
      }

    } catch (CancelledKeyException e) {
      log.error("Received cancelled key exception");

    } catch (Exception e) {
      log.error("Server returned an element not in linked list.");
      log.error("Dumping linked list contents: " + this.messageDigests.toString());
      log.printStackTrace(e);
    }
  }

  /**
   * The following function is responsible for generating and sending random Protocol.MESSAGE_SIZE KB size messages to
   * the server. After this message has been sent to the server, it computes the SHA1 hash of the message and adds it
   * to the linked list data structure of message digests to keep track of messages sent to the server.
   *
   * @throws IOException
   */
  public void sendMessage() throws IOException {
    try {
      // generate a random byte array
      byte[] message = Util.randByteArray(Protocol.MESSAGE_SIZE.getValue());

      // Compute the hash and put into messageDigests linked list
      String messageDigest = String.format("%40s", Util.SHA1FromBytes(message)).replace(" ", "-");
      this.addToMessageDigest(messageDigest);
      this.counter.incrementSentCount();

      // Send the message to the server
      log.info("Sending message to server.");
      int bytesWritten = Util.writeToChannel(serverConnection, message);
      log.info("bytesWritten: " + bytesWritten);
      log.info("Message sent to server.");

    } catch (IOException e) {
      throw e;

    } catch (Exception e) {
      log.printStackTrace(e);
    }
  }


  private synchronized void addToMessageDigest(String message) {
    this.messageDigests.add(message);
    log.info("Added " + message + " to linked list.");
  }

  private synchronized void removeFromMessageDigest(String message) throws Exception {
    boolean returnValue = this.messageDigests.remove(message);
    if (returnValue) {
      log.info("Successfully removed message: " + message);
    } else {
      log.error("Linked list does not contain message: " + message);
      log.error("Dumping linked list content: " + messageDigests.toString());
      if (log.getLogStatus()) {
        System.exit(-1);
      }
      throw new Exception("Linked list does not contain message: " + message);

    }
  }


}
