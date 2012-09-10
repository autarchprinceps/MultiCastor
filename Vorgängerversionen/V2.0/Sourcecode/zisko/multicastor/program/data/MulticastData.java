package zisko.multicastor.program.data;

import java.net.InetAddress;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Diese Bean-Klasse haellt Informationen ueber einen Multicast.
 * Objekte von dieser Klasse werden dafuer benutzt Multicast-
 * Informationen innerhalb des Programms zu verteilen.
 * 
 * !!In dieser Klasse ist keinerlei Logik implementiert!!
 */
public class MulticastData {
	
	//********************************************
	// Daten, die gehalten werden
	//********************************************
	private InetAddress groupIp = null;
	private InetAddress sourceIp = null; 
	private int udpPort = -1;
	private int packetLength = -1;
	private int ttl = -1;
	private int packetRateDesired = -1;
	private int packetRateMeasured = -1;
	private Typ typ = Typ.UNDEFINED;
	private int threadID = -1;
	private String hostID = "";
	private boolean active = false;
	private int maxInterruptionTime = -1;
	private int numberOfInterruptions = -1; 
	private int averageInterruptionTime = -1;
	private int packetLossPerSecond = -1;
	private int jitter = -1;
	private int traffic = -1;
	/** Shows if data from multiple Senders is received */
	private senderState senders = senderState.NONE;
	/** Total packets sent/received	 */
	private long packetCount = -1;
	/** Packet source program */
	private Source packetSource = Source.UNDEFINED;
	private byte[] mmrpGroupMac;
	private byte[] mmrpSourceMac;
	private long jitterAvg = -1;
	private long packetRateAvg = -1;
	private long packetLossPerSecondAvg = -1;
	private long trafficAvg = -1;
	
	private int packetLostCount = 0;
	private String randomID = "0";
	
	//********************************************
	// Eigene Datentypen
	//********************************************	
	public enum Typ {
		UNDEFINED, CONFIG, L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER
	}
	
	public enum Source {
		UNDEFINED, HIRSCHMANN, MULTICASTOR
	}
	
	public enum senderState {
		NONE, SINGLE, RECENTLY_CHANGED, MULTIPLE, NETWORK_ERROR
	}

	/**
	 * sets default values for the MulticastData object
	 */
	public void resetValues(){
		int d = 0;
		
		if(typ == Typ.L3_RECEIVER){
			ttl = d;
			packetRateDesired = d;
			packetLength = d;
		}
		
		maxInterruptionTime = d;
		packetRateAvg = d;
		packetRateMeasured = d;
		numberOfInterruptions = d;
		averageInterruptionTime = d;
		packetLossPerSecondAvg = d;
		jitter = d;
		jitterAvg = d;
		packetCount = d;
		packetLostCount = d;
		traffic = d;
		trafficAvg = d;
		packetSource = Source.UNDEFINED;
	}

	//********************************************
	// Getters und Setters
	//********************************************

	
	/**
	 * returns the multicast ip address
	 * @return returns the multicast ip address
	 */
	public InetAddress getGroupIp() {
		return groupIp;
	}

	/**
	 * sets the multicast ip address
	 * @param groupIp the new ip address
	 */
	public void setGroupIp(InetAddress groupIp) {
		this.groupIp = groupIp;
	}
	
	/**
	 * returns the network adapter
	 * @return returns the network adapter
	 */
	public InetAddress getSourceIp() {
		return sourceIp;
	}

	/**
	 * sets the network adapter
	 * @param sourceIp the network adapter to be set
	 */
	public void setSourceIp(InetAddress sourceIp) {
		this.sourceIp = sourceIp;
	}

	/**
	 * returns the udp port
	 * @return returns the udp port
	 */
	public int getUdpPort() {
		return udpPort;
	}

	/**
	 * sets the udp port
	 * @param udpPort the udp port to be set
	 */
	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	/**
	 * returns the packet length
	 * @return returns the packet length
	 */
	public int getPacketLength() {
		return packetLength;
	}

