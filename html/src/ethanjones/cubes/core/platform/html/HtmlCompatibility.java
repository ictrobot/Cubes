package ethanjones.cubes.core.platform.html;

import ethanjones.cubes.core.gwt.ExitException;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.LogWriter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.Launcher;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.world.storage.WorldStorageInterface;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtFileHandle;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;

import static ethanjones.cubes.graphics.assets.AssetFinder.addAssetManager;

public class HtmlCompatibility extends Compatibility {
  ApplicationListener applicationListener;
  
  HtmlCompatibility(Launcher launcher) {
    super(launcher, ApplicationType.WebGL);
  }
  
  @Override
  protected void run(ApplicationListener applicationListener) {
    this.applicationListener = applicationListener;
  }
  
  @Override
  public int getFreeMemory() {
    return 0;
  }
  
  @Override
  public boolean functionModifier() {
    return false;
  }
  
  @Override
  public native boolean guessTouchScreen()
/*-{
    if ("ontouchstart" in window || navigator.maxTouchPoints || navigator.msMaxTouchPoints) {
        return true;
    } else {
        return false;
    }
}-*/;
  
  @Override
  public native void setCursorCatched(boolean b)
/*-{
    $wnd.cubesWantsCursorCatched = b;
    if ($wnd.cubesCursorCatched && !$wnd.cubesWantsCursorCatched) {
          try {
              if ($wnd.document.exitPointerLock) {
                  $wnd.document.exitPointerLock();
              }
          } catch (e) {
              $wnd.console.log("exitPointerLock", e);
          }
    }
}-*/;
  
  @Override
  public native void update()
/*-{
    if ($wnd.cubesEsc) {
        $wnd.cubesEsc = false;
        @ethanjones.cubes.side.client.CubesClient::escape()();
    }
}-*/;
  
  @Override
  public int getDeltaX(int pointer) {
    int js = _getDeltaX();
    if (js != 0) return js;
    return super.getDeltaX(pointer);
  }
  
  @Override
  public int getDeltaY(int pointer) {
    int js = _getDeltaY();
    if (js != 0) return js;
    return super.getDeltaY(pointer);
  }
  
  public native int _getDeltaX()
/*-{
    return $wnd.cubesMovementX || 0;
}-*/;
  
  public native int _getDeltaY()
/*-{
    return $wnd.cubesMovementY || 0;
}-*/;

  @Override
  public native void openURL(String url)/*-{
    return $wnd.open(url, 'target=_blank')
}-*/;
  
  @Override
  public void _exit(int status) {
    throw new ExitException();
  }
  
  private final DateTimeFormat format = DateTimeFormat.getFormat("dd-MMM-yy HH:mm:ss");
  
  @Override
  public String timestamp() {
    return format.format(new Date());
  }
  
  @Override
  public LogWriter getCustomLogWriter() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    return new HtmlLogWriter();
  }
  
  @Override
  public String line_separator() {
    return "\n";
  }
  
  public void setupAssets() {
    AssetManager assetManager = new AssetManager(Assets.CORE);
    Preloader preloader = ((GwtApplication) Gdx.app).getPreloader();
    ArrayList<String> paths = new ArrayList<String>();
    for (String p : preloader.audio.keys()) {
      if (p.startsWith("assets/")) paths.add(p.substring(7));
    }
    for (String p : preloader.binaries.keys()) {
      if (p.startsWith("assets/")) paths.add(p.substring(7));
    }
    for (String p : preloader.images.keys()) {
      if (p.startsWith("assets/")) paths.add(p.substring(7));
    }
    for (String p : preloader.texts.keys()) {
      if (p.startsWith("assets/")) paths.add(p.substring(7));
    }
    preloader.hashCode();
    for (String p: paths) {
      Asset a = new Asset(assetManager, p, new GwtFileHandle(preloader, "assets/" + p, FileType.Internal));
      int hashcode = p.hashCode();
      int length = p.length();
      assetManager.assets.put(p, a);
      int aas = assetManager.assets.size();
    }
    addAssetManager(assetManager);
  }
  
  @Override
  public WorldStorageInterface getWorldStorageInterface() {
    if (!HtmlWorldStorage.unavailable()) return new HtmlWorldStorage();
    Log.warning("html world storage unavailable: " + HtmlWorldStorage.unavailable());
    return super.getWorldStorageInterface();
  }
  
  @Override
  public void logCrash(Throwable throwable) {
    GWT.log("crash", throwable);
  }
}
