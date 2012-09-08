   
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
 
  

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.RegistryHeaderErrors;

import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.model.ByteTools;
import dhbw.multicastor.program.model.MacAddress;
import dhbw.multicastor.program.model.MmrpAdapter;
import dhbw.multicastor.program.model.NetworkAdapter;
import dhbw.multicastor.program.model.mmrp.DataHeader;
import dhbw.multicastor.program.model.mmrp.IMmrpSenderAndReceiver;
import dhbw.multicastor.program.model.mmrp.MMRP;
import dhbw.multicastor.program.model.mmrp.MulticastReceiverMmrp;
import dhbw.multicastor.program.model.mmrp.MulticastSenderMmrp;
import dhbw.multicastor.program.model.mmrp.PcapListener;
import dhbw.multicastor.program.model.mmrp.consumer.MMRPLocalController;


public class MMRPController {

	private ArrayList<Long> ungultigAddress = new ArrayList<Long>();

	private HashMap<MmrpAdapter, PcapListenerThread> pcapAndListenerMapping = new HashMap<MmrpAdapter, PcapListenerThread>();
	private Vector<IMmrpSenderAndReceiver> mmrpSenderAndReceiverVector = new Vector<IMmrpSenderAndReceiver>();
	private static Logger logger = Logger
			.getLogger("dhbw.multicastor.program.controller.main");
	int snaplen = 64 * 1024; // Capture all packets, no trucation  
	int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
	int timeout = 200; 
	StringBuilder errbuf = new StringBuilder(); // For any error msgs 


	public MMRPController() {

		//regiestriere die notwendigen Headers
		registerHeader();
		registerHeaderData();

		List<MmrpAdapter> mmrpAdapters = this.getMmrpAdapters();

		for(MmrpAdapter mmrpAdapter : mmrpAdapters){

			Pcap pcap = this.getPcapForMacSourceId(mmrpAdapter.getMacAdress());
			PcapListenerThread pcapListenerThread = new PcapListenerThread(new PcapListener(pcap));
			pcapListenerThread.start();

			pcapAndListenerMapping.put(mmrpAdapter, pcapListenerThread);

		}
		NetworkAdapter.macInterfaces = getMmrpAdapterVector();
		
		ungultigAddress.add(ByteTools.macToLong(new MacAddress(new byte[]{01,80,(byte) 0xC2,00,00,00}).getMacAddress()));
		ungultigAddress.add(ByteTools.macToLong(new MacAddress(new byte[]{01,80,(byte) 0xC2,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}).getMacAddress()));
		ungultigAddress.add(ByteTools.macToLong(new MacAddress(new byte[]{01,00,0x5E,00,00,01}).getMacAddress()));
	}


	/**
	 * This method will only be used to first time read out all Mmrp-Interfaces
	 */
	private List<MmrpAdapter> getMmrpAdapters() {
		List<MmrpAdapter> mmrpAdapters = new ArrayList<MmrpAdapter>();

		List<PcapIf> alldevsTemp = new ArrayList<PcapIf>(); // Will be filled with NICs  


		int r = Pcap.findAllDevs(alldevsTemp, errbuf);  
		if (r != Pcap.OK || alldevsTemp.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
		} 

		PcapIf device;
		for(int i=0; i<alldevsTemp.size(); i++)
		{
			device = alldevsTemp.get(i);
			try {
				MacAddress macAddress = new MacAddress(device.getHardwareAddress());
				if(macAddress.getMacAddress() != null){
					MmrpAdapter mmrpAdapter = new MmrpAdapter(macAddress,device.getDescription());
					mmrpAdapters.add(mmrpAdapter);
				}
			} catch (IOException e) {
				logger.log(Level.FINEST, "Can not read Pcaps Mac-Adresses");
				e.printStackTrace();
			}

		}

		return mmrpAdapters;

	}



	private boolean testPcapForMmrp(Pcap pcap) {
		//@TODO need to be implemented
		// with sending MMRPPacket
		return true;
	}


