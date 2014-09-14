package ethanjones.modularworld.core.util;

import com.badlogic.gdx.math.Vector3;
import ethanjones.data.DataList;
import ethanjones.data.basic.DataFloat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VectorUtil {

  public static DataList<DataFloat> dataFromVector3(Vector3 vector3) {
    DataList<DataFloat> dataList = new DataList<DataFloat>();
    dataList.add(new DataFloat(vector3.x));
    dataList.add(new DataFloat(vector3.y));
    dataList.add(new DataFloat(vector3.z));
    return dataList;
  }

  public static Vector3 vector3FromData(DataList<DataFloat> dataList) {
    return new Vector3(dataList.get(0).get(), dataList.get(1).get(), dataList.get(2).get());
  }

  public static void writeVector3(Vector3 vector3, DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeFloat(vector3.x);
    dataOutputStream.writeFloat(vector3.y);
    dataOutputStream.writeFloat(vector3.z);
  }

  public static Vector3 readVector3(DataInputStream dataInputStream) throws IOException {
    return new Vector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
  }

}
