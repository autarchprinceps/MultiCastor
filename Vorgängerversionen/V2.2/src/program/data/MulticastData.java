package program.data;

import java.io.IOException;
import java.net.InetAddress;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import program.model.NetworkAdapter;
import sun.nio.cs.ext.PCK;
import sun.security.action.GetLongAction;

/**
 * Diese Bean-Klasse h�llt Informationen �ber einen Multicast.
 * Objekte von dieser Klasse werden daf�r benutzt Multicast-
 * Informationen innerhalb des Programms zu verteilen.
 *
 *
 */
public class MulticastData {

	//********************************************
	// Daten, die gehalten werden
	//********************************************
	private InetAddress groupIp = null;  // InetAddress.getByName()
	private InetAddress sourceIp = null; // siehe dr�ber
	private int udpPort = -1;
	private int packetLength = -1;
	private int ttl = -1;
	private int packetRateDesired = -1; // wird versendet von jannik
	private int packetRateMeasured = -1;
	private Typ typ = Typ.UNDEFINED;
	private int threadID = -1;
	private String hostID = ""; //$NON-NLS-1$
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
	// haette ich gerne noch Ende
	// Avg Werte
	private long jitterAvg = -1;
	private long packetRateAvg = -1;
	private long packetLossPerSecondAvg = -1;
	private long trafficAvg = -1;

	//********************************************
	// Zusätzliche Daten für MMRP
	//********************************************
	private byte[] groupMac = null; //6 Bytes
	private byte[] sourceMac = null;
//	private byte[] hostMac = null;
	private PcapIf device = null;
	private Pcap pcapp = null;

	//********************************************
	// Eigene Datentypen
	//********************************************
	public enum Typ {
		UNDEFINED, SENDER_V4, RECEIVER_V4, SENDER_V6, RECEIVER_V6, SENDER_MMRP, RECEIVER_MMRP, CONFIG;
		/* folgende Methoden erleichtern die Abfragen auf Untergruppen des ENUMS
		 */
		public static boolean isSender(Typ typ){
			if(typ == SENDER_V4 || typ == SENDER_MMRP || typ == SENDER_V6){
				return true;
			}else
				return false;
		}
		public static boolean isReceiver(Typ typ){
			if(typ == RECEIVER_V4 || typ == RECEIVER_MMRP || typ == RECEIVER_V6){
				return true;
			}else
				return false;
		}
		public static boolean isIP(Typ typ){
			if(typ == RECEIVER_V4 || typ == SENDER_V4 || typ == RECEIVER_V6 || typ == SENDER_V6){
				return true;
			}else
				return false;
		}
		public static boolean isMMRP(Typ typ){
			if(typ == SENDER_MMRP || typ == RECEIVER_MMRP){
				return true;
			}else
				return false;
		}
		public static boolean isV4(Typ typ){
			if(typ == RECEIVER_V4 || typ == SENDER_V4 ){
				return true;
			}else
				return false;
		}
		public static boolean isV6(Typ typ){
			if(typ == RECEIVER_V6 || typ == SENDER_V6){
				return true;
			}else
				return false;
		}

		public String toShortString(){
			switch(this){
				case SENDER_MMRP:
				case RECEIVER_MMRP: return "MMRP"; //$NON-NLS-1$
				case SENDER_V4:
				case RECEIVER_V4: return "IPv4"; //$NON-NLS-1$
				case SENDER_V6:
				case RECEIVER_V6: return "IPv6"; //$NON-NLS-1$
				default: return null;
			}
		}


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
	}
	
	public MulticastData(InetAddress groupIp, InetAddress sourceIp, int udpPort,
			int packetLength, int ttl, int packetRateDesired, boolean active,  Typ typ) {
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.packetLength = packetLength;
		this.ttl = ttl;
		this.packetRateDesired = packetRateDesired;
		this.typ = typ;
		this.active = active;
	}
	public MulticastData(InetAddress groupIp, InetAddress sourceIp, int udpPort, boolean active, Typ typ){
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.active = active;
		this.typ = typ;
	}
	public MulticastData(InetAddress groupIp, InetAddress sourceIp, int udpPort,
			int packetLength, int ttl, int packetRateDesired,
			int packetRateMeasured, Typ typ, int threadID, String hostID,
			boolean active, int numberOfInterruptions,
			int averageInterruptionTime, int packetLossPerSecond, int jitter) {
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.packetLength = packetLength;
		this.ttl = ttl;
		this.packetRateDesired = packetRateDesired;
		this.packetRateMeasured = packetRateMeasured;
		this.typ = typ;
		this.threadID = threadID;
		this.active = active;
		this.numberOfInterruptions = numberOfInterruptions;
		this.averageInterruptionTime = averageInterruptionTime;
		this.packetLossPerSecond = packetLossPerSecond;
		this.jitter = jitter;
	}

