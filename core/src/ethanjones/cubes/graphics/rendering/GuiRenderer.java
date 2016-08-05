package ethanjones.cubes.graphics.rendering;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.player.PlayerInventory;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.hud.FrametimeGraph;
import ethanjones.cubes.graphics.hud.ImageButtons;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.input.keyboard.KeyTypedAdapter;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.side.client.ClientDebug;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

import static ethanjones.cubes.graphics.GUI.HEIGHT;
import static ethanjones.cubes.graphics.GUI.WIDTH;
import static ethanjones.cubes.graphics.Graphics.screenViewport;
import static ethanjones.cubes.graphics.Graphics.spriteBatch;
import static ethanjones.cubes.graphics.menu.Menu.skin;

public class GuiRenderer implements Disposable {

  private class KeyListener extends KeyTypedAdapter {

    static final int hideGUI = Keys.F1;
    static final int screenshot = Keys.F2;
    static final int debug = Keys.F3;
    static final int chat = Keys.F4;
    static final int blocksMenu = Keys.E;

    private boolean functionKeys(int keycode) {
      if (keycode == hideGUI) {
        hideGuiEnabled = !hideGuiEnabled;
        return true;
      }
      if (keycode == screenshot) {
        Graphics.takeScreenshot();
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
        chatEnabled = !chatEnabled;
        return true;
      }
      return false;
    }

    @Override
    public void keyDown(int keycode) {
      functionKeys(keycode);

      if (keycode == blocksMenu) blocksMenuEnabled = !blocksMenuEnabled;

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
      if (keycode == Keys.NUM_0) selected = 9;
      if (selected != -1) {
        Cubes.getClient().player.getInventory().hotbarSelected = selected;
        Cubes.getClient().player.getInventory().sync();
      }
    }
  }

  Stage stage;

  TextField chat;
  Label chatLog;
  ArrayList<String> chatStrings = new ArrayList<String>();

  KeyListener keyListener;

  Touchpad touchpad;
  ImageButton jumpButton;
  ImageButton debugButton;
  ImageButton chatButton;
  ImageButton blockSelectorButton;

  Texture crosshair;
  Texture hotbarSlot;
  Texture hotbarSelected;
  ItemStack[][] itemstacks;

  public boolean chatEnabled;
  public boolean debugEnabled;
  public boolean hideGuiEnabled;
  public boolean blocksMenuEnabled;

