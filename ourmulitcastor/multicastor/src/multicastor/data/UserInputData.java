package multicastor.data;

import java.util.ArrayList;

import multicastor.data.MulticastData.Typ;

public class UserInputData {
	private String activeButton = "";
	private ArrayList<Integer> columnOrder; // muss nicht gespeichert werden ->
	@SuppressWarnings("unused")
	private String columnOrderString = "";
	// lueuft ueber columnOrderString
	private ArrayList<Integer> columnVisibility; // siehe columnVisiblityString
	@SuppressWarnings("unused")
	private String columnVisibilityString = "";
	private String groupadress = "";
	private String isAutoSaveEnabled = ""; // neu
	private String networkInterface = "0";
	private String packetlength = "";
	private String packetrate = "";
	private String port = "";
	/* ATTENTION: the next three attributes cannot be deleted */
	@SuppressWarnings("unused")
	private String selectedRows = "";
	private String selectedTab = Typ.L3_SENDER.toString();
	private String ttl = "";

	/**
	 * creates the user input data object with default values. Restores the
	 * default table view before.
	 */
	public UserInputData() {
		resetColumns();
	}

	/**
	 * lets you change the order of the colums
	 * 
	 * @param from
	 *            id of first column
	 * @param to
	 *            id of target column
	 */
	public void changeColumns(final int from, final int to) {
		final Integer bufferTo = columnVisibility.get(to);
		final Integer bufferFrom = columnVisibility.get(from);
		Integer orderTo = null;
		Integer orderFrom = null;
		for(int i = 0; i < 11; i++) {
			if(bufferTo.intValue() == columnOrder.get(i).intValue()) {
				orderTo = i;
			}
			if(bufferFrom.intValue() == columnOrder.get(i).intValue()) {
				orderFrom = i;
			}
		}
		final Integer bufferOrderTo = columnOrder.get(orderTo);
		final Integer bufferOrderFrom = columnOrder.get(orderFrom);
		columnVisibility.set(from, bufferTo);
		columnVisibility.set(to, bufferFrom);
		columnOrder.set(orderTo, bufferOrderFrom);
		columnOrder.set(orderFrom, bufferOrderTo);
	}

	/**
	 * returns the state of the active button
	 * 
	 * @return the state of the active button as String
	 */
	public String getActiveButton() {
		return activeButton;
	}

	/**
	 * return the column order
	 * 
	 * @return the column order
	 */
	public ArrayList<Integer> getColumnOrder() {
		return columnOrder;
	}

	/**
	 * returns the column visibility of all columns
	 * 
	 * @return the column visibility
	 */
	public ArrayList<Integer> getColumnVisbility() {
		return columnVisibility;
	}

	/**
	 * returns the multicast address
	 * 
	 * @return the multicast address as String
	 */
	public String getGroupadress() {
		return groupadress;
	}

	/**
	 * returns the network interface
	 * 
	 * @return the network interface as String
	 */
	public String getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * returns the index of the original ordner
	 * 
	 * @param i
	 *            the current index
	 * @return the original index
	 */
	public int getOriginalIndex(final int i) {
		int ret = -1;
		for(int x = 0; x < 11; x++) {
			if(columnVisibility.get(i).intValue() == columnOrder.get(x)
					.intValue()) {
				ret = columnOrder.get(x).intValue();
			}
		}
		return ret;
	}

	/**
	 * returns the packet length
	 * 
	 * @return the packet length as String
	 */
	public String getPacketlength() {
		return packetlength;
	}

	/**
	 * returns the packet rate
	 * 
	 * @return the packet rate as String
	 */
	public String getPacketrate() {
		return packetrate;
	}

	/**
	 * returns the port
	 * 
	 * @return the port as String
	 */
	public String getPort() {
		return port;
	}

	/**
	 * returns the selected tab
	 * 
	 * @return the selected tab as String
	 */
	public String getSelectedTab() {
		return selectedTab;
	}

	/**
	 * returns index of network adapter
	 * 
	 * @return index of network adapter
	 */
	public int getSourceAdressIndex() {
		return Integer.parseInt(networkInterface);
	}

	/**
	 * returns the time to live
	 * 
	 * @return the time to live as String
	 */
	public String getTtl() {
		return ttl;
	}

	/**
	 * returns typ of selected tab
	 * 
	 * @return type (L2/L3 Sender/Receiver)
	 */
	public Typ getTyp() {
		if(selectedTab.equals(Typ.L3_SENDER.toString())) {
			return Typ.L3_SENDER;
		} else if(selectedTab.equals(Typ.L2_SENDER.toString())) {
			return Typ.L2_SENDER;
		} else if(selectedTab.equals(Typ.L3_RECEIVER.toString())) {
			return Typ.L3_RECEIVER;
		} else if(selectedTab.equals(Typ.L2_RECEIVER.toString())) {
			return Typ.L2_RECEIVER;
		}
		return Typ.UNDEFINED;
	}

	/**
	 * hide colums i
	 * 
	 * @param i
	 *            the column id to be hidden
	 */
	public void hideColumn(final int i) {
		columnVisibility.remove(i);
	}

	/**
	 * returns state of active button
	 * 
	 * @return state of active button
	 */
	public boolean isActive() {
		return activeButton.equals("true");
	}

