   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenstr�men. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation ver�ffentlicht, 
 *	weitergeben und/oder modifizieren, gem�� Version 3 der Lizenz.
 *
 *  Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
  

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import dhbw.multicastor.program.data.MulticastData;


//import dhbw.multicastor.program.view.SnakeGimmick.SNAKE_DIRECTION;

/**
 * Zeichnet einen Grafen auf einer Flache mit einer H�he von 100 Pixeln und
 * einer variablen Breite. Der Graph selber ist 66 Pixel hoch (fest) und
 * ebenfalls variabel breit (wird bei resize gestaucht). Mittels der
 * Update-Funktion kann ein Funktionswert hinzugef�gt werden. Es werden die
 * letzten 62 Werte (Die 60 Sekunden der Skala + Ursprung + einen Wert au�erhalb
 * der Skala (�bergang zum rechten Rand)) in einem Array gespeichert. Sind 62
 * Werte erreicht, werden die ersten Werte wieder �berschrieben usw.
 * 
 * @author Jannik M�ller
 */

@SuppressWarnings("serial")
public class PanelGraph extends JPanel {
	private int maxY = 100, numberOfValues = 62;
	private Dimension panelSize = new Dimension(600, 100);
	private boolean staticScale = true;
	private int dynScaleCount = 0;

	// Beschriftungs- und Anzeigewerte Variablen
	private String lblX = "", lblY = "";
	Font graphFont = new Font("SansSerif", Font.PLAIN, 9);

	// Variablen f�r das zeichnen der Funktionswerte
	private int yCoord1 = 0, yCoord2 = 0, ursprungX = 0, ursprungY = 0;
	private double pixProX = 0.0, // Pixel, die pro X-/Y-Einheit zur verf�gung
									// stehen
			pixProY = 0.0, x = 0.0; // Zwischenspeicher f�r x-Werte an mehreren
									// Stellen

	// Variablen f�r "double Buffering"
	private final GraphicsConfiguration myGraphicConf = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();
	private BufferedImage bufImage;

	/**
	 * Einziger Konstruktor der Klasse
	 * 
	 * @param maxY
	 *            der h�chst m�gliche Y-Wert (int)
	 * @param labelOfX
	 *            Beschriftung der X-Achse (String)
	 * @param labelOfY
	 *            Beschriftung der Y-Achse (String)
	 * @param staticScale
	 *            Bestimmt ob der h�chstm�gliche Y-Wert automatisch angepasst
	 *            werden soll (false) oder nicht (true)
	 */
	public PanelGraph(int maxY, String labelOfX, String labelOfY,
			boolean staticScale) {
		// Panel Konfiguration
		setBackground(Color.BLACK);
		// setBorder(BorderFactory.createLineBorder(Color.red));

		// Initialisieren
		this.maxY = maxY;
		this.staticScale = staticScale;
		lblX = labelOfX;
		lblY = labelOfY;
		reset();
	}

	/**
	 * Gibt die aktuelle und damit bevorzugte Gr��e des Panels zur�ck
	 * 
	 * @return eine Dimension
	 */
	public Dimension getPreferredSize() {
		return panelSize;
	}

	/**
	 * Resized das Panel und veranlasst ein repaint.
	 * 
	 * @param newPZ
	 *            die neue Dimension des Pannels
	 */
	@Override
	public void resize(Dimension newPZ) {
		panelSize = newPZ;
		this.paintComponent(this.getGraphics());
	}

