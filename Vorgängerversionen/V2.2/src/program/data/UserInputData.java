package program.data;

import java.util.ArrayList;

import program.data.MulticastData.Typ;

public class UserInputData {

	private String DefaultL3GroupAddress = "224.0.0.1"; //$NON-NLS-1$
	private String DefaultL3InterfaceAddress = "127.0.0.1"; //$NON-NLS-1$
	private String DefaultL2GroupAddress = "01:00:5e:00:00:01"; //$NON-NLS-1$
	private String DefaultL2InterfaceAddress = "00:00:00:00:00:00"; //$NON-NLS-1$
	private Integer DefaultL3UdpPort = 4711;
	private Integer DefaultL3TTL = 32;
	private Integer DefaultL3PacketRate = 10;
	private Integer DefaultL3PacketLength = 1024;
	private Integer DefaultL2PacketRate = 10;
	private Integer DefaultL2PacketLength = 1024;
	private String Filename = "Default";
	private String Language = "en";

	private ArrayList<Integer> columnOrderSender; // muss nicht gespeichert
													// werden -> l�uft �ber
													// columnOrderString
	private ArrayList<Integer> columnVisibilitySender; // siehe
														// columnVisiblityString

	private ArrayList<Integer> columnOrderReceiver; // muss nicht gespeichert
													// werden -> l�uft �ber
													// columnOrderString
	private ArrayList<Integer> columnVisibilityReceiver; // siehe
															// columnVisiblityString
	private String selectedTab = Typ.SENDER_V4.toString();
	private String groupadress = ""; //$NON-NLS-1$
	private String isAutoSaveEnabled = "1"; //neu //$NON-NLS-1$
	private String networkInterface = "0"; //$NON-NLS-1$
	private String port = ""; //$NON-NLS-1$
	private String ttl = ""; //$NON-NLS-1$
	private String packetrate = ""; //$NON-NLS-1$
	private String packetlength = ""; //$NON-NLS-1$
	private String activeButton = ""; //$NON-NLS-1$
	private String selectedRows = ""; //$NON-NLS-1$
	private String columnOrderStringSender = ""; //$NON-NLS-1$
	private String columnVisibilityStringSender = ""; //$NON-NLS-1$
	private String columnOrderStringReceiver = ""; //$NON-NLS-1$
	private String columnVisibilityStringReceiver = ""; //$NON-NLS-1$

	public String getFilename() {
		return Filename;
	}

	public void setFilename(String filename) {
		Filename = filename;
	}

	public String getDefaultL3GroupAddress() {
		return DefaultL3GroupAddress;
	}

	public void setDefaultL3GroupAddress(String defaultL3GroupAddress) {
		DefaultL3GroupAddress = defaultL3GroupAddress;
	}

	public String getDefaultL3InterfaceAddress() {
		return DefaultL3InterfaceAddress;
	}

	public void setDefaultL3InterfaceAddress(String defaultL3InterfaceAddress) {
		DefaultL3InterfaceAddress = defaultL3InterfaceAddress;
	}

	public String getDefaultL2GroupAddress() {
		return DefaultL2GroupAddress;
	}

	public void setDefaultL2GroupAddress(String defaultL2GroupAddress) {
		DefaultL2GroupAddress = defaultL2GroupAddress;
	}

	public String getDefaultL2InterfaceAddress() {
		return DefaultL2InterfaceAddress;
	}

	public void setDefaultL2InterfaceAddress(String defaultL2InterfaceAddress) {
		DefaultL2InterfaceAddress = defaultL2InterfaceAddress;
	}

	public Integer getDefaultL3UdpPort() {
		return DefaultL3UdpPort;
	}

	public void setDefaultL3UdpPort(Integer defaultL3UdpPort) {
		DefaultL3UdpPort = defaultL3UdpPort;
	}

	public Integer getDefaultL3TTL() {
		return DefaultL3TTL;
	}

	public void setDefaultL3TTL(Integer defaultL3TTL) {
		DefaultL3TTL = defaultL3TTL;
	}

	public Integer getDefaultL3PacketRate() {
		return DefaultL3PacketRate;
	}

