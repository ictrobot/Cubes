package ethanjones.cubes.core.mod.json;

import ethanjones.cubes.core.json.JsonLoader;
import ethanjones.cubes.core.json.JsonStage;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.mod.event.InitializationEvent;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.core.mod.event.PreInitializationEvent;
import ethanjones.cubes.core.util.Multimap;
import ethanjones.cubes.graphics.assets.AssetManager;

import com.badlogic.gdx.files.FileHandle;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonModInstance extends ModInstance {
  
  public final Map<String, FileHandle> jsonFiles;
  private List<ModState> modStates;
  private Multimap<JsonStage, JsonValue> map;

  public JsonModInstance(String name, FileHandle file, AssetManager assetManager, Map<String, FileHandle> jsonFiles) {
    super(name, file, assetManager);
    this.jsonFiles = jsonFiles;
    List<ModState> modStates = new ArrayList<ModState>();
    modStates.add(ModState.Json);
    this.modStates = Collections.unmodifiableList(modStates);
  }

  @Override
  protected void init() throws Exception {
    map = JsonLoader.load(this);
  }

  @Override
  protected void event(ModEvent modEvent) throws Exception {
    if (modEvent instanceof PreInitializationEvent) JsonLoader.firstStage(map);
    if (modEvent instanceof InitializationEvent) JsonLoader.secondStage(map);
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
