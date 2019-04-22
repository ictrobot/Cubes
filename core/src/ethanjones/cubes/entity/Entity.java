package ethanjones.cubes.entity;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.gravity.WorldGravity;
import ethanjones.cubes.world.storage.Area;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class Entity implements DataParser, Disposable {

  public static float GRAVITY = 8f;

  public UUID uuid;
  public float height = 0f;
  public final Vector3 position;
  public final Vector3 angle;
  public final Vector3 motion;
  public final String id;
  protected Vector3 tmpVector;

  public Entity(String id) {
    this(UUID.randomUUID(), id, new Vector3(), new Vector3(1f, 0f, 0f));
  }

  public Entity(String id, Vector3 position, Vector3 angle) {
    this(UUID.randomUUID(), id, position, angle);
  }

  public Entity(UUID uuid, String id, Vector3 position, Vector3 angle) {
    this.uuid = uuid;
    this.id = id;
    this.position = position;
    this.motion = new Vector3();
    this.angle = angle;
    this.tmpVector = new Vector3();
  }

  /**
   * @return true to be removed
   */
  public boolean update() {
    if (Side.isServer()) updatePosition(Cubes.tickMS / 1000f);
    motion.scl(0.9f, 1f, 0.9f);
    if (motion.len2() < 0.01f) motion.set(0f, 0f, 0f);

    return false;
  }

  public void updatePosition(float time) {
    if (!inLoadedArea()) return;
    World world = Side.getCubes().world;
    float r = 0f;

    if (!motion.isZero() || !WorldGravity.onBlock(world, position, height, r)) {
      position.add(motion.x * time, motion.y * time, motion.z * time);
      motion.y -= GRAVITY * time;

      if (WorldGravity.onBlock(world, position, height, r) && motion.y < 0) {
        position.y = WorldGravity.getBlockY(position, height) + 1 + height;
        motion.y = 0f;
      }

      if (Side.isServer()) world.syncEntity(uuid);
    }
  }

  public boolean inLoadedArea() {
    Area area = Side.getCubes().world.getArea(CoordinateConverter.area(position.x), CoordinateConverter.area(position.z));
    return area != null && !area.isUnloaded();
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("id", id);
    dataGroup.put("uuid", uuid);
    dataGroup.put("pos", VectorUtil.array(position));
    dataGroup.put("motion", VectorUtil.array(motion));
    dataGroup.put("ang", VectorUtil.array(angle));
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    this.position.set(VectorUtil.array(data.getArray("pos", Float.class)));
    this.motion.set(VectorUtil.array(data.getArray("motion", Float.class)));
    this.angle.set(VectorUtil.array(data.getArray("ang", Float.class)));
    this.uuid = (UUID) data.get("uuid");
    if (!this.id.equals(data.getString("id")))
      throw new IllegalArgumentException(this.id + "!=" + data.getString("id"));
  }

  public static Entity readType(DataGroup data) {
    try {
      Class<? extends Entity> c = EntityManager.toClass(data.getString("id"));
      Entity entity = ClassReflection.newInstance(c);
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

  @Override
  public String toString() {
    return id + " " + uuid.toString();
  }

  public boolean inFrustum(Frustum frustum) {
    return frustum.sphereInFrustum(position, 2f);
  }
}
