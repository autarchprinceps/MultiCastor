package multicastor.data;

import java.net.InetAddress;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Diese Bean-Klasse haellt Informationen ueber einen Multicast. Objekte von
 * dieser Klasse werden dafuer benutzt Multicast- Informationen innerhalb des
 * Programms zu verteilen. !!In dieser Klasse ist keinerlei Logik
 * implementiert!!
 */
public class MulticastData {

	public enum senderState {
		MULTIPLE, NETWORK_ERROR, NONE, RECENTLY_CHANGED, SINGLE
	}

	public enum Source {
		HIRSCHMANN, MULTICASTOR, UNDEFINED
	}

	// ********************************************
	// Eigene Datentypen
	// ********************************************
	public enum Typ {
		CONFIG, L2_RECEIVER, L2_SENDER, L3_RECEIVER, L3_SENDER, UNDEFINED
	}

	private boolean active = false;
	private int averageInterruptionTime = -1;
	// ********************************************
	// Daten, die gehalten werden
	// ********************************************
	private InetAddress groupIp = null;
	private String hostID = "";
	private int jitter = -1;
	private long jitterAvg = -1;
	private int maxInterruptionTime = -1;
	private byte[] mmrpGroupMac;
	private byte[] mmrpSourceMac;
	private int numberOfInterruptions = -1;
	/** Total packets sent/received */
	private long packetCount = -1;
	private int packetLength = -1;
	private int packetLossPerSecond = -1;
	private long packetLossPerSecondAvg = -1;
	private int packetLostCount = 0;
	private long packetRateAvg = -1;
	private int packetRateDesired = -1;
	private int packetRateMeasured = -1;
	/** Packet source program */
	private Source packetSource = Source.UNDEFINED;
	private String randomID = "0";
	/** Shows if data from multiple Senders is received */
	private senderState senders = senderState.NONE;
	private InetAddress sourceIp = null;
	private int threadID = -1;

	private int traffic = -1;
	private long trafficAvg = -1;

	private int ttl = -1;

	private Typ typ = Typ.UNDEFINED;

	private int udpPort = -1;

	/**
	 * adds lost packets to the lost packets counter
	 * 
	 * @param lost
	 *            the number of packets added to the lost packets counter
	 */
	public void addLostPackets(final int lost) {
		packetLostCount += lost;
	}

	// ********************************************
	// Getters und Setters
	// ********************************************

	/**
	 * returns the average interruption time
	 * 
	 * @return returns the average interruption time
	 */
	public int getAverageInterruptionTime() {
		return averageInterruptionTime;
	}

	/**
	 * returns the multicast ip address
	 * 
	 * @return returns the multicast ip address
	 */
	public InetAddress getGroupIp() {
		return groupIp;
	}

	/**
	 * returns the host id associated with the dataset receiver: the host id of
	 * the sender sender: its host id
	 * 
	 * @return returns the host id
	 */
	public String getHostID() {
		return hostID;
	}

	/**
	 * returns the jitter
	 * 
	 * @return returns the jitter
	 */
	public int getJitter() {
		return jitter;
	}

	/**
	 * returns the average jitter
	 * 
	 * @return the average jitter
	 */
	public long getJitterAvg() {
		return jitterAvg;
	}

	/**
	 * returns the lost packets
	 * 
	 * @return the lost packets
	 */
	public int getLostPackets() {
		return packetLostCount;
	}

	/**
	 * returns the maximum interruption time
	 * 
	 * @return the maximum interruption time
	 */
	public int getMaxInterruptionTime() {
		return maxInterruptionTime;
	}

	/**
	 * returns a MMRP MAC address as byte array
	 * 
	 * @param s
	 *            String reprensentation of the MAC address
	 * @return byte array MAC address
	 * @throws Exception
	 *             if string is not a MAC address
	 */
	public byte[] getMMRPFromString(final String s) throws Exception {
		final byte[] b = new byte[6];
		String substring;
		final HexBinaryAdapter h = new HexBinaryAdapter();

		for (int begin = 0, end, counter = 0; counter < 6; counter++) {
			end = s.indexOf(":", begin);

			if (end != -1) {
				substring = (String) s.subSequence(begin, end);
			} else {
				substring = (String) s.subSequence(begin, s.length());
			}

			// Shouldn't happen ;)
			if (substring.length() == 1) {
				substring = "0" + substring;
			}

			b[counter] = h.unmarshal(substring)[0];
			begin = end + 1;
		}
		return b;
	}

	/**
	 * returns the MMRP multicast MAC address
	 * 
	 * @return returns the MAC address as byte array
	 */
	public byte[] getMmrpGroupMac() {
		return mmrpGroupMac;
	}

