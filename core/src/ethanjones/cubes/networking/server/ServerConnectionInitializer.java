package ethanjones.cubes.networking.server;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.net.NetJavaSocketImpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;

import static ethanjones.cubes.networking.client.ClientConnectionInitializer.extractJavaSocket;

public class ServerConnectionInitializer {

  public static final int TIMEOUT = 5000;

  private static class Checker implements Callable<Object> {

    private final Socket javaSocket;
    private final NetJavaSocketImpl gdxSocket;

    private Checker(Socket javaSocket, NetJavaSocketImpl gdxSocket) {
      this.javaSocket = javaSocket;
      this.gdxSocket = gdxSocket;
    }

    @Override
    public Object call() throws Exception {
      try {
        initialConnect(javaSocket, gdxSocket);
      } catch (Exception e) {
        if (e instanceof SocketTimeoutException) {
          throw new IOException("Client did not respond in time", e);
        } else {
          throw e;
        }
      }
      return null;
    }
  }

  public static void check(com.badlogic.gdx.net.Socket gdxSocket) throws Exception {
    Socket javaSocket = extractJavaSocket(gdxSocket);
    NetJavaSocketImpl netJavaSocketImpl = (NetJavaSocketImpl) gdxSocket;
    Executor.execute(new Checker(javaSocket, netJavaSocketImpl));
  }

  private static void initialConnect(Socket javaSocket, NetJavaSocketImpl gdxSocket) throws Exception {
    javaSocket.setSoTimeout(TIMEOUT);
    DataInputStream dataInputStream = new DataInputStream(javaSocket.getInputStream());
    byte b = dataInputStream.readByte();
    DataOutputStream dataOutputStream = new DataOutputStream(javaSocket.getOutputStream());
    dataOutputStream.writeInt(Branding.VERSION_MAJOR);
    dataOutputStream.writeInt(Branding.VERSION_MINOR);
    dataOutputStream.writeInt(Branding.VERSION_POINT);
    dataOutputStream.writeInt(Branding.VERSION_BUILD);
    dataOutputStream.writeUTF(Branding.VERSION_HASH);
    switch (b) {
      case 0:
        connect(javaSocket, gdxSocket, dataOutputStream, dataInputStream);
        return;
      case 1:
        ping(javaSocket, gdxSocket, dataOutputStream, dataInputStream);
        return;
      default:
        throw new IOException("Unrecognised connection code " + b);
    }
  }

  private static void connect(Socket javaSocket, NetJavaSocketImpl gdxSocket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws Exception {
    javaSocket.setSoTimeout(0);
    ((ServerNetworking) Sided.getNetworking()).accepted(gdxSocket);
  }

  private static void ping(Socket javaSocket, NetJavaSocketImpl gdxSocket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws Exception {
    Log.debug(gdxSocket.getRemoteAddress() + " pinged the server");
    List<ClientIdentifier> clients = Cubes.getServer().getAllClients();
    dataOutputStream.writeInt(clients.size());
    for (ClientIdentifier client : clients) {
      dataOutputStream.writeUTF(client.getPlayer().username);
    }
    dataOutputStream.flush();
    gdxSocket.dispose();
  }
  
}
