package cs455.scaling.threadpool;

import cs455.scaling.util.LOGGER;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {

  // Logging
  private static final LOGGER log = new LOGGER(ThreadPool.class.getSimpleName(), false);

  private BlockingQueue<Task> taskQueue;
  private ArrayList<WorkerThread> workers;
  private boolean status = false; // indicates whether or not the workers in the pool are running
  private int idleThreads = -1; // number of threads in the pool that are idle, negative numbers indicate
  // no workers

  public ThreadPool(int numThreads) {
    this.workers = new ArrayList<WorkerThread>();
    this.taskQueue = new LinkedBlockingQueue<Task>();

    log.info("Initializing thread pool with "+numThreads+" threads.");
    // Initialize the worker threads
    for (int i=0; i < numThreads; ++i) {
      log.info("Initializing worker thread "+i);
      this.workers.add(new WorkerThread(this, i));
    }
    log.info("Done initializing threads.");

    // Set the number of idle threads
    idleThreads = this.workers.size();
  }

  public Task getTask() throws InterruptedException {
    return this.taskQueue.take();
  }

//  Add a task to the queue. We do not need synchronized here since LinkedBlockingQueue is thread-safe
//  @param e The task that is to be added to the task queue maintained by the threadpool.

  public synchronized void addTask(Task e) throws InterruptedException {
    this.taskQueue.put(e);
  }

//  Call the start method on each worker. Unless this method is called, none of the workers thread start. Calling
//  this function turns each of the worker objects into threads by invoking the run method on each.
  public void startWorkers() {
    for (WorkerThread worker : this.workers)
      worker.start();
    status = true;
  }

//
//    When a thread is busy executing a task, calling the following function from the worker thread before the
//    beginning of the execution will decrement the number of idle threads in the pool by 1, since the current worker
//    thread that called the following function is about the start executing its task and will be busy till it is done
//    with the task.
//
  public synchronized void decrementIdleThreads() {
     this.idleThreads--;
  }

  /**
   * When a thread is done executing its task, calling the following from the worker thread increments the idle
   * thread counter since now the number of idle threads have increased by 1.
   */
  public synchronized void incrementIdleThread() {
    this.idleThreads++;
  }

  public int getIdleThreads() {
    return this.idleThreads;
  }

//  Get the status of the workers in the pool.
  public boolean getStatus() {
    return this.status;
  }

//  Return true if all the threads in the pool are done with all the tasks in the taskQueue.
  public boolean isDone() {
    return (this.getIdleThreads() == this.workers.size()) && (this.taskQueue.size() == 0);
  }

  public String toString() {
    return new String(
        "ThreadPool with "+this.workers.size()+" worker threads and "+
            this.taskQueue.size()+" elements in task queue."
    );
  }

  public int size() {
    return this.workers.size();
  }

  public int getTaskQueueSize() {
    return this.taskQueue.size();
  }

  public void shutdown() {
    for (WorkerThread worker : this.workers) {
      worker.kill();
    }
    this.taskQueue = null;
    this.workers = null;
    this.idleThreads = -1;
  }

}
