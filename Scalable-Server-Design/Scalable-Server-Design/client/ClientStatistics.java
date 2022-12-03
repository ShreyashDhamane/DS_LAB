package cs455.scaling.client;


public class ClientStatistics {

  private long totalSentCount = 0;

  private long totalReceiveCount = 0;

  public void incrementReceiveCount() {
    this.totalReceiveCount++;
  }

  public void incrementSentCount() {
    this.totalSentCount++;
  }


  public long getTotalSentCount() {
    return this.totalSentCount;
  }

  public long getTotalReceiveCount() {
    return this.totalReceiveCount;
  }
  public void resetCounters() {
    this.totalSentCount = 0;
    this.totalReceiveCount = 0;
  }

}
