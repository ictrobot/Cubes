package ethanjones.cubes.side;

import ethanjones.cubes.core.messaging.Message;

public class ControlMessage extends Message {

  public static enum Status {
    Stop, Stopped
  }

  public Status status;
  
}
