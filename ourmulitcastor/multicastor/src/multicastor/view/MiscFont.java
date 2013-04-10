package multicastor.view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

/**
 * Abstrakte Hilfsklasse zum Laden von Schriftarten
 */
@SuppressWarnings("unused")
public abstract class MiscFont {
	private static float font_size = 12;
	private static int font_style = 1;
	private static String font_type = "";
	private static Font mc_font = null;

	/**
	 * Statischer Codeblock zum laden der Schriftart
	 */
	static {
		try {
			mc_font = Font
					.createFont(
							Font.PLAIN,
							MiscFont.class
									.getResourceAsStream("/multicastor/fonts/MC.ttf"));
		} catch(final FontFormatException e) {
			System.out.println("ERROR: Font format exception! Program Exit!");
			e.printStackTrace();
			System.exit(0);
		} catch(final IOException e) {
			System.out.println("ERROR: Font File not found! Program Exit!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Statische Funktion zum Anfordern der Standardschriftart mit
	 * Standardformatierung.
	 * 
	 * @return die angeforderte Standardschrift.
	 */
	public static Font getFont() {
		font_size = 11;
		font_style = 0;
		return createFont();
	}

	/**
	 * Statische Funktion zum Anfordern der Standardschriftart betimmter
	 * Formatierung.
	 * 
	 * @param style
	 *            0=normal, 1=bold, 2=italic, 3=bolditalic
	 * @param size
	 *            die gewuehlte Schriftgrueuee
	 * @return die angeforderte Standardschrift.
	 */
	public static Font getFont(final int style, final float size) {
		font_size = size;
		font_style = style;
		return createFont();
	}

	private static Font createFont() {
		mc_font = mc_font.deriveFont(font_size);
		mc_font = mc_font.deriveFont(font_style);
		return mc_font;
	}
}
