package ethanjones.cubes.core.mod.json;

import ethanjones.cubes.core.json.JsonLoader;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.graphics.assets.AssetManager;

import com.badlogic.gdx.files.FileHandle;

import java.util.*;

public class JsonModInstance extends ModInstance {
  
  public final Map<String, FileHandle> jsonFiles;
  private List<ModState> modStates;

  public JsonModInstance(String name, FileHandle file, AssetManager assetManager, Map<String, FileHandle> jsonFiles) {
    super(name, file, assetManager);
    this.jsonFiles = jsonFiles;
    List<ModState> modStates = new ArrayList<ModState>();
    modStates.add(ModState.Json);
    this.modStates = Collections.unmodifiableList(modStates);
  }

  @Override
  protected void init() throws Exception {
    JsonLoader.load(this);
  }

  @Override
  protected void event(ModEvent modEvent) throws Exception {

  }

  @Override
  protected void addState(ModState modState) {

  }

  @Override
  public List<ModState> getModStates() {
    return modStates;
  }

  @Override
  public Object getMod() {
    return this;
  }
}