	public void addMmrpSenderOrReceiverToPcapListener(IMmrpSenderAndReceiver mts, MacAddress macAdress){
		MmrpAdapter mmrpAdapter = null;
		for(MmrpAdapter temp : pcapAndListenerMapping.keySet()){
			if(temp.getMacAdress().equals(macAdress)) mmrpAdapter = temp;	
		}

		if(mmrpAdapter == null){
			logger.log(Level.FINEST, "**ERROR ** Canï¿½t find Mmrp-Adapter for Mac-Adress "+macAdress.toString());
			return;
		}

		PcapListenerThread pcapListenerThread = pcapAndListenerMapping.get(mmrpAdapter);
		
		PcapListener pcapListener = pcapListenerThread.pcapListener;
		connectNeighbour(mts);
		addToPcapListener(pcapListener.senderAndReceiver,mts);
		pcapListenerThread.setActive(true);
		
		synchronized (pcapListener) {
			pcapListener.notify();			
		}
		
	}



	public void removeMmrpSenderOrReceiverFromPcapListener(IMmrpSenderAndReceiver mts, MacAddress macAdress){
		MmrpAdapter mmrpAdapter = null;

		disconnectNeighbour(mts);

		for(MmrpAdapter a : pcapAndListenerMapping.keySet()){
			mmrpAdapter = a ;
			if(mmrpAdapter.getMacAdress().equals(macAdress)) break;	
		}		

		PcapListenerThread pcapListenerThread = pcapAndListenerMapping
				.get(mmrpAdapter);
		PcapListener pcapListener = pcapListenerThread.pcapListener;


		synchronized(pcapListener.senderAndReceiver){
			pcapListener.senderAndReceiver.remove(mts);
			System.out.println("Controller: "+mmrpSenderAndReceiverVector.size());
			System.out.println("PcapListener: "+pcapListener.senderAndReceiver.size());

			if (pcapListener.senderAndReceiver.isEmpty()) {
				pcapListenerThread.setActive(false);
				pcapListenerThread.getPcap().breakloop();
			}
		}
	}



	/**
	 * This method opens a new Pcap for a specific MMRP-Interface
	 * @param macSourceId MacAdress of an MMRP-Interface
	 * @return Returns a new initialized Pcap for a specific Mmrp Interface
	 */
	public Pcap getPcapForMacSourceId(MacAddress macSourceId){


		List<PcapIf> alldevsTemp = new ArrayList<PcapIf>(); // Will be filled with NICs  

		StringBuilder errbuf = new StringBuilder(); // For any error msgs  
		int r = Pcap.findAllDevs(alldevsTemp, errbuf);  
		if (r != Pcap.OK || alldevsTemp.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
		} 

		PcapIf device;
		for(int i=0; i<alldevsTemp.size(); i++)
		{
			device = alldevsTemp.get(i);
			try {
				MacAddress macAdress = new MacAddress(device.getHardwareAddress());
				if(macAdress.equals(macSourceId)){	

					Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

					PcapBpfProgram program = new PcapBpfProgram();
					pcap.compile(program, "not ether src "+macSourceId.toString(), 0, 0xFFFFFF00);

					pcap.setFilter(program);

					return pcap;

				}
			} catch (IOException e) {
				logger.log(Level.FINEST, "Canï¿½t read Pcaps Mac-Adresses");
				e.printStackTrace();
			}
		}

		logger.log(Level.FINEST, "No PCap found for MacSourceId: "+macSourceId);
		return null;

	}



	class PcapListenerThread extends Thread{
		PcapListener pcapListener;

		PcapListenerThread(PcapListener pcapListener){
			super(pcapListener);
			this.pcapListener = pcapListener;
		}

		void setActive (boolean active){
			pcapListener.isActive = active;

			if(active){
				synchronized (pcapListener) {
					pcapListener.notifyAll();
				}
			}
		}

		public void setPcap(Pcap pcap){
			this.pcapListener.pcap = pcap;
		}

		public Pcap getPcap(){
			return pcapListener.pcap;
		}
	}


	public static void registerHeader(){
		/**
		 * Registering the header
		 * 
		 * 
		 * */
		try {  
			JRegistry.register(MMRP.class);  

		} catch (RegistryHeaderErrors e) {  
			e.printStackTrace();  
			System.exit(1);  
		}  
	}
	public MulticastSenderMmrp createNewMulticastSenderMmrp(MMRPData m,
			Logger logger) {
		Pcap pcap = this.getPcapForMacSourceId(m.getMacSourceId());
		MulticastSenderMmrp mts = new MulticastSenderMmrp(m, logger, pcap);
		sortToTheList(mts);
		return mts;
	}

	public static void registerHeaderData(){
		/**
		 * Registering the header
		 * 
		 * 
		 * */
		try {  
			JRegistry.register(DataHeader.class);  

		} catch (RegistryHeaderErrors e) {  
			e.printStackTrace();  
			System.exit(1);  
		}  
	}


