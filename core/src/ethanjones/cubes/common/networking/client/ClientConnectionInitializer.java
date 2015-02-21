package ethanjones.cubes.common.networking.client;

import com.badlogic.gdx.net.NetJavaSocketImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketTimeoutException;

import ethanjones.cubes.common.core.logging.Log;
import ethanjones.cubes.common.core.system.Branding;

public class ClientConnectionInitializer {

  public static void connect(com.badlogic.gdx.net.Socket gdxSocket) throws Exception {
    Socket javaSocket = extractJavaSocket(gdxSocket);
    DataOutputStream dataOutputStream = new DataOutputStream(javaSocket.getOutputStream());
    dataOutputStream.writeByte(0); //0 is connect
    javaSocket.setSoTimeout(500);
    int serverMajor;
    int serverMinor;
    int serverPoint;
    int serverBuild;
    String serverHash;
    try {
      DataInputStream dataInputStream = new DataInputStream(javaSocket.getInputStream());
      serverMajor = dataInputStream.readInt();
      serverMinor = dataInputStream.readInt();
      serverPoint = dataInputStream.readInt();
      serverBuild = dataInputStream.readInt();
      serverHash = dataInputStream.readUTF();
    } catch (IOException e) {
      if (e instanceof SocketTimeoutException) {
        throw new IOException("Server did not respond in time", e);
      } else {
        throw e;
      }
    }
    if (serverMajor == Branding.VERSION_MAJOR && serverMinor == Branding.VERSION_MINOR && serverPoint == Branding.VERSION_POINT) {
      if (serverBuild == Branding.VERSION_BUILD) {
        if (!serverHash.equals(Branding.VERSION_HASH)) {
          Log.warning("Server reports the same build, but has a different hash");
        } else {
          Log.debug("Server is running exactly the same build");
        }
      } else {
        Log.warning("Server is running build " + serverBuild);
      }
    } else {
      String str = serverMajor + "." + serverMinor + "." + serverPoint;
      throw new IOException("Server is running version " + str + " not " + Branding.VERSION_MAJOR_MINOR_POINT);
    }
    javaSocket.setSoTimeout(0);
  }

  public static Socket extractJavaSocket(com.badlogic.gdx.net.Socket gdxSocket) throws IOException {
    if (gdxSocket instanceof NetJavaSocketImpl) {
      try {
        Field f = NetJavaSocketImpl.class.getDeclaredField("socket");
        f.setAccessible(true);
        Socket javaSocket = (java.net.Socket) f.get(gdxSocket);
        if (javaSocket != null) {
          return javaSocket;
        } else {
          throw new NullPointerException();
        }
      } catch (Exception e) {
        throw new IOException("Failed to get java socket", e);
      }
    } else {
      throw new IOException("libGDX socket is not a " + NetJavaSocketImpl.class.getSimpleName());
    }
  }

  public static PingResult ping(com.badlogic.gdx.net.Socket gdxSocket) throws Exception {
    Socket javaSocket = extractJavaSocket(gdxSocket);
    DataOutputStream dataOutputStream = new DataOutputStream(javaSocket.getOutputStream());
    Long firstTime = System.currentTimeMillis();
    dataOutputStream.writeByte(1); //1 is ping
    javaSocket.setSoTimeout(500);
    try {
      DataInputStream dataInputStream = new DataInputStream(javaSocket.getInputStream());
      PingResult pingResult = new PingResult();
      pingResult.serverMajor = dataInputStream.readInt();
      Long secondTime = System.currentTimeMillis();
      pingResult.serverMinor = dataInputStream.readInt();
      pingResult.serverPoint = dataInputStream.readInt();
      pingResult.serverBuild = dataInputStream.readInt();
      pingResult.serverHash = dataInputStream.readUTF();
      int playerNum = dataInputStream.readInt();
      pingResult.players = new String[playerNum];
      for (int i = 0; i < pingResult.players.length; i++) {
        pingResult.players[i] = dataInputStream.readUTF();
      }
      pingResult.ping = (int) (secondTime - firstTime);
      gdxSocket.dispose();
      return pingResult;
    } catch (IOException e) {
      if (e instanceof SocketTimeoutException) {
        throw new IOException("Server did not respond in time", e);
      } else {
        throw e;
      }
    }
  }
}