	/**
	 * returns the MMRP multicast MAC address
	 * 
	 * @return returns the MAC address as String
	 */
	public String getMmrpGroupMacAsString() {
		String s = "";
		if (mmrpGroupMac != null) {
			for (int i = 0; i < mmrpGroupMac.length; i++) {

				String tmp = Integer.toHexString(mmrpGroupMac[i]);

				// Falls negativer Wert wird fffffXX zurueckgegeben deswegen nur
				// XX nehmen
				if (tmp.length() > 2) {
					tmp = tmp.substring(tmp.length() - 2, tmp.length());
				}

				if (tmp.length() == 1) {
					tmp = "0" + tmp;
				}

				s += tmp;
				if (i != (mmrpGroupMac.length - 1)) {
					s += ":";
				}
			}
		}
		return s;
	}

	/**
	 * returns Layer2 network adapter
	 * 
	 * @return Layer2 network adapter
	 */
	public byte[] getMmrpSourceMac() {
		return mmrpSourceMac;
	}

	/**
	 * returns a Layer2 network adapter as string
	 * 
	 * @return string representation of a Layer2 network adapter
	 */
	public String getMmrpSourceMacAsString() {
		String s = "";
		for (int i = 0; i < mmrpSourceMac.length; i++) {
			String tmp = Integer.toHexString(mmrpSourceMac[i]);

			// Falls negativer Wert wird fffffXX zurueckgegeben deswegen nur XX
			// nehmen
			if (tmp.length() > 2) {
				tmp = tmp.substring(tmp.length() - 2, tmp.length());
			}

			if (tmp.length() == 1) {
				tmp = "0" + tmp;
			}

			s += tmp;
			if (i != (mmrpSourceMac.length - 1)) {
				s += ":";
			}
		}
		return s;
	}

	/**
	 * returns the number of interruptions
	 * 
	 * @return returns the number of interruptions
	 */
	public int getNumberOfInterruptions() {
		return numberOfInterruptions;
	}

	/**
	 * returns the packet count
	 * 
	 * @return the packet count
	 */
	public long getPacketCount() {
		return packetCount;
	}

	/**
	 * returns the packet length
	 * 
	 * @return returns the packet length
	 */
	public int getPacketLength() {
		return packetLength;
	}

	/**
	 * returns the packet loss / sec
	 * 
	 * @return returns the packet los / sec
	 */
	public int getPacketLossPerSecond() {
		return packetLossPerSecond;
	}

	/**
	 * returns the average packet loss per sec
	 * 
	 * @return the average packet loss per sec
	 */
	public long getPacketLossPerSecondAvg() {
		return packetLossPerSecondAvg;
	}

	/**
	 * returns the average packet rate
	 * 
	 * @return the average packet rate
	 */
	public long getPacketRateAvg() {
		return packetRateAvg;
	}

	/**
	 * returns the desired packet rate
	 * 
	 * @return returns the desired packet rate
	 */
	public int getPacketRateDesired() {
		return packetRateDesired;
	}

	/**
	 * returns the measured packet rate
	 * 
	 * @return returns the measured packet rate
	 */
	public int getPacketRateMeasured() {
		return packetRateMeasured;
	}

	/**
	 * returns the packet source
	 * 
	 * @return the packet source
	 */
	public Source getPacketSource() {
		return packetSource;
	}

	/**
	 * returns the random id assingt to the dataset
	 * 
	 * @return the random id as String
	 */
	public String getRandomID() {
		return randomID;
	}

	/**
	 * returns the received packets counter
	 * 
	 * @return the received packets
	 */
	public int getReceivedPackets() {
		return (int) packetCount;
	}

	/**
	 * Returns a unique SenderID consisting of the hostID, the threadID and a
	 * random number to differentiate between multiple instances
	 * 
	 * @return a unique sender id
	 */
	public String getSenderID() {
		return hostID + threadID + ((randomID != null) ? "-" + randomID : "");
	}

	/**
	 * returns the sender state
	 * 
	 * @return the sender state
	 */
	public senderState getSenders() {
		return senders;
	}

	/**
	 * returns the network adapter
	 * 
	 * @return returns the network adapter
	 */
	public InetAddress getSourceIp() {
		return sourceIp;
	}

	/**
	 * returns the current thread id of the dataset
	 * 
	 * @return returns the thread id
	 */
	public int getThreadID() {
		return threadID;
	}

	/**
	 * returns the current traffic
	 * 
	 * @return the current traffic
	 */
	public int getTraffic() {
		return traffic;
	}

