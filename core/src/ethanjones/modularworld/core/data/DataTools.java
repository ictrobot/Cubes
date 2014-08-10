package ethanjones.modularworld.core.data;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.data.basic.*;
import ethanjones.modularworld.core.data.other.DataEnd;

import java.io.*;

public class DataTools {

  public static Data read(DataInput input) throws IOException {
    Data data = getData(input.readByte());
    data.read(input);
    return data;
  }

  public static void write(Data data, DataOutput output) throws IOException {
    output.write(data.getId());
    data.write(output);
  }

  // Byte Array
  public static Data decompress(byte[] bytes) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
    Data data;

    try {
      data = read(dataInputStream);
    } finally {
      dataInputStream.close();
    }

    return data;
  }

  public static byte[] compress(Data data) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

    try {
      write(data, dataOutputStream);
    } finally {
      dataOutputStream.close();
    }

    return byteArrayOutputStream.toByteArray();
  }

  public static void write(Data data, File file) throws IOException {
    DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

    try {
      write(data, output);
    } finally {
      output.close();
    }
  }

  public static Data read(File file) throws IOException {
    DataInputStream input = new DataInputStream(new FileInputStream(file));
    Data data;

    try {
      data = read(input);
    } finally {
      input.close();
    }

    return data;
  }


  public static Data getData(byte type) {
    switch (type) {
      //case -3:
      //  return new DataMap();
      case -2:
        return new DataList();
      case -1:
        return new DataGroup();
      case 1:
        return new DataByte();
      case 2:
        return new DataBoolean();
      case 3:
        return new DataShort();
      case 4:
        return new DataInteger();
      case 5:
        return new DataLong();
      case 6:
        return new DataFloat();
      case 7:
        return new DataDouble();
      case 8:
        return new DataString();
      case 127:
        return new DataEnd();
    }
    throw new ModularWorldException("Invalid Data type: " + type);
  }
}