	/**
	 * sets the packet length
	 * @param packetLength the packet length to be set
	 */
	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	/**
	 * returns the time to live
	 * @return returns the time to live
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * sets the time to live
	 * @param ttl the time to live to be set
	 */
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	/**
	 * returns the desired packet rate
	 * @return returns the desired packet rate
	 */
	public int getPacketRateDesired() {
		return packetRateDesired;
	}

	/**
	 * sets the desired packet rate
	 * @param packetRateDesired the desired packet rate to be set
	 */
	public void setPacketRateDesired(int packetRateDesired) {
		this.packetRateDesired = packetRateDesired;
	}

	/**
	 * returns the measured packet rate
	 * @return returns the measured packet rate
	 */
	public int getPacketRateMeasured() {
		return packetRateMeasured;
	}

	/**
	 * sets the measured packet rate
	 * @param packetRateMeasured the measured packet rate to be set
	 */
	public void setPacketRateMeasured(int packetRateMeasured) {
		this.packetRateMeasured = packetRateMeasured;
	}

	/**
	 * returns the typ (L2/L3 Sender/Receiver)
	 * @return returns the type
	 */
	public Typ getTyp() {
		return typ;
	}

	/**
	 * sets the type (L2/L3 Receiver/Sender)
	 * @param typ the type to be set
	 */
	public void setTyp(Typ typ) {
		this.typ = typ;
	}

	/**
	 * returns the current thread id of the dataset
	 * @return returns the thread id
	 */
	public int getThreadID() {
		return threadID;
	}