	/**
	 * returns the average traffic
	 * 
	 * @return the average traffic
	 */
	public long getTrafficAvg() {
		return trafficAvg;
	}

	/**
	 * returns the time to live
	 * 
	 * @return returns the time to live
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * returns the typ (L2/L3 Sender/Receiver)
	 * 
	 * @return returns the type
	 */
	public Typ getTyp() {
		return typ;
	}

	/**
	 * returns the udp port
	 * 
	 * @return returns the udp port
	 */
	public int getUdpPort() {
		return udpPort;
	}

	/**
	 * return the type, senderID (hostid, random number, threadid) and mc ip
	 * address as String
	 * 
	 * @return the identify string
	 */
	public String identify() {
		if ((typ == Typ.L2_SENDER) || (typ == Typ.L2_RECEIVER)) {
			return typ + "_" + getSenderID() + "_" + getMmrpGroupMacAsString();
		} else {
			return typ + "_" + getSenderID() + "_" + getGroupIp();
		}
	}

	/**
	 * returns whether the dataset is active right now
	 * 
	 * @return returns active state
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * sets default values for the MulticastData object
	 */
	public void resetValues() {
		final int d = 0;

		if (typ == Typ.L3_RECEIVER) {
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

	/**
	 * sets the active state
	 * 
	 * @param active
	 *            the active state
	 */
	public void setActive(final boolean active) {
		this.active = active;
	}

	/**
	 * sets the average interruption time
	 * 
	 * @param averageInterruptionTime
	 *            the average interruption time to be set
	 */
	public void setAverageInterruptionTime(final int averageInterruptionTime) {
		this.averageInterruptionTime = averageInterruptionTime;
	}

	/**
	 * sets the multicast ip address
	 * 
	 * @param groupIp
	 *            the new ip address
	 */
	public void setGroupIp(final InetAddress groupIp) {
		this.groupIp = groupIp;
	}

	/**
	 * sets the host id
	 * 
	 * @param hostID
	 *            the host id to be set
	 */
	public void setHostID(final String hostID) {
		this.hostID = hostID;
	}

	/**
	 * sets the jitter
	 * 
	 * @param jitter
	 *            the jitter to be set
	 */
	public void setJitter(final int jitter) {
		this.jitter = jitter;
	}

	/**
	 * sets the average jitter
	 * 
	 * @param jitterAvg
	 *            the average jitter to be set
	 */
	public void setJitterAvg(final long jitterAvg) {
		this.jitterAvg = jitterAvg;
	}

	/**
	 * sets the maximum interruption time that is allowed
	 * 
	 * @param maxInterruptionTime
	 *            the maximum interruption time
	 */
	public void setMaxInterruptionTime(final int maxInterruptionTime) {
		this.maxInterruptionTime = maxInterruptionTime;
	}

	/**
	 * sets the MMRP MAC address
	 * 
	 * @param mmrpGroupMac
	 *            a MMRP MAC address as byte array
	 */
	public void setMmrpGroupMac(final byte[] mmrpGroupMac) {
		this.mmrpGroupMac = mmrpGroupMac;
	}

	/**
	 * sets the Layer2 network adapter
	 * 
	 * @param mmrpSourceMac
	 *            layer2 network adapter as byte array
	 */
	public void setMmrpSourceMac(final byte[] mmrpSourceMac) {
		this.mmrpSourceMac = mmrpSourceMac;
	}

	/**
	 * sets the number of interruptions
	 * 
	 * @param numberOfInterruptions
	 *            the number of interruptions to be set
	 */
	public void setNumberOfInterruptions(final int numberOfInterruptions) {
		this.numberOfInterruptions = numberOfInterruptions;
	}

	/**
	 * sets the packet count
	 * 
	 * @param packetCount
	 *            the packet count to be set
	 */
	public void setPacketCount(final long packetCount) {
		this.packetCount = packetCount;
	}

	/**
	 * sets the packet length
	 * 
	 * @param packetLength
	 *            the packet length to be set
	 */
	public void setPacketLength(final int packetLength) {
		this.packetLength = packetLength;
	}

	/**
	 * sets the packet loss / sec
	 * 
	 * @param packetLossPerSecond
	 *            the packet loss / sec to be set
	 */
	public void setPacketLossPerSecond(final int packetLossPerSecond) {
		this.packetLossPerSecond = packetLossPerSecond;
	}

	/**
	 * sets the average packet loss per sec
	 * 
	 * @param packetLossPerSecondAvg
	 *            the average packet loss per sec to be set
	 */
	public void setPacketLossPerSecondAvg(final long packetLossPerSecondAvg) {
		this.packetLossPerSecondAvg = packetLossPerSecondAvg;
	}

	/**
	 * sets the average packet rate
	 * 
	 * @param packetRateAvg
	 *            the average packet rate to be set
	 */
	public void setPacketRateAvg(final long packetRateAvg) {
		this.packetRateAvg = packetRateAvg;
	}

	/**
	 * sets the desired packet rate
	 * 
	 * @param packetRateDesired
	 *            the desired packet rate to be set
	 */
	public void setPacketRateDesired(final int packetRateDesired) {
		this.packetRateDesired = packetRateDesired;
	}

	/**
	 * sets the measured packet rate
	 * 
	 * @param packetRateMeasured
	 *            the measured packet rate to be set
	 */
	public void setPacketRateMeasured(final int packetRateMeasured) {
		this.packetRateMeasured = packetRateMeasured;
	}

	/**
	 * sets the packet source
	 * 
	 * @param packetSource
	 *            the packet source to be set
	 */
	public void setPacketSource(final Source packetSource) {
		this.packetSource = packetSource;
	}

	/**
	 * sets the random id. Used to differentiate between multiple instances
	 * 
	 * @param randomID
	 *            random id as String
	 */
	public void setRandomID(final String randomID) {
		this.randomID = randomID;
	}

	/**
	 * sets the sender state
	 * 
	 * @param senders
	 *            the sender state to be set
	 */
	public void setSenders(final senderState senders) {
		this.senders = senders;
	}

	/**
	 * sets the network adapter
	 * 
	 * @param sourceIp
	 *            the network adapter to be set
	 */
	public void setSourceIp(final InetAddress sourceIp) {
		this.sourceIp = sourceIp;
	}

	/**
	 * sets the current thread id
	 * 
	 * @param threadID
	 *            the current thread id to be set
	 */
	public void setThreadID(final int threadID) {
		this.threadID = threadID;
	}

	/**
	 * sets the current traffic
	 * 
	 * @param traffic
	 *            the current traffic to be set
	 */
	public void setTraffic(final int traffic) {
		this.traffic = traffic;
	}

	/**
	 * sets the average traffic
	 * 
	 * @param trafficAvg
	 *            the average traffic to be set
	 */
	public void setTrafficAvg(final long trafficAvg) {
		this.trafficAvg = trafficAvg;
	}

	/**
	 * sets the time to live
	 * 
	 * @param ttl
	 *            the time to live to be set
	 */
	public void setTtl(final int ttl) {
		this.ttl = ttl;
	}

	/**
	 * sets the type (L2/L3 Receiver/Sender)
	 * 
	 * @param typ
	 *            the type to be set
	 */
	public void setTyp(final Typ typ) {
		this.typ = typ;
	}

	/**
	 * sets the udp port
	 * 
	 * @param udpPort
	 *            the udp port to be set
	 */
	public void setUdpPort(final int udpPort) {
		this.udpPort = udpPort;
	}

	@Override
	public String toString() {
		return "MulticastData [\n\nactive=" + active
				+ "\naverageInterruptionTime=" + averageInterruptionTime
				+ "\ngroupIp=" + groupIp + "\nsourceIp=" + sourceIp
				+ "\nhostID=" + hostID + "\njitter=" + jitter
				+ "\nnumberOfInterruptions=" + numberOfInterruptions
				+ "\npacketLength=" + packetLength + "\npacketLossPerSecond="
				+ packetLossPerSecond + "\npacketRateDesired="
				+ packetRateDesired + "\npacketRateMeasured="
				+ packetRateMeasured + "\nthreadID=" + threadID + "\nttl="
				+ ttl + "\ntyp=" + typ + "\nudpPort=" + udpPort + "\nactive= "
				+ active + "]";
	}

	/**
	 * a string representation of this object - console friendly output
	 * 
	 * @return console friendly toString of this object
	 */
	public String toStringConsole() {
		if (typ == Typ.L3_SENDER) {
			return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t"
					+ packetRateDesired + "\t" + threadID + "\t" + ttl + "\t"
					+ packetCount + "\t" + typ + "\t" + getSenderID() + "\t";
		}

		return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t" + jitter
				+ "\t" + numberOfInterruptions + "\t" + packetLossPerSecond
				+ "\t" + packetRateDesired + "\t" + packetRateMeasured + "\t"
				+ threadID + "\t" + ttl + "\t" + packetCount + "\t" + typ
				+ "\t" + getSenderID() + "\t" + packetSource + "\t" + senders;
	}
}
