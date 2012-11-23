package zisko.multicastor.program.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.view.SnakeGimmick.SNAKE_DIRECTION;

/**
 * Zeichnet einen Grafen auf einer Flache mit einer Huehe von 100 Pixeln und
 * einer variablen Breite. Der Graph selber ist 66 Pixel hoch (fest) und
 * ebenfalls variabel breit (wird bei resize gestaucht). Mittels der
 * Update-Funktion kann ein Funktionswert hinzugefuegt werden. Es werden die
 * letzten 62 Werte (Die 60 Sekunden der Skala + Ursprung + einen Wert
 * auueerhalb der Skala (uebergang zum rechten Rand)) in einem Array
 * gespeichert. Sind 62 Werte erreicht, werden die ersten Werte wieder
 * ueberschrieben usw.
 */

@SuppressWarnings("serial")
public class PanelGraph extends JPanel {
	public Boolean runSnake = false;
	Font graphFont = new Font("SansSerif", Font.PLAIN, 9);
	protected LanguageManager lang;
	private int actualValue = 0;
	private BufferedImage bufImage;

	private final int[] data;
	private int dynScaleCount = 0;
	// Beschriftungs- und Anzeigewerte Variablen
	private String lblX = "", lblY = "";

	private int maxY = 100;
	private final int numberOfValues = 62;
	private int dataPointer = 0;
	// Variablen fuer "double Buffering"
	private final GraphicsConfiguration myGraphicConf = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();

	// snake-gimmick variablen
	private SnakeGimmick mySG = null;
	private Dimension panelSize = new Dimension(600, 100);

	private double pixProX = 0.0, // Pixel, die pro X-/Y-Einheit zur verfuegung
									// stehen
			pixProY = 0.0, x = 0.0; // Zwischenspeicher fuer x-Werte an mehreren
									// Stellen
	private boolean staticScale = true;

	// Variablen fuer das zeichnen der Funktionswerte
	private int yCoord1 = 0, yCoord2 = 0, ursprungX = 0, ursprungY = 0;

	/**
	 * Einziger Konstruktor der Klasse
	 * 
	 * @param maxY
	 *            der huechst muegliche Y-Wert (int)
	 * @param labelOfX
	 *            Beschriftung der X-Achse (String)
	 * @param labelOfY
	 *            Beschriftung der Y-Achse (String)
	 * @param staticScale
	 *            Bestimmt ob der huechstmuegliche Y-Wert automatisch angepasst
	 *            werden soll (false) oder nicht (true)
	 */
	public PanelGraph(final int maxY, final String labelOfX,
			final String labelOfY, final boolean staticScale) {
		lang = LanguageManager.getInstance();
		// Panel Konfiguration
		setBackground(Color.BLACK);
		// setBorder(BorderFactory.createLineBorder(Color.red));

		// Initialisieren
		data = new int[numberOfValues]; // Daten der letzten Minute + jetzt
										// (Sekunde 0-60) speicherbar
		this.maxY = maxY;
		this.staticScale = staticScale;
		lblX = labelOfX;
		lblY = labelOfY;
		reset();
	}

	/**
	 * Getter fuer den maximalen Y-Wert der Skala
	 * 
	 * @return den maximalen Y-Wert
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * Gibt die aktuelle und damit bevorzugte Grueuee des Panels zurueck
	 * 
	 * @return eine Dimension
	 */
	@Override
	public Dimension getPreferredSize() {
		return panelSize;
	}

	/**
	 * Bewegt die Snake in eine der 4 Himmelsrichtungen
	 * 
	 * @param d
	 *            die Richtung
	 * @return der Status der Snake<br>
	 *         1: Apfel gefunden<br>
	 *         0: nichts passiert<br>
	 *         -1:Snake ist gecrashed
	 */
	public int moveSnake(final SnakeGimmick.SNAKE_DIRECTION d) {
		final int retV = mySG.moveSnake(d);
		if(retV == -1) {
			mySG.initSnake();
		}
		return retV;
	}

