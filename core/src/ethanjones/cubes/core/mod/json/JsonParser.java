package ethanjones.cubes.core.mod.json;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.BlockFace;

public class JsonParser {

  public static void parse(JsonModInstance jsonModInstance, JsonValue jsonValue) throws Exception {
    if (jsonValue.child != null && jsonValue.child.name().equals("block") && jsonValue.child.type() == ValueType.array) {
      for (JsonValue entry = jsonValue.child.child; entry != null; entry = entry.next) {
        JsonBlockParameter block = block(jsonModInstance, entry);
        if (block != null) jsonModInstance.blockParameters.add(block);
      }
    }
  }

  private static JsonBlockParameter block(JsonModInstance jsonModInstance, JsonValue jsonValue) throws Exception {
    JsonBlockParameter jsonBlockParameter = new JsonBlockParameter();
    jsonBlockParameter.id = jsonValue.getString("id");
    jsonBlockParameter.fullID = jsonModInstance.getName() + ":" + jsonBlockParameter.id;
    JsonValue texture = jsonValue.get("texture");
    if (texture != null) {
      if (texture.isString()) {
        jsonBlockParameter.singleTexture = texture.asString();
        jsonBlockParameter.textures = null;
      } else if (texture.isObject()) {
        jsonBlockParameter.singleTexture = null;
        jsonBlockParameter.textures = new String[BlockFace.values().length];
        for (JsonValue entry = texture.child; entry != null; entry = entry.next) {
          String value = entry.asString();
          if (entry.name.equals("side")) {
            jsonBlockParameter.textures[BlockFace.negX.index] = value;
            jsonBlockParameter.textures[BlockFace.posX.index] = value;
            jsonBlockParameter.textures[BlockFace.negZ.index] = value;
            jsonBlockParameter.textures[BlockFace.posZ.index] = value;
            continue;
          } else if (entry.name.equals("main")) {
            jsonBlockParameter.singleTexture = value;
            continue;
          }
          BlockFace blockFace = BlockFace.valueOf(entry.name);
          jsonBlockParameter.textures[blockFace.ordinal()] = value;
        }
      } else {
        throw new CubesException("Invalid type for \"texture\" attribute in \"block\"");
      }
    } else {
      jsonBlockParameter.singleTexture = null;
      jsonBlockParameter.textures = null;
    }
    return jsonBlockParameter;
  }
  
}
