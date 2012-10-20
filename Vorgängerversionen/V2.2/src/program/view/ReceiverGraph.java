package program.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData;

/**
 *
 * @author jannik
 * Diese Klasse ist ein "Wrapper" f�r die PanelGraph-Klasse.
 * Sie l�sst den user zwischen 3 Graphen "wechseln":
 * <br>- Jitter
 * <br>- Lost Packets
 * <br>- Measured Packet Rate
 * <br>Intern wird nur ein Graph ver�ndert.
 * Anders als die PanelGraph-Klasse, erwartet diese Klasse beim Update ein Array aus MulticastData-Objekten,
 * aus denen sie den jeweils ben�tigten Wert extrahiert.
 */
@SuppressWarnings("serial")
public class ReceiverGraph extends PanelGraph{
	/**
	 * Enum zum Bestimmen des Typs der gerade vom Graphen
	 * dargestellten Werte
	 */
	public static enum valueType 			{
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
								  			 MEASPKT}

	private valueType		curValueType	= valueType.MEASPKT;
	JRadioButton			jitterRB,
							lostPktsRB,
							measPktRtRB;
	ButtonGroup 			myButtonGroup;
	Font					rbFont			= new Font("SansSerif", Font.PLAIN, 10); //$NON-NLS-1$

	/**
	 * Konstruktor. Erwartet einen {@link viewController} als Parameter, der die
	 * Radiobuttons �berwacht.
	 * @param ctrl der {@link viewController} der ReceiverGraph-Instanz
	 */
	public ReceiverGraph(ViewController ctrl){
		super(50, "sec", "Y: ", false); //$NON-NLS-1$ //$NON-NLS-2$
		this.setLayout(null);

//Radiobuttons zum Umschalten zwischen den Graphen
		jitterRB 	= new JRadioButton(Messages.getString("ReceiverGraph.3"), true); //$NON-NLS-1$
		jitterRB.addItemListener(ctrl);
		jitterRB.setActionCommand("jitter"); //$NON-NLS-1$
		jitterRB.setLocation(30, 0);
		jitterRB.setSize(70, 12);
		jitterRB.setBackground(Color.BLACK);
		jitterRB.setForeground(Color.LIGHT_GRAY);
		jitterRB.setFont(rbFont);

		lostPktsRB 		= new JRadioButton(Messages.getString("ReceiverGraph.5")); //$NON-NLS-1$
		lostPktsRB.addItemListener(ctrl);
		lostPktsRB.setActionCommand("lostPackets"); //$NON-NLS-1$
		lostPktsRB.setLocation(100, 0);
		lostPktsRB.setSize(120, 12);
		lostPktsRB.setBackground(Color.BLACK);
		lostPktsRB.setForeground(Color.LIGHT_GRAY);
		lostPktsRB.setFont(rbFont);

		measPktRtRB		= new JRadioButton(Messages.getString("ReceiverGraph.7")); //$NON-NLS-1$
		measPktRtRB.addItemListener(ctrl);
		measPktRtRB.setActionCommand("measuredPacketRate"); //$NON-NLS-1$
		measPktRtRB.setLocation(220, 0);
		measPktRtRB.setSize(140, 12);
		measPktRtRB.setBackground(Color.BLACK);
		measPktRtRB.setForeground(Color.LIGHT_GRAY);
		measPktRtRB.setFont(rbFont);

		//Gruppierung der Radiobuttons
		myButtonGroup = new ButtonGroup();
		myButtonGroup.add(jitterRB);
		myButtonGroup.add(lostPktsRB);
		myButtonGroup.add(measPktRtRB);

		//Radioboxen zur view adden
		this.add(jitterRB);
		this.add(lostPktsRB);
		this.add(measPktRtRB);
	}

	/**
	 * Getter f�r den "Jitter"-Radiobutton
	 * @return "Jitter"-Radiobutton-Instanz
	 */
	public JRadioButton getJitterRB() {
		return jitterRB;
	}

	/**
	 * Getter f�r den "Lost Packets"-Radiobutton
	 * @return "Lost Packets"-Radiobutton-Instanz
	 */
	public JRadioButton getLostPktsRB() {
		return lostPktsRB;
	}

	/**
	 * Getter f�r den "Measured Packet Rate"-Radiobutton
	 * @return "Measured Packet Rate"-Radiobutton-Instanz
	 */
	public JRadioButton getMeasPktRtRB() {
		return measPktRtRB;
	}

	/**
	 * Wird aufgerufen, wenn ein andere Radiobutton ausgew�hlt wird
	 * @param newValueType der neue {@link valueType}
	 */
	public void selectionChanged(valueType newValueType){
		this.reset();
		curValueType = newValueType;
	}

	/**
	 * Diese Methode updated den Graph, indem sie je nach ausgew�hltem
	 * Radiobutton die entsprechenden Daten aus den {@link MulticastData}-Objekt extrahiert.
	 * Danach wird intern die updateGraph(int)-Methode aufgerufen.
	 * @param mcData
	 */
	public void updateGraph(MulticastData[] mcDataArray, boolean repaint){
		int newValue=0;

		if(mcDataArray.length!=0){
			switch(curValueType){
				case JITTER:	for(int i=0;i<mcDataArray.length;i++)
									newValue += mcDataArray[i].getJitter();
								break;
				case LOSTPKT:	for(int i=0;i<mcDataArray.length;i++)
									newValue += mcDataArray[i].getPacketLossPerSecond();
									break;
				case MEASPKT:	for(int i=0;i<mcDataArray.length;i++)
									newValue += mcDataArray[i].getPacketRateMeasured();
									break;
			}
		}
		else		newValue = 0;

		this.updateGraph(newValue, repaint);
	}
}
