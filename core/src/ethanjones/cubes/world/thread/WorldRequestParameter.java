package ethanjones.cubes.world.thread;

import ethanjones.cubes.world.reference.AreaReference;

import java.util.Comparator;

public class WorldRequestParameter {
  public static final WorldRequestParameter DEFAULT = new WorldRequestParameter(null, null);

  public final AreaReference prioritise;
  public final Runnable afterCompletion;

  public WorldRequestParameter(AreaReference prioritise, Runnable afterCompletion) {
    this.prioritise = prioritise;
    this.afterCompletion = afterCompletion;
  }

  public Comparator<AreaReference> getComparator() {
    if (prioritise == null) throw new NullPointerException();

    return new Comparator<AreaReference>() {
      @Override
      public int compare(AreaReference o1, AreaReference o2) {
        if (o1.areaX == o2.areaX && o1.areaZ == o2.areaZ) return 0;
        final float dst = prioritise.distance2(o1) - prioritise.distance2(o2);
        return dst < 0 ? -1 : 1;
      }
    };
  }
}
