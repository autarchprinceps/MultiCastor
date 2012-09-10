   
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
 
  

import dhbw.multicastor.program.interfaces.MMRPInterface;
import dhbw.multicastor.program.model.ByteTools;
import dhbw.multicastor.program.model.MacAddress;

public class MMRPData extends MulticastData implements MMRPInterface{
	
	private MacAddress macGroupId;
	private long macGroupIdnum;
	private MacAddress macSourceId;
	private MacAddress receivedPacketId;
	
	private int timerJoinMt = 10;
	

	public MMRPData(ProtocolType type){
		this.setProtocolType(type);
	}
	
	public MacAddress getMacGroupId() {
		return macGroupId;
	}
	public void setMacGroupId(MacAddress macGroupId) {
		this.macGroupId = macGroupId;
		this.setMacGroupIdnum(macGroupId);
	}
	public MacAddress getMacSourceId() {
		return macSourceId;
	}
	public void setMacSourceId(MacAddress macSourceId) {
		this.macSourceId = macSourceId;
	}
	
	@Override
	public String toString() {
		return "MulticastData [\n\nactive=" + active
				+ "\naverageInterruptionTime=" + averageInterruptionTime
				+ "\nmacGroupId=" + macGroupId + "\nmacSourceId=" + macSourceId + "\nhostID=" + hostID + "\njitter="
				+ jitter + "\nnumberOfInterruptions=" + numberOfInterruptions
				+ "\npacketLength=" + packetLength + "\npacketLossPerSecond="
				+ packetLossPerSecond + "\npacketRateDesired="
				+ packetRateDesired + "\npacketRateMeasured="
				+ packetRateMeasured + "\nthreadID="
				+ threadID + "\ntyp=" + protocolType
				+ "\nactive= " + active + "]";
	}
	
	public String toStringConsole(){
			return macGroupId + "\t" + "\t" + macSourceId + "\t" + packetRateDesired + "\t"
			       + threadID + "\t" + packetCount + "\t" + protocolType + "\t" + getSenderID() + "\t"; 
	}
	
	@Override
	public String identify(){
		return this.protocolType + "_" + this.getSenderID() + "_" + macGroupId.toString();
	}

	@Override
	public void resetValues() {
		int d = 0;
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

	@Override
	public ProtocolType getType() {
		return protocolType;
	}

	@Override
	public MulticastData copy() {
		MMRPData mc = new MMRPData(this.protocolType);
		mc.setMacGroupId(this.macGroupId);
		mc.setMacSourceId(this.macSourceId);
		mc.setMulticastID(this.getMulticastID());
		mc.setActive(false);
		return mc;
	}
	
	public int getTimerJoinMt() {
		return timerJoinMt;
	}

	public void setTimerJoinMt(int timerJoinMt) {
		this.timerJoinMt = timerJoinMt;
	}

	@Override
	public void setReceivedPackSource(Object source) {
		this.receivedPacketId =(MacAddress) source;
	}

	@Override
	public Object getReceivedPackSource() {
		return receivedPacketId;
	}
	
	public long getMacGroupIdnum() {
		return macGroupIdnum;
	}

	public void setMacGroupIdnum(long macGroupIdnum) {
		this.macGroupIdnum = macGroupIdnum;
	}
	
	public void setMacGroupIdnum(MacAddress macGroupId) {
		this.macGroupIdnum = ByteTools.macToLong(macGroupId.getMacAddress());
	}

	
}
