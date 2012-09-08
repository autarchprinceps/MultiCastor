   
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
 
  

import java.util.UUID;

/**
 * Diese Bean-Klasse hï¿½llt Informationen ï¿½ber einen Multicast.
 * Objekte von dieser Klasse werden dafï¿½r benutzt Multicast-
 * Informationen innerhalb des Programms zu verteilen.
 * 
 * !!In dieser Klasse ist keinerlei Logik implementiert!!
 */
public abstract class MulticastData {
	
	//********************************************
	// Daten, die gehalten werden
	//********************************************

	private String color = "000000";
	protected int packetLength = -1;
	protected int packetRateDesired = -1; // wird versendet von jannik
	protected int packetRateMeasured = -1;
	protected ProtocolType protocolType = ProtocolType.UNDEFINED;
	
	private static int counter = 0;
	protected int threadID = -1;
	protected String hostID = "";
	
	protected boolean active = false;
	protected boolean graph = false;

	protected int maxInterruptionTime = -1;
	protected int numberOfInterruptions = -1; 
	protected int averageInterruptionTime = -1;
	protected int packetLossPerSecond = -1;
	protected int jitter = -1;
	protected int traffic = -1;
	/** Shows if data from multiple Senders is received */
	private senderState senders = senderState.NONE;
	/** Total packets sent/received	 */
	protected long packetCount = -1;
	/** Packet source program */
	protected Source packetSource = Source.UNDEFINED;
	// haette ich gerne noch Ende
	// Avg Werte
	protected long jitterAvg = -1;
	protected long packetRateAvg = -1;
	protected long packetLossPerSecondAvg = -1;
	protected long trafficAvg = -1;
	private UUID multicastID = null;
	
	//********************************************
	// Eigene Datentypen
	//********************************************	

	public enum ProtocolType {
		IGMP, MMRP, MLD, UNDEFINED, CONFIG
	}
	
	public enum Source {
		UNDEFINED, HIRSCHMANN, MULTICASTOR
	}
	
	public enum senderState {
		NONE, SINGLE, RECENTLY_CHANGED, MULTIPLE
	}
	
	//********************************************
	// Constructors
	//********************************************
	public MulticastData(){
		counter++;
		setThreadID(counter);
	}

	//********************************************
	// Getters und Setters
	//********************************************
	public abstract ProtocolType getType();

	public int getPacketLength() {
		return packetLength;
	}

	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	public int getPacketRateDesired() {
		return packetRateDesired;
	}

	public void setPacketRateDesired(int packetRateDesired) {
		this.packetRateDesired = packetRateDesired;
	}

	public int getPacketRateMeasured() {
		return packetRateMeasured;
	}

	public void setPacketRateMeasured(int packetRateMeasured) {
		this.packetRateMeasured = packetRateMeasured;
	}

	public ProtocolType getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(ProtocolType typ) {
		this.protocolType = typ;
	}

	public int getThreadID() {
		return threadID;
	}

	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}

	public String getHostID() {
		return hostID;
	}

	public void setHostID(String hostID) {
		this.hostID = hostID;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getNumberOfInterruptions() {
		return numberOfInterruptions;
	}

	public void setNumberOfInterruptions(int numberOfInterruptions) {
		this.numberOfInterruptions = numberOfInterruptions;
	}

	public int getAverageInterruptionTime() {
		return averageInterruptionTime;
	}

	public void setAverageInterruptionTime(int averageInterruptionTime) {
		this.averageInterruptionTime = averageInterruptionTime;
	}

	public int getPacketLossPerSecond() {
		return packetLossPerSecond;
	}

	public void setPacketLossPerSecond(int packetLossPerSecond) {
		this.packetLossPerSecond = packetLossPerSecond;
	}

	public int getJitter() {
		return jitter;
	}

	public void setJitter(int jitter) {
		this.jitter = jitter;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public long getPacketRateAvg() {
		return packetRateAvg;
	}
	public void setPacketRateAvg(long packetRateAvg) {
		this.packetRateAvg = packetRateAvg;
	}
	public long getPacketLossPerSecondAvg() {
		return packetLossPerSecondAvg;
	}
	public void setPacketLossPerSecondAvg(long packetLossPerSecondAvg) {
		this.packetLossPerSecondAvg = packetLossPerSecondAvg;
	}
	public long getJitterAvg() {
		return jitterAvg;
	}
	public void setJitterAvg(long jitterAvg) {
		this.jitterAvg = jitterAvg;
	}
	public int getTraffic() {
		return traffic;
	}
	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}
	public long getTrafficAvg() {
		return trafficAvg;
	}
	public void setTrafficAvg(long trafficAvg) {
		this.trafficAvg = trafficAvg;
	}
	public long getPacketCount() {
		return packetCount;
	}
	public void setPacketCount(long packetCount) {
		this.packetCount = packetCount;
	}
	/**
	 * Returns a unique SenderID consisting of the hostID and the threadID
	 * @return
	 */
	public String getSenderID(){
		// TODO change to a unique ID
		return hostID + threadID;
	}
	public Source getPacketSource() {
		return packetSource;
	}
	public void setPacketSource(Source packetSource) {
		this.packetSource = packetSource;
	}
	public senderState getSenders() {
		return senders;
	}
	public void setSenders(senderState senders) {
		this.senders = senders;
	}
	public void setMaxInterruptionTime(int maxInterruptionTime) {
		this.maxInterruptionTime = maxInterruptionTime;
	}
	public int getMaxInterruptionTime() {
		return maxInterruptionTime;
	}
	
	public boolean isGraph() {
		return graph;
	}

	public void setGraph(boolean graph) {
		this.graph = graph;
	}
	
	abstract public String toStringConsole();
	abstract public String toString() ;
	abstract public String identify();

	public abstract void resetValues();
	
	public abstract MulticastData copy();

	public UUID getMulticastID() {
		return multicastID;
	}

	public void setMulticastID(UUID multicastID) {
		this.multicastID = multicastID;
	}
	
	public abstract void setReceivedPackSource(Object source);
	public abstract Object getReceivedPackSource();
}
