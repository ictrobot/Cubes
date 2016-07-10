package ethanjones.cubes.core.lua.convert;

import org.luaj.vm2.LuaValue;

public interface LuaConverter<E> {

  public LuaValue toLua(E e);

  public E toJava(LuaValue l, Class<? extends E> c);

}
