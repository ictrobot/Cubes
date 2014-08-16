package ethanjones.modularworld.core.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.data.DataList;
import ethanjones.modularworld.core.data.basic.DataFloat;

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

  public static DataList<DataFloat> dataFromVector2(Vector2 vector2) {
    DataList<DataFloat> dataList = new DataList<DataFloat>();
    dataList.add(new DataFloat(vector2.x));
    dataList.add(new DataFloat(vector2.y));
    return dataList;
  }

  public static Vector2 vector2FromData(DataList<DataFloat> dataList) {
    return new Vector2(dataList.get(0).get(), dataList.get(1).get());
  }

}
