package ethanjones.cubes.networking.client;

import com.badlogic.gdx.net.NetJavaSocketImpl;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketTimeoutException;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Branding;

public class ClientConnectionInitializer {

  public static void check(com.badlogic.gdx.net.Socket gdxSocket) throws Exception {
    Socket javaSocket = null;
    if (gdxSocket instanceof NetJavaSocketImpl) {
      try {
        Field f = NetJavaSocketImpl.class.getDeclaredField("socket");
        f.setAccessible(true);
        javaSocket = (java.net.Socket) f.get(gdxSocket);
        if (javaSocket == null) throw new NullPointerException();
      } catch (Exception e) {
        throw new IOException("Failed to get java socket", e);
      }
    } else {
      throw new IOException("libGDX socket is not a " + NetJavaSocketImpl.class.getSimpleName());
    }
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
}
