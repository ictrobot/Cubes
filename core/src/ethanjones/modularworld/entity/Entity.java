package ethanjones.modularworld.entity;

import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.other.DataParser;
import ethanjones.modularworld.core.util.VectorUtil;

public class Entity implements DataParser<DataGroup> {

  //FIXME

  public Vector3 position;
  public Vector3 angle;
  public boolean gravity = true;

  public Entity() {
    this.position = new Vector3(0, 6, 0);
    this.angle = new Vector3(0, 0.5f, 0);
  }

  public void update() {

  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setList("position", VectorUtil.dataFromVector3(position));
    dataGroup.setList("angle", VectorUtil.dataFromVector3(angle));
    dataGroup.setBoolean("gravity", gravity);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    this.position = VectorUtil.vector3FromData(data.getList("position"));
    this.angle = VectorUtil.vector3FromData(data.getList("angle"));
    this.gravity = data.getBoolean("gravity");
  }
}
