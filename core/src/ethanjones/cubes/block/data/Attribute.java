package ethanjones.cubes.block.data;

import java.util.List;

public interface Attribute<T> {

  String getName();

  List<T> getValues();
}