	public void setDefaultL3PacketRate(Integer defaultL3PacketRate) {
		DefaultL3PacketRate = defaultL3PacketRate;
	}

	public Integer getDefaultL3PacketLength() {
		return DefaultL3PacketLength;
	}

	public void setDefaultL3PacketLength(Integer defaultL3PacketLength) {
		DefaultL3PacketLength = defaultL3PacketLength;
	}

	public Integer getDefaultL2PacketRate() {
		return DefaultL2PacketRate;
	}

	public void setDefaultL2PacketRate(Integer defaultL2PacketRate) {
		DefaultL2PacketRate = defaultL2PacketRate;
	}

	public Integer getDefaultL2PacketLength() {
		return DefaultL2PacketLength;
	}

	public void setDefaultL2PacketLength(Integer defaultL2PacketLength) {
		DefaultL2PacketLength = defaultL2PacketLength;
	}

	public String getLanguage() {
		return Language;
	}

	public void setLanguage(String language) {
		Language = language;
	}

	public UserInputData() {
		resetColumns(Typ.SENDER_V4);
		resetColumns(Typ.RECEIVER_V4);
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public String getGroupadress() {
		return groupadress;
	}

	public String getNetworkInterface() {
		return networkInterface;
	}

	public String getPort() {
		return port;
	}

	public String getTtl() {
		return ttl;
	}

	public String getPacketrate() {
		return packetrate;
	}

	public String getPacketlength() {
		return packetlength;
	}

	public String getActiveButton() {
		return activeButton;
	}

	public void setSelectedTab(Typ typ) {
		switch (typ) {
		case SENDER_V4:
			selectedTab = Typ.SENDER_V4.toString();
			break;
		case SENDER_V6:
			selectedTab = Typ.SENDER_V6.toString();
			break;
		case RECEIVER_V4:
			selectedTab = Typ.RECEIVER_V4.toString();
			break;
		case RECEIVER_V6:
			selectedTab = Typ.RECEIVER_V6.toString();
			break;
		case CONFIG:
			selectedTab = Typ.CONFIG.toString();
			break;
		default:
			selectedTab = Typ.UNDEFINED.toString();
			break;
		}
	}

	public void setSelectedTab(String typ) {
		this.selectedTab = typ;
	}


	public void setGroupadress(String groupadress) {
		this.groupadress = groupadress;
	}

	public void setNetworkInterface(int selectedInterface) {
		this.networkInterface = "" + selectedInterface; //$NON-NLS-1$
	}

	public void setNetworkInterface(String selectedInterface) {
		this.networkInterface = selectedInterface;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public void setPacketrate(String packetrate) {
		this.packetrate = packetrate;
	}

	public void setPacketlength(String packetlength) {
		this.packetlength = packetlength;
	}

	public void setActiveButton(boolean activeButton) {
		this.activeButton = "" + activeButton; //$NON-NLS-1$
	}

	public void setActiveButton(String activeButton) {
		this.activeButton = activeButton;
	}

	@Override
	public String toString() {
		return "selectedTab: " + selectedTab + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"groupadress: " + groupadress + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"networkInterface: " + networkInterface + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"port: " + port + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"ttl: " + ttl + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"packetrate: " + packetrate + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"packetlength: " + packetlength + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"activeButton: " + activeButton + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setSelectedRows(int[] i) {
		selectedRows = i.toString();
	}

	public void resetColumns(Typ typ) {
		if (Typ.isSender(typ)) {
			columnOrderSender = new ArrayList<Integer>();
			columnVisibilitySender = new ArrayList<Integer>();
			columnOrderSender.clear();
			columnVisibilitySender.clear();
			for (int i = 0; i < 12; i++) {
				columnOrderSender.add(new Integer(i));
				columnVisibilitySender.add(new Integer(i));
			}
			setColumnVisibilityString(columnVisibilitySender.toString(), typ);
			setColumnOrderString(columnOrderSender.toString(), typ);
		} else {
			columnOrderReceiver = new ArrayList<Integer>();
			columnVisibilityReceiver = new ArrayList<Integer>();
			columnOrderReceiver.clear();
			columnVisibilityReceiver.clear();
			for (int i = 0; i < 12; i++) {
				columnOrderReceiver.add(new Integer(i));
				columnVisibilityReceiver.add(new Integer(i));
			}
			setColumnVisibilityString(columnVisibilityReceiver.toString(), typ);
			setColumnOrderString(columnOrderReceiver.toString(), typ);
		}
	}

	public void hideColumn(int i, Typ typ) {
		if (Typ.isSender(typ))
			columnVisibilitySender.remove(i);
		else
			columnVisibilityReceiver.remove(i);
	}

	public void changeColumns(int from, int to, Typ typ) {
		if (Typ.isSender(typ)) {
			Integer bufferTo = columnVisibilitySender.get(to);
			Integer bufferFrom = columnVisibilitySender.get(from);
			Integer orderTo = null;
			Integer orderFrom = null;
			for (int i = 0; i < 12; i++) {
				if (bufferTo.intValue() == columnOrderSender.get(i).intValue()) {
					orderTo = i;
				}
				if (bufferFrom.intValue() == columnOrderSender.get(i)
						.intValue()) {
					orderFrom = i;
				}
			}
			Integer bufferOrderTo = columnOrderSender.get(orderTo);
			Integer bufferOrderFrom = columnOrderSender.get(orderFrom);
			columnVisibilitySender.set(from, bufferTo);
			columnVisibilitySender.set(to, bufferFrom);
			columnOrderSender.set(orderTo, bufferOrderFrom);
			columnOrderSender.set(orderFrom, bufferOrderTo);
		} else {
			Integer bufferTo = columnVisibilityReceiver.get(to);
			Integer bufferFrom = columnVisibilityReceiver.get(from);
			Integer orderTo = null;
			Integer orderFrom = null;
			for (int i = 0; i < 12; i++) {
				if (bufferTo.intValue() == columnOrderReceiver.get(i)
						.intValue()) {
					orderTo = i;
				}
				if (bufferFrom.intValue() == columnOrderReceiver.get(i)
						.intValue()) {
					orderFrom = i;
				}
			}
			Integer bufferOrderTo = columnOrderReceiver.get(orderTo);
			Integer bufferOrderFrom = columnOrderReceiver.get(orderFrom);
			columnVisibilityReceiver.set(from, bufferTo);
			columnVisibilityReceiver.set(to, bufferFrom);
			columnOrderReceiver.set(orderTo, bufferOrderFrom);
			columnOrderReceiver.set(orderFrom, bufferOrderTo);
		}
	}

	public ArrayList<Integer> getColumnOrder(Typ typ) {
		if (Typ.isSender(typ))
			return columnOrderSender;
		else
			return columnOrderReceiver;
	}

	public ArrayList<Integer> getColumnVisbility(Typ typ) {
		if (Typ.isSender(typ))
			return columnVisibilitySender;
		else
			return columnVisibilityReceiver;
	}

	public int getOriginalIndex(int i, Typ typ) {
		if (Typ.isSender(typ)) {
			int ret = -1;
			for (int x = 0; x < 12; x++) {
				if (columnVisibilitySender.get(i).intValue() == columnOrderSender
						.get(x).intValue()) {
					ret = columnOrderSender.get(x).intValue();
				}
			}
			return ret;
		} else {
			int ret = -1;
			for (int x = 0; x < 12; x++) {
				if (columnVisibilityReceiver.get(i).intValue() == columnOrderReceiver
						.get(x).intValue()) {
					ret = columnOrderReceiver.get(x).intValue();
				}
			}
			return ret;
		}
	}

	public ArrayList<Integer> getSavedColumnVisibility(Typ typ) {
		if (Typ.isSender(typ)) {
			ArrayList<Integer> ret = new ArrayList<Integer>();
			String s = columnVisibilityStringSender.substring(1,
					columnVisibilityStringSender.length() - 1);
			String order[] = s.split(", "); //$NON-NLS-1$
			for (int i = 0; i < order.length; i++) {
				ret.add(Integer.parseInt(order[i]));
			}

			return ret;
		} else {
			ArrayList<Integer> ret = new ArrayList<Integer>();
			String s = columnVisibilityStringReceiver.substring(1,
					columnVisibilityStringReceiver.length() - 1);
			String order[] = s.split(", "); //$NON-NLS-1$
			for (int i = 0; i < order.length; i++) {
				ret.add(Integer.parseInt(order[i]));
			}

			return ret;
		}
	}

	public void setColumnOrderString(String columnOrderString, Typ typ) {
		if (Typ.isSender(typ))
			this.columnOrderStringSender = columnOrderString;
		else
			this.columnOrderStringReceiver = columnOrderString;
	}

	public String getColumnOrderString(Typ typ) {
		if (Typ.isSender(typ))
			return columnOrderStringSender;
		else
			return columnOrderStringReceiver;
	}

	public void setSelectedRows(String selectedRows) {
		this.selectedRows = selectedRows;
	}

	public void setSelectedRowsArray(int[] selectedRows) {
		ArrayList<Integer> r = new ArrayList<Integer>();
		for (int i = 0; i < selectedRows.length; i++) {
			r.add(new Integer(selectedRows[i]));
		}
		this.selectedRows = r.toString();
	}

	public int[] getSelectedRowsArray() {
		int[] ret = null;
		String[] s = selectedRows.substring(1, selectedRows.length()).split(
				", "); //$NON-NLS-1$
		ret = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			ret[i] = Integer.parseInt(s[i]);
		}
		return ret;
	}

	public String getSelectedRows() {
		return selectedRows;
	}

	public void setColumnVisibilityString(String columnVisibilityString, Typ typ) {
		if(typ==Typ.SENDER_V4){
			  this.columnVisibilityStringSender = columnVisibilityString;
		}else this.columnVisibilityStringReceiver =columnVisibilityString;
	}

	public String getColumnVisibilityString(Typ typ) {
		if(typ==Typ.SENDER_V4) return columnVisibilityStringSender;
		else return columnVisibilityStringReceiver;
	}

	public ArrayList<Integer> getColumnVisibility(Typ typ) {
		if(typ==Typ.SENDER_V4) return columnVisibilitySender;
		else return columnVisibilityReceiver;
	}

	public void setColumnVisibility(ArrayList<Integer> columnVisibility, Typ typ) {
		if(typ==Typ.SENDER_V4){
		this.columnVisibilitySender = columnVisibility;
		columnVisibilityStringSender = this.columnVisibilitySender.toString();
		} else {
			this.columnVisibilityReceiver = columnVisibility;
			columnVisibilityStringReceiver = this.columnVisibilityReceiver.toString();
		}
	}

	public void setColumnOrder(ArrayList<Integer> columnOrder, Typ typ) {
		if(typ==Typ.SENDER_V4){
		this.columnOrderSender = columnOrder;
		columnOrderStringSender = this.columnOrderSender.toString();}
		else{
			this.columnOrderReceiver= columnOrder;
			columnOrderStringReceiver = this.columnOrderReceiver.toString();
		}
	}

	public Typ getTyp() {
		Typ ret = Typ.UNDEFINED;
		if (selectedTab.equals(Typ.SENDER_V4.toString())) {
			ret = Typ.SENDER_V4;
		} else if (selectedTab.equals(Typ.SENDER_V6.toString())) {
			ret = Typ.SENDER_V6;
		} else if (selectedTab.equals(Typ.RECEIVER_V4.toString())) {
			ret = Typ.RECEIVER_V4;
		} else if (selectedTab.equals(Typ.RECEIVER_V6.toString())) {
			ret = Typ.RECEIVER_V6;
		}
		return ret;
	}

	public boolean isActive() {
		// System.out.println("isactive: "+activeButton);
		boolean ret = false;
		if (activeButton.equals("true")) { //$NON-NLS-1$
			ret = true;
		}
		return ret;
	}

	public boolean isAutoSaveEnabled() {
		boolean ret = false;
		if (isAutoSaveEnabled.equals("true")) { //$NON-NLS-1$
			ret = true;
		}
		return ret;
	}

	public int getSourceAdressIndex() {
		return Integer.parseInt(networkInterface);
	}

	public void setIsAutoSaveEnabled(String isAutoSaveEnabled) {
		this.isAutoSaveEnabled = isAutoSaveEnabled;
	}

	public String getIsAutoSaveEnabled() {
		return isAutoSaveEnabled;
	}
}