package ethanjones.cubes.world.generator.smooth;

public class Feature {

  private final long seed;
  private final OpenSimplexNoise noise;
  private final int octaves;
  private final double[] d;

  protected Feature(long seed, int octaves, double change) {
    this.seed = seed;
    this.noise = new OpenSimplexNoise(seed);
    this.octaves = octaves;

    d = new double[octaves];
    for (int i = 0; i < octaves; i++) {
      d[i] = (24d * Math.pow(3, (i + 1))) / change;
    }
  }

  public double eval(int x, int y) {
    double e = 0;
    for (int i = 0; i < octaves; i++) {
      e += noise.eval(x / d[i], y / d[i], 0) / octaves;
    }
    return (e + 1) / 2;
  }
}
