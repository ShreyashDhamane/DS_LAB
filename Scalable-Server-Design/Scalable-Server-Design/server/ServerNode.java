package cs455.scaling.server;

import cs455.scaling.task.BatchTaskManager;
import cs455.scaling.task.ClientRegistrationTask;
import cs455.scaling.task.ReadAndRespondTask;
import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.util.LOGGER;
import cs455.scaling.util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
public class ServerNode {

  // for logging
  private static final LOGGER log = new LOGGER(ServerNode.class.getSimpleName(), false);
//
//  /**
//   * Hashmap to map the IP and Port pair of the registered clients to the {@link ServerStatistics} object
//   * for keeping track of the number of messages received from the particular client.
//   */
  private final ConcurrentHashMap<String, ServerStatistics> registeredClients;
  private final int port_num;

//  When a server receives a message, it is not serviced immediately, instead it is put in a batch which is managed
//   by the batch task manager, i.e. the following variable. When the timer of a batch expires or the batch has
//reached its capacity, the batch task manager dispatches that batch to the thread pool to be serviced by a thread.

  private final BatchTaskManager batch;
  private final ThreadPool threadPool;
  public ServerNode(int port_num, int thread_pool_size, int batch_size, double batch_time) {
    this.port_num = port_num;

    // initialize the thread pool
    this.threadPool = new ThreadPool(thread_pool_size);
    this.threadPool.startWorkers(); // start the worker threads

    this.registeredClients = new ConcurrentHashMap<>();
    this.batch = new BatchTaskManager(batch_size, batch_time, threadPool);
  }

  public void runPrinter() {
    log.info("Starting printer thread");
    Thread t = new Thread(() -> {
      while (true) {
        double serverThroughput = 0; // throughput of the server, message rate per 20 seconds
        int numClients = this.registeredClients.size(); // number of registered clients
        long timestamp = Util.getTimestamp();
        ArrayList<Double> throughputPerClient = new ArrayList<>(); // Store the total throughput per client

        // get total messages per client
        synchronized (this.registeredClients) {
          Enumeration<String> keys = registeredClients.keys();
          while (keys.hasMoreElements()) {
            ServerStatistics cs = registeredClients.get(keys.nextElement());
            double throughput = cs.getTotalMessages() / 20.0; // calculate the throughput in the past 20 sec.
            serverThroughput += throughput;
            throughputPerClient.add(throughput);
            cs.resetMessagesCount();
          }
        }

        // mean per client throughput
        double meanClientThroughput = (numClients == 0) ? 0 : serverThroughput / numClients;

        // Compute standard deviation
        double stdDev = (numClients == 0) ? 0 : Util.computeStandardDeviation(throughputPerClient, meanClientThroughput);

        System.out.println(
            String.format("[%d] Server Throughput: %.2f messages/s, Active Client Connections: %d, "
                    + "Mean Per-client Throughput: %.2f messages/s, Std. Dev. Of Per-client Throughput: "
                    + "%.2f messages/s",
                timestamp, serverThroughput, numClients, meanClientThroughput, stdDev)
        );

        try {
          log.info("printer thread sleeping.");
          Thread.sleep(20 * 1000);
          log.info("Printer thread woke up.");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }
    });
    t.start();
    log.info("Started printer thread.");
  }

// If the key has the OP_ACCEPT interest flag set, a new registration task is
//    created with the current selection key and sent to the batch. If, however the OP_READ interest flag was set,
//   then a new read and respond task is created and sent to the batch task manager to be later sent to the thread pool.

  public void startServer() throws IOException {
    this.batch.START();
    log.info("Starting server...");

    Selector selector = Selector.open(); // open the selector for listening to channels
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); // create input channel
    serverSocketChannel.bind(new InetSocketAddress(port_num)); // bind the channel to the port
    serverSocketChannel.configureBlocking(false);

    log.info("Started server socket channel on port " + port_num);

    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // register channel to the selector

    while (true) { // Loop on the selector

      // Selects a set of keys whose corresponding channels are ready for I/O operations.
      // block until at least one channel is ready for operation
      if (selector.select() == 0) {
        log.info("selector.select() == 0");
        continue;
      }

      // Loop over ready keys
      Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
      while (iter.hasNext()) {
        SelectionKey key = iter.next(); // Grab the current key
        iter.remove();

        if (!key.isValid()) {
          log.error("Invalid key.");
          continue;
        }

        // New connection on server socket, add registration request to thread pool
        if (key.isValid() && key.attachment() == null) {
          try {
            log.info("key.isAcceptable(): " + key.isAcceptable());
            log.info("key.isReadable(): " + key.isReadable());
            if (key.isValid() && key.isAcceptable()) {
              key.attach(new Object());
              batch.addTask(new ClientRegistrationTask(key, registeredClients));
            } else if (key.isValid() && key.isReadable()) {
              key.attach(new Object());
              batch.addTask(new ReadAndRespondTask(key, registeredClients));
            }
          } catch (CancelledKeyException | InterruptedException e) {
            log.error("Received cancellation exception.");
          }
        }

      }

    }
  }


}
