package ethanjones.cubes.graphics.rendering;

import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.Keybinds;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.util.Toggle;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.hud.ChatActor;
import ethanjones.cubes.graphics.hud.FrametimeGraph;
import ethanjones.cubes.graphics.hud.ImageButtons;
import ethanjones.cubes.graphics.hud.inv.*;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.side.client.ClientDebug;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import static ethanjones.cubes.graphics.Graphics.*;
import static ethanjones.cubes.graphics.hud.inv.HotbarActor.scroll;
import static ethanjones.cubes.graphics.menu.Menu.skin;

public class GuiRenderer implements Disposable {

  Stage stage;

  TextField chat;
  ChatActor chatLog;

  Touchpad touchpad;
  ImageButton jumpButton;
  ImageButton descendButton;
  ImageButton debugButton;
  ImageButton chatButton;
  ImageButton blockSelectorButton;
  public ImageButton inventoryModifierButton;

  public InventoryActor playerInv;
  public HotbarActor hotbar;

  Texture crosshair;

  public Toggle chatToggle = new Toggle() {
    @Override
    public void doEnable() {
      stage.addActor(chat);
      stage.setKeyboardFocus(chat);
      chatLog.open.enable();
      hotbar.remove();
    }

    @Override
    public void doDisable() {
      stage.setKeyboardFocus(null);
      stage.getRoot().removeActor(chat);
      chatLog.open.disable();
      stage.addActor(hotbar);
    }
  };
  public boolean debugEnabled;
  public boolean hideGuiEnabled;

