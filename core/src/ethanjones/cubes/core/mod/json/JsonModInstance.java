package ethanjones.cubes.core.mod.json;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModState;
import ethanjones.cubes.core.mod.event.*;
import ethanjones.cubes.graphics.assets.AssetManager;

public class JsonModInstance extends ModInstance {
  
  private final List<FileHandle> jsonFiles;
  private List<ModState> modStates;

  public List<JsonBlockParameter> blockParameters = new ArrayList<JsonBlockParameter>();

  public JsonModInstance(String name, FileHandle file, AssetManager assetManager, List<FileHandle> jsonFiles) {
    super(name, file, assetManager);
    this.jsonFiles = jsonFiles;
    List<ModState> modStates = new ArrayList<ModState>();
    modStates.add(ModState.Json);
    this.modStates = Collections.unmodifiableList(modStates);
  }

  @Override
  protected void init() throws Exception {
    for (FileHandle fileHandle : jsonFiles) {
      try {
        JsonValue jsonValue = new JsonReader().parse(fileHandle);
        JsonParser.parse(this, jsonValue);
      } catch (Exception e) {
        Log.error("Failed to read " + fileHandle.name() + " from " + name, e);
      }
    }
  }

  @Override
  protected void event(ModEvent modEvent) throws Exception {
    if (modEvent instanceof PreInitializationEvent) {
      for (JsonBlockParameter blockParameter : blockParameters) {
        blockParameter.init(this);
      }
    } else if (modEvent instanceof PostInitializationEvent) {
      for (JsonBlockParameter blockParameter : blockParameters) {
        blockParameter.loadGraphics();
      }
    } else if (modEvent instanceof StartingClientEvent || modEvent instanceof StartingServerEvent) {
      for (JsonBlockParameter blockParameter : blockParameters) {
        blockParameter.register(this);
      }
    }
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
