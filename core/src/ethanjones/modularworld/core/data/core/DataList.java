package ethanjones.modularworld.core.data.core;

import ethanjones.modularworld.core.data.Data;
import ethanjones.modularworld.core.data.DataTools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class DataList<T extends Data> extends Data implements List<T> {

  private static DataEnd dataEnd = new DataEnd();
  private ArrayList<T> list;

  public DataList() {
    list = new ArrayList<T>();
  }

  @Override
  public void write(DataOutput output) throws IOException {
    if (list.size() == 0) {
      output.writeByte(dataEnd.getId());//Code to allow blank arrays
      return;
    }
    output.writeByte(0);//Code to allow blank arrays
    output.writeByte(list.get(0).getId());
    output.writeInt(list.size());
    for (int i = 0; i < list.size(); i++) {
      list.get(i).write(output);
    }
  }

  @Override
  public void read(DataInput input) throws IOException {
    list.clear();
    byte b = input.readByte();
    if (b != 0) return;//Code to allow blank arrays
    byte type = input.readByte();
    int size = input.readInt();
    for (int i = 0; i < size - 1; i++) {
      Data data = DataTools.getData(type);
      data.read(input);
      list.add((T) data);
    }
  }

  @Override
  public String writeNotation() {
    return "";
  }

  @Override
  public void readNotation(String str) {

  }

  @Override
  public byte getId() {
    return -2;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DataList) return ((DataList) o).list.equals(this.list);
    return false;
  }

  @Override
  public String toString() {
    return list.toString();
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }

  @Override
  public T get(int index) {
    return list.get(index);
  }

  @Override
  public T set(int index, T element) {
    return list.set(index, element);
  }

  @Override
  public void add(int index, T element) {
    list.add(index, element);
  }

  @Override
  public T remove(int index) {
    return list.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  @Override
  public ListIterator<T> listIterator() {
    return list.listIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return list.listIterator();
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return list.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return list.iterator();
  }

  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @Override
  public <A> A[] toArray(A[] a) {
    return list.toArray(a);
  }

  public boolean add(T value) {
    return list.add(value);
  }

  @Override
  public boolean remove(Object o) {
    return list.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return list.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return list.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    return list.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return list.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return list.removeAll(c);
  }

  @Override
  public void clear() {
    list.clear();
  }
}
