package ethanjones.cubes.world.generator;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.world.generator.smooth.SmoothWorld;
import ethanjones.cubes.world.save.SaveOptions;

import java.util.LinkedHashMap;
import java.util.Set;

public class GeneratorManager {
  private static final LinkedHashMap<String, TerrainGeneratorFactory> generators = new LinkedHashMap<String, TerrainGeneratorFactory>();

  static {
    generators.put("core:smooth", new TerrainGeneratorFactory() {
      @Override
      public TerrainGenerator getTerrainGenerator(SaveOptions saveOptions) {
        return new SmoothWorld(saveOptions.worldSeed);
      }
    });
    generators.put("core:basic", new TerrainGeneratorFactory() {
      @Override
      public TerrainGenerator getTerrainGenerator(SaveOptions saveOptions) {
        return new BasicTerrainGenerator(saveOptions.worldSeedString);
      }
    });
    generators.put("core:test", new TerrainGeneratorFactory() {
      @Override
      public TerrainGenerator getTerrainGenerator(SaveOptions saveOptions) {
        return new TestTerrainGenerator();
      }
    });
    generators.put("core:void", new TerrainGeneratorFactory() {
      @Override
      public TerrainGenerator getTerrainGenerator(SaveOptions saveOptions) {
        return new VoidTerrainGenerator();
      }
    });
  }

  public static void register(String str, TerrainGeneratorFactory generator) {
    if (str == null || str.isEmpty()) throw new NullPointerException();
    if (generators.containsKey(str)) throw new IllegalStateException(str + " has already been registered");
    generators.put(str, generator);
  }

  public static TerrainGenerator getTerrainGenerator(SaveOptions saveOptions) {
    TerrainGeneratorFactory factory = generators.get(saveOptions.worldType);
    if (factory == null) throw new IllegalStateException("No such generator " + saveOptions.worldType);
    return factory.getTerrainGenerator(saveOptions);
  }
  
  public static boolean terrainGeneratorExists(String id) {
    return generators.containsKey(id);
  }

  public static String getName(String id) {
    return Localization.get("terrain." + id.replaceFirst(":", "."));
  }

  public static String[] ids() {
    Set<String> strings = generators.keySet();
    return strings.toArray(new String[strings.size()]);
  }

  public interface TerrainGeneratorFactory {
    TerrainGenerator getTerrainGenerator(SaveOptions saveOptions);
  }
}
