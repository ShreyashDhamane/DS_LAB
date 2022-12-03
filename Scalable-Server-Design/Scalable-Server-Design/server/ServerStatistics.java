package cs455.scaling.server;



public class ServerStatistics {


  private int messages = 0;

  public ServerStatistics() {}

  public void incrementMessageCount() {
    this.messages++;
  }


  public int getTotalMessages() {
    return this.messages;
  }

  public void resetMessagesCount() {
    this.messages = 0;
  }

}