	public MulticastReceiverMmrp createNewMulticastReceiverMmrp(MMRPData m,
			Logger logger) {
		Pcap pcap = this.getPcapForMacSourceId(m.getMacSourceId());
		MulticastReceiverMmrp mts = new MulticastReceiverMmrp(m, logger, pcap);
		sortToTheList(mts);
		if(((MMRPData)m).getTimerJoinMt() > -1){ 
			this.addMmrpSenderOrReceiverToPcapListener(mts, m.getMacSourceId());
		}
		return mts;
	}


	public void deleteMulticastSenderMmrp(MMRPData m) {
		IMmrpSenderAndReceiver mc = null;
		for(IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector){
			if(mts.getMulticastID() == m.getMulticastID()){

				mc = mts;
			}
		}
		mc.destroy();
		mmrpSenderAndReceiverVector.remove(mc);
	}


	public void deleteMulticastReceiverMmrp(MMRPData m) {
		IMmrpSenderAndReceiver mc = null;
		for(IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector){
			if (mts.getMulticastID() == m.getMulticastID()) {
				if(((MMRPData)m).getTimerJoinMt() > -1) {
				this.removeMmrpSenderOrReceiverFromPcapListener(mts,
						m.getMacSourceId());
				}
				mc = mts;
			}
		}
		mc.destroy();
		mmrpSenderAndReceiverVector.remove(mc);
	}

	public Vector<MmrpAdapter> getMmrpAdapterVector() {
		Vector<MmrpAdapter> returnVector = new Vector<MmrpAdapter>();
		returnVector.addAll(this.pcapAndListenerMapping.keySet());
		return returnVector;
	}

	public Thread getSenderAndReceiver(MMRPData m, Logger logger, Modus modus){
		Thread t = null;
		if (modus == Modus.SENDER) {
			for (IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector) {
				if (mts.getMulticastID() == m.getMulticastID()) {
					mts.setAlive(true);
					this.addMmrpSenderOrReceiverToPcapListener(mts, m.getMacSourceId());
					mts.getConsumer().setThread(new Thread((Runnable) mts));
					t = new Thread(mts.getConsumer());
				}
			}
		} else {
			for (IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector) {
				if (mts.getMulticastID() == m.getMulticastID()) {
					if(!(((MMRPData)m).getTimerJoinMt() > -1)){
						this.addMmrpSenderOrReceiverToPcapListener(mts, m.getMacSourceId());
					}
					t = new Thread((Runnable) mts);
					mts.getConsumer().setThread(t);
				}
			}
		}
		return t;
	}

	public void stopMulticast(MMRPData m, Modus modus) {

		for (IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector) {
			if (mts.getMulticastID() == m.getMulticastID()) {
				if (modus.equals(Modus.RECEIVER)) {
					Queue<JPacket> queue = ((MulticastReceiverMmrp) mts).getEthernetPacketQueue();
					synchronized (queue) {
						queue.notify();
					}
					if(!(((MMRPData)m).getTimerJoinMt() > -1)){
						this.removeMmrpSenderOrReceiverFromPcapListener(mts,
								m.getMacSourceId());
					}
				} else {
					this.removeMmrpSenderOrReceiverFromPcapListener(mts, m.getMacSourceId());
				}
			}
		}
	}

	public void leaveAll(MulticastData m){
		for (IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector) {
			if (mts.getMulticastID() == m.getMulticastID()) {
				mts.leaveAll();
			}
		}
	}

	public void leave(MulticastData m) {
		for (IMmrpSenderAndReceiver mts : mmrpSenderAndReceiverVector) {
			if (mts.getMulticastID() == m.getMulticastID()) {
				mts.leaveGroup();
			}
		}
	}