	/**
	 * returns whether auto save is enabled
	 * 
	 * @return returns whether auto save is enabled
	 */
	public boolean isAutoSaveEnabled() {
		return isAutoSaveEnabled.equals("true");
	}

	/**
	 * resets the view of the table
	 */
	public void resetColumns() {
		columnOrder = new ArrayList<Integer>();
		columnVisibility = new ArrayList<Integer>();
		columnOrder.clear();
		columnVisibility.clear();
		for(int i = 0; i < 11; i++) {
			columnOrder.add(new Integer(i));
			columnVisibility.add(new Integer(i));
		}
		setColumnVisibilityString(columnVisibility.toString());
		setColumnOrderString(columnOrder.toString());
	}

	/**
	 * sets the state of the active button
	 * 
	 * @param activeButton
	 *            the state of the active button
	 */
	public void setActiveButton(final boolean activeButton) {
		this.activeButton = "" + activeButton;
	}

	/**
	 * sets the column order via array list
	 * 
	 * @param columnOrder
	 *            array list<integer> to controll column order
	 */
	public void setColumnOrder(final ArrayList<Integer> columnOrder) {
		this.columnOrder = columnOrder;
		columnOrderString = this.columnOrder.toString();
	}

	/**
	 * sets column order as String
	 * 
	 * @param columnOrderString
	 *            column order string
	 */
	public void setColumnOrderString(final String columnOrderString) {
		this.columnOrderString = columnOrderString;
	}

	/**
	 * sets column visibility via array list
	 * 
	 * @param columnVisibility
	 *            array list<integer> to controll column visibility
	 */
	public void setColumnVisibility(final ArrayList<Integer> columnVisibility) {
		this.columnVisibility = columnVisibility;
		columnVisibilityString = this.columnVisibility.toString();
	}

	/**
	 * sets the columns visibility via string
	 * 
	 * @param columnVisibilityString
	 *            string to controll column visibility
	 */
	public void setColumnVisibilityString(final String columnVisibilityString) {
		this.columnVisibilityString = columnVisibilityString;
	}

	/**
	 * sets the multicast ip address
	 * 
	 * @param groupadress
	 *            multicast ip address as String
	 */
	public void setGroupadress(final String groupadress) {
		this.groupadress = groupadress;
	}

	/**
	 * sets the autosave enabled function
	 * 
	 * @param isAutoSaveEnabled
	 *            string to set it
	 */
	public void setIsAutoSaveEnabled(final String isAutoSaveEnabled) {
		this.isAutoSaveEnabled = isAutoSaveEnabled;
	}

	/**
	 * sets the network interface
	 * 
	 * @param selectedInterface
	 *            the network interface to be set
	 */
	public void setNetworkInterface(final int selectedInterface) {
		networkInterface = "" + selectedInterface;
	}

	/**
	 * sets the packet length
	 * 
	 * @param packetlength
	 *            the packet length to be set
	 */
	public void setPacketlength(final String packetlength) {
		this.packetlength = packetlength;
	}

	/**
	 * sets the packet rate
	 * 
	 * @param packetrate
	 *            the packet rate to be set
	 */
	public void setPacketrate(final String packetrate) {
		this.packetrate = packetrate;
	}

	/**
	 * sets the port
	 * 
	 * @param port
	 *            the port to be set
	 */
	public void setPort(final String port) {
		this.port = port;
	}

	/**
	 * sets the selected rows
	 * 
	 * @param i
	 *            int array of ids of rows to be selected
	 */
	public void setSelectedRows(final int[] i) {
		selectedRows = i.toString();
	}

	/**
	 * selects the rows with the ids out of selectedRows
	 * 
	 * @param selectedRows
	 *            int array with ids of rows to be selected
	 */
	public void setSelectedRowsArray(final int[] selectedRows) {
		final ArrayList<Integer> r = new ArrayList<Integer>();
		for(final int selectedRow : selectedRows) {
			r.add(new Integer(selectedRow));
		}
		this.selectedRows = r.toString();
	}

	/**
	 * sets the selected tab
	 * 
	 * @param typ
	 *            type of tab (L2_SENDER, L2_RECEIVER, L3_SENDER, L3_RECEIVER,
	 *            CONFIG)
	 */
	public void setSelectedTab(final Typ typ) {
		switch(typ) {
			case L3_SENDER:
				selectedTab = Typ.L3_SENDER.toString();
				break;
			case L2_SENDER:
				selectedTab = Typ.L2_SENDER.toString();
				break;
			case L3_RECEIVER:
				selectedTab = Typ.L3_RECEIVER.toString();
				break;
			case L2_RECEIVER:
				selectedTab = Typ.L2_RECEIVER.toString();
				break;
			case CONFIG:
				selectedTab = Typ.CONFIG.toString();
				break;
			default:
				selectedTab = Typ.UNDEFINED.toString();
				break;
		}
	}

	/**
	 * sets the time to live
	 * 
	 * @param ttl
	 *            the time to live to be set
	 */
	public void setTtl(final String ttl) {
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return "selectedTab: " + selectedTab + "\n" + "groupadress: "
				+ groupadress + "\n" + "networkInterface: " + networkInterface
				+ "\n" + "port: " + port + "\n" + "ttl: " + ttl + "\n"
				+ "packetrate: " + packetrate + "\n" + "packetlength: "
				+ packetlength + "\n" + "activeButton: " + activeButton + "\n";
	}
}