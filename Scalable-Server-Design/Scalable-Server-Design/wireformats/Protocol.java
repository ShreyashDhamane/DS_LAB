package cs455.scaling.wireformats;

public enum Protocol {
  MESSAGE_SIZE(8192), //bytes
  SERVER_RESPONSE_MESSAGE_SIZE(40); // bytes

  private final int id;

  Protocol(int id) {
    this.id = id;
  }

  public int getValue() {
    return id;
  }
}
