   
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
 
 package dhbw.multicastor.program.model.mmrp;

import java.util.Arrays;
import java.util.Vector;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;


/**
 * Filtert aus den einkommenden Packeten die relevanten MMRP-Steuerungs- und 
 * Ethernet-Pakete und legt diese in die Queues der Consumer ab
 * 
 */
public class PcapListener implements PcapPacketHandler<Object>, Runnable{
	/** Final initialisierter MMRP-Header. Wird benötigt, um in der nextPacket()-Methode MMRP-Pakete zu bestimmen.**/
	private final MMRP mmrp = new MMRP(); 

	/** Final initialisierter Data-Header. Wird benötigt, um in der nextPacket()-Methode "unsere" Ethernet-Pakete herauszufiltern. 
	 * Diese Pakete besitzen einen eigenen Header, mit dessen Hilfe wir unsere Pakete wiedererkennen.**/
	private final DataHeader dataHeader = new DataHeader();

	/** Final initialisierter Ethernet-Header. Wird benötigt, um in der addEthernetPacket()-Methode die Zieladresse des Paketes zu bestimmen.**/
	private final Ethernet ethernetHeader = new Ethernet();

	/** Sagt aus, ob der PcapListener aktiv ist oder nicht. (Pakete captured oder nicht).**/
	public boolean isActive = false;

	/** Sagt aus, ob die Pcap des PcapListeners geschlossen wurde oder noch aktiv ist.**/
	public boolean alive = true;

	/** Pcap, auf der der PcapListener horcht.**/
	public Pcap pcap;	
	
	/** Liste mit allen Sendern und Receivern, die Pakete von dieser Schnittstelle empfangen. Wird vom MMRP-Controller
	 * gefüllt und geleert. Wenn die Liste leer ist, schläft der PcapListener.**/
	public Vector<IMmrpSenderAndReceiver> senderAndReceiver = new Vector<IMmrpSenderAndReceiver>();

	/**
	 * Zentraler Konstruktor. Jeder PcapListener ist für eine Pcap, also ein Netzwerkinterface verantwortlich.
	 * Die PcapListener werden einmalig vom MMRPController initialisiert.
	 * @param pcap Pcap, von welcher Pakete gecaptured werden sollen.
	 */
	public PcapListener(Pcap pcap){
		this.pcap = pcap;
	}

	/**
	 * Methode vom PcapPacketHandlerInterface. Einkommende Pakete kommen dekodiert in dieser Methode an. 
	 * Hier wird das Paket dann nach Ethernet- oder MMRP-Paket unterschieden und den entsprechenenden Methoden
	 * addEthernetPacket() bzw. analyzeEvents() übergeben.
	 */
	@Override
	public void nextPacket(PcapPacket packet, Object o) {

		if ((packet.hasHeader(mmrp))) {
			try {
				analyzeAttributes();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(packet.hasHeader(dataHeader)){
			try {
				addEthernetPacket(packet);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Analysiert die Attribute in einem MMRP-Paket. Wenn in einem Attribut ein LeaveAll-Event enthalten ist,
	 * wird dieses an alle Sender und Receiver weitergeleitet, andernfalls wird das Attribut nur an die 
	 * Sender und Receiver weitergeleitet, für dessen Multicast-Gruppe das Attribut bestimmt ist.
	 */
	private void analyzeAttributes() throws InterruptedException{
		for (Attribute at : mmrp.getListMes()) {
			if (at.getLeaveAll() == 0) {
				forwardEvents(at);
			} else {
				notifyLeaveAll(at);
			}
		}
	}

	/**
	 * Legt ein Attribut mit einem LeaveAll in die Consumer-Queue´s alle Sender und Receiver
	 * @param at Übergebenes Attribut mit LeaveAll-Event
	 */
	private void notifyLeaveAll(Attribute at){
		for(IMmrpSenderAndReceiver mc : senderAndReceiver){
			synchronized(mc){
				mc.getMmrpQueue().add(at);
				mc.notifyMmrpPacketQueue();
			}
		}
	}

	/**
	 * Legt ein Attribut in alle Consumer-Queue´s, deren Sender und Receiver 
	 * für dessen Multicast-Gruppe bestimmt sind
	 * @param at Übergegebenes Attribut
	 */
	private void forwardEvents(Attribute at) {
		boolean found = false;
		synchronized (senderAndReceiver) {
			for (IMmrpSenderAndReceiver mc : senderAndReceiver) {
				synchronized (mc) {
					if (Arrays.equals(at.getFirstValue(), mc.getGroupAddress())) {
						mc.getMmrpQueue().add(at);
						mc.notifyMmrpPacketQueue();
						found = true;
						break;
					}
				}
			}
			if (!found && at.getFirstValue().length == 6) {
				// setzt voraus, dass sie sortiert sind
				for (IMmrpSenderAndReceiver mc : senderAndReceiver) {
					synchronized (mc) {
						long value = mc.getGroupAddressNum() - at.getfV();
						if (value > 0 && value < at
								.getNumOfEvents()) {
							mc.getMmrpQueue().add(at);
							mc.notifyMmrpPacketQueue();
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Leitet Multicast-Ethernet-Packete an alle Receiver weiter, die für die Multicast-Adresse registriert sind,
	 * für die Adresse das Ethernet-Packet bestimmt ist.
	 * @param packet Übergebenes Ethernet-Paket
	 */
	private void addEthernetPacket(PcapPacket packet) throws InterruptedException{
		for (IMmrpSenderAndReceiver obj : senderAndReceiver) {
			synchronized(obj){
				if(obj.isReceiver()){
					MulticastReceiverMmrp receiver = (MulticastReceiverMmrp) obj;
					if(Arrays.equals(obj.getGroupAddress(), packet.getHeader(ethernetHeader).destination())){
						receiver.getEthernetPacketQueue().add(packet);
						receiver.notifyEthernetPacketQueue();
					}
				}
			}
		}

	}

	/**
	 * Capture-Methode. Solange er aktiv ist, Captured der Thread jeweils ein Paket. Wenn er deaktiviert wird,
	 * schläft der Thread.
	 */
	@Override
	public void run() {
		while(true){			
			while(isActive){
//				try{
					pcap.loop(1, this, null);
//				}catch(OutOfMemoryError e){
//					System.out.println("Try to cleanup native memory");
//					DisposableGC.getDefault().startCleanupThread();
//				}
			}
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}


}
