package ethanjones.data;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataFormatter {

    /*
        [ - Array
        ( - ArrayList
        { - HashMap
        < - DataGroup
     */

  private static final String indentStr = "  ";

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Argument: Data file");
      return;
    }

    Object obj = null;
    File in = new File(args[0]);

    try {
      obj = Data.input(in);
    } catch (Exception a) {
      try {
        obj = Data.inputCompressed(in);
      } catch (Exception b) {
        System.out.println("Failed to read file");
        a.printStackTrace();
        b.printStackTrace();
        return;
      }
    }
    if (obj == null) return;

    String str = str(obj);
    System.out.println(str(obj));

    String filename = in.getName();
    int pos = filename.lastIndexOf(".");
    if (pos > 0) {
      filename = filename.substring(0, pos);
    }
    File out = new File(in.getParentFile(), filename + ".txt");

    PrintWriter printWriter = null;
    try {
      printWriter = new PrintWriter(out);
      printWriter.println(str);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        printWriter.close();
      } catch (Exception ignored) {
      }
    }
  }

  public static String str(Object obj) {
    return str(obj, "", new StringBuilder()).toString();
  }

  private static StringBuilder str(Object obj, String indent, StringBuilder stringBuilder) {
    if (obj.getClass().isArray()) {
      stringBuilder.append("[");
      String newIndent = indent + indentStr;
      for (Object object : ((Object[]) obj)) {
        stringBuilder.append("\n").append(newIndent);
        str(object, newIndent, stringBuilder);
      }
      stringBuilder.append("\n").append(indent).append("]");
    } else if (obj instanceof ArrayList) {
      stringBuilder.append("(");
      String newIndent = indent + indentStr;
      for (Object object : ((ArrayList) obj)) {
        stringBuilder.append("\n").append(newIndent);
        str(object, newIndent, stringBuilder);
      }
      stringBuilder.append("\n").append(indent).append(")");
    } else if (obj instanceof HashMap) {
      stringBuilder.append("{");
      String newIndent = indent + indentStr;
      for (Object o : ((HashMap) obj).entrySet()) {
        Map.Entry entry = (Map.Entry) o;
        stringBuilder.append("\n").append(newIndent);
        str(entry.getKey(), newIndent, stringBuilder);
        stringBuilder.append(":");
        str(entry.getValue(), newIndent, stringBuilder);
      }
      stringBuilder.append("\n").append(indent).append("}");
    } else if (obj instanceof DataGroup) {
      stringBuilder.append("<");
      String newIndent = indent + indentStr;
      for (Map.Entry<String, Object> entry : ((DataGroup) obj).entrySet()) {
        stringBuilder.append("\n").append(newIndent).append(entry.getKey()).append(":");
        str(entry.getValue(), newIndent, stringBuilder);
      }
      stringBuilder.append("\n").append(indent).append(">");
    } else {
      stringBuilder.append(obj.toString());
    }
    return stringBuilder;
  }
}