	private void sortToTheList(IMmrpSenderAndReceiver newNeighbour){
		boolean added = false;
		IMmrpSenderAndReceiver neighbourNext = null;
		if (mmrpSenderAndReceiverVector.isEmpty()) {
			mmrpSenderAndReceiverVector.add(newNeighbour);
			System.out.println("mmrpSenderAndReceiverVector: "+mmrpSenderAndReceiverVector.size());
		} else {

			int index= findDeviceInterface(newNeighbour);
			if (index == -1) {
				mmrpSenderAndReceiverVector.add(newNeighbour);
				System.out.println("mmrpSenderAndReceiverVector: "+mmrpSenderAndReceiverVector.size());
			} else {
				neighbourNext = mmrpSenderAndReceiverVector.get(index);
				do {
					if (neighbourNext.getGroupAddressNum() > newNeighbour
							.getGroupAddressNum()) {
						mmrpSenderAndReceiverVector.add(index, newNeighbour);
						System.out.println("mmrpSenderAndReceiverVector: "+mmrpSenderAndReceiverVector.size());
						added = true;
						break;
					}
					// schaut, ob list(index) bereits das letzte element war
					// wahr, so wird es an der letzten Stelle hinzugefuegt
					if (mmrpSenderAndReceiverVector.size() == index + 1) {
						neighbourNext = mmrpSenderAndReceiverVector.get(index);
						mmrpSenderAndReceiverVector.add(newNeighbour);
						System.out.println("mmrpSenderAndReceiverVector: "+mmrpSenderAndReceiverVector.size());
						added = true;
						break;
					}
					index++;
					neighbourNext = mmrpSenderAndReceiverVector.get(index);
				} while (Arrays.equals(neighbourNext.getMyAddress(),
						newNeighbour.getMyAddress()));
				//es wurden alle multicasts mit demselben Deviceinterface durchsucht. Keine GroupId ist groesser.
				//hinter dem letzten multicast 
				if (!added) {
					index = mmrpSenderAndReceiverVector.indexOf(neighbourNext);
					mmrpSenderAndReceiverVector.add(index, newNeighbour);
					System.out.println("mmrpSenderAndReceiverVector: "+mmrpSenderAndReceiverVector.size());
				}
			}
		}
	}


	private void connectNeighbour(IMmrpSenderAndReceiver mts) {
		IMmrpSenderAndReceiver neighbourTemp;
		int index = mmrpSenderAndReceiverVector.indexOf(mts);
		index--;
		// prueft, ob es noch einen Vorgaenger gibt
		if (index >= 0) {
			neighbourTemp = mmrpSenderAndReceiverVector
					.get(index);
			// fuegt diesen Multicast dem vorherigen hinzu
			if (Arrays.equals(neighbourTemp.getMyAddress(), mts.getMyAddress())) {
				neighbourTemp.getConsumer().addNeighbour(mts.getConsumer());
			}

		}
		index = index + 2;
		// prueft ob noch Elemente da sind
		if (mmrpSenderAndReceiverVector.size() <= index) {
			return;
		} else {
			neighbourTemp = mmrpSenderAndReceiverVector.get(index);
			// fuegt den naechsten Mutlicast hinzu
			if (Arrays.equals(neighbourTemp.getMyAddress(), mts.getMyAddress())) {
				mts.getConsumer().addNeighbour(neighbourTemp.getConsumer());
			}
		}
	}

	private int findDeviceInterface(
			IMmrpSenderAndReceiver newNeighbour) {
		int index=0;
		for (IMmrpSenderAndReceiver mc : mmrpSenderAndReceiverVector) {
			if (!Arrays.equals(mc.getMyAddress(), newNeighbour.getMyAddress())) {
				index++;
			} else {
				return index;
			}
		}
		return -1;
	}


	private void disconnectNeighbour(IMmrpSenderAndReceiver mts) {
		int index = mmrpSenderAndReceiverVector.indexOf(mts);
		index--;
		if(index >= 0){
			IMmrpSenderAndReceiver neighbourTemp = mmrpSenderAndReceiverVector.get(index);
			if(Arrays.equals(neighbourTemp.getMyAddress(),mts.getMyAddress())){
				neighbourTemp.getConsumer().unconnect();
			}
		}
		mts.getConsumer().unconnect();
	}



	private void addToPcapListener(
			Vector<IMmrpSenderAndReceiver> senderAndReceiver,
			IMmrpSenderAndReceiver mts) {
		int index = 0;
		for(IMmrpSenderAndReceiver mc : senderAndReceiver){
			if(mc.getGroupAddressNum() > mts.getGroupAddressNum()){
				break;
			} else {
				index++;
			}
		}
		synchronized (senderAndReceiver) {
			senderAndReceiver.add(index,mts);
			System.out.println("Controller: "+mmrpSenderAndReceiverVector.size());
			System.out.println("PcapListener: "+senderAndReceiver.size());
		}			
	}
}
