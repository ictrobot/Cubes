package ethanjones.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public abstract class DataInterpreter<T> {

  protected static Map<Class, DataInterpreter> classInterpreterMap = new HashMap<Class, DataInterpreter>();
  protected static Map<Byte, DataInterpreter> byteInterpreterMap = new HashMap<Byte, DataInterpreter>();
  protected static DataInterpreter arrayInterpreter;

  protected static <T> void register(DataInterpreter<T> interpreter, Class<T> tClass) {
    classInterpreterMap.put(tClass, interpreter);
    byteInterpreterMap.put(interpreter.id(), interpreter);
  }

  protected static void registerArray(DataInterpreter interpreter) {
    arrayInterpreter = interpreter;
    byteInterpreterMap.put(interpreter.id(), interpreter);
  }

  public static <T> DataInterpreter<T> get(Class<T> tClass) {
    if (tClass.isArray()) return arrayInterpreter;
    DataInterpreter<T> interpreter = null;
    Class c = tClass;
    while (c != Object.class) {
      interpreter = classInterpreterMap.get(c);
      if (interpreter != null) break;
      try {
        c = c.getSuperclass();
      } catch (Exception e) {
        throw new NoInterpreterException(tClass);
      }
    }
    if (interpreter == null) {
      throw new NoInterpreterException(tClass);
    }
    return interpreter;
  }

  public static DataInterpreter get(Byte b) {
    DataInterpreter interpreter = byteInterpreterMap.get(b);
    if (interpreter == null) {
      throw new NoInterpreterException(b);
    }
    return interpreter;
  }

  static {
    register(new HashMapInterpreter(), HashMap.class);
    register(new DataGroupInterpreter(), DataGroup.class);
    register(new ArrayListInterpreter(), ArrayList.class);
    registerArray(new ArrayInterpreter());
    register(new EndInterpreter(), End.class);
    register(new BooleanInterpreter(), Boolean.class);
    register(new ByteInterpreter(), Byte.class);
    register(new ShortInterpreter(), Short.class);
    register(new IntegerInterpreter(), Integer.class);
    register(new FloatInterpreter(), Float.class);
    register(new LongInterpreter(), Long.class);
    register(new DoubleInterpreter(), Double.class);
    register(new StringInterpreter(), String.class);
    register(new UUIDInterpreter(), UUID.class);
  }

  private static class HashMapInterpreter extends DataInterpreter<HashMap> {

    @Override
    protected void output(HashMap obj, DataOutput output) throws IOException {
      for (Entry<Object, Object> entry : ((HashMap<Object, Object>) obj).entrySet()) {
        Data.output(entry.getValue(), output);
        Data.output(entry.getKey(), output);
      }
      output.write(0);
    }

    @Override
    protected HashMap input(DataInput input) throws IOException {
      HashMap hashMap = new HashMap();
      Object obj;
      while (!((obj = Data.input(input)) instanceof End)) {
        hashMap.put(Data.input(input), obj);
      }
      return hashMap;
    }

    @Override
    protected Class<HashMap> tClass() {
      return HashMap.class;
    }

    @Override
    public byte id() {
      return -4;
    }
  }

  private static class DataGroupInterpreter extends DataInterpreter<DataGroup> {

    @Override
    protected void output(DataGroup obj, DataOutput output) throws IOException {
      for (Entry<String, Object> entry : obj.map.entrySet()) {
        Data.output(entry.getValue(), output);
        output.writeUTF(entry.getKey());
      }
      output.write(0);
    }

    @Override
    protected DataGroup input(DataInput input) throws IOException {
      DataGroup dataGroup = new DataGroup();
      Object obj;
      while (!((obj = Data.input(input)) instanceof End)) {
        dataGroup.map.put(input.readUTF(), obj);
      }
      return dataGroup;
    }

    @Override
    protected Class<DataGroup> tClass() {
      return DataGroup.class;
    }

    @Override
    public byte id() {
      return -3;
    }
  }

  /**
   * Only supports ArrayLists where every object is the same class and that class has an interpreter
   */
  private static class ArrayListInterpreter extends DataInterpreter<ArrayList> {

    @Override
    protected void output(ArrayList obj, DataOutput output) throws IOException {
      output.writeInt(obj.size());
      if (obj.size() == 0) return;

      DataInterpreter interpreter = get(obj.get(0).getClass());
      output.writeByte(interpreter.id());

      for (Object o : obj) {
        interpreter.output(o, output);
      }
    }

    @Override
    protected ArrayList input(DataInput input) throws IOException {
      int size = input.readInt();
      ArrayList arrayList = new ArrayList(size);
      if (size == 0) return arrayList;

      DataInterpreter interpreter = get(input.readByte());

      for (int i = 0; i < size; i++) {
        arrayList.add(interpreter.input(input));
      }
      return arrayList;
    }

    @Override
    protected Class<ArrayList> tClass() {
      return ArrayList.class;
    }

    @Override
    public byte id() {
      return -2;
    }
  }

  private static class ArrayInterpreter extends DataInterpreter {

    @Override
    protected void output(Object obj, DataOutput output) throws IOException {
      Class root = obj.getClass();
      while (root.isArray()) {
        root = root.getComponentType();
      }

      DataInterpreter interpreter = get(root);
      output.writeByte(interpreter.id());
      output(obj, output, interpreter);
    }

    private void output(Object obj, DataOutput output, DataInterpreter interpreter) throws IOException{
      if (obj.getClass().isArray()) {
        Object[] array = (Object[]) obj;
        if (obj.getClass().getComponentType().isArray()) {
          output.writeInt(array.length);
        } else {
          output.writeInt(-array.length);
        }
        for (Object o : array) {
          output(o, output, interpreter);
        }
      } else {
        interpreter.output(obj, output);
      }
    }



    @Override
    protected Object input(DataInput input) throws IOException {
      DataInterpreter interpreter = get(input.readByte());
      return input(input, interpreter);
    }

    private Object input(DataInput input, DataInterpreter interpreter) throws IOException {
      int length = input.readInt();
      if (length <= 0) {
        length = -length;
        Object[] array = (Object[]) Array.newInstance(interpreter.tClass(), length);
        for (int i = 0; i < length; i++) {
          array[i] = interpreter.input(input);
        }
        return array;
      } else {
        Object first = input(input, interpreter);
        Object[] array = (Object[]) Array.newInstance(first.getClass(), length);
        array[0] = first;
        for (int i = 1; i < length; i++) {
          array[i] = input(input, interpreter);
        }
        return array;
      }
    }

    @Override
    protected Class tClass() {
      throw new UnsupportedOperationException();
    }

    @Override
    public byte id() {
      return -1;
    }
  }

  private static class EndInterpreter extends DataInterpreter<End> {

    private static final End END = new End();

    @Override
    protected void output(End obj, DataOutput output) throws IOException {

    }

    @Override
    protected End input(DataInput input) throws IOException {
      return END;
    }

    @Override
    protected Class<End> tClass() {
      return End.class;
    }

    @Override
    public byte id() {
      return 0;
    }
  }

  private static class BooleanInterpreter extends DataInterpreter<Boolean> {

    @Override
    protected void output(Boolean obj, DataOutput output) throws IOException {
      output.writeBoolean(obj);
    }

    @Override
    protected Boolean input(DataInput input) throws IOException {
      return input.readBoolean();
    }

    @Override
    protected Class<Boolean> tClass() {
      return Boolean.class;
    }

    @Override
    public byte id() {
      return 1;
    }
  }

  private static class ByteInterpreter extends DataInterpreter<Byte> {

    @Override
    protected void output(Byte obj, DataOutput output) throws IOException {
      output.writeByte(obj);
    }

    @Override
    protected Byte input(DataInput input) throws IOException {
      return input.readByte();
    }

    @Override
    protected Class<Byte> tClass() {
      return Byte.class;
    }

    @Override
    public byte id() {
      return 2;
    }
  }

  private static class ShortInterpreter extends DataInterpreter<Short> {

    @Override
    protected void output(Short obj, DataOutput output) throws IOException {
      output.writeShort(obj);
    }

    @Override
    protected Short input(DataInput input) throws IOException {
      return input.readShort();
    }

    @Override
    protected Class<Short> tClass() {
      return Short.class;
    }

    @Override
    public byte id() {
      return 3;
    }
  }

  private static class IntegerInterpreter extends DataInterpreter<Integer> {

    @Override
    protected void output(Integer obj, DataOutput output) throws IOException {
      output.writeInt(obj);
    }

    @Override
    protected Integer input(DataInput input) throws IOException {
      return input.readInt();
    }

    @Override
    protected Class<Integer> tClass() {
      return Integer.class;
    }

    @Override
    public byte id() {
      return 4;
    }
  }

  private static class FloatInterpreter extends DataInterpreter<Float> {

    @Override
    protected void output(Float obj, DataOutput output) throws IOException {
      output.writeFloat(obj);
    }

    @Override
    protected Float input(DataInput input) throws IOException {
      return input.readFloat();
    }

    @Override
    protected Class<Float> tClass() {
      return Float.class;
    }

    @Override
    public byte id() {
      return 5;
    }
  }

  private static class LongInterpreter extends DataInterpreter<Long> {

    @Override
    protected void output(Long obj, DataOutput output) throws IOException {
      output.writeLong(obj);
    }

    @Override
    protected Long input(DataInput input) throws IOException {
      return input.readLong();
    }

    @Override
    protected Class<Long> tClass() {
      return Long.class;
    }

    @Override
    public byte id() {
      return 6;
    }
  }

  private static class DoubleInterpreter extends DataInterpreter<Double> {

    @Override
    protected void output(Double obj, DataOutput output) throws IOException {
      output.writeDouble(obj);
    }

    @Override
    protected Double input(DataInput input) throws IOException {
      return input.readDouble();
    }

    @Override
    protected Class<Double> tClass() {
      return Double.class;
    }

    @Override
    public byte id() {
      return 7;
    }
  }

  private static class StringInterpreter extends DataInterpreter<String> {

    @Override
    protected void output(String obj, DataOutput output) throws IOException {
      output.writeUTF(obj);
    }

    @Override
    protected String input(DataInput input) throws IOException {
      return input.readUTF();
    }

    @Override
    protected Class<String> tClass() {
      return String.class;
    }

    @Override
    public byte id() {
      return 8;
    }
  }

  private static class UUIDInterpreter extends DataInterpreter<UUID> {

    @Override
    protected void output(UUID obj, DataOutput output) throws IOException {
      output.writeLong(obj.getMostSignificantBits());
      output.writeLong(obj.getLeastSignificantBits());
    }

    @Override
    protected UUID input(DataInput input) throws IOException {
      return new UUID(input.readLong(), input.readLong());
    }

    @Override
    protected Class<UUID> tClass() {
      return UUID.class;
    }

    @Override
    public byte id() {
      return 9;
    }
  }

  private static final class End {

    private End() {}

  }

  public static class NoInterpreterException extends RuntimeException {

    private NoInterpreterException(Class c) {
      super("No interpreter for " + c.toString());
    }

    private NoInterpreterException(Byte b) {
      super("No interpreter for " + b);
    }
  }

  protected abstract void output(T obj, DataOutput output) throws IOException;

  protected abstract T input(DataInput input) throws IOException;

  protected abstract Class<T> tClass();

  public abstract byte id();
}
