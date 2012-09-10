package zisko.multicastor.program.data;

/**
 * Diese Bean-Klasse haellt Informationen ueber die GUI Config.
 * 
 * !!In dieser Klasse ist keinerlei Logik implementiert!!
 */
public class GUIData {	
	private TabState L3_SENDER = TabState.visible;
	private TabState L3_RECEIVER = TabState.visible;
	private TabState L2_SENDER = TabState.visible;
	private TabState L2_RECEIVER = TabState.visible;
	private TabState PLUS = TabState.visible;
	private TabState ABOUT = TabState.invisible;
	private String windowName = "MCastor 2.0";
	private String Language = "english";

	//[Daniel Becker]
	//This are default values for the whole programm
	//therefore we use them as public, because they
	//do not need to be protected in any form

	public String Default_L3_GroupIp="";
	public String Default_L3_UdpPort="";
	public String Default_L3_Ttl="";
	public String Default_L3_PacketRateDesired="";
	public String Default_L3_PacketLength="";
	public String Default_L2_GroupMac="";
	public String Default_L2_PacketRateDesired="";
	public String Default_L2_PacketLength="";

	
	//********************************************
	// Eigene Datentypen
	//********************************************	
	public enum TabState {
		visible, invisible, selected;
		public String toString() {
			return name();
		};
	}

	/**
	 * restores the default view of the MultiCastor
	 */
	public void resetValues(){
		this.L3_SENDER = TabState.visible;
		this.L3_RECEIVER = TabState.visible;
		this.L2_SENDER = TabState.visible;
		this.L2_RECEIVER = TabState.visible;
		this.PLUS = TabState.visible;
		this.ABOUT = TabState.invisible;
		this.windowName = "MCastor 2.0";
		this.Language = "english";
	}

	//********************************************
	// Getters und Setters
	//********************************************
	/**
	 * returns the tab state of L3_SENDER
	 * can be visible, invisible or selected
	 * @return tab state of L3_SENDER
	 */
	public TabState getL3_SENDER() {
		return L3_SENDER;
	}

	/**
	 * sets the tab state of L3_SENDER
	 * @param l3_SENDER the tab state to be set
	 */
	public void setL3_SENDER(TabState l3_SENDER) {
		L3_SENDER = l3_SENDER;
	}

	/**
	 * returns the tab state of L3_RECEIVER
	 * can be visible, invisible or selected
	 * @return tab state of L3_RECEIVER
	 */
	public TabState getL3_RECEIVER() {
		return L3_RECEIVER;
	}

	/**
	 * sets the tab state of L3_RECEIVER
	 * @param l3_RECEIVER the tab state to be set
	 */
	public void setL3_RECEIVER(TabState l3_RECEIVER) {
		L3_RECEIVER = l3_RECEIVER;
	}

	/**
	 * returns the tab state of L2_SENDER
	 * can be visible, invisible or selected
	 * @return tab state of L2_SENDER
	 */
	public TabState getL2_SENDER() {
		return L2_SENDER;
	}

	/**
	 * sets the tab state of L2_SENDER
	 * @param l2_SENDER the tab state to be set
	 */
	public void setL2_SENDER(TabState l2_SENDER) {
		L2_SENDER = l2_SENDER;
	}

	/**
	 * returns the tab state of L2_RECEIVER
	 * can be visible, invisible or selected
	 * @return tab state of L2_RECEIVER
	 */
	public TabState getL2_RECEIVER() {
		return L2_RECEIVER;
	}
	
	/**
	 * sets the tab state of L2_RECEIVER
	 * @param l2_RECEIVER the tab state to be set
	 */
	public void setL2_RECEIVER(TabState l2_RECEIVER) {
		L2_RECEIVER = l2_RECEIVER;
	}
	
	/**
	 * returns the tab state of PLUS
	 * can be visible, invisible or selected
	 * @return tab state of PLUS Tab
	 */
	public TabState getPLUS() {
		return PLUS;
	}


	/**
	 * sets the tab state of PLUS Tab
	 * It's not allowed to set the Plus Tab invisible
	 * @param pLUS the tab state to be set
	 */
	public void setPLUS(TabState pLUS) {
		if(pLUS!= TabState.invisible)
			PLUS = pLUS;
	}

	/**
	 * returns the tab state of ABOUT
	 * can be visible, invisible or selected
	 * @return tab state of ABOUT Tab
	 */
	public TabState getABOUT() {
		return ABOUT;
	}

	/**
	 * sets the tab state of ABOUT Tab
	 * @param aBOUT the tab state to be set
	 */
	public void setABOUT(TabState aBOUT) {
		ABOUT = aBOUT;
	}

	/**
	 * returns the window name
	 * @return the window name of the MultiCastor
	 */
	public String getWindowName() {
		return windowName;
	}

	/**
	 * sets the window name
	 * @param windowName the new window name
	 */
	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}

	/**
	 * to get current language (default: english)
	 * @return returns the current language
	 */
	public String getLanguage() {
		return Language;
	}

	/**
	 * to set the current language
	 * @param language the new language as String
	 */
	public void setLanguage(String language) {
		Language = language;
	}
	
	@Override
	public String toString() {
		String message = "";
		message += "L3_SENDER: "+this.L3_SENDER.toString()+"\n"+
				"L3_RECEIVER: "+this.L3_RECEIVER.toString()+"\n"+
				"L2_SENDER: "+this.L2_SENDER.toString()+"\n"+
				"L2_RECEIVER: "+this.L2_RECEIVER.toString()+"\n"+
				"PLUS: "+this.L2_RECEIVER.toString()+"\n"+
				"ABOUT: "+this.L2_RECEIVER.toString()+"\n"+
				"WindowName: "+this.windowName+"\n"+
				"Language: "+this.Language+"\n";
		return message;
	}
}
