package ethanjones.modularworld.networking;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;

public class NetworkUtil {

  public final static ServerSocketHints serverSocketHints;
  public final static SocketHints socketHints;
  public final static Net.Protocol protocol = Net.Protocol.TCP;

  static {
    serverSocketHints = new ServerSocketHints();
    serverSocketHints.acceptTimeout = 0;
    socketHints = new SocketHints();
    socketHints.keepAlive = true;
    socketHints.connectTimeout = 30000;
  }

}
