   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
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
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.data.MulticastData;


/**
 * 
 * @author jannik Diese Klasse ist ein "Wrapper" fï¿½r die PanelGraph-Klasse. Sie
 *         lï¿½sst den user zwischen 3 Graphen "wechseln": <br>
 *         - Jitter <br>
 *         - Lost Packets <br>
 *         - Measured Packet Rate <br>
 *         Intern wird nur ein Graph verï¿½ndert. Anders als die
 *         PanelGraph-Klasse, erwartet diese Klasse beim Update ein Array aus
 *         MulticastData-Objekten, aus denen sie den jeweils benï¿½tigten Wert
 *         extrahiert.
 */
@SuppressWarnings("serial")
public class ReceiverGraph extends PanelGraph {
	/**
	 * Enum zum Bestimmen des Typs der gerade vom Graphen dargestellten Werte
	 */
	public static enum valueType {
		/**
		 * Die dargestellten Daten sind Jitter-Werte
		 */
		JITTER,
		/**
		 * Die dargestellten Daten sind "lost Packets"-Werte
		 */
		LOSTPKT,
		/**
		 * Die dargestellten Daten sind "Measured Packet-Rate"-Werte
		 */
		MEASPKT
	}

	private valueType curValueType = valueType.JITTER;
	JRadioButton jitterRB, lostPktsRB, measPktRtRB;
	ButtonGroup myButtonGroup;
	Font rbFont = new Font("SansSerif", Font.PLAIN, 10);

	/**
	 * Konstruktor. Erwartet einen {@link viewController} als Parameter, der die
	 * Radiobuttons ï¿½berwacht.
	 * 
	 * @param ctrl
	 *            der {@link viewController} der ReceiverGraph-Instanz
	 */
	public ReceiverGraph(ViewController ctrl) {
		super(50, "time", "Y: ", false);
		this.setLayout(null);

		// Radiobuttons zum Umschalten zwischen den Graphen
		jitterRB = new JRadioButton("Jitter", true);
		jitterRB.addItemListener(ctrl);
		jitterRB.setActionCommand("jitter");
		jitterRB.setLocation(30, 0);
		jitterRB.setSize(50, 12);
		jitterRB.setBackground(Color.BLACK);
		jitterRB.setForeground(Color.LIGHT_GRAY);
		jitterRB.setFont(rbFont);

		lostPktsRB = new JRadioButton("Lost Packets");
		lostPktsRB.addItemListener(ctrl);
		lostPktsRB.setActionCommand("lostPackets");
		lostPktsRB.setLocation(80, 0);
		lostPktsRB.setSize(80, 12);
		lostPktsRB.setBackground(Color.BLACK);
		lostPktsRB.setForeground(Color.LIGHT_GRAY);
		lostPktsRB.setFont(rbFont);

		measPktRtRB = new JRadioButton("Measured Packet Rate");
		measPktRtRB.addItemListener(ctrl);
		measPktRtRB.setActionCommand("measuredPacketRate");
		measPktRtRB.setLocation(160, 0);
		measPktRtRB.setSize(120, 12);
		measPktRtRB.setBackground(Color.BLACK);
		measPktRtRB.setForeground(Color.LIGHT_GRAY);
		measPktRtRB.setFont(rbFont);

		// Gruppierung der Radiobuttons
		myButtonGroup = new ButtonGroup();
		myButtonGroup.add(jitterRB);
		myButtonGroup.add(lostPktsRB);
		myButtonGroup.add(measPktRtRB);

		// Radioboxen zur view adden
		this.add(jitterRB);
		this.add(lostPktsRB);
		this.add(measPktRtRB);
	}

	/**
	 * Getter fï¿½r den "Jitter"-Radiobutton
	 * 
	 * @return "Jitter"-Radiobutton-Instanz
	 */
	public JRadioButton getJitterRB() {
		return jitterRB;
	}

	/**
	 * Getter fï¿½r den "Lost Packets"-Radiobutton
	 * 
	 * @return "Lost Packets"-Radiobutton-Instanz
	 */
	public JRadioButton getLostPktsRB() {
		return lostPktsRB;
	}

	/**
	 * Getter fï¿½r den "Measured Packet Rate"-Radiobutton
	 * 
	 * @return "Measured Packet Rate"-Radiobutton-Instanz
	 */
	public JRadioButton getMeasPktRtRB() {
		return measPktRtRB;
	}

	/**
	 * Wird aufgerufen, wenn ein andere Radiobutton ausgewï¿½hlt wird
	 * 
	 * @param newValueType
	 *            der neue {@link valueType}
	 */
	public void selectionChanged(valueType newValueType) {
		this.reset();
		curValueType = newValueType;
	}

	/**
	 * Diese Methode updated den Graph, indem sie je nach ausgewï¿½hltem
	 * Radiobutton die entsprechenden Daten aus den {@link MulticastData}-Objekt
	 * extrahiert. Danach wird intern die updateGraph(int)-Methode aufgerufen.
	 * 
	 * @param mcData
	 */
	public void updateGraph(ArrayList<MulticastData> mcDataArray) {
		int newValue = 0;
		if (mcDataArray != null) {
			switch (curValueType) {
			case JITTER:
				for (MulticastData mc : mcDataArray) {
					if (mc.getJitter() >= 0) {
						newValue += mc.getJitter();
					}
				}
				break;
			case LOSTPKT:
				for (MulticastData mc : mcDataArray) {
					if (mc.getPacketLossPerSecond() >= 0) {
						newValue += mc.getPacketLossPerSecond();
					}
				}
				break;
			case MEASPKT:
				for (MulticastData mc : mcDataArray) {
					if (mc.getPacketRateMeasured() >= 0) {
						newValue += mc.getPacketRateMeasured();
					}
				}
				break;
			}
			GraphLines.setValue(mcDataArray, newValue);
		}

		this.updateGraph();
		jitterRB.repaint();
		lostPktsRB.repaint();
		measPktRtRB.repaint();
	}

	@Override
	public void reset(MulticastData mc) {
		GraphLines.deleteMC(mc);
	}
}
