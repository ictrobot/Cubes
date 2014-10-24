package ethanjones.modularworld.side;

import ethanjones.modularworld.block.BlockManager;
import ethanjones.modularworld.block.Blocks;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.events.EventBus;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.settings.SettingsManager;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.core.system.ModularWorldSecurity;
import ethanjones.modularworld.core.timing.Timing;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.asset.AssetManager;

public class Sided {

  private static Data clientData;
  private static Data serverData;
  private static ThreadLocal<Side> sideLocal = new ThreadLocal<Side>();
  private static ThreadLocal<Boolean> mainLocal = new ThreadLocal<Boolean>() {
    @Override
    public Boolean initialValue() {
      return false;
    }
  };
  //SHARED
  private static AssetManager assetManager;
  private static SettingsManager settingsManager;

  public static Side getSide() {
    return sideLocal.get();
  }

  /**
   * Allow network threads etc. to access sided objects
   */
  public static void setSide(Side side) {
    if (mainLocal.get()) return;
    sideLocal.set(side);
  }

  public static SettingsManager getSettingsManager() {
    return settingsManager;
  }

  public static AssetManager getAssetManager() {
    return assetManager;
  }

  public static EventBus getEventBus() {
    return getData().eventBus;
  }

  public static Timing getTiming() {
    return getData().timing;
  }

  public static BlockManager getBlockManager() {
    return getData().blockManager;
  }

  public static void setupGlobal() {
    ModularWorldSecurity.checkSidedSetup();

    settingsManager = new SettingsManager();
    Settings.processAll();
    settingsManager.readFromFile();
    settingsManager.print();

    assetManager = new AssetManager();
    Compatibility.get().getAssets(assetManager);

    if (!Compatibility.get().isHeadless()) {
      GraphicsHelper.init(assetManager);
    }
  }

  public static void setup(Side side) {
    ModularWorldSecurity.checkSidedSetup();

    if (side == null || getData(side) != null) return;

    sideLocal.set(side);
    mainLocal.set(true);

    Data data = null;
    switch (side) {
      case Client:
        clientData = new Data();
        data = clientData;
        break;
      case Server:
        serverData = new Data();
        data = serverData;
        break;
    }

    data.eventBus = new EventBus();
    data.timing = new Timing();
    data.blockManager = new BlockManager();
    Blocks.init();
  }

  public static void reset(Side side) {
    ModularWorldSecurity.checkSidedReset();

    if (side == null) return;

    switch (side) {
      case Client:
        clientData = null;
        break;
      case Server:
        serverData = null;
        break;
    }
  }

  private static Data getData() {
    Side side = getSide();
    if (side == null)
      throw new ModularWorldException("Sided objects cannot be accessed from thread: " + Thread.currentThread().getName());
    Data data = getData(side);
    if (data == null)
      throw new ModularWorldException("Sided objects have not been setup yet");
    return data;
  }

  private static Data getData(Side side) {
    switch (side) {
      case Client:
        return clientData;
      case Server:
        return serverData;
    }
    return null;
  }

  private static class Data {
    EventBus eventBus;
    Timing timing;
    BlockManager blockManager;
  }
}