	/**
	 * Macht dasselbe wie moveSnake, aber macht zusuetzlich ein viewUpdate
	 * 
	 * @param d
	 */
	public void moveSnakeAndUpdateView(final SNAKE_DIRECTION d) {
		if(runSnake) {
			moveSnake(d);
			paintComponent(getGraphics());
		}
	}

	public void reloadLanguage() {
		lblX = lang.getProperty("graph.sec");
		lblY = lang.getProperty("graph.packetsPerSec");
	}

	/**
	 * Initialisiert das data-Array, indem es jedem Feld Integer.MIN_VALUE
	 * zuweist.
	 */
	public void reset() {
		for(int i = 0; i < numberOfValues; i++) {
			data[i] = Integer.MIN_VALUE;
		}
		dynScaleCount = 0;
	}

	/**
	 * Resized das Panel und veranlasst ein repaint.
	 * 
	 * @param newPZ
	 *            die neue Dimension des Pannels
	 */
	@Override
	public void resize(final Dimension newPZ) {
		panelSize = newPZ;
		paintComponent(getGraphics());
	}

	public void setLblX(final String lblX) {
		this.lblX = lblX;
	}

	public void setLblY(final String lblY) {
		this.lblY = lblY;
	}

	/**
	 * Setter fuer den maximalen Y-Wert der Skala
	 * 
	 * @param maxY
	 *            der neue maximale Y-Wert
	 */
	public void setMaxY(final int maxY) {
		this.maxY = maxY;
	}

	/**
	 * Aktiviert oder deaktiviert das Snake-Gimmick.
	 * 
	 * @param active
	 */
	public void snake(final Boolean active) {
		if(active) {
			mySG = new SnakeGimmick();
		}
		runSnake = active;
		if(!active) {
			mySG = null;
		}
	}

	/**
	 * Updated den Graphen mit einem neuen Wert. Dabei wird "x" inkrementiert.
	 * <p/>
	 * staticScale true: <br>
	 * - Funktionswerte > maxY (maximaler Y-Wert, bei Konstruktoraufruf
	 * anzugeben) werden maxY groue <br>
	 * - Funktionswerte < 0 werden 0
	 * <p/>
	 * staticScale false: Skala wird ggf. angepasst. Dazu wird ein counter
	 * benutzt, um zu ueberpruefen, ob die Werte dauerhaft klein bleiben. Ist
	 * der Count bei einem gewissen Wert, wird die Skala wieder angepasst. MaxY
	 * sinkt nicht unter 10. <br>
	 * - value > maximaler Y-Wert: max. Y-Wert wird gleich value <br>
	 * - value > (3/4)*max. Y-Wert: counter==0 <br>
	 * - value < (1/2)*max. Y-Wert: counter++
	 * 
	 * @param value
	 *            value der neue Y-Wert
	 * @param repaint
	 *            bestimmt, ob ein Repaint des Panels erfolgt
	 */
	public void updateGraph(int value, final boolean repaint) {
		// Pointer erhuehen oder bei Erreichen vom letzten Wert "Umbruch" auf 0
		if((dataPointer + 1) < numberOfValues) {
			dataPointer++;
		} else {
			dataPointer = 0;
		}

		// Werte < 0 sind nicht erlaubt
		if(value < 0) {
			value = 0;
		}
		// Ist der maxY-Wert fest?
		if(staticScale) {
			// Pruefen ob Werte im "Rahmen" liegen
			if(value > maxY) {
				value = maxY;
			}
		} else {
			// Skala soll angepasst werden, wenn sich die Y-Werte veruendern
			if(value > maxY) {
				maxY = value;
				dynScaleCount = 0;
			}
			if(value > ((3 * maxY) / 4)) {
				dynScaleCount = 0;
			}
			if(value < (maxY / 2)) {
				dynScaleCount++;
			}

			if(dynScaleCount > 10) {
				dynScaleCount -= 5;
				// huechsten Y-Wert herausfinden und ihn als (3/4) maxY
				// verwenden. Minimaler Y-Wert: 10
				maxY = 10;
				for(final int element : data) {
					if(maxY < ((element * 4) / 3)) {
						maxY = ((element * 4) / 3);
					}
				}
			}
		}

		data[dataPointer] = value;
		if(repaint) {
			paintComponent(getGraphics());
		}
	}

