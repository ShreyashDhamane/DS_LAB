package cs455.scaling.threadpool;

import cs455.scaling.util.LOGGER;

/**
 * This class is the worker thread. This class is responsible for picking a task at a time from the threadpool and
 * executing it till there are no more tasks in the pool.
 */

public class WorkerThread extends Thread {

  // Logging
  private static final LOGGER log = new LOGGER(WorkerThread.class.getSimpleName(), false);
  private boolean _kill_ = false;

  private ThreadPool pool;
  private int workerID;
  public WorkerThread(ThreadPool pool, int id) {
    this.pool = pool;
    this.workerID = id;
    log.info("Started worker thread "+id);
  }

  public void kill() {
    this._kill_ = true;
    this.interrupt();
  }
  private boolean stayAlive() {
    return !_kill_;
  }

  @Override
  public void run() {
    log.info("Worker thread "+workerID+" started execution.");
    while (stayAlive()) {
      try {

        Task task = pool.getTask(); // grab a task from the pool, blocking
        this.pool.decrementIdleThreads(); // let the pool know that this thread is active
        task.execute(); // execute the task
        this.pool.incrementIdleThread(); // let the pool know that this thread is inactive
        log.info("Task Queue Size: " + pool.getTaskQueueSize());

      } catch (InterruptedException e) {
        log.info("Thread interrupted.");

      } catch (Exception e) {
        log.printStackTrace(e);
      }
    }
    log.info("Worker thread shutting down.");
  }
}
