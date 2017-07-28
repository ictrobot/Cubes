package ethanjones.cubes.core.mod;

import ethanjones.cubes.core.system.CubesException;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class ModInputStream implements Closeable {
  
  /**
   * @return Next file or none or if at the end
   */
  public abstract ModFile getNextModFile() throws IOException;
  
  public interface ModFile {
    String getName();
    
    boolean isFolder();
    
    InputStream getInputStream() throws IOException;
  }
  
  public static class ZipModInputStream extends ModInputStream {
  
    private InputStream inputStream;
    private ZipInputStream zipInputStream;
    private InputStream fileStream;
    
    public ZipModInputStream(FileHandle file) {
      try {
        inputStream = new FileInputStream(file.file());
        zipInputStream = new ZipInputStream(inputStream);
        fileStream = new FilterInputStream(zipInputStream) {
          @Override
          public void close() throws IOException {
            // no close
          }
        };
      } catch (Exception e) {
        try {
          close();
        } catch (IOException ignored) {
        }
        throw new CubesException("Failed to create zip mod input stream", e);
      }
    }
    
    @Override
    public ModFile getNextModFile() throws IOException {
      ZipEntry entry = zipInputStream.getNextEntry();
      return entry == null ? null : new ZipModFile(entry);
    }
  
    @Override
    public void close() throws IOException {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception ignored) {
      
        }
        inputStream = null;
        zipInputStream = null;
        fileStream = null;
      }
    }
  
    private class ZipModFile implements ModFile {
  
      private final ZipEntry entry;
  
      private ZipModFile(ZipEntry entry) {
        this.entry = entry;
      }
  
      @Override
      public String getName() {
        return entry.getName().replace('\\', '/');
      }
  
      @Override
      public boolean isFolder() {
        return entry.isDirectory();
      }
  
      @Override
      public InputStream getInputStream() throws IOException {
        if (isFolder()) throw new IllegalStateException("Cannot getInputStream() for a folder");
        return fileStream;
      }
    }
  }
  
  public static class FolderModInputStream extends ModInputStream {
  
    private final FileHandle root;
    private final LinkedBlockingDeque<FileHandle> files = new LinkedBlockingDeque<FileHandle>();
    private final LinkedBlockingDeque<FileHandle> folders = new LinkedBlockingDeque<FileHandle>();
    
    public FolderModInputStream(FileHandle file) {
      root = file;
      for (FileHandle f : file.list()) {
        if (f.isDirectory()) {
          folders.add(f);
        } else {
          files.add(f);
        }
      }
    }
    
    @Override
    public ModFile getNextModFile() throws IOException {
      FileHandle file = files.pollFirst();
      if (file != null) return new FolderModFile(file);
      FileHandle folder = folders.pollFirst();
      if (folder == null) return null;
      for (FileHandle f : folder.list()) {
        if (f.isDirectory()) {
          folders.add(f);
        } else {
          files.add(f);
        }
      }
      return new FolderModFile(folder);
    }
    
    @Override
    public void close() throws IOException {
      files.clear();
      folders.clear();
    }
    
    private class FolderModFile implements ModFile {
  
      private final FileHandle f;
  
      private FolderModFile(FileHandle f) {
        this.f = f;
      }
      
      @Override
      public String getName() {
        String s = f.file().getAbsolutePath();
        String parent = root.file().getAbsolutePath();
        if (!s.startsWith(parent)) throw new IllegalStateException(s + " | " + parent);
        String path = s.substring(parent.length()).replace('\\', '/');
        if (path.startsWith("/")) path = path.substring(1);
        return path;
      }
  
      @Override
      public boolean isFolder() {
        return f.isDirectory();
      }
  
      @Override
      public InputStream getInputStream() throws IOException {
        if (isFolder()) throw new IllegalStateException("Cannot getInputStream() for a folder");
        return new FileInputStream(f.file());
      }
    }
  }
  
  protected static ModInputStream get(FileHandle fileHandle) {
    return fileHandle.isDirectory() ? new FolderModInputStream(fileHandle) : new ZipModInputStream(fileHandle);
  }
}
