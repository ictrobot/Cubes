package ethanjones.data;

public interface DataParser {

  DataGroup write();

  void read(DataGroup dataGroup);
}