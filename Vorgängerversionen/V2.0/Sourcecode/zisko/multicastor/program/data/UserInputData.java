package zisko.multicastor.program.data;
import java.util.ArrayList;

import zisko.multicastor.program.data.MulticastData.Typ;

public class UserInputData {
	private ArrayList<Integer> columnOrder; //muss nicht gespeichert werden -> lueuft ueber columnOrderString
	private ArrayList<Integer> columnVisibility; // siehe columnVisiblityString
	private String selectedTab = Typ.L3_SENDER.toString();
	private String groupadress = "";
	private String isAutoSaveEnabled =""; //neu
	private String networkInterface = "0";
	private String port = "";
	private String ttl = "";
	private String packetrate = "";
	private String packetlength = "";
	private String activeButton = "";
	/* ATTENTION: the next three attributes cannot be deleted */
	@SuppressWarnings("unused")
	private String selectedRows = "";
	@SuppressWarnings("unused")
	private String columnOrderString="";
	@SuppressWarnings("unused")
	private String columnVisibilityString="";
	
	/**
	 * creates the user input data object with default values. Restores the default table view before.
	 */
	public UserInputData(){
		resetColumns();
	}
	
	/**
	 * returns the selected tab
	 * @return the selected tab as String
	 */
	public String getSelectedTab() {
		return selectedTab;
	}
	
	/**
	 * returns the multicast address
	 * @return the multicast address as String
	 */
	public String getGroupadress() {
		return groupadress;
	}
	
	/**
	 * returns the network interface
	 * @return the network interface as String
	 */
	public String getNetworkInterface() {
		return networkInterface;
	}
	
	/**
	 * returns the port
	 * @return the port as String
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * returns the time to live
	 * @return the time to live as String
	 */
	public String getTtl() {
		return ttl;
	}
	
	/**
	 * returns the packet rate
	 * @return the packet rate as String
	 */
	public String getPacketrate() {
		return packetrate;
	}
	
	/**
	 * returns the packet length
	 * @return the packet length as String
	 */
	public String getPacketlength() {
		return packetlength;
	}
	
	/**
	 * returns the state of the active button
	 * @return the state of the active button as String
	 */
	public String getActiveButton() {
		return activeButton;
	}
	
	/**
	 * sets the selected tab
	 * @param typ type of tab (L2_SENDER, L2_RECEIVER, L3_SENDER, L3_RECEIVER, CONFIG)
	 */
	public void setSelectedTab(Typ typ) {
		switch(typ){
			case L3_SENDER: selectedTab = Typ.L3_SENDER.toString(); break;
			case L2_SENDER: selectedTab = Typ.L2_SENDER.toString(); break;
			case L3_RECEIVER: selectedTab = Typ.L3_RECEIVER.toString(); break;
			case L2_RECEIVER: selectedTab = Typ.L2_RECEIVER.toString(); break;
			case CONFIG: selectedTab = Typ.CONFIG.toString(); break;
			default: selectedTab = Typ.UNDEFINED.toString(); break;
		}
	}
	
	/**
	 * sets the multicast ip address
	 * @param groupadress multicast ip address as String
	 */
	public void setGroupadress(String groupadress) {
		this.groupadress = groupadress;
	}
	
	/**
	 * sets the network interface
	 * @param selectedInterface the network interface to be set
	 */
	public void setNetworkInterface(int selectedInterface) {
		this.networkInterface = ""+selectedInterface;
	}
	
	/**
	 * sets the port
	 * @param port the port to be set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * sets the time to live
	 * @param ttl the time to live to be set
	 */
	public void setTtl(String ttl) {
		this.ttl = ttl;
	}
	
	/**
	 * sets the packet rate
	 * @param packetrate the packet rate to be set
	 */
	public void setPacketrate(String packetrate) {
		this.packetrate = packetrate;
	}
	
	/**
	 * sets the packet length
	 * @param packetlength the packet length to be set
	 */
	public void setPacketlength(String packetlength) {
		this.packetlength = packetlength;
	}
	
	/**
	 * sets the state of the active button
	 * @param activeButton the state of the active button
	 */
	public void setActiveButton(boolean activeButton) {
		this.activeButton = ""+activeButton;
	}
	
	public String toString(){
		return 	"selectedTab: "+selectedTab+"\n"+
				"groupadress: "+groupadress+"\n"+
				"networkInterface: "+networkInterface+"\n"+
				"port: "+port+"\n"+
				"ttl: "+ttl+"\n"+
				"packetrate: "+packetrate+"\n"+
				"packetlength: "+packetlength+"\n"+
				"activeButton: "+activeButton+"\n";
	}
	
	/**
	 * sets the selected rows
	 * @param i int array of ids of rows to be selected
	 */
	public void setSelectedRows(int[] i){
		selectedRows = i.toString();
	}
	
	/**
	 * resets the view of the table
	 */
	public void resetColumns(){
		columnOrder = new ArrayList<Integer>();
		columnVisibility = new ArrayList<Integer>();
		columnOrder.clear();
		columnVisibility.clear();
		for(int i = 0 ; i < 11 ; i++){
			columnOrder.add(new Integer(i));
			columnVisibility.add(new Integer(i));
		}
		setColumnVisibilityString(columnVisibility.toString());
		setColumnOrderString(columnOrder.toString());
	}
	
