package ethanjones.cubes.world.thread;

public interface GenerationTask {

  public int totalGenerate();

  public int totalFeatures();

  public int doneGenerate();

  public int doneFeatures();

}
