package ethanjones.cubes.core.util;

import com.badlogic.gdx.math.Vector3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VectorUtil {

  public static Float[] array(Vector3 vector3) {
    Float[] floats = new Float[3];
    floats[0] = vector3.x;
    floats[1] = vector3.y;
    floats[2] = vector3.z;
    return floats;
  }

  public static Vector3 array(Float[] floats) {
    return new Vector3(floats[0], floats[1], floats[2]);
  }

  public static void stream(Vector3 vector3, DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeFloat(vector3.x);
    dataOutputStream.writeFloat(vector3.y);
    dataOutputStream.writeFloat(vector3.z);
  }

  public static Vector3 stream(DataInputStream dataInputStream) throws IOException {
    return new Vector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
  }

}
