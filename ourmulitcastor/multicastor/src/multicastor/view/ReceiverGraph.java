package multicastor.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import multicastor.controller.ViewController;
import multicastor.data.MulticastData;

/**
 * Diese Klasse ist ein "Wrapper" fuer die PanelGraph-Klasse. Sie luesst den
 * user zwischen 3 Graphen "wechseln": <br>
 * - Jitter <br>
 * - Lost Packets <br>
 * - Measured Packet Rate <br>
 * Intern wird nur ein Graph veruendert. Anders als die PanelGraph-Klasse,
 * erwartet diese Klasse beim Update ein Array aus MulticastData-Objekten, aus
 * denen sie den jeweils benuetigten Wert extrahiert.
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

	JRadioButton jitterRB, lostPktsRB, measPktRtRB;
	ButtonGroup myButtonGroup;
	Font rbFont = new Font("SansSerif", Font.PLAIN, 10);
	private valueType curValueType = valueType.JITTER;

	/**
	 * Konstruktor. Erwartet einen {@link multicastor.controller.viewController}
	 * als Parameter, der die Radiobuttons ueberwacht.
	 * 
	 * @param ctrl
	 *            der {@link controller.viewController} der
	 *            ReceiverGraph-Instanz
	 */
	public ReceiverGraph(final ViewController ctrl) {
		super(50, "", "", false);
		setLayout(null);

		// Radiobuttons zum Umschalten zwischen den Graphen
		jitterRB = new JRadioButton("", true);
		jitterRB.addItemListener(ctrl);
		jitterRB.setActionCommand("jitter");
		jitterRB.setLocation(30, 0);
		jitterRB.setSize(50, 12);
		jitterRB.setBackground(Color.BLACK);
		jitterRB.setForeground(Color.LIGHT_GRAY);
		jitterRB.setFont(rbFont);

		lostPktsRB = new JRadioButton();
		lostPktsRB.addItemListener(ctrl);
		lostPktsRB.setActionCommand("lostPackets");
		lostPktsRB.setLocation(80, 0);
		lostPktsRB.setSize(80, 12);
		lostPktsRB.setBackground(Color.BLACK);
		lostPktsRB.setForeground(Color.LIGHT_GRAY);
		lostPktsRB.setFont(rbFont);

		measPktRtRB = new JRadioButton();
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

		reloadLanguage();
	}

	/**
	 * Getter fuer den "Jitter"-Radiobutton
	 * 
	 * @return "Jitter"-Radiobutton-Instanz
	 */
	public JRadioButton getJitterRB() {
		return jitterRB;
	}

	/**
	 * Getter fuer den "Lost Packets"-Radiobutton
	 * 
	 * @return "Lost Packets"-Radiobutton-Instanz
	 */
	public JRadioButton getLostPktsRB() {
		return lostPktsRB;
	}

	/**
	 * Getter fuer den "Measured Packet Rate"-Radiobutton
	 * 
	 * @return "Measured Packet Rate"-Radiobutton-Instanz
	 */
	public JRadioButton getMeasPktRtRB() {
		return measPktRtRB;
	}

	/**
	 * Diese Methode aktualisiert den textuellen Inhalt, wenn die Sprache
	 * geaendert wird.
	 */
	@Override
	public void reloadLanguage() {
		setLblX(lang.getProperty("graph.time"));
		setLblY(lang.getProperty("graph.y"));
		jitterRB.setText(lang.getProperty("graph.jitter"));
		lostPktsRB.setText(lang.getProperty("graph.lostPackets"));
		measPktRtRB.setText(lang.getProperty("graph.measuredPacketRate"));
	}

	/**
	 * Wird aufgerufen, wenn ein andere Radiobutton ausgewuehlt wird
	 * 
	 * @param newValueType
	 *            der neue {@link valueType}
	 */
	public void selectionChanged(final valueType newValueType) {
		reset();
		curValueType = newValueType;
	}

	/**
	 * Diese Methode updated den Graph, indem sie je nach ausgewuehltem
	 * Radiobutton die entsprechenden Daten aus den
	 * {@link multicastor.data.MulticastData}-Objekt extrahiert. Danach wird
	 * intern die updateGraph(int)-Methode aufgerufen.
	 * 
	 * @param mcDataArray
	 * @param repaint
	 *            Gibt an, ob der Graph neugezeichnet werden soll oder nicht.
	 */
	public void updateGraph(final MulticastData[] mcDataArray,
			final boolean repaint) {
		int newValue = 0;
		if (mcDataArray.length != 0) {
			switch (curValueType) {
			case JITTER:
				for (final MulticastData element : mcDataArray) {
					newValue += element.getJitter();
				}
				break;
			case LOSTPKT:
				for (final MulticastData element : mcDataArray) {
					newValue += element.getPacketLossPerSecond();
				}
				break;
			case MEASPKT:
				for (final MulticastData element : mcDataArray) {
					newValue += element.getPacketRateMeasured();
				}
				break;
			}
		} else {
			newValue = 0;
		}
		this.updateGraph(newValue, repaint);
		if (repaint) {
			jitterRB.repaint();
			lostPktsRB.repaint();
			measPktRtRB.repaint();
		}
	}
}
