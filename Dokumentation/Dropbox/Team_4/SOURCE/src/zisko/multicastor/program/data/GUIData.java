package zisko.multicastor.program.data;

/**
 * Diese Bean-Klasse haellt Informationen ueber die GUI Config. !!In dieser
 * Klasse ist keinerlei Logik implementiert!!
 */
public class GUIData {
	// ********************************************
	// Eigene Datentypen
	// ********************************************
	public enum TabState {
		invisible, selected, visible;
		@Override
		public String toString() {
			return name();
		};
	}

	public String Default_L2_GroupMac = "";
	public String Default_L2_PacketLength = "";
	public String Default_L2_PacketRateDesired = "";
	public String Default_L3_GroupIp = "";
	public String Default_L3_PacketLength = "";
	public String Default_L3_PacketRateDesired = "";
	public String Default_L3_Ttl = "";

	// [Daniel Becker]
	// This are default values for the whole programm
	// therefore we use them as public, because they
	// do not need to be protected in any form

	public String Default_L3_UdpPort = "";
	private TabState ABOUT = TabState.invisible;
	private TabState L2_RECEIVER = TabState.visible;
	private TabState L2_SENDER = TabState.visible;
	private TabState L3_RECEIVER = TabState.visible;
	private TabState L3_SENDER = TabState.visible;
	private String Language = "english";
	private TabState PLUS = TabState.visible;

	private String windowName = "MCastor 2.0";

	/**
	 * returns the tab state of ABOUT can be visible, invisible or selected
	 * 
	 * @return tab state of ABOUT Tab
	 */
	public TabState getABOUT() {
		return ABOUT;
	}

	/**
	 * returns the tab state of L2_RECEIVER can be visible, invisible or
	 * selected
	 * 
	 * @return tab state of L2_RECEIVER
	 */
	public TabState getL2_RECEIVER() {
		return L2_RECEIVER;
	}

	/**
	 * returns the tab state of L2_SENDER can be visible, invisible or selected
	 * 
	 * @return tab state of L2_SENDER
	 */
	public TabState getL2_SENDER() {
		return L2_SENDER;
	}

	/**
	 * returns the tab state of L3_RECEIVER can be visible, invisible or
	 * selected
	 * 
	 * @return tab state of L3_RECEIVER
	 */
	public TabState getL3_RECEIVER() {
		return L3_RECEIVER;
	}

	// ********************************************
	// Getters und Setters
	// ********************************************
	/**
	 * returns the tab state of L3_SENDER can be visible, invisible or selected
	 * 
	 * @return tab state of L3_SENDER
	 */
	public TabState getL3_SENDER() {
		return L3_SENDER;
	}

	/**
	 * to get current language (default: english)
	 * 
	 * @return returns the current language
	 */
	public String getLanguage() {
		return Language;
	}

	/**
	 * returns the tab state of PLUS can be visible, invisible or selected
	 * 
	 * @return tab state of PLUS Tab
	 */
	public TabState getPLUS() {
		return PLUS;
	}

	/**
	 * returns the window name
	 * 
	 * @return the window name of the MultiCastor
	 */
	public String getWindowName() {
		return windowName;
	}

	/**
	 * restores the default view of the MultiCastor
	 */
	public void resetValues() {
		L3_SENDER = TabState.visible;
		L3_RECEIVER = TabState.visible;
		L2_SENDER = TabState.visible;
		L2_RECEIVER = TabState.visible;
		PLUS = TabState.visible;
		ABOUT = TabState.invisible;
		windowName = "MCastor 2.0";
		Language = "english";
	}

	/**
	 * sets the tab state of ABOUT Tab
	 * 
	 * @param aBOUT
	 *            the tab state to be set
	 */
	public void setABOUT(final TabState aBOUT) {
		ABOUT = aBOUT;
	}

	/**
	 * sets the tab state of L2_RECEIVER
	 * 
	 * @param l2_RECEIVER
	 *            the tab state to be set
	 */
	public void setL2_RECEIVER(final TabState l2_RECEIVER) {
		L2_RECEIVER = l2_RECEIVER;
	}

	/**
	 * sets the tab state of L2_SENDER
	 * 
	 * @param l2_SENDER
	 *            the tab state to be set
	 */
	public void setL2_SENDER(final TabState l2_SENDER) {
		L2_SENDER = l2_SENDER;
	}

	/**
	 * sets the tab state of L3_RECEIVER
	 * 
	 * @param l3_RECEIVER
	 *            the tab state to be set
	 */
	public void setL3_RECEIVER(final TabState l3_RECEIVER) {
		L3_RECEIVER = l3_RECEIVER;
	}

	/**
	 * sets the tab state of L3_SENDER
	 * 
	 * @param l3_SENDER
	 *            the tab state to be set
	 */
	public void setL3_SENDER(final TabState l3_SENDER) {
		L3_SENDER = l3_SENDER;
	}

	/**
	 * to set the current language
	 * 
	 * @param language
	 *            the new language as String
	 */
	public void setLanguage(final String language) {
		Language = language;
	}

	/**
	 * sets the tab state of PLUS Tab It's not allowed to set the Plus Tab
	 * invisible
	 * 
	 * @param pLUS
	 *            the tab state to be set
	 */
	public void setPLUS(final TabState pLUS) {
		if(pLUS != TabState.invisible) {
			PLUS = pLUS;
		}
	}

	/**
	 * sets the window name
	 * 
	 * @param windowName
	 *            the new window name
	 */
	public void setWindowName(final String windowName) {
		this.windowName = windowName;
	}

	@Override
	public String toString() {
		String message = "";
		message += "L3_SENDER: " + L3_SENDER.toString() + "\n"
				+ "L3_RECEIVER: " + L3_RECEIVER.toString() + "\n"
				+ "L2_SENDER: " + L2_SENDER.toString() + "\n" + "L2_RECEIVER: "
				+ L2_RECEIVER.toString() + "\n" + "PLUS: "
				+ L2_RECEIVER.toString() + "\n" + "ABOUT: "
				+ L2_RECEIVER.toString() + "\n" + "WindowName: " + windowName
				+ "\n" + "Language: " + Language + "\n";
		return message;
	}
}
