package ethanjones.cubes.entity;

import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.UUID;

public class Entity implements DataParser, Disposable {

  public static HashMap<String, Class<? extends Entity>> entityTypes = new HashMap<String, Class<? extends Entity>>();

  static {
    Entity.entityTypes.put("core:player", Player.class);
    Entity.entityTypes.put("core:item", ItemEntity.class);
  }

  public UUID uuid;
  public final Vector3 position;
  public final Vector3 angle;
  public final String type;

  public Entity(String type) {
    this(UUID.randomUUID(), type, new Vector3(), new Vector3(1f, 0f, 0f));
  }

  public Entity(String type, Vector3 position, Vector3 angle) {
    this(UUID.randomUUID(), type, position, angle);
  }

  public Entity(UUID uuid, String type, Vector3 position, Vector3 angle) {
    this.uuid = uuid;
    this.type = type;
    this.position = position;
    this.angle = angle;
  }

  public void update() {

  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("type", type);
    dataGroup.put("uuid", uuid);
    dataGroup.put("pos", VectorUtil.array(position));
    dataGroup.put("ang", VectorUtil.array(angle));
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    this.position.set(VectorUtil.array(data.getArray("pos", Float.class)));
    this.angle.set(VectorUtil.array(data.getArray("ang", Float.class)));
    this.uuid = (UUID) data.get("uuid");
    if (!this.type.equals(data.getString("type")))
      throw new IllegalArgumentException(this.type + "!=" + data.getString("type"));
  }

  public static Entity readType(DataGroup data) {
    try {
      Class<? extends Entity> c = entityTypes.get(data.getString("type"));
      Entity entity = c.newInstance();
      entity.read(data);
      return entity;
    } catch (Exception e) {
      Debug.crash(e);
      return null;
    }
  }

  @Override
  public void dispose() {

  }
}