	/**
	 * sets the current thread id
	 * @param threadID the current thread id to be set
	 */
	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}

	/**
	 * returns the host id associated with the dataset
	 * receiver: the host id of the sender
	 * sender: its host id
	 * @return returns the host id
	 */
	public String getHostID() {
		return hostID;
	}

	/**
	 * sets the host id
	 * @param hostID the host id to be set
	 */
	public void setHostID(String hostID) {
		this.hostID = hostID;
	}

	/**
	 * returns whether the dataset is active right now
	 * @return returns active state
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * sets the active state
	 * @param active the active state
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * returns the number of interruptions
	 * @return returns the number of interruptions
	 */
	public int getNumberOfInterruptions() {
		return numberOfInterruptions;
	}

	/**
	 * sets the number of interruptions
	 * @param numberOfInterruptions the number of interruptions to be set
	 */
	public void setNumberOfInterruptions(int numberOfInterruptions) {
		this.numberOfInterruptions = numberOfInterruptions;
	}

	/**
	 * returns the average interruption time
	 * @return returns the average interruption time
	 */
	public int getAverageInterruptionTime() {
		return averageInterruptionTime;
	}

	/**
	 * sets the average interruption time
	 * @param averageInterruptionTime the average interruption time to be set
	 */
	public void setAverageInterruptionTime(int averageInterruptionTime) {
		this.averageInterruptionTime = averageInterruptionTime;
	}

	/**
	 * returns the packet loss / sec
	 * @return returns the packet los / sec
	 */
	public int getPacketLossPerSecond() {
		return packetLossPerSecond;
	}

	/**
	 * sets the packet loss / sec
	 * @param packetLossPerSecond the packet loss / sec to be set
	 */
	public void setPacketLossPerSecond(int packetLossPerSecond) {
		this.packetLossPerSecond = packetLossPerSecond;
	}

	/**
	 * returns the jitter
	 * @return returns the jitter
	 */
	public int getJitter() {
		return jitter;
	}

	/**
	 * sets the jitter
	 * @param jitter the jitter to be set
	 */
	public void setJitter(int jitter) {
		this.jitter = jitter;
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
				+ threadID + "\nttl=" + ttl + "\ntyp=" + typ + "\nudpPort="
				+ udpPort 
				+ "\nactive= " + active + "]";
	}
	
	/**
	 * a string representation of this object - console friendly output
	 * @return console friendly toString of this object
	 */
	public String toStringConsole(){
		if (typ == Typ.L3_SENDER)
			return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t" + packetRateDesired + "\t"
			       + threadID + "\t" + ttl + "\t" + packetCount + "\t" + typ + "\t" + getSenderID() + "\t"; 
		
		return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t" + jitter + "\t" 
			+ numberOfInterruptions + "\t" + packetLossPerSecond + "\t" + packetRateDesired + "\t"
			+ packetRateMeasured + "\t" + threadID + "\t" + ttl + "\t" + packetCount + "\t" + typ + "\t" + getSenderID() + "\t" + packetSource + "\t" + senders;
	}
	
	/**
	 * return the type, senderID (hostid, random number, threadid) and mc ip address as String
	 * @return the identify string
	 */
	public String identify(){
		if(typ == Typ.L2_SENDER || typ == Typ.L2_RECEIVER)
			return this.typ + "_" + this.getSenderID() + "_" + getMmrpGroupMacAsString();
		else
			return this.typ + "_" + this.getSenderID() + "_" + getGroupIp();
	}	
	
	/**
	 * returns the average packet rate
	 * @return the average packet rate
	 */
	public long getPacketRateAvg() {
		return packetRateAvg;
	}
	
	/**
	 * sets the average packet rate
	 * @param packetRateAvg the average packet rate to be set
	 */
	public void setPacketRateAvg(long packetRateAvg) {
		this.packetRateAvg = packetRateAvg;
	}
	
	/**
	 * returns the average packet loss per sec
	 * @return the average packet loss per sec
	 */
	public long getPacketLossPerSecondAvg() {
		return packetLossPerSecondAvg;
	}
	
	/**
	 * sets the average packet loss per sec
	 * @param packetLossPerSecondAvg the average packet loss per sec to be set
	 */
	public void setPacketLossPerSecondAvg(long packetLossPerSecondAvg) {
		this.packetLossPerSecondAvg = packetLossPerSecondAvg;
	}
	
	/**
	 * returns the average jitter
	 * @return the average jitter
	 */
	public long getJitterAvg() {
		return jitterAvg;
	}
	
	/**
	 * sets the average jitter
	 * @param jitterAvg the average jitter to be set
	 */
	public void setJitterAvg(long jitterAvg) {
		this.jitterAvg = jitterAvg;
	}
	
	/**
	 * returns the current traffic
	 * @return the current traffic
	 */
	public int getTraffic() {
		return traffic;
	}
	
	/**
	 * sets the current traffic
	 * @param traffic the current traffic to be set
	 */
	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}
	
	/**
	 * returns the average traffic
	 * @return the average traffic
	 */
	public long getTrafficAvg() {
		return trafficAvg;
	}
	
	/**
	 * sets the average traffic
	 * @param trafficAvg the average traffic to be set
	 */
	public void setTrafficAvg(long trafficAvg) {
		this.trafficAvg = trafficAvg;
	}
	
	/**
	 * returns the packet count
	 * @return the packet count
	 */
	public long getPacketCount() {
		return packetCount;
	}
	
	/**
	 * sets the packet count
	 * @param packetCount the packet count to be set
	 */
	public void setPacketCount(long packetCount) {
		this.packetCount = packetCount;
	}
	/**
	 * Returns a unique SenderID consisting of the hostID, the threadID and a random number to differentiate between multiple instances
	 * @return a unique sender id
	 */
	public String getSenderID(){
		return hostID + threadID + ((randomID != null) ? "-" + randomID : "");
	}
	
	/**
	 * returns the packet source
	 * @return the packet source
	 */
	public Source getPacketSource() {
		return packetSource;
	}
	
	/**
	 * sets the packet source
	 * @param packetSource the packet source to be set
	 */
	public void setPacketSource(Source packetSource) {
		this.packetSource = packetSource;
	}
	
	/**
	 * returns the sender state
	 * @return the sender state
	 */
	public senderState getSenders() {
		return senders;
	}
	
	/**
	 * sets the sender state
	 * @param senders the sender state to be set
	 */
	public void setSenders(senderState senders) {
		this.senders = senders;
	}
	
	/**
	 * sets the maximum interruption time that is allowed
	 * @param maxInterruptionTime the maximum interruption time
	 */
	public void setMaxInterruptionTime(int maxInterruptionTime) {
		this.maxInterruptionTime = maxInterruptionTime;
	}
	
	/**
	 * returns the maximum interruption time
	 * @return the maximum interruption time
	 */
	public int getMaxInterruptionTime() {
		return maxInterruptionTime;
	}
	
	/**
	 * adds lost packets to the lost packets counter
	 * @param lost the number of packets added to the lost packets counter
	 */
	public void addLostPackets(int lost){
		this.packetLostCount += lost;
	}
	
	/**
	 * returns the lost packets
	 * @return the lost packets
	 */
	public int getLostPackets() {
		return packetLostCount;
	}
	
	/**
	 * returns the received packets counter
	 * @return the received packets
	 */
	public int getReceivedPackets() {
		return (int)packetCount;
	}
	
	/**
	 * returns the MMRP multicast MAC address 
	 * @return returns the MAC address as byte array
	 */
	public byte[] getMmrpGroupMac() {
		return mmrpGroupMac;
	}
	
	/**
	 * returns the MMRP multicast MAC address 
	 * @return returns the MAC address as String
	 */
	public String getMmrpGroupMacAsString(){
		String s = "";
		if(mmrpGroupMac != null)
			for(int i = 0; i < mmrpGroupMac.length; i++){

				String tmp = Integer.toHexString(mmrpGroupMac[i]);

				//Falls negativer Wert wird fffffXX zurueckgegeben deswegen nur XX nehmen
				if(tmp.length() > 2)
					tmp = tmp.substring(tmp.length()-2,tmp.length());
			

				if(tmp.length() == 1)
					tmp = "0"+tmp;

				s+=tmp;
				if(i != (mmrpGroupMac.length-1))
					s+= ":";
			}
		return s;
	}
	
	/**
	 * returns a MMRP MAC address as byte array
	 * @param s String reprensentation of the MAC address
	 * @return byte array MAC address
	 * @throws Exception if string is not a MAC address
	 */
	public byte[] getMMRPFromString(String s) throws Exception {
		byte[] b = new byte[6];
		String substring;
		HexBinaryAdapter h = new HexBinaryAdapter();
		
		for(int begin = 0, end, counter = 0; counter < 6 ; counter++ ){
			end = s.indexOf(":", begin);
			
			if(end != -1)
				substring = (String)s.subSequence(begin, end);
			else
				substring = (String)s.subSequence(begin, s.length());
			
			//Shouldn't happen ;)
			if(substring.length() == 1)
				substring = "0" + substring;
			
		
			b[counter] = h.unmarshal(substring)[0];
			begin = end+1;
		}
		return b;
	}
	
	/**
	 * returns a Layer2 network adapter as string
	 * @return string representation of a Layer2 network adapter
	 */
	public String getMmrpSourceMacAsString(){
		String s = "";
		for(int i = 0; i < mmrpSourceMac.length; i++){
			String tmp = Integer.toHexString((int)mmrpSourceMac[i]);
			
			//Falls negativer Wert wird fffffXX zurueckgegeben deswegen nur XX nehmen
			if(tmp.length() > 2)
				tmp = tmp.substring(tmp.length()-2,tmp.length());
			
			if(tmp.length() == 1)
				tmp = "0"+tmp;

			s+=tmp;
			if(i != (mmrpSourceMac.length-1))
				s+= ":";
		}
		return s;
	}
	
	/**
	 * sets the MMRP MAC address
	 * @param mmrpGroupMac a MMRP MAC address as byte array
	 */
	public void setMmrpGroupMac(byte[] mmrpGroupMac) {
		this.mmrpGroupMac = mmrpGroupMac;
	}
	
	/**
	 * returns Layer2 network adapter
	 * @return Layer2 network adapter
	 */
	public byte[] getMmrpSourceMac() {
		return mmrpSourceMac;
	}
	
	/**
	 * sets the Layer2 network adapter
	 * @param mmrpSourceMac layer2 network adapter as byte array
	 */
	public void setMmrpSourceMac(byte[] mmrpSourceMac) {
		this.mmrpSourceMac = mmrpSourceMac;
	}
	
	/**
	 * sets the random id. Used to differentiate between multiple instances
	 * @param randomID random id as String
	 */
	public void setRandomID(String randomID) {
		this.randomID = randomID;
	}
	
	/**
	 * returns the random id assingt to the dataset
	 * @return the random id as String
	 */
	public String getRandomID() {
		return randomID;
	}
}
