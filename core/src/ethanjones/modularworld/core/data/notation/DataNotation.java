package ethanjones.modularworld.core.data.notation;

import ethanjones.modularworld.core.data.Data;

public class DataNotation {

  public static String toString(Data data) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(data.getClass().getSimpleName());
    stringBuilder.append(" {");
    stringBuilder.append(data.writeNotation());
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  public static Data fromString(String string) throws BadNotationException {
    try {
      string.trim();
      int index = string.indexOf(" ");
      Class<? extends Data> c = NotationTools.getClass(string.substring(0, index).trim());
      Data data = c.newInstance();
      data.readNotation(string.substring(index + 1, string.length()).trim());
      return data;
    } catch (Exception e) {
      throw new BadNotationException(string, e);
    }
  }
}
