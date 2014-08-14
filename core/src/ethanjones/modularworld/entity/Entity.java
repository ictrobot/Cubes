package ethanjones.modularworld.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.DataList;
import ethanjones.modularworld.core.data.basic.DataFloat;
import ethanjones.modularworld.core.data.other.DataParser;

public class Entity implements DataParser<DataGroup> {

  //FIXME

  public Vector3 position;
  public Vector2 angle;
  public boolean gravity = true;

  public Entity() {
    this.position = new Vector3(0, 6, 0);
    this.angle = new Vector2(180, 0);
  }

  public void update() {

  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setList("position", dataFromVector3(position));
    dataGroup.setList("angle", dataFromVector2(angle));
    dataGroup.setBoolean("gravity", gravity);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    this.position = vector3FromData(data.getList("position"));
    this.angle = vector2FromData(data.getList("angle"));
    this.gravity = data.getBoolean("gravity");
  }

  protected static DataList<DataFloat> dataFromVector3(Vector3 vector3) {
    DataList<DataFloat> dataList = new DataList<DataFloat>();
    dataList.add(new DataFloat(vector3.x));
    dataList.add(new DataFloat(vector3.y));
    dataList.add(new DataFloat(vector3.z));
    return dataList;
  }

  protected static Vector3 vector3FromData(DataList<DataFloat> dataList) {
    return new Vector3(dataList.get(0).get(), dataList.get(1).get(), dataList.get(2).get());
  }

  protected static DataList<DataFloat> dataFromVector2(Vector2 vector2) {
    DataList<DataFloat> dataList = new DataList<DataFloat>();
    dataList.add(new DataFloat(vector2.x));
    dataList.add(new DataFloat(vector2.y));
    return dataList;
  }

  protected static Vector2 vector2FromData(DataList<DataFloat> dataList) {
    return new Vector2(dataList.get(0).get(), dataList.get(1).get());
  }
}
