package zisko.multicastor.program.view;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Abstrakte Hilfsklasse zum Laden von Schriftarten
 */
@SuppressWarnings("unused")
public abstract class MiscFont{
	private static String font_type = "";
	private static float font_size = 12;
	private static int font_style = 1;
	private static Font mc_font = null;

	/**
	 * Statischer Codeblock zum laden der Schriftart
	 */
	static{
		try {
			mc_font = Font.createFont(Font.PLAIN, MiscFont.class.getResourceAsStream("/zisko/multicastor/resources/fonts/MC.ttf"));
		} catch (FontFormatException e) {
			System.out.println("ERROR: Font format exception! Program Exit!");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("ERROR: Font File not found! Program Exit!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * Statische Funktion zum Anfordern der Standardschriftart mit Standardformatierung.
	 * @return die angeforderte Standardschrift.
	 */
	public static Font getFont(){
		font_size=11;
		font_style=0;
		return createFont();
	}
	/**
	 * Statische Funktion zum Anfordern der Standardschriftart betimmter Formatierung.
	 * @param style 0=normal, 1=bold, 2=italic, 3=bolditalic
	 * @param size die gewuehlte Schriftgrueuee
	 * @return die angeforderte Standardschrift.
	 */
	public static Font getFont(int style, float size){
		font_size=size;
		font_style=style;
		return createFont();
	}
	private static Font createFont(){
		mc_font=mc_font.deriveFont(font_size);
		mc_font=mc_font.deriveFont(font_style);
		return mc_font;
	}
}