  public GuiRenderer() {
    stage = new Stage(screenViewport, spriteBatch);

    final Input input = new Input();
    Cubes.getClient().inputChain.stageHud = stage;
    Cubes.getClient().inputChain.hud = input;

    stage.addListener(scroll);

    InventoryManager.setup(stage);

    final TextField.TextFieldStyle defaultStyle = skin.get("default", TextField.TextFieldStyle.class);
    final TextField.TextFieldStyle chatStyle = new TextField.TextFieldStyle(defaultStyle);
    chatStyle.font = Fonts.hud;
    chatStyle.background = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ChatBackground.png"));

    chat = new TextField("", chatStyle) {
      protected InputListener createInputListener() {
        return new TextFieldClickListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            return input.functionKeys(keycode) || super.keyDown(event, keycode);
          }
        };
      }
    };
    chat.setTextFieldListener(new TextField.TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char c) {
        if (c == '\n' || c == '\r') {
          String msg = chat.getText().trim();
          if (!msg.isEmpty()) {
            PacketChat packetChat = new PacketChat();
            packetChat.msg = msg;
            NetworkingManager.sendPacketToServer(packetChat);
          }
          chat.setText("");
          chatToggle.disable();
        }
      }
    });
    chatLog = new ChatActor();
    stage.addActor(chatLog);

    if (Compatibility.get().isTouchScreen()) {
      touchpad = new Touchpad(0, skin);
      jumpButton = new ImageButton(ImageButtons.jumpButton());
      descendButton = new ImageButton(ImageButtons.descendButton());

      Cubes.getClient().inputChain.cameraController.touchpad = touchpad;
      Cubes.getClient().inputChain.cameraController.jumpButton = jumpButton;
      Cubes.getClient().inputChain.cameraController.descendButton = descendButton;

      debugButton = new ImageButton(ImageButtons.debugButton());
      debugButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          if (Compatibility.get().functionModifier()) {
            Performance.toggleTracking();
          } else {
            debugEnabled = !debugEnabled;
          }
        }
      });
      chatButton = new ImageButton(ImageButtons.chatButton());
      chatButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          chatToggle.toggle();
        }
      });
      blockSelectorButton = new ImageButton(ImageButtons.blocksButton());
      blockSelectorButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          toggleInventory();
        }
      });
      inventoryModifierButton = new ImageButton(ImageButtons.inventoryModifierButton());

      InventoryManager.GROUP_HIDDEN.addActor(touchpad);
      InventoryManager.GROUP_HIDDEN.addActor(jumpButton);
      InventoryManager.GROUP_HIDDEN.addActor(debugButton);
      InventoryManager.GROUP_HIDDEN.addActor(chatButton);
      InventoryManager.GROUP_HIDDEN.addActor(descendButton);

      stage.addActor(blockSelectorButton);

      InventoryManager.GROUP_SHOWN.addActor(inventoryModifierButton);
    }

    crosshair = Assets.getTexture("core:hud/Crosshair.png");
  }

  public void playerCreated() {
    playerInv = new InventoryActor(Cubes.getClient().player.getInventory());
    hotbar = new HotbarActor(Cubes.getClient().player.getInventory());

    InventoryManager.GROUP_HIDDEN.addActor(hotbar);
  }

  public void render() {
    FrametimeGraph.update();
    if (descendButton != null) descendButton.setVisible(Cubes.getClient().gamemode == Gamemode.creative || Cubes.getClient().player.noClip());
    hotbar.setVisible(!hideGuiEnabled);

    stage.act();
    stage.draw();


    spriteBatch.setProjectionMatrix(screenViewport.getCamera().combined);
    spriteBatch.begin();
    float crosshairSize = 10f;
    if (!hideGuiEnabled) {
      if (!InventoryManager.isInventoryOpen())
        spriteBatch.draw(crosshair, (GUI_WIDTH / 2) - crosshairSize, (GUI_HEIGHT / 2) - crosshairSize, crosshairSize * 2, crosshairSize * 2);
    }
    if (debugEnabled) {
      FrametimeGraph.drawLines(spriteBatch);
      Fonts.debug.draw(spriteBatch, ClientDebug.getDebugString(), 5f, GUI_HEIGHT - 5);
    }
    spriteBatch.end();
    if (debugEnabled) {
      FrametimeGraph.drawPoints();
    }
  }

  public void resize() {
    chat.setBounds(0, 0, GUI_WIDTH, chat.getStyle().font.getLineHeight() * 1.5f);
    chatLog.setBounds(0, chat.getHeight(), GUI_WIDTH, chatLog.getPrefHeight());

    if (touchpad != null) {
      float hbSize = 48;
      float padding = 10;
      float size = GUI_HEIGHT * Settings.getFloatSettingValue(Settings.INPUT_TOUCHPAD_SIZE);
      if (Settings.getBooleanSettingValue(Settings.INPUT_TOUCHPAD_LEFT)) {
        touchpad.setBounds(padding, hbSize + padding, size, size);
        jumpButton.setBounds(GUI_WIDTH - hbSize - padding, hbSize * 2 + padding, hbSize, hbSize);
        descendButton.setBounds(GUI_WIDTH - hbSize - padding, hbSize + padding, hbSize, hbSize);
      } else {
        touchpad.setBounds(GUI_WIDTH - size - padding, hbSize, size, size);
        jumpButton.setBounds(padding, hbSize * 2 + padding, hbSize, hbSize);
        descendButton.setBounds(padding, hbSize + padding, hbSize, hbSize);
      }
    }
    if (chatButton != null && debugButton != null && blockSelectorButton != null) {
      float width = blockSelectorButton.getPrefWidth() / 3 * 2;
      float height = blockSelectorButton.getPrefHeight() / 3 * 2;
      blockSelectorButton.setBounds(GUI_WIDTH - width, GUI_HEIGHT - height, width, height);
      chatButton.setBounds(GUI_WIDTH - width - width, GUI_HEIGHT - height, width, height);
      debugButton.setBounds(GUI_WIDTH - width - width - width, GUI_HEIGHT - height, width, height);
      inventoryModifierButton.setBounds(0, GUI_HEIGHT - height, width, height);
    }

    InventoryManager.resize();

    MenuTools.center(hotbar);
    hotbar.setY(0);
  }

  public void newMessage(String string) {
    if (chatLog != null) chatLog.newMessage(string);
  }

  public void toggleInventory() {
    if (InventoryManager.isInventoryOpen()) {
      InventoryManager.hideInventory();
    } else {
      if (Cubes.getClient().gamemode == Gamemode.creative) {
        InventoryManager.showInventory(new InventoryWindow(new DoubleInventory(new CreativeInventoryActor(), playerInv)));
      } else {
        InventoryManager.showInventory(new InventoryWindow(new DoubleInventory(new CraftingInventoryActor(), playerInv)));
      }
    }
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  public boolean noCursorCatching() {
    return chatToggle.isEnabled() || InventoryManager.isInventoryOpen() || hideGuiEnabled;
  }

  private class Input extends InputAdapter {

    int hideGUI = Keybinds.getCode(Keybinds.KEYBIND_HIDEGUI);
    int debug = Keybinds.getCode(Keybinds.KEYBIND_DEBUG);
    int chat = Keybinds.getCode(Keybinds.KEYBIND_CHAT);
    int blocksMenu = Keybinds.getCode(Keybinds.KEYBIND_INVENTORY);

    private boolean functionKeys(int keycode) {
      if (keycode == hideGUI) {
        hideGuiEnabled = !hideGuiEnabled;
        return true;
      }
      if (keycode == debug) {
        if (Compatibility.get().functionModifier()) {
          Performance.toggleTracking();
        } else {
          debugEnabled = !debugEnabled;
        }
        return true;
      }
      if (keycode == chat) {
        chatToggle.toggle();
        return true;
      }
      return false;
    }

    @Override
    public boolean keyDown(int keycode) {
      if (functionKeys(keycode)) return true;

      if (keycode == blocksMenu) {
        toggleInventory();
        return true;
      }

      int selected = -1;
      if (keycode == Keys.NUM_1) selected = 0;
      if (keycode == Keys.NUM_2) selected = 1;
      if (keycode == Keys.NUM_3) selected = 2;
      if (keycode == Keys.NUM_4) selected = 3;
      if (keycode == Keys.NUM_5) selected = 4;
      if (keycode == Keys.NUM_6) selected = 5;
      if (keycode == Keys.NUM_7) selected = 6;
      if (keycode == Keys.NUM_8) selected = 7;
      if (keycode == Keys.NUM_9) selected = 8;
      if (selected != -1) {
        Cubes.getClient().player.getInventory().hotbarSelected = selected;
        Cubes.getClient().player.getInventory().sync();
        return true;
      }
      return false;
    }
  }
}
