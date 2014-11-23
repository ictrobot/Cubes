package ethanjones.cubes.networking.server;

import com.badlogic.gdx.net.NetJavaSocketImpl;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.concurrent.Callable;

import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.side.Sided;

public class ServerConnectionInitializer {

  public static void check(com.badlogic.gdx.net.Socket gdxSocket) throws Exception {
    Socket javaSocket = null;
    NetJavaSocketImpl netJavaSocketImpl = null;
    if (gdxSocket instanceof NetJavaSocketImpl) {
      netJavaSocketImpl = (NetJavaSocketImpl) gdxSocket;
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
    Executor.execute(new Checker(javaSocket, netJavaSocketImpl));
  }

  private static void initialConnect(Socket javaSocket, NetJavaSocketImpl gdxSocket) throws Exception {
    DataOutputStream dataOutputStream = new DataOutputStream(javaSocket.getOutputStream());
    dataOutputStream.writeInt(Branding.VERSION_MAJOR);
    dataOutputStream.writeInt(Branding.VERSION_MINOR);
    dataOutputStream.writeInt(Branding.VERSION_POINT);
    dataOutputStream.writeInt(Branding.VERSION_BUILD);
    dataOutputStream.writeUTF(Branding.VERSION_HASH);
    ((ServerNetworking) Sided.getNetworking()).accepted(gdxSocket);
  }

  private static class Checker implements Callable<Object> {

    private final Socket javaSocket;
    private final NetJavaSocketImpl gdxSocket;

    private Checker(Socket javaSocket, NetJavaSocketImpl gdxSocket) {
      this.javaSocket = javaSocket;
      this.gdxSocket = gdxSocket;
    }

    @Override
    public Object call() throws Exception {
      initialConnect(javaSocket, gdxSocket);
      return null;
    }
  }
  
}