	/**
	 * Updated den Graphen mit einem neuen Wert. Dabei wird "x" inkrementiert.
	 * <p/>
	 * staticScale true: <br>
	 * - Funktionswerte > maxY (maximaler Y-Wert, bei Konstruktoraufruf
	 * anzugeben) werden maxY gro� <br>
	 * - Funktionswerte < 0 werden 0
	 * <p/>
	 * staticScale false: Skala wird ggf. angepasst. Dazu wird ein counter
	 * benutzt, um zu �berpr�fen, ob die Werte dauerhaft klein bleiben. Ist der
	 * Count bei einem gewissen Wert, wird die Skala wieder angepasst. MaxY
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
	public void updateGraph() {
		// Pointer erh�hen oder bei Erreichen vom letzten Wert "Umbruch" auf 0
		// if((dataPointer+1)<numberOfValues) dataPointer++;
		// else dataPointer = 0;
		//
		// //Werte < 0 sind nicht erlaubt
		// if(value<0) value = 0;
		// //Ist der maxY-Wert fest?
		// if(staticScale){
		// //Pr�fen ob Werte im "Rahmen" liegen
		// if(value>maxY) value = maxY;
		// }
		// else{
		// //Skala soll angepasst werden, wenn sich die Y-Werte ver�ndern
		// if(value > maxY){
		// maxY = value;
		// dynScaleCount = 0;
		// }
		// if(value > ((3*maxY)/4)) dynScaleCount = 0;
		// if(value < (maxY/2)) dynScaleCount++;
		//
		// if(dynScaleCount>10){
		// dynScaleCount-=5;
		// //h�chsten Y-Wert herausfinden und ihn als (3/4) maxY verwenden.
		// Minimaler Y-Wert: 10
		maxY = 10;
		int max = GraphLines.getHighestValue();
		if (max > 10) {
			maxY = (max * 4 / 3);
		}
		// }
		// }
		this.paintComponent(this.getGraphics());
	}

	/**
	 * Getter f�r den maximalen Y-Wert der Skala
	 * 
	 * @return den maximalen Y-Wert
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * Setter f�r den maximalen Y-Wert der Skala
	 * 
	 * @param maxY
	 *            der neue maximale Y-Wert
	 */
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	/**
	 * "Wrapper"-Methode f�r das eigentliche painten des Graphen (findet in
	 * paintContent(Graphics g) statt). hier wird der Double-Buffer realisiert
	 */
	@Override
	protected void paintComponent(Graphics g) {
		int height = getHeight();
		if (height != 0) {
			bufImage = myGraphicConf.createCompatibleImage(panelSize.width,
					height);
		}else{
			bufImage = myGraphicConf.createCompatibleImage(panelSize.width,
					600);
		}
		paintContent(bufImage.createGraphics());
		g.drawImage(bufImage, 0, 0, this);
	}

	/**
	 * Wird nur von der paintComponent(Graphics g) Methode verwendet. Diese
	 * Methode ist f�r das eigentliche Zeichnen des Graphen zust�ndig.
	 */
	private void paintContent(Graphics g) {

		pixProY = 60.0 / (double) maxY;

		// Variables, da m�glicherweise ein resize stattgefunden hat
		// wird die Breite jedes mal neu gesetzt
		int x2 = panelSize.width - 5, // X-Wert der letzten Y-Achse des Rasters
										// (bei 60)
		x3 = panelSize.width; // X-Wert des Rands des Graphen (und des Panels)
		int gWidth = x2 - 25; // Gesamtbreite des Graphen (links ein 25px
								// Abstand zum Rand)
		ursprungX = 25; // Daraus folgt: Der x-Wert des Ursprungs ist bei Pixel
						// 25
						// (fest, Stauchung erst ab hier)
		ursprungY = 75; // Y-Wert des Ursprungs ist bei 75 Pixeln

		// Setzen der Schrift-Einstellungen f�r den Graphen
		g.setFont(graphFont);

		// Farbe der Graphen-Linien
		g.setColor(Color.gray);

		// X-Achsen zeichnen (Raster)
		for (int i = 0; i <= 2; i++) {
			g.drawLine(22, 15 + (30 * i), x3, 15 + (30 * i));
		}

		// Y-Achsen Zeichnen (Raster)
		// Wegen ungeraden Werten die in der Summe zu sichtbaren
		// Ungenauigkeiten f�hren, hier an wichtigen Stellen so lange es geht
		// double-Werte
		int time = 0; // Zeitwert, an X-Achse aufgetragen
		x = 0.0;
		for (int i = 6; i >= 0; i--) {
			x = ((double) gWidth / 6.0) * (double) i;
			g.drawLine(x2 - (int) x, 12, x2 - (int) x, 78);

			// X-Achse beschriften
			if (time == 0)
				g.drawString(time + "", x2 - ((int) x + 2), 88);
			else
				g.drawString(time + "", x2 - ((int) x + 5), 88);

			time += 10;
		}

		// X-Achsenbezeichnung
		g.drawString(lblX, x2 - gWidth + 10, 98);

		// Y-Achse beschriften & bezeichnen
		g.drawString(lblY, 10, 9);
		g.drawString(maxY + "", 1, 19);
		g.drawString(maxY / 2 + "", 1, 49);
		g.drawString("0", 12, 79);

		for (GraphLines data : GraphLines.returnAllGraphLines()) {
			if (!data.isHighlight()) {
				drawAllGraphLines(g, x2, gWidth, data);
			}
		}
		for (GraphLines data : GraphLines.returnAllGraphLines()) {
			if (data.isHighlight()) {
				drawAllGraphLines(g, x2, gWidth, data);
			}
		}
	}

