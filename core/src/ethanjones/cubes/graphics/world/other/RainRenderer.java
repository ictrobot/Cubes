package ethanjones.cubes.graphics.world.other;

import ethanjones.cubes.graphics.CubesVertexAttributes;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.generator.RainStatus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.Iterator;
import java.util.LinkedList;

import static ethanjones.cubes.world.CoordinateConverter.block;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class RainRenderer {

  private static class Particle {

    Vector3 position;
    float velocityDown;

    private Particle(Vector3 position) {
      this.position = position;
      this.velocityDown = RAIN_VELOCITY_Y - 1f + (2f * rand.nextFloat());
    }
  }

  private static class RainShader extends DefaultShader {

    private static final String VERTEX_SHADER = Assets.getAsset("core:shaders/rain.vertex.glsl").readString();
    private static final String FRAGMENT_SHADER = Assets.getAsset("core:shaders/rain.fragment.glsl").readString();

    public RainShader(Renderable renderable) {
      super(renderable, new Config(VERTEX_SHADER, FRAGMENT_SHADER));
    }

    @Override
    public int compareTo(Shader other) {
      return other == null ? -1 : 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
      return true;
    }
  }

  private static class RainMesh {

    private static final VertexAttributes VERTEX_ATTRIBUTES = new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
    private static final int MAX_INDICES = 32760;
    private static final int MAX_PARTICLES = MAX_INDICES / 36;
    private static final int MAX_VERTICES = MAX_PARTICLES * 8;
    private static final int MAX_VERTEX_OFFSET = MAX_VERTICES * 3;
    private static final int SAFE_VERTEX_OFFSET = MAX_VERTEX_OFFSET - (8 * 3);
    private static short[] indices;
    private static float[] vertices;
    private static RainShader SHADER = null;

    static {
      vertices = new float[MAX_VERTEX_OFFSET];
      indices = new short[MAX_INDICES];
      short[] indicesOffset = new short[]{
          // front
          0, 1, 2,
          2, 3, 0,
          // top
          1, 5, 6,
          6, 2, 1,
          // back
          7, 6, 5,
          5, 4, 7,
          // bottom
          4, 0, 3,
          3, 7, 4,
          // left
          4, 5, 1,
          1, 0, 4,
          // right
          3, 2, 6,
          6, 7, 3
      };

      for (int i = 0, j = 0; i < indices.length; i += indicesOffset.length, j += 8) {
        for (int idx = 0; idx < indicesOffset.length; idx++) {
          indices[i + idx] = (short) (j + indicesOffset[idx]);
        }
      }
    }

    private Mesh mesh;
    private MeshPart meshPart;

    private RainMesh() {
      mesh = new Mesh(true, MAX_VERTICES, MAX_INDICES, VERTEX_ATTRIBUTES);
      meshPart = new MeshPart();
      meshPart.mesh = mesh;
      meshPart.primitiveType = GL20.GL_TRIANGLES;
      meshPart.offset = 0;
      mesh.setIndices(indices);
    }

    private void saveVertices(int vertexCount) {
      mesh.setVertices(vertices, 0, vertexCount);
      int v = vertexCount / CubesVertexAttributes.components(mesh.getVertexAttributes());
      meshPart.size = v / 8 * 36;

      // define approximate center and halfExtents to prevent having to recalculate each time
      meshPart.center.set(Cubes.getClient().renderer.worldRenderer.camera.position);
      meshPart.halfExtents.set(AREA_WIDTH / 2f, AREA_HEIGHT / 2f, AREA_WIDTH / 2f);
      meshPart.radius = meshPart.halfExtents.len();
    }

    private Renderable renderable() {
      Renderable renderable = new Renderable(); // FIXME use pool
      renderable.material = Assets.blockItemSheet.getMaterial(); // not actually used
      renderable.meshPart.set(meshPart);
      if (SHADER == null) {
        SHADER = new RainShader(renderable);
        SHADER.init();
      }
      renderable.shader = SHADER;
      return renderable;
    }
  }


  private static final float AREA_WIDTH = 7.5f;
  private static final float AREA_HEIGHT = 6f;

  private static final float RAIN_VELOCITY_Y = 5;
  private static final float RAIN_SIZE = 1f / 32f;

  private static final float VOLUME = AREA_WIDTH * AREA_WIDTH * AREA_HEIGHT * 8;
  private static final float VOLUME_TICK = AREA_WIDTH * AREA_WIDTH * 4 * (RAIN_VELOCITY_Y * Cubes.tickMS / 1000);

  private static final float BASE_RAIN_DROPS_PER_VOLUME = 1;
  private static final float EXTRA_RAIN_DROPS_PER_VOLUME = 2;

  private static final LinkedList<Particle> particles = new LinkedList<Particle>();
  private static final RainMesh rainMesh = new RainMesh();
  private static final Vector3 posPrevious = new Vector3(-100, -100, -100);
  private static final RandomXS128 rand = new RandomXS128();

  public static void tick() {
    Vector3 playerPos = Cubes.getClient().player.position;
    World world = Cubes.getClient().world;
    RainStatus rainStatus = ((WorldClient) world).rainStatus;
    float dropsPerVolume = BASE_RAIN_DROPS_PER_VOLUME + (EXTRA_RAIN_DROPS_PER_VOLUME * rainStatus.rainRate);

    BoundingBox oldBB = new BoundingBox(
        posPrevious.cpy().sub(AREA_WIDTH, AREA_HEIGHT, AREA_WIDTH),
        posPrevious.cpy().add(AREA_WIDTH, AREA_HEIGHT, AREA_WIDTH));

    BoundingBox newBB = new BoundingBox(
        playerPos.cpy().sub(AREA_WIDTH, AREA_HEIGHT, AREA_WIDTH),
        playerPos.cpy().add(AREA_WIDTH, AREA_HEIGHT, AREA_WIDTH));

    Iterator<Particle> iterator = particles.iterator();
    while (iterator.hasNext()) {
      Vector3 position = iterator.next().position;
      if (!newBB.contains(position) || world.getBlock(block(position.x), block(position.y), block(position.z)) != null) {
        iterator.remove();
      }
    }

    if (rainStatus.raining) {
      float newVolume = VOLUME - (
          max(min(oldBB.max.x, newBB.max.x) - max(oldBB.min.x, newBB.min.x), 0) *
              max(min(oldBB.max.y, newBB.max.y) - max(oldBB.min.y, newBB.min.y), 0) *
              max(min(oldBB.max.z, newBB.max.z) - max(oldBB.min.z, newBB.min.z), 0));

      int newDrops = (int) (newVolume * dropsPerVolume / 2f);
      Vector3 checkPos = new Vector3();
      Vector3 nBBDim = newBB.getDimensions(new Vector3());
      while (newDrops > 0) {
        checkPos.set(newBB.min).add(rand.nextFloat() * nBBDim.x, rand.nextFloat() * nBBDim.y, rand.nextFloat() * nBBDim.z);
        if (oldBB.contains(checkPos)) continue;

        if (checkPos.y > world.heightmap(block(checkPos.x), block(checkPos.z))) {
          particles.add(new Particle(checkPos.cpy()));
        }
        newDrops--;
      }

      for (int i = 0; i < rand.nextInt((int) (VOLUME_TICK * dropsPerVolume * 2)); i++) {
        float offsetX = (rand.nextFloat() * AREA_WIDTH * 2) - AREA_WIDTH;
        float offsetZ = (rand.nextFloat() * AREA_WIDTH * 2) - AREA_WIDTH;
        Vector3 position = new Vector3(posPrevious.x + offsetX, posPrevious.y + AREA_HEIGHT, posPrevious.z + offsetZ);
        if (world.getBlock(block(position.x), block(position.y), block(position.z)) == null && position.y > world.heightmap(block(position.x), block(position.z))) {
          particles.add(new Particle(position));
        }
      }
    }

    posPrevious.set(playerPos);
  }

  public static void draw(ModelBatch modelBatch) {
    float rawDeltaTime = Gdx.graphics.getRawDeltaTime();
    int vertexOffset = 0;

    Frustum frustum = Cubes.getClient().renderer.worldRenderer.camera.frustum;
    for (Particle particle : particles) {
      particle.position.y -= particle.velocityDown * rawDeltaTime;
    }
    for (Particle particle : particles) {
      if (vertexOffset > RainMesh.SAFE_VERTEX_OFFSET) {
        break;
      }
      if (frustum.pointInFrustum(particle.position)) {
        RainMesh.vertices[vertexOffset++] = particle.position.x;
        RainMesh.vertices[vertexOffset++] = particle.position.y;
        RainMesh.vertices[vertexOffset++] = particle.position.z + RAIN_SIZE;

        RainMesh.vertices[vertexOffset++] = particle.position.x + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.y;
        RainMesh.vertices[vertexOffset++] = particle.position.z + RAIN_SIZE;

        RainMesh.vertices[vertexOffset++] = particle.position.x + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.y + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.z + RAIN_SIZE;

        RainMesh.vertices[vertexOffset++] = particle.position.x;
        RainMesh.vertices[vertexOffset++] = particle.position.y + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.z + RAIN_SIZE;

        RainMesh.vertices[vertexOffset++] = particle.position.x;
        RainMesh.vertices[vertexOffset++] = particle.position.y;
        RainMesh.vertices[vertexOffset++] = particle.position.z;

        RainMesh.vertices[vertexOffset++] = particle.position.x + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.y;
        RainMesh.vertices[vertexOffset++] = particle.position.z;

        RainMesh.vertices[vertexOffset++] = particle.position.x + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.y + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.z;

        RainMesh.vertices[vertexOffset++] = particle.position.x;
        RainMesh.vertices[vertexOffset++] = particle.position.y + RAIN_SIZE;
        RainMesh.vertices[vertexOffset++] = particle.position.z;
      }
    }
    if (vertexOffset > 0) {
      rainMesh.saveVertices(vertexOffset);
      modelBatch.render(rainMesh.renderable());
    }
  }

}
