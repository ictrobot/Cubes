package ethanjones.modularworld.networking.common;

public enum NetworkingState {
  Starting, Running, Stopping, Stopped;

  public boolean shouldThrowException() {
    return this != Stopping && this != Stopped;
  }
}
