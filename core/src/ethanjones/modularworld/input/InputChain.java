package ethanjones.modularworld.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.debug.Debug;

public class InputChain extends InputMultiplexer {

  public Stage hud;
  public GameInputHandler game;

  public InputChain() {
    super();
  }

  public InputChain init() {
    this.addProcessor(hud);
    this.addProcessor(game = new GameInputHandler());
    return this;
  }

  public void beforeRender() {
    ModularWorld.instance.player.movementHandler.updatePosition();
    game.updateTouch();
    Debug.position();
  }

  public void afterRender() {
    ModularWorld.instance.player.movementHandler.afterRender();
  }
}
