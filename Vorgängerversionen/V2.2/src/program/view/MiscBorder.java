package program.view;


import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import program.controller.Messages;
/**
 * Hilfsklasse zum verwalten der R�nder f�r Textfelder.
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class MiscBorder extends TitledBorder {
	/**
	 * Enum welches bestimmt ob der Rahmen rot, gr�n oder neutral gezeichnet werden muss,
	 * je nach dem ob ein Korrekter Input vorliegt.
	 * @author Daniel Becker
	 *
	 */
	public enum BorderType{
		NEUTRAL, TRUE, FALSE
	}
	/**
	 * Enum welches bestimmt um welchen Rahmen es sich handelt
	 * @author Daniel Becker
	 * MMRP Hinzugefügt
	 * @author Tobias Schöneberger
	 */
	public enum BorderTitle{
		IPv4GROUP, IPv4SOURCE, MMRPGROUP, MMRPSOURCE, IPv6GROUP, IPv6SOURCE, PORT, RATE, LENGTH, TTL
	}
	private static String[] ipv4Names = {Messages.getString("MiscBorder.0"),Messages.getString("MiscBorder.1"),Messages.getString("MiscBorder.2"),Messages.getString("MiscBorder.3"), Messages.getString("MiscBorder.4"), Messages.getString("MiscBorder.5"), Messages.getString("MiscBorder.6"),Messages.getString("MiscBorder.7"), Messages.getString("MiscBorder.8"),Messages.getString("MiscBorder.9") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
	private static Vector<TitledBorder> b_neutral = new Vector<TitledBorder>();
	private static Vector<TitledBorder> b_true = new Vector<TitledBorder>();
	private static Vector<TitledBorder> b_false = new Vector<TitledBorder>();
	private static Color enabledColor=new Color(0,175,0);
	private static Color disabledColor=new Color(200,0,0);
	private static Color neutralLineColor=  new Color(240,240,240);
	private static Color neutralTextColor= Color.gray;
	private static Color lineColor=Color.lightGray;
	static Font f = MiscFont.getFont(0,12);
	private Color c = Color.black;
	/**
	 * initialisiert die 3 Vectoren welche die fertigen R�nder halten, so m�ssen zur Laufzeit keine neuen genertiert werden.
	 * Die k�nnen abgefragt werden sobald sie ben�tigt werden.
	 */
	static {
		for(int i = 0; i < ipv4Names.length; i++){
			b_neutral.add(createBorder(ipv4Names[i], neutralTextColor, neutralLineColor));
			b_true.add(createBorder(ipv4Names[i], enabledColor, enabledColor));
			b_false.add(createBorder(ipv4Names[i], disabledColor, disabledColor));
		}
	}
	/**
	 * Kontruktor welcher eine LineBorder mit einem Bestimmten Namen erstellt.
	 * @param title Der Name der neuen LineBorder.
	 */
	public MiscBorder(String title) {
		super(title);
		setBorder(new LineBorder(lineColor));
		titleFont = f;
		titleColor = c;
	}
	/**
	 * Statische Funktion zum erstellen von Lineborders.
	 * @param title Name der LineBorder.
	 * @param titleColor ZeichenFarbe der LineBorder.
	 * @param lineColor LinienFarbe der LineBorder.
	 * @return Die LineBorder mit dem bestimmten Namen.
	 */
	private static TitledBorder createBorder(String title, Color titleColor, Color lineColor){
		TitledBorder b = new TitledBorder(new LineBorder(lineColor), title, TitledBorder.LEFT, TitledBorder.TOP, f, titleColor);
		return b;
	}
	/**
	 * Statische Funktion zum Abrufen einer LineBorder
	 * @param title Name der LineBorder (Titel)
	 * @param bordertype Farbe der Border
	 * @return die erstellte LineBorder
	 */
	public static TitledBorder getBorder(BorderTitle title, BorderType bordertype){
		TitledBorder ret = null;
		switch(bordertype){
			case NEUTRAL:
				switch(title){
					case IPv4GROUP: ret = b_neutral.get(0);  break;
					case IPv4SOURCE: ret = b_neutral.get(1);  break;
					case PORT: ret = b_neutral.get(2);  break;
					case RATE: ret = b_neutral.get(3);  break;
					case LENGTH: ret = b_neutral.get(4);  break;
					case TTL: ret = b_neutral.get(5);  break;
					case IPv6GROUP: ret = b_neutral.get(6); break;
					case IPv6SOURCE: ret = b_neutral.get(7); break;
					case MMRPGROUP: ret = b_neutral.get(8); break;
					case MMRPSOURCE: ret = b_neutral.get(9);break;
				}
				break;
			case TRUE:
				switch(title){
					case IPv4GROUP: ret = b_true.get(0);  break;
					case IPv4SOURCE: ret = b_true.get(1);  break;
					case PORT: ret = b_true.get(2);  break;
					case RATE: ret = b_true.get(3);  break;
					case LENGTH: ret = b_true.get(4);  break;
					case TTL: ret = b_true.get(5);  break;
					case IPv6GROUP: ret = b_true.get(6); break;
					case IPv6SOURCE: ret = b_true.get(7); break;
					case MMRPGROUP: ret = b_true.get(8); break;
					case MMRPSOURCE: ret = b_true.get(9);break;
			}
			break;
			case FALSE:
				switch(title){
					case IPv4GROUP: ret = b_false.get(0);  break;
					case IPv4SOURCE: ret = b_false.get(1);  break;
					case PORT: ret = b_false.get(2);  break;
					case RATE: ret = b_false.get(3);  break;
					case LENGTH: ret = b_false.get(4);  break;
					case TTL: ret = b_false.get(5);  break;
					case IPv6GROUP: ret = b_false.get(6); break;
					case IPv6SOURCE: ret = b_false.get(7); break;
					case MMRPGROUP: ret = b_false.get(8); break;
					case MMRPSOURCE: ret = b_false.get(9);break;
			}
		}
		return ret;
	}

}
