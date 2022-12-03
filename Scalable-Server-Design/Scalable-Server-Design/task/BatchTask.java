package cs455.scaling.task;

import cs455.scaling.threadpool.Task;
import cs455.scaling.util.LOGGER;

import java.util.concurrent.LinkedBlockingQueue;

public class BatchTask implements Task{

  // for logging
  private static final LOGGER log = new LOGGER(BatchTask.class.getSimpleName(), false);

  private final LinkedBlockingQueue<Task> tasks;
  private final int maxSize;

  public BatchTask(int maxSize) {
    this.maxSize = maxSize;
    this.tasks = new LinkedBlockingQueue<>();
  }

  public int size() {
    return this.tasks.size();
  }

  public int maxSize() {
    return this.maxSize;
  }

  public void addTask(Task t) throws InterruptedException {
    this.tasks.put(t);
  }

  @Override
  public void execute() {
    log.info("Total tasks in this batch: "+ tasks.size());
    for (Task t : tasks) {
      t.execute();
    }
  }
}
