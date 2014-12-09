package ethanjones.cubes.block.data;

public interface Attribute<T> {

  String getName();

  int getAttribute(T t);

  T getAttribute(int i);

  int getDefault();
}
