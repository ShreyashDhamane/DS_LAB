package cs455.scaling.task;

import cs455.scaling.server.ServerStatistics;
import cs455.scaling.threadpool.Task;
import cs455.scaling.util.LOGGER;
import cs455.scaling.util.Util;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;


public class ClientRegistrationTask implements Task {

  // for logging
  private static final transient LOGGER log = new LOGGER(ClientRegistrationTask.class.getSimpleName(), false);

  private final SelectionKey selectionKey;
  private final Selector selector;
  private final ServerSocketChannel serverSocketChannel;
  private final ConcurrentHashMap<String, ServerStatistics> registeredClients;

  public ClientRegistrationTask(SelectionKey selectionKey,
                                ConcurrentHashMap<String, ServerStatistics> registeredClients) {
    this.selectionKey = selectionKey;
    this.selector = selectionKey.selector();
    this.serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
    this.registeredClients = registeredClients;
  }
  @Override
  public void execute() {
    log.info("Running client registration task");
    try {
      selector.wakeup();
      synchronized (selector) {
        log.info("Proceeding to register client.");
        // Grab the incoming socket from the serverSocket
        SocketChannel client = serverSocketChannel.accept();
        if (client != null) {
          // Configure it to be a new channel and key that our selector should monitor
          client.configureBlocking(false);
          client.register(selector, SelectionKey.OP_READ);
          registeredClients.put(Util.getRemoteAddressPortPair(client.socket()), new ServerStatistics());
          log.info("New client registered " + Util.getRemoteAddressPortPair(client.socket()));
          log.info("Number of registered clients: " + registeredClients.size());

        } else {
          log.error(log.RED("Client is null."));
        }
      }

    } catch (IOException e) {
      log.printStackTrace(e);


    } finally {
      selectionKey.attach(null); // detach the object
    }
  }
}