	/**
	 * "Wrapper"-Methode fuer das eigentliche painten des Graphen (findet in
	 * paintContent(Graphics g) statt). hier wird der Double-Buffer realisiert
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		bufImage = myGraphicConf.createCompatibleImage(panelSize.width,
				getHeight());
		paintContent(bufImage.createGraphics());
		g.drawImage(bufImage, 0, 0, this);
	}

	/**
	 * Wird nur von der paintComponent(Graphics g) Methode verwendet. Diese
	 * Methode ist fuer das eigentliche Zeichnen des Graphen zustuendig.
	 */
	private void paintContent(final Graphics g) {

		pixProY = 60.0 / maxY;

		// Variables, da mueglicherweise ein resize stattgefunden hat
		// wird die Breite jedes mal neu gesetzt
		final int x2 = panelSize.width - 5, // X-Wert der letzten Y-Achse des
											// Rasters
		// (bei 60)
		x3 = panelSize.width; // X-Wert des Rands des Graphen (und des Panels)
		final int gWidth = x2 - 25; // Gesamtbreite des Graphen (links ein 25px
		// Abstand zum Rand)
		ursprungX = 25; // Daraus folgt: Der x-Wert des Ursprungs ist bei Pixel
						// 25
						// (fest, Stauchung erst ab hier)
		ursprungY = 75; // Y-Wert des Ursprungs ist bei 75 Pixeln

		// Setzen der Schrift-Einstellungen fuer den Graphen
		g.setFont(graphFont);

		// Farbe der Graphen-Linien
		g.setColor(Color.gray);

		// X-Achsen zeichnen (Raster)
		for(int i = 0; i <= 2; i++) {
			g.drawLine(22, 15 + (30 * i), x3, 15 + (30 * i));
		}

		// Y-Achsen Zeichnen (Raster)
		// Wegen ungeraden Werten die in der Summe zu sichtbaren
		// Ungenauigkeiten fuehren, hier an wichtigen Stellen so lange es geht
		// double-Werte
		int time = 0; // Zeitwert, an X-Achse aufgetragen
		x = 0.0;
		for(int i = 6; i >= 0; i--) {
			x = (gWidth / 6.0) * i;
			g.drawLine(x2 - (int)x, 12, x2 - (int)x, 78);

			// X-Achse beschriften
			if(time == 0) {
				g.drawString(time + "", x2 - ((int)x + 2), 88);
			} else {
				g.drawString(time + "", x2 - ((int)x + 5), 88);
			}

			time += 10;
		}

		// X-Achsenbezeichnung
		g.drawString(lblX, (x2 - gWidth) + 10, 98);

		// Y-Achse beschriften & bezeichnen
		g.drawString(lblY, 10, 9);
		g.drawString(maxY + "", 1, 19);
		g.drawString((maxY / 2) + "", 1, 49);
		g.drawString("0", 12, 79);

		// Anzeige rechts oben in der Ecke
		// Zeigt den aktuellen Wert in der rechten oberen Ecke, wenn der Panel
		// nicht zu klein ist
		// oder im Datenarray an der Stelle dataPointer nicht noch der
		// Initialisierungswert steht
		// (In dem Fall wird 0 als aktuellen Wert gesetzt)
		// Zu klein wuere das Panel bei
		// der ungefuehren lblY-String - Breite + Platzhalter und dem aktuellen
		// Wert
		if(data[dataPointer] != Integer.MIN_VALUE) {
			actualValue = data[dataPointer];
		}
		if(((lblY.length() * 5) + 35) < x2) {
			g.drawString(
					lang.getProperty("graph.current") + ": " + actualValue,
					(x2 - 70), 9);
		}

		/*
		 * +++ Funktionswert zeichnen, von links (aktueller Wert) bis rechts +++
		 * Da die Standart-Koordinatenangaben ab der linken oberen Ecke
		 * unpraktisch sind, wird hier immer bezug auf die Koordinaten des
		 * Ursprungs (ursprungX, ursprungY) genommen. Da Werte wie der maximale
		 * Y-Wert und die Anzahl der Pixel in X-Richtung variabel sind und
		 * geteilte Werte wie die Anzahl der Pixel pro Y-Einheit / pro Sekunde
		 * damit nicht vorhersehbar sind, sind diese sehr ungerade. Um die
		 * Ungenauigkeit fuer die einzelnen Koordinaten so klein wie mueglich zu
		 * halten, wird mueglichst lange mit Gleitkommazahlen gerechnet.
		 */

		// Steht der dataPointer noch auf -1 (Initialwert), wurden noch keine
		// Daten empfangen
		// und der gesamte Codeblock zum Funktion zeichnen uebersprungen
		if(dataPointer != -1) {
			// Pixel pro X-Wert (Sekunde)
			pixProX = gWidth / 60.0;
			g.setColor(Color.GREEN);

			// 1. Huelfte der Werte zeichnen (Werte an den Stellen dataPointer
			// bis 0)
			x = ursprungX;
			for(int i = dataPointer; i > 0; i--, x += pixProX) {
				// Wenn Wert ungleich Initialisierungswert
				if((data[i] != Integer.MIN_VALUE)
						&& (data[i - 1] != Integer.MIN_VALUE)) {
					// Y-Koordinate des Datenwertes errechnen
					yCoord1 = (int)(data[i] * pixProY);
					yCoord2 = (int)(data[i - 1] * pixProY);
					g.drawLine((int)x, (ursprungY - yCoord1),
							(int)(x + pixProX), (ursprungY - yCoord2));
					// System.out.println(i + ". Aufruf. x= " + (int)x);
					// System.out.println("(1)Zeichne dp "+dataPointer+" & i " +
					// i + ": " + x + ", " + (ursprungY - yCoord1) + ", " +
					// (x+pixProX) + ", " + (ursprungY - yCoord2));
				} else {
					break;
				}
			}

			// Funktionswert am Ende des Arrays mit 0. Array Wert verbinden,
			// wenn beide != Initialisierungswert
			if((data[0] != Integer.MIN_VALUE)
					&& (data[numberOfValues - 2] != Integer.MIN_VALUE)) {
				yCoord1 = (int)(data[0] * pixProY);
				yCoord2 = (int)(data[numberOfValues - 1] * pixProY);
				x = ursprungX + (dataPointer * pixProX);
				g.drawLine((int)x, (ursprungY - yCoord1), (int)(x + pixProX),
						(ursprungY - yCoord2));
			}

			// 2. Huelfte der Werte zeichnen (Werte an den Stellen
			// numberOfValues bis dataPointer)
			x = ursprungX + ((dataPointer + 1) * pixProX);
			for(int i = (numberOfValues - 1); i > (dataPointer + 1); i--, x += pixProX) {
				// Wenn Wert ungleich Initialisierungswert
				if((data[i] != Integer.MIN_VALUE)
						&& (data[i - 1] != Integer.MIN_VALUE)) {
					// Y-Koordinate des Datenwertes errechnen
					yCoord1 = (int)(data[i] * pixProY);
					yCoord2 = (int)(data[i - 1] * pixProY);
					g.drawLine((int)x, (ursprungY - yCoord1),
							(int)(x + pixProX), (ursprungY - yCoord2));
					// System.out.println("(2)Zeichne dp "+dataPointer+" & i " +
					// i + ": " + x + ", " + (ursprungY - yCoord1) + ", " +
					// (x+pixProX) + ", " + (ursprungY - yCoord2));
				} else {
					break;
				}
			}
		} // Ende if-Abfrage ob dataPointer!=-1

		// Ist das Gimmick aktiviert? wenn ja, zeichne es
		if(runSnake) {
			mySG.drawSnake(g);
		}
	}

}