	public void resetValues(){
		int d = 0;

		if((typ == Typ.RECEIVER_V4)||(typ == Typ.RECEIVER_V6)){
			ttl = d;
			packetRateDesired = d;
			packetLength = d;
		}

		maxInterruptionTime = d;
		packetRateAvg = d;
		packetRateMeasured = d;
	//	hostID = "";	// muss man mal noch �berlegen ob man das zur�cksetzten m�chte
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

	//********************************************
	// Getters und Setters
	//********************************************
	public InetAddress getGroupIp() {
		return groupIp;
	}

	public String getGroupAddress(){
		if(groupIp==null)
			return NetworkAdapter.byteArrayToMac(groupMac);
		else
			return groupIp.toString().substring(1);
	}

	public void setGroupIp(InetAddress groupIp) {
		this.groupIp = groupIp;
	}
	public String getSourceAddress() {
		if(groupIp==null)
			return NetworkAdapter.byteArrayToMac(sourceMac);
		else
			return sourceIp.toString().substring(1);
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

	public int getPacketLength() {
		return packetLength;
	}

	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
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

	public Typ getTyp() {
		return typ;
	}

	public void setTyp(Typ typ) {
		this.typ = typ;
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

	public boolean isActive() {
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

	@Override
	public String toString() {
		return "MulticastData [\n\nactive=" + active //$NON-NLS-1$
				+ "\naverageInterruptionTime=" + averageInterruptionTime //$NON-NLS-1$
				+ "\ngroupIp=" + groupIp + "\nsourceIp=" + sourceIp + "\nhostID=" + hostID + "\njitter=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ jitter + "\nnumberOfInterruptions=" + numberOfInterruptions //$NON-NLS-1$
				+ "\npacketLength=" + packetLength + "\npacketLossPerSecond=" //$NON-NLS-1$ //$NON-NLS-2$
				+ packetLossPerSecond + "\npacketRateDesired=" //$NON-NLS-1$
				+ packetRateDesired + "\npacketRateMeasured=" //$NON-NLS-1$
				+ packetRateMeasured + "\nthreadID=" //$NON-NLS-1$
				+ threadID + "\nttl=" + ttl + "\ntyp=" + typ + "\nudpPort=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ udpPort
				+ "\nactive= " + active + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	/**
	 * liefert GroupMac als String zurück
	 * @return liefert GroupMac als String zurück
	 */
	public String getStringGroupMac(){
		return NetworkAdapter.byteArrayToMac(groupMac);
	}


	public String toStringConsole(){
		if(Typ.isIP(typ)){
			if(		(typ == Typ.SENDER_V4)||	(typ == Typ.SENDER_V6)){
				return "Typ: " + typ + "\tID: " + getSenderID() + "\tGRP ADR: " + groupIp + "\tD RATE: " + packetRateDesired + "\t#SENT: " + packetCount + "\tPORT: " + udpPort  + "\tSRC ADR: " + sourceIp + "\tTTL: " + ttl;
				} 
			else{
				return "Typ: " + typ + "\tID: " + getSenderID() + "\tGRP ADR: " + groupIp + "\tD RATE: " + packetRateDesired+ "\t#RECEIVED: " + packetCount + "\tPORT: " + udpPort + "\tLOSS/S AVG: " + getPacketLossPerSecondAvg();
			}
		}else{ 
			if(typ==Typ.SENDER_MMRP){
				return "Typ: " + typ + "\tID: " + getSenderID() + "\tGRP ADR: " + NetworkAdapter.byteArrayToMac(groupMac)+ "\tD RATE: " + packetRateDesired + "\t#SENT: " + packetCount + "\tSRC ADR: " + NetworkAdapter.byteArrayToMac(sourceMac);
			}
			else {
				return "Typ: " + typ + "\tID: " + getSenderID() + "\tGRP ADR: " + NetworkAdapter.byteArrayToMac(groupMac) + "\tD RATE: " + packetRateDesired + "\t#RECEIVED: " + packetCount + "\tLOSS/S AVG: " + getPacketLossPerSecondAvg();			
			}
		}
	}

	public String identify(){
		if(groupIp!=null){
			return this.typ + "\t" + this.getSenderID() + "\t" + groupIp; //$NON-NLS-1$ //$NON-NLS-2$
		} else if(groupMac!=null){
			return this.typ + "\t" + this.getSenderID() + "\t" + getStringGroupMac(); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return null;
		}
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
	public byte[] getGroupMac() {
		return groupMac;
	}
	public void setGroupMac(byte[] streamMac) {
		this.groupMac = streamMac;
	}
	public byte[] getSourceMac() {
		return sourceMac;
	}
	public void setSourceMac(byte[] sourceMac) {
		this.sourceMac = sourceMac;
	}
	public PcapIf getDevice() {
		return device;
	}
	public void setDevice(PcapIf device) {
		try {
			this.sourceMac = device.getHardwareAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.device = device;
	}
	public Pcap getPcapp() {
		return pcapp;
	}
	public void setPcapp(Pcap pcapp) {
		this.pcapp = pcapp;
	}
}