  public GuiRenderer() {
    stage = new Stage(screenViewport, spriteBatch);
    Cubes.getClient().inputChain.hud = stage;

    keyListener = new KeyListener();
    KeyboardHelper.addKeyTypedListener(keyListener);

    final TextField.TextFieldStyle defaultStyle = skin.get("default", TextField.TextFieldStyle.class);
    final TextField.TextFieldStyle chatStyle = new TextField.TextFieldStyle(defaultStyle);
    chatStyle.font = Fonts.hud;
    chatStyle.background = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ChatBackground.png"));

    chat = new TextField("", chatStyle) {
      protected InputListener createInputListener() {
        return new TextFieldClickListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            return keyListener.functionKeys(keycode) || super.keyDown(event, keycode);
          }
        };
      }
    };
    chat.setTextFieldListener(new TextField.TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char c) {
        if (c == '\n' || c == '\r') {
          PacketChat packetChat = new PacketChat();
          packetChat.msg = chat.getText();
          NetworkingManager.sendPacketToServer(packetChat);
          chat.setText("");
          chatEnabled = false;
        }
      }
    });
    chatLog = new Label("", new LabelStyle(Fonts.hud, Color.WHITE));
    chatLog.setAlignment(Align.bottomLeft, Align.left);

    if (Compatibility.get().isTouchScreen()) {
      touchpad = new Touchpad(0, skin);
      jumpButton = new ImageButton(ImageButtons.jumpButton());

      Cubes.getClient().inputChain.cameraController.touchpad = touchpad;
      Cubes.getClient().inputChain.cameraController.jumpButton = jumpButton;

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
          chatEnabled = !chatEnabled;
        }
      });
      blockSelectorButton = new ImageButton(ImageButtons.blocksButton());
      blockSelectorButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          blocksMenuEnabled = !blocksMenuEnabled;
          ;
        }
      });
      stage.addActor(touchpad);
      stage.addActor(jumpButton);
      stage.addActor(debugButton);
      stage.addActor(chatButton);
      stage.addActor(blockSelectorButton);
    }

    crosshair = Assets.getTexture("core:hud/Crosshair.png");
    hotbarSelected = Assets.getTexture("core:hud/HotbarSelected.png");
    hotbarSlot = Assets.getTexture("core:hud/HotbarSlot.png");

    itemstacks = new ItemStack[10][6];
    int i = 0;
    List<ItemStack> list = new ArrayList<ItemStack>();
    for (ItemBlock itemBlock : IDManager.getItemBlocks()) {
      for (int j : itemBlock.block.displayMetaValues()) {
        list.add(new ItemStack(itemBlock, 1, j));
      }
    }
    for (Item item : IDManager.getItems()) {
      list.add(new ItemStack(item));
    }
    for (int y = 0; y < 6; y++) {
      for (int x = 0; x < 10; x++, i++) {
        if (i >= list.size()) break;
        itemstacks[x][y] = list.get(i);
      }
      if (i >= list.size()) break;
    }
  }

  public void render() {
    FrametimeGraph.update();

    stage.getRoot().removeActor(chat);
    stage.getRoot().removeActor(chatLog);
    if (chatEnabled) {
      stage.addActor(chat);
      stage.setKeyboardFocus(chat);
      stage.addActor(chatLog);
    } else {
      stage.setKeyboardFocus(null);
    }
    stage.act();
    stage.draw();

    spriteBatch.begin();
    float crosshairSize = 10f;
    if (!hideGuiEnabled) {
      spriteBatch.draw(crosshair, (WIDTH / 2) - crosshairSize, (HEIGHT / 2) - crosshairSize, crosshairSize * 2, crosshairSize * 2);
      if (!chatEnabled) renderHotbar();
    }
    if (blocksMenuEnabled) renderBlockMenu();
    if (debugEnabled) {
      FrametimeGraph.drawLines(spriteBatch);
      Fonts.debug.draw(spriteBatch, ClientDebug.getDebugString(), 5f, HEIGHT - 5);
    }
    spriteBatch.end();
    if (debugEnabled) {
      FrametimeGraph.drawPoints();
    }
  }

  public void renderHotbar() {
    PlayerInventory inv = Cubes.getClient().player.getInventory();
    float itemSize = 32;
    float hotbarSize = 48;
    float itemOffset = 8;

    float startWidth = (WIDTH / 2) - (5 * hotbarSize);
    for (int i = 0; i < 10; i++) {
      float minX = startWidth + (i * hotbarSize);
      if (i == inv.hotbarSelected) {
        spriteBatch.draw(hotbarSelected, minX, 0, hotbarSize, hotbarSize);
      } else {
        spriteBatch.draw(hotbarSlot, minX, 0, hotbarSize, hotbarSize);
      }
      ItemStack itemStack = inv.itemStacks[i];
      if (itemStack != null) {
        TextureRegion texture = itemStack.item.getTextureRegion();
        spriteBatch.draw(texture, minX + itemOffset, itemOffset, itemSize, itemSize);

        BitmapFontCache cache = Fonts.smallHUD.getCache();
        cache.clear();
        if (itemStack.item instanceof ItemTool) continue;
        GlyphLayout layout = cache.addText(itemStack.count + "", minX + itemOffset, itemOffset, itemSize, Align.right, false);
        cache.translate(0, layout.height);
        cache.draw(spriteBatch);
      }
    }
  }

  public void renderBlockMenu() {
    float itemSize = 32;
    float hotbarSize = 48;
    float itemOffset = 8;

    int i = 0;
    float startWidth = (WIDTH / 2) - (5 * hotbarSize);
    float startHeight = (HEIGHT / 2) - (3 * hotbarSize);
    for (int y = 0; y < 6; y++) {
      float minY = startHeight + ((5 - y) * hotbarSize);
      for (int x = 0; x < 10; x++, i++) {
        float minX = startWidth + (x * hotbarSize);
        spriteBatch.draw(hotbarSlot, minX, minY, hotbarSize, hotbarSize);
        ItemStack itemStack = itemstacks[x][y];
        if (itemStack == null || itemStack.item == null) continue;
        spriteBatch.draw(itemStack.item.getTextureRegion(), minX + itemOffset, minY + itemOffset, itemSize, itemSize);
      }
    }
  }

  public void resize() {
    chat.setBounds(0, 0, WIDTH, chat.getStyle().font.getLineHeight() * 1.5f);
    chatLog.setBounds(0, chat.getHeight(), WIDTH, chatLog.getStyle().font.getLineHeight() * 5);

    if (touchpad != null) {
      float hbSize = 48;
      float padding = 10;
      float size = HEIGHT * Settings.getFloatSettingValue(Settings.INPUT_TOUCHPAD_SIZE);
      if (Settings.getBooleanSettingValue(Settings.INPUT_TOUCHPAD_LEFT)) {
        touchpad.setBounds(padding, hbSize + padding, size, size);
        jumpButton.setBounds(WIDTH - hbSize - padding, hbSize, hbSize, hbSize);
      } else {
        touchpad.setBounds(WIDTH - size - padding, hbSize, size, size);
        jumpButton.setBounds(padding, hbSize + padding, hbSize, hbSize);
      }
    }
    if (chatButton != null && debugButton != null && blockSelectorButton != null) {
      float width = blockSelectorButton.getPrefWidth() / 3 * 2;
      float height = blockSelectorButton.getPrefHeight() / 3 * 2;
      blockSelectorButton.setBounds(WIDTH - width, HEIGHT - height, width, height);
      chatButton.setBounds(WIDTH - width - width, HEIGHT - height, width, height);
      debugButton.setBounds(WIDTH - width - width - width, HEIGHT - height, width, height);
    }
  }

  public void print(String string) {
    chatStrings.add(0, string);
    StringBuilder builder = new StringBuilder();
    for (int i = Math.min(4, chatStrings.size() - 1); i >= 0; i--) {
      builder.append(chatStrings.get(i)).append("\n");
    }
    chatLog.setText(builder.toString());
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  public boolean noCursorCatching() {
    return chatEnabled || blocksMenuEnabled || hideGuiEnabled;
  }

  public boolean touch(int screenX, int screenY, int pointer, int button) {
    float itemSize = 32;
    float hotbarSize = 48;
    float itemOffset = 8;

    float startWidth = (WIDTH / 2) - (hotbarSize * 5);
    float startHeight = (HEIGHT / 2) - (hotbarSize * 3);

    if (blocksMenuEnabled) {
      float x = screenX - startWidth;
      float y = screenY - startHeight;
      if (x < 0 || y < 0) return false;
      float remX = x % hotbarSize;
      float remY = y % hotbarSize;
      if (remX >= itemOffset && remX <= itemSize + itemOffset && remY >= itemOffset && remY <= itemSize + itemOffset) {
        int slotX = (int) (x / hotbarSize);
        int slotY = (int) (y / hotbarSize);
        if (slotX >= itemstacks.length || slotY >= itemstacks[0].length) return false;

        ItemStack base = itemstacks[slotX][slotY];
        ItemStack itemStack = null;
        if (base != null && base.item != null) {
          itemStack = new ItemStack(base.item, base.item.getStackCountMax(), base.meta);
        }
        PlayerInventory inventory = Cubes.getClient().player.getInventory();
        inventory.itemStacks[inventory.hotbarSelected] = itemStack;
        inventory.sync();
        return true;
      }
    }
    if (blocksMenuEnabled || Compatibility.get().isTouchScreen()) {
      if (screenX >= startWidth && screenX <= (startWidth + (hotbarSize * 10)) && screenY >= (HEIGHT - hotbarSize)) {
        int slot = (int) ((screenX - startWidth) / hotbarSize);
        if (slot >= 0 && slot <= 10) {
          Cubes.getClient().player.getInventory().hotbarSelected = slot;
          Cubes.getClient().player.getInventory().sync();
        }
        return true;
      }
    }
    return false;
  }
}
