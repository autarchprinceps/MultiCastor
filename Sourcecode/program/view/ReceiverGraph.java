package zisko.multicastor.program.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData;

/**
 * 
 * @author jannik
 * Diese Klasse ist ein "Wrapper" für die PanelGraph-Klasse.
 * Sie lässt den user zwischen 3 Graphen "wechseln":
 * <br>- Jitter
 * <br>- Lost Packets
 * <br>- Measured Packet Rate
 * <br>Intern wird nur ein Graph verändert.
 * Anders als die PanelGraph-Klasse, erwartet diese Klasse beim Update ein Array aus MulticastData-Objekten,
 * aus denen sie den jeweils benötigten Wert extrahiert.
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
	
	private valueType		curValueType	= valueType.JITTER;
	JRadioButton			jitterRB,
							lostPktsRB,
							measPktRtRB;
	ButtonGroup 			myButtonGroup;
	Font					rbFont			= new Font("SansSerif", Font.PLAIN, 10);
	
	/**
	 * Konstruktor. Erwartet einen {@link viewController} als Parameter, der die
	 * Radiobuttons überwacht.
	 * @param ctrl der {@link viewController} der ReceiverGraph-Instanz
	 */
	public ReceiverGraph(ViewController ctrl){
		super(50, "time", "Y: ", false);
		this.setLayout(null);
		
//Radiobuttons zum Umschalten zwischen den Graphen
		jitterRB 		= new JRadioButton("Jitter", true);
		jitterRB.addItemListener(ctrl);
		jitterRB.setActionCommand("jitter");
		jitterRB.setLocation(30, 0);
		jitterRB.setSize(50, 12);
		jitterRB.setBackground(Color.BLACK);
		jitterRB.setForeground(Color.LIGHT_GRAY);
		jitterRB.setFont(rbFont);
		
		lostPktsRB 		= new JRadioButton("Lost Packets");
		lostPktsRB.addItemListener(ctrl);
		lostPktsRB.setActionCommand("lostPackets");
		lostPktsRB.setLocation(80, 0);
		lostPktsRB.setSize(80, 12);
		lostPktsRB.setBackground(Color.BLACK);
		lostPktsRB.setForeground(Color.LIGHT_GRAY);
		lostPktsRB.setFont(rbFont);
		
		measPktRtRB		= new JRadioButton("Measured Packet Rate");
		measPktRtRB.addItemListener(ctrl);
		measPktRtRB.setActionCommand("measuredPacketRate");
		measPktRtRB.setLocation(160, 0);
		measPktRtRB.setSize(120, 12);
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
	 * Getter für den "Jitter"-Radiobutton
	 * @return "Jitter"-Radiobutton-Instanz
	 */
	public JRadioButton getJitterRB() {
		return jitterRB;
	}

	/**
	 * Getter für den "Lost Packets"-Radiobutton
	 * @return "Lost Packets"-Radiobutton-Instanz
	 */
	public JRadioButton getLostPktsRB() {
		return lostPktsRB;
	}

	/**
	 * Getter für den "Measured Packet Rate"-Radiobutton
	 * @return "Measured Packet Rate"-Radiobutton-Instanz
	 */
	public JRadioButton getMeasPktRtRB() {
		return measPktRtRB;
	}

	/**
	 * Wird aufgerufen, wenn ein andere Radiobutton ausgewählt wird
	 * @param newValueType der neue {@link valueType}
	 */
	public void selectionChanged(valueType newValueType){
		this.reset();
		curValueType = newValueType;
	}
	
	/**
	 * Diese Methode updated den Graph, indem sie je nach ausgewähltem
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
		if(repaint&&!this.runSnake){
				jitterRB.repaint();
				lostPktsRB.repaint();
				measPktRtRB.repaint();
		}
	}
}
