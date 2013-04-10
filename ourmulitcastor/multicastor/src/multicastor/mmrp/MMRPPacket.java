package multicastor.mmrp;

/**
 * Build and return the MMRPPacket
 */
public class MMRPPacket {

	// MultiCastor will send a MAC address as a attribute this is the reason why
	// we will need an attribute length of 6
	private static final byte attributeLength = (byte)6;
	// MultiCastor will always send a MAC address(AttributeType = 2) to the
	// switch.
	private static final byte attributeType = (byte)2;
	// the destination is a constant MAC address which is used for the
	// recognition by the switch
	private static final byte[] destination = { (byte)0x01, (byte)0x80,
			(byte)0xc2, (byte)0x00, (byte)0x00, (byte)0x20 };
	private static final int empty = 4;
	// defines the end of the packet
	private static final byte[] endmark = { (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00 };
	private static final int in = 2;
	private static final int joinEmpty = 3;
	// Set up the events for MMRP
	private static final int joinIn = 1;
	private static final int leave = 5;

	// defines if a leaveAll event will be send
	private static final byte leaveAll = (byte)0x20;
	// defines if no leaveAll event will be send
	private static final byte noLeaveAll = (byte)0x00;
	// defines how many events will be send by one packet
	private static final byte numberOfValues = (byte)1;
	private static final byte protocolVersion = (byte)1;
	// 0x88f6 is the protocol number for MMRP
	private static final byte[] type = { (byte)0x88, (byte)0xf6 };

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getEmpty(final byte[] source, final byte[] firstValue) {
		return buildMMRPPacket(source, firstValue, MMRPPacket.empty, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getIn(final byte[] source, final byte[] firstValue) {
		return buildMMRPPacket(source, firstValue, MMRPPacket.in, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getJoinEmpty(final byte[] source,
			final byte[] firstValue) {
		return buildMMRPPacket(source, firstValue, MMRPPacket.joinEmpty, false);
	}

	/**
	 * return a joinIn
	 * 
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getJoinIn(final byte[] source, final byte[] firstValue) {
		return buildMMRPPacket(source, firstValue, MMRPPacket.joinIn, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getLeave(final byte[] source, final byte[] firstValue) {
		return buildMMRPPacket(source, firstValue, MMRPPacket.leave, false);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 */
	public static byte[] getLeaveAll(final byte[] source,
			final byte[] firstValue) {
		return buildMMRPPacket(source, firstValue, MMRPPacket.leave, true);
	}

	/**
	 * @param source
	 *            is the MAC address of the device which will send the packet
	 * @param firstValue
	 *            is the MAC address of the multicast group
	 * @param event
	 *            defines which MMRP event should be send
	 * @param leaveAll
	 *            defines if the message contains a leaveAll event
	 */
	private static byte[] buildMMRPPacket(final byte[] source,
			final byte[] firstValue, final int event, final boolean leaveAll) {
		final byte[] mmrpPacket = new byte[30];

		for(int i = 0; i < 6; i++) {
			mmrpPacket[i] = MMRPPacket.destination[i];
			mmrpPacket[i + 6] = source[i];
			mmrpPacket[i + 19] = firstValue[i];
		}

		for(int i = 12; i < 14; i++) {
			mmrpPacket[i] = MMRPPacket.type[i - 12];
		}
		mmrpPacket[14] = MMRPPacket.protocolVersion;
		mmrpPacket[15] = MMRPPacket.attributeType;
		mmrpPacket[16] = MMRPPacket.attributeLength;

		if(leaveAll) {
			mmrpPacket[17] = MMRPPacket.leaveAll;
		} else {
			mmrpPacket[17] = MMRPPacket.noLeaveAll;
		}

		mmrpPacket[18] = MMRPPacket.numberOfValues;
		mmrpPacket[25] = (byte)(36 * event);

		for(int i = 26; i < 30; i++) {
			mmrpPacket[i] = MMRPPacket.endmark[i - 26];
		}

		return mmrpPacket;
	}
}