	private void drawAllGraphLines(Graphics g, int x2, int gWidth,
			GraphLines graphLines) {
		int[] data = graphLines.getData();

		// -1 because dataPointer points on the spot which can be overwritten
		int dataPointer = graphLines.getDataPointer() - 1;

		/*
		 * +++ Funktionswert zeichnen, von links (aktueller Wert) bis rechts +++
		 * Da die Standart-Koordinatenangaben ab der linken oberen Ecke
		 * unpraktisch sind, wird hier immer bezug auf die Koordinaten des
		 * Ursprungs (ursprungX, ursprungY) genommen. Da Werte wie der maximale
		 * Y-Wert und die Anzahl der Pixel in X-Richtung variabel sind und
		 * geteilte Werte wie die Anzahl der Pixel pro Y-Einheit / pro Sekunde
		 * damit nicht vorhersehbar sind, sind diese sehr ungerade. Um die
		 * Ungenauigkeit f�r die einzelnen Koordinaten so klein wie m�glich zu
		 * halten, wird m�glichst lange mit Gleitkommazahlen gerechnet.
		 */

		// Steht der dataPointer noch auf -1 (Initialwert), wurden noch keine
		// Daten empfangen
		// und der gesamte Codeblock zum Funktion zeichnen �bersprungen
		// Pixel pro X-Wert (Sekunde)
		pixProX = (double) gWidth / 60.0;
		// if(isHighlight){
		// g.setColor(new Color(192, 192, 192));
		// g.setFont(new Font("SansSerif", Font.PLAIN, 11));
		// }else{
		// g.setFont(new Font("SansSerif", Font.PLAIN, 9));
		g.setColor(graphLines.getColor());
		// }
		// 1. H�lfte der Werte zeichnen (Werte an den Stellen dataPointer bis 0)
		x = ursprungX;
		for (int i = dataPointer; i > 0; i--, x += pixProX) {
			// Wenn Wert ungleich Initialisierungswert
			if ((data[i] != Integer.MIN_VALUE)
					&& (data[i - 1] != Integer.MIN_VALUE)) {
				// Y-Koordinate des Datenwertes errechnen
				yCoord1 = (int) ((double) (data[i] * pixProY));
				yCoord2 = (int) ((double) (data[i - 1] * pixProY));
				g.drawLine((int) x, (ursprungY - yCoord1), (int) (x + pixProX),
						(ursprungY - yCoord2));
				// System.out.println(i + ". Aufruf. x= " + (int)x);
				// System.out.println("(1)Zeichne dp "+dataPointer+" & i " + i +
				// ": " + x + ", " + (ursprungY - yCoord1) + ", " + (x+pixProX)
				// + ", " + (ursprungY - yCoord2));
			} else
				break;
		}

		// Funktionswert am Ende des Arrays mit 0. Array Wert verbinden,
		// wenn beide != Initialisierungswert
		if ((data[0] != Integer.MIN_VALUE)
				&& (data[numberOfValues - 2] != Integer.MIN_VALUE)) {
			yCoord1 = (int) ((double) (data[0] * pixProY));
			yCoord2 = (int) ((double) (data[numberOfValues - 1] * pixProY));
			x = ursprungX + (dataPointer * pixProX);
			g.drawLine((int) x, (ursprungY - yCoord1), (int) (x + pixProX),
					(ursprungY - yCoord2));
		}

		// 2. H�lfte der Werte zeichnen (Werte an den Stellen numberOfValues bis
		// dataPointer)
		x = ursprungX + ((dataPointer + 1) * pixProX);
		for (int i = (numberOfValues - 1); i > (dataPointer + 1); i--, x += pixProX) {
			// Wenn Wert ungleich Initialisierungswert
			if ((data[i] != Integer.MIN_VALUE)
					&& (data[i - 1] != Integer.MIN_VALUE)) {
				// Y-Koordinate des Datenwertes errechnen
				yCoord1 = (int) ((double) (data[i] * pixProY));
				yCoord2 = (int) ((double) (data[i - 1] * pixProY));
				g.drawLine((int) x, (ursprungY - yCoord1), (int) (x + pixProX),
						(ursprungY - yCoord2));
				// System.out.println("(2)Zeichne dp "+dataPointer+" & i " + i +
				// ": " + x + ", " + (ursprungY - yCoord1) + ", " + (x+pixProX)
				// + ", " + (ursprungY - yCoord2));
			} else
				break;
		}
	} // Ende if-Abfrage ob dataPointer!=-1

	/**
	 * Initialisiert das data-Array, indem es jedem Feld Integer.MIN_VALUE
	 * zuweist.
	 */
	public void reset() {
		GraphLines.resetAll();
	}

	public void reset(MulticastData mc) {
		GraphLines.deleteMC(mc);
	}
}