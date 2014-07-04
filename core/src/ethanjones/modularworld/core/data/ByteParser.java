package ethanjones.modularworld.core.data;

public interface ByteParser<b extends ByteBase> {

  public b write();

  public void read(b bytebase);

  public static interface ByteDataParser extends ByteParser<ByteData> {

  }
}