	/**
	 * hide colums i
	 * @param i the column id to be hidden
	 */
	public void hideColumn(int i){
		columnVisibility.remove(i);
	}
	
	/**
	 * lets you change the order of the colums
	 * @param from id of first column
	 * @param to id of target column
	 */
	public void changeColumns(int from, int to){
		Integer bufferTo = columnVisibility.get(to);
		Integer bufferFrom = columnVisibility.get(from);
		Integer orderTo = null;
		Integer orderFrom = null;
		for(int i = 0 ; i < 11 ; i++){
			if(bufferTo.intValue() == columnOrder.get(i).intValue()){
				orderTo = i;
			}
			if(bufferFrom.intValue() == columnOrder.get(i).intValue()){
				orderFrom = i;
			}
		}
		Integer bufferOrderTo = columnOrder.get(orderTo);
		Integer bufferOrderFrom = columnOrder.get(orderFrom);
		columnVisibility.set(from, bufferTo);
		columnVisibility.set(to, bufferFrom);
		columnOrder.set(orderTo, bufferOrderFrom);
		columnOrder.set(orderFrom, bufferOrderTo);
	}
	
	/**
	 * return the column order
	 * @return the column order
	 */
	public ArrayList<Integer> getColumnOrder(){
		return columnOrder;
	}
	
	/**
	 * returns the column visibility of all columns
	 * @return the column visibility
	 */
	public ArrayList<Integer> getColumnVisbility(){
		return columnVisibility;
	}
	
	/**
	 * returns the index of the original ordner
	 * @param i the current index
	 * @return the original index
	 */
	public int getOriginalIndex(int i){
		int ret = -1;
		for(int x = 0 ; x < 11 ; x++){
			if(columnVisibility.get(i).intValue() == columnOrder.get(x).intValue()){
				ret = columnOrder.get(x).intValue();
			}
		}
		return ret;
	}
	
	/**
	 * sets column order as String
	 * @param columnOrderString column order string
	 */
	public void setColumnOrderString(String columnOrderString)
	{
		this.columnOrderString = columnOrderString;
	}
	
	/**
	 * selects the rows with the ids out of selectedRows
	 * @param selectedRows int array with ids of rows to be selected
	 */
	public void setSelectedRowsArray(int[] selectedRows)
	{
		ArrayList<Integer> r = new ArrayList<Integer>();
		for(int i = 0 ; i < selectedRows.length ; i++){
			r.add(new Integer(selectedRows[i]));
		}
		this.selectedRows = r.toString();
	}
	
	/**
	 * sets the columns visibility via string
	 * @param columnVisibilityString string to controll column visibility
	 */
	public void setColumnVisibilityString(String columnVisibilityString)
	{
		this.columnVisibilityString = columnVisibilityString;
	}
	
	/**
	 * sets column visibility via array list
	 * @param columnVisibility array list<integer> to controll column visibility
	 */
	public void setColumnVisibility(ArrayList<Integer> columnVisibility) {
		this.columnVisibility = columnVisibility;
		columnVisibilityString = this.columnVisibility.toString();
	}
	
	/**
	 * sets the column order via array list
	 * @param columnOrder array list<integer> to controll column order
	 */
	public void setColumnOrder(ArrayList<Integer> columnOrder) {
		this.columnOrder = columnOrder;
		columnOrderString = this.columnOrder.toString();
	}
	
	/**
	 * returns typ of selected tab
	 * @return type (L2/L3 Sender/Receiver)
	 */
	public Typ getTyp(){
		Typ ret = Typ.UNDEFINED;
		if(selectedTab.equals(Typ.L3_SENDER.toString())){
			ret = Typ.L3_SENDER;
		}
		else if(selectedTab.equals(Typ.L2_SENDER.toString())){
			ret = Typ.L2_SENDER;
		}
		else if(selectedTab.equals(Typ.L3_RECEIVER.toString())){
			ret = Typ.L3_RECEIVER;
		}
		else if(selectedTab.equals(Typ.L2_RECEIVER.toString())){
			ret = Typ.L2_RECEIVER;
		}
		return ret;
	}
	
	/**
	 * returns state of active button
	 * @return state of active button
	 */
	public boolean isActive(){
		boolean ret = false;
		if(activeButton.equals("true")){
			ret=true;
		}
		return ret;
	}
	
	/**
	 * returns whether auto save is enabled
	 * @return returns whether auto save is enabled
	 */
	public boolean isAutoSaveEnabled(){
		boolean ret = false;
		if(isAutoSaveEnabled.equals("true")){
			ret=true;
		}
		return ret;
	}
	
	/**
	 * returns index of network adapter
	 * @return index of network adapter
	 */
	public int getSourceAdressIndex(){
		return Integer.parseInt(networkInterface);
	}
	
	/**
	 * sets the autosave enabled function
	 * @param isAutoSaveEnabled string to set it
	 */
	public void setIsAutoSaveEnabled(String isAutoSaveEnabled)
	{
		this.isAutoSaveEnabled = isAutoSaveEnabled;
	}
}