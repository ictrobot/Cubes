package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.DataBasic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataString extends DataBasic<DataString, String> {

  public DataString() {
    this("");
  }

  public DataString(String s) {
    super(8, s);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeUTF(obj);
  }

  @Override
  public void read(DataInput input) throws IOException {
    obj = input.readUTF();
  }

  @Override
  public String writeNotation() {
    return "\"" + obj + "\"";
  }

  @Override
  public void readNotation(String str) {
    obj = str.substring(1, str.length() - 1);
  }
}
