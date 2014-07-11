package ethanjones.modularworld;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.events.EventBus;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.settings.SettingsManager;
import ethanjones.modularworld.core.timing.Timing;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.rendering.Renderer;
import ethanjones.modularworld.input.InputChain;
import ethanjones.modularworld.networking.Networking;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.generator.BasicWorldGenerator;

public class ModularWorld implements ApplicationListener {

  public static ModularWorld instance;

  public Player player;
  public InputChain inputChain;
  public Renderer renderer;
  public World world;

  public EventBus eventBus;
  public Compatibility compatibility;
  public SettingsManager settings;
  public Timing timing;

  public FileHandle baseFolder;

  public Networking networking;

  public ModularWorld() {
    this(null);
  }

  public ModularWorld(Compatibility compatibility) {
    ModularWorld.instance = this;
    this.compatibility = compatibility;
  }

  @Override
  public void create() {
    if (compatibility == null) {
      Log.error(new ModularWorldException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));
    }
    eventBus = new EventBus().register(this);
    compatibility.init();
    //TODO: Have compatibility log extra stuff about environment such as android version etc
    baseFolder = compatibility.getBaseFolder();
    baseFolder.mkdirs();

    Log.info(Branding.NAME, Branding.DEBUG);
    String version = Gdx.files.internal("version").file().getAbsolutePath();
    Log.info(version + " " + version.endsWith("version"));
    Debug.printProperties();

    if (!compatibility.isHeadless()) {
      GraphicsHelper.init();
    }

    player = new Player();

    BlockFactories.init();

    if (!compatibility.isHeadless()) {
      inputChain = new InputChain();
      renderer = new Renderer();
      BlockFactories.loadGraphics();
    }

    world = new World(new BasicWorldGenerator());

    if (!compatibility.isHeadless()) {
      Gdx.input.setInputProcessor(inputChain.init());
      Gdx.input.setCursorCatched(true);
    }

    settings = new SettingsManager();
    Settings.processAll();
    settings.readFromFile();
    settings.print();

    timing = new Timing();
  }

  @Override
  public void resize(int width, int height) {
    if (!compatibility.isHeadless()) {
      renderer.resize();
    }
  }

  @Override
  public void render() {
    long currentTimeMillis = System.currentTimeMillis();
    timing.update();
    if (!compatibility.isHeadless()) {
      inputChain.beforeRender();
      renderer.render();
      inputChain.afterRender();
      Debug.renderingLoop(System.currentTimeMillis() - currentTimeMillis);
    }
  }

  public void write() {
    settings.writeToFile();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    write();
    if (!compatibility.isHeadless()) {
      renderer.dispose();
    }
    world.dispose();
  }

}
