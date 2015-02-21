package ethanjones.cubes.common.entity;

import com.badlogic.gdx.math.Vector3;
import ethanjones.data.DataGroup;
import ethanjones.data.other.DataParser;

import ethanjones.cubes.common.util.Vectors;

public class Entity implements DataParser<DataGroup> {

  //FIXME

  public final Vector3 position;
  public final Vector3 angle;

  public Entity() {
    this(new Vector3(), new Vector3(0, 0.5f, 0));
  }

  public Entity(Vector3 position, Vector3 angle) {
    this.position = position;
    this.angle = angle;
  }

  public void update() {

  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setList("position", Vectors.dataFromVector3(position));
    dataGroup.setList("angle", Vectors.dataFromVector3(angle));
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    this.position.set(Vectors.vector3FromData(data.getList("position")));
    this.angle.set(Vectors.vector3FromData(data.getList("angle")));
  }
}
