package multicastor.gmrp;

/**
 * Build and return the MMRPPacket
 */
public class MMRPPacket {

	// MultiCastor will send a MAC address as a attribute this is the reason why
	// we will need an attribute length of 6
	private static final byte attributeLength = (byte) 6;
	// MultiCastor will always send a MAC address(AttributeType = 2) to the
	// switch.
	private static final byte attributeType = (byte) 2;
	// the destination is a constant MAC address which is used for the
	// recognition by the switch
	private static final byte[] destination = { (byte) 0x01, (byte) 0x80,
			(byte) 0xc2, (byte) 0x00, (byte) 0x00, (byte) 0x20 };
	
	//TODO remove unused 
	private static final byte endmark =  		(byte) 0x00;
	private static final byte joinEmpty = 		(byte) 0x01;
	private static final byte joinIn = 			(byte) 0x02;
	private static final byte leaveEmpty = 		(byte) 0x03;
	private static final byte leaveIn = 		(byte) 0x04;
	private static final byte empty = 			(byte) 0x05;
	
	private static final byte leaveAll = 		(byte) 0x00;
	
	
	// LLC
	private static final byte[] llc = { (byte) 0x42 , (byte) 0x42 , (byte) 0x03 };
	
	// 0x0001 is the protocol number for MMRP
	private static final byte[] type = { (byte) 0x00, (byte) 0x01 };

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getEmpty(final byte[] source, final byte[] mCastGrp) {
		return buildMMRPPacket(source, mCastGrp, MMRPPacket.empty, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getIn(final byte[] source, final byte[] mCastGrp) {
		return buildMMRPPacket(source, mCastGrp, MMRPPacket.joinIn, false);
	}

	/*
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getJoinEmpty(final byte[] source,
			final byte[] mCastGrp) {
		return buildMMRPPacket(source, mCastGrp, MMRPPacket.joinEmpty, false);
	}

	/**
	 * return a joinIn
	 * 
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getJoinIn(final byte[] source, final byte[] mCastGrp) {
		return buildMMRPPacket(source, mCastGrp, MMRPPacket.joinIn, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getLeave(final byte[] source, final byte[] mCastGrp) {
		return buildMMRPPacket(source, mCastGrp, MMRPPacket.leaveAll, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getLeaveAll(final byte[] source,
			final byte[] mCastGrp) {
		return buildMMRPPacket(source, mCastGrp, MMRPPacket.leaveAll, true);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param mCastGrp
	 *            is the MAC address of the multicast group
	 * @param event
	 *            defines which MMRP event should be send
	 * @param leaveAll
	 *            defines if the message contains a leaveAll event
	 */
	private static byte[] buildMMRPPacket(final byte[] source,
			final byte[] mCastGrp, final byte event, final boolean leaveAll) {
		final byte[] mmrpPacket = new byte[30];

		System.arraycopy(destination, 0, mmrpPacket, 0, 6);
		System.arraycopy(source,	  0, mmrpPacket, 6, 6);
		mmrpPacket[12]=0x00;
		mmrpPacket[13]=0x10;
		System.arraycopy(llc, 		  0, mmrpPacket, 14, 3);
		System.arraycopy(type,		  0, mmrpPacket, 17, 2);
		mmrpPacket[19] = 0x01;
		mmrpPacket[20] = 0x08;
		mmrpPacket[21] = event;
		System.arraycopy(mCastGrp,	  0, mmrpPacket, 22, 6);
		mmrpPacket[28] = endmark;//element endmark
		mmrpPacket[29] = endmark;//gmrp endmark

		return mmrpPacket;
	}
}
