package ethanjones.cubes.world.reference.multi;

import ethanjones.cubes.world.reference.AreaReference;

import java.util.HashSet;
import java.util.Set;

public class AreaReferenceSet extends HashSet<AreaReference> implements MultiAreaReference {

  @Override
  public Set<AreaReference> getAreaReferences() {
    return this;
  }
}
