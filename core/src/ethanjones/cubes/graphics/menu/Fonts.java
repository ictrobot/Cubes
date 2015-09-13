package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.graphics.assets.Assets;

public class Fonts {

  public static final BitmapFont Size1;
  public static final BitmapFont Size2;
  public static final BitmapFont Size3;
  public static final BitmapFont Size4;
  public static final BitmapFont Size5;
  public static final BitmapFont Size6;
  public static final BitmapFont Size7;
  public static final BitmapFont Size8;
  public static final BitmapFont Size9;
  public static final BitmapFont Size10;
  public static final BitmapFont Size11;
  public static final BitmapFont Size12;
  public static final BitmapFont Size13;
  public static final BitmapFont Size14;
  public static final BitmapFont Size15;
  public static final BitmapFont Size16;

  public static final BitmapFont[] FONTS = new BitmapFont[16];

  static {
    Log.debug("Generating font");
    FreeTypeFontGenerator.setMaxTextureSize(2048);
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Assets.getAsset("core:font/font.ttf").getFileHandle());

    int base = (int) Gdx.graphics.getPpcX() / 2;

    for (int i = 0; i < FONTS.length; i++) {
      FreeTypeFontParameter parameter = new FreeTypeFontParameter();
      parameter.size = (int) (base * ((i / 2f) + 0.5f));
      parameter.incremental = true;

      FONTS[i] = generator.generateFont(parameter);
    }

    Size1 = FONTS[0];
    Size2 = FONTS[1];
    Size3 = FONTS[2];
    Size4 = FONTS[3];
    Size5 = FONTS[4];
    Size6 = FONTS[5];
    Size7 = FONTS[6];
    Size8 = FONTS[7];
    Size9 = FONTS[8];
    Size10= FONTS[9];
    Size11= FONTS[10];
    Size12= FONTS[11];
    Size13= FONTS[12];
    Size14= FONTS[13];
    Size15= FONTS[14];
    Size16= FONTS[15];

    Log.debug("Font generated");
  }

  /**
   Num	Ind	Siz
   1	0	0.0 + 0.5 = 0.5
   2 	1	0.5 + 0.5 = 1
   3	2	1.0 + 0.5 = 1.5
   4	3	1.5 + 0.5 = 2
   5	4	2.0 + 0.5 = 2.5
   6	5	2.5 + 0.5 = 3
   7	6	3.0 + 0.5 = 3.5
   8	7	3.5 + 0.5 = 4
   9	8	4.0 + 0.5 = 4.5
   10	9	4.5 + 0.5 = 5
   11	10	5.0 + 0.5 = 5.5
   12	11	5.5 + 0.5 = 6
   13	12	6.0 + 0.5 = 6.5
   14	13	6.5 + 0.5 = 7
   15	14	7.0 + 0.5 = 7.5
   16	15	7.5 + 0.5 = 8

   Size = (Index / 2) + 0.5
   Index = Num - 1
   */
}
