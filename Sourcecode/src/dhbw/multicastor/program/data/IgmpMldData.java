   
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
 
 package dhbw.multicastor.program.data;

import java.net.InetAddress;

import dhbw.multicastor.program.interfaces.IgmpMldInterface;


public class IgmpMldData extends MulticastData implements IgmpMldInterface{
	
	private InetAddress groupIp = null;  // InetAddress.getByName()
	private InetAddress sourceIp = null; // siehe drï¿½ber
	private InetAddress receivedPackId;
	private int udpPort = -1;
	private int ttl = -1;
	
	public IgmpMldData(ProtocolType type){
		this.setProtocolType(type);
	}
	
	public IgmpMldData(InetAddress groupIp, InetAddress sourceIp, int udpPort,
			int packetLength, int ttl, int packetRateDesired, boolean active,  ProtocolType typ) {
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.packetLength = packetLength;
		this.ttl = ttl;
		this.packetRateDesired = packetRateDesired;
		this.protocolType = typ;
		this.active = active;
	}

	public void resetValues(){
		int d = 0;

		ttl = d;
		packetRateDesired = d;
		packetLength = d;
		maxInterruptionTime = d;
		packetRateAvg = d;
		packetRateMeasured = d;
	//	hostID = "";	// muss man mal noch ï¿½berlegen ob man das zurï¿½cksetzten mï¿½chte
		numberOfInterruptions = d;
		averageInterruptionTime = d;
		packetLossPerSecondAvg = d;
		jitter = d;
		jitterAvg = d;
		packetCount = d;
		traffic = d;
		trafficAvg = d;
		packetSource = Source.UNDEFINED;
	}
	
	public InetAddress getGroupIp() {
		return groupIp;
	}

	public void setGroupIp(InetAddress groupIp) {
		this.groupIp = groupIp;
	}
	
	public InetAddress getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(InetAddress sourceIp) {
		this.sourceIp = sourceIp;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	@Override
	public String toString() {
		return "MulticastData [\n\nactive=" + active
				+ "\naverageInterruptionTime=" + averageInterruptionTime
				+ "\ngroupIp=" + groupIp + "\nsourceIp=" + sourceIp + "\nhostID=" + hostID + "\njitter="
				+ jitter + "\nnumberOfInterruptions=" + numberOfInterruptions
				+ "\npacketLength=" + packetLength + "\npacketLossPerSecond="
				+ packetLossPerSecond + "\npacketRateDesired="
				+ packetRateDesired + "\npacketRateMeasured="
				+ packetRateMeasured + "\nthreadID="
				+ threadID + "\nttl=" + ttl + "\ntyp=" + protocolType + "\nudpPort="
				+ udpPort 
				+ "\nactive= " + active + "]";
	}
	
	public String toStringConsole(){
			return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t" + packetRateDesired + "\t"
			       + threadID + "\t" + ttl + "\t" + packetCount + "\t" + protocolType + "\t" + getSenderID() + "\t"; 
	}
	
	@Override
	public String identify(){
		return this.protocolType + "_" + this.getSenderID() + "_" + groupIp.toString();
	}

	@Override
	public ProtocolType getType() {
		return protocolType;
	}
	
	public String getGroupIpToString(){
		return groupIp.toString().substring(1);
	}

	@Override
	public MulticastData copy() {
		IgmpMldData mc = new IgmpMldData(this.protocolType);
		mc.setGroupIp(this.groupIp);
		mc.setUdpPort(this.udpPort);
		mc.setMulticastID(this.getMulticastID());
		mc.setActive(false);
		return mc;
	}

	@Override
	public void setReceivedPackSource(Object source) {
		this.receivedPackId = (InetAddress) source;
	}

	@Override
	public Object getReceivedPackSource() {
		return this.receivedPackId;
	}
	
}
