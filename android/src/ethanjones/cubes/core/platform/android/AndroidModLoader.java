package ethanjones.cubes.core.platform.android;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.system.CubesException;

import android.content.Context;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StreamUtils;
import dalvik.system.DexClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class AndroidModLoader implements ModLoader {
  private static final ModType[] TYPES = new ModType[]{ModType.dex, ModType.jar};

  private final AndroidCompatibility androidCompatibility;

  public AndroidModLoader(AndroidCompatibility androidCompatibility) {
    this.androidCompatibility = androidCompatibility;
  }

  @Override
  public ModType[] getTypes() {
    return TYPES;
  }

  @Override
  public Class<?> loadClass(FileHandle classFile, String className, ModType modType) throws Exception {
    if (modType == ModType.dex) {
      return loadDex(classFile, className);
    } else if (modType == ModType.jar) {
      FileHandle dexFile = convertToDex(classFile);
      return loadDex(dexFile, className);
    } else {
      throw new IllegalStateException(String.valueOf(modType));
    }
  }
  
  private Class<?> loadDex(FileHandle classFile, String className) throws Exception {
    DexClassLoader classLoader = new DexClassLoader(classFile.file().getAbsolutePath(), androidCompatibility.androidLauncher.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), null, AndroidModLoader.class.getClassLoader());
    return classLoader.loadClass(className);
  }
  
  private FileHandle convertToDex(FileHandle fileHandle) throws Exception {
    String inputHash = hashFile(fileHandle);
    
    FileHandle cacheDir = new FileHandle(androidCompatibility.androidLauncher.getCacheDir());
    FileHandle dexDir = cacheDir.child("converted_mod_dex");
    dexDir.mkdirs();
    FileHandle dexFile = dexDir.child(inputHash + ".dex");
    
    if (!dexFile.exists()) {
      Log.warning("Trying to convert jar to dex: " + fileHandle.file().getAbsolutePath());
      com.android.dx.command.dexer.Main.Arguments arguments = new com.android.dx.command.dexer.Main.Arguments();
      arguments.parse(new String[]{"--output=" + dexFile.file().getAbsolutePath(), fileHandle.file().getAbsolutePath()});
      int result = com.android.dx.command.dexer.Main.run(arguments);
      if (result != 0) throw new CubesException("Failed to convert jar to dex [" + result + "]: " + fileHandle.file().getAbsolutePath());
      Log.warning("Converted jar to dex: " + fileHandle.file().getAbsolutePath());
    }
    
    return dexFile;
  }
  
  public static String hashFile(FileHandle fileHandle) throws Exception {
    InputStream inputStream = fileHandle.read();
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
  
      byte[] bytesBuffer = new byte[1024];
      int bytesRead;
  
      while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
        digest.update(bytesBuffer, 0, bytesRead);
      }
  
      byte[] hashedBytes = digest.digest();
      return convertByteArrayToHexString(hashedBytes);
    } catch (IOException ex) {
      throw new CubesException("Could not generate hash from file " + fileHandle.path(), ex);
    } finally {
      StreamUtils.closeQuietly(inputStream);
    }
  }
  
  private static String convertByteArrayToHexString(byte[] arrayBytes) {
    StringBuilder stringBuffer = new StringBuilder();
    for (byte arrayByte : arrayBytes) {
      stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16).substring(1));
    }
    return stringBuffer.toString();
  }
}
