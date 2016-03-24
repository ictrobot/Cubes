package ethanjones.cubes.entity;

import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import com.badlogic.gdx.math.Vector3;

public class Entity implements DataParser {

  //FIXME

  public final Vector3 position;
  public final Vector3 angle;

  public Entity() {
    this(new Vector3(), new Vector3(1f, 0, 0));
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
    dataGroup.put("pos", VectorUtil.array(position));
    dataGroup.put("ang", VectorUtil.array(angle));
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    this.position.set(VectorUtil.array(data.getArray("position", Float.class)));
    this.angle.set(VectorUtil.array(data.getArray("angle", Float.class)));
  }
}
