package zisko.multicastor.program.data;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JSeparator;

import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.data.UserlevelData.Userlevel;

public class UserInputData {
	private ArrayList<Integer> columnOrder; //muss nicht gespeichert werden -> läuft über columnOrderString
	private ArrayList<Integer> columnVisibility; // siehe columnVisiblityString
	private String selectedTab = Typ.SENDER_V4.toString();
	private String selectedUserlevel = Userlevel.EXPERT.toString();
	private String groupadress = "";
	private String isAutoSaveEnabled =""; //neu
	private String networkInterface = "0";
	private String port = "";
	private String ttl = "";
	private String packetrate = "";
	private String packetlength = "";
	private String activeButton = "";
	private String selectedRows = "";
	private String columnOrderString="";
	private String columnVisibilityString="";
	public UserInputData(){
		resetColumns();
	}
	public String getSelectedTab() {
		return selectedTab;
	}
	public String getSelectedUserlevel() {
		return selectedUserlevel;
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
		switch(typ){
			case SENDER_V4: selectedTab = Typ.SENDER_V4.toString(); break;
			case SENDER_V6: selectedTab = Typ.SENDER_V6.toString(); break;
			case RECEIVER_V4: selectedTab = Typ.RECEIVER_V4.toString(); break;
			case RECEIVER_V6: selectedTab = Typ.RECEIVER_V6.toString(); break;
			case CONFIG: selectedTab = Typ.CONFIG.toString(); break;
			default: selectedTab = Typ.UNDEFINED.toString(); break;
		}
	}
	public void setSelectedTab(String typ) {
		this.selectedTab = typ;
	}
	public void setSelectedUserlevel(Userlevel level) {
		switch(level){
			case BEGINNER: selectedUserlevel = Userlevel.BEGINNER.toString(); break;
			case EXPERT: selectedUserlevel = Userlevel.EXPERT.toString(); break;
			case CUSTOM: selectedUserlevel = Userlevel.CUSTOM.toString(); break;
			default: selectedUserlevel = Userlevel.UNDEFINED.toString(); break;
		}
	}
	public void setSelectedUserlevel(String level) {
		this.selectedUserlevel = level;
	}
	
	public void setGroupadress(String groupadress) {
		this.groupadress = groupadress;
	}
	public void setNetworkInterface(int selectedInterface) {
		this.networkInterface = ""+selectedInterface;
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
		this.activeButton = ""+activeButton;
	}
	public void setActiveButton(String activeButton) {
		this.activeButton = activeButton;
	}
	public String toString(){
		return 	"selectedTab: "+selectedTab+"\n"+
				"selectedUserlevel: "+selectedUserlevel+"\n"+
				"groupadress: "+groupadress+"\n"+
				"networkInterface: "+networkInterface+"\n"+
				"port: "+port+"\n"+
				"ttl: "+ttl+"\n"+
				"packetrate: "+packetrate+"\n"+
				"packetlength: "+packetlength+"\n"+
				"activeButton: "+activeButton+"\n";
	}
	public void setSelectedRows(int[] i){
		selectedRows = i.toString();
	}
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
		//System.out.println(columnVisibility.toString());
		//System.out.println(columnOrder.toString());
	}
	public void hideColumn(int i){
		columnVisibility.remove(i);
//		System.out.println("visibility: "+columnVisibility.toString());
//		System.out.println(columnOrder.toString());
	}
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
//		System.out.println(columnVisibility.toString());
//		System.out.println(columnOrder.toString());
	}
	public ArrayList<Integer> getColumnOrder(){
		return columnOrder;
	}
	public ArrayList<Integer> getColumnVisbility(){
		return columnVisibility;
	}
	public int getOriginalIndex(int i){
		int ret = -1;
		for(int x = 0 ; x < 11 ; x++){
			if(columnVisibility.get(i).intValue() == columnOrder.get(x).intValue()){
				ret = columnOrder.get(x).intValue();
			}
		}
		return ret;
	}
	public ArrayList getSavedColumnVisibility(){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		String s = columnVisibilityString.substring(1, columnVisibilityString.length()-1);
//		System.out.println("after substring: "+s);
		String order[] = s.split(", ");
		for(int i = 0 ; i < order.length ; i++){
			ret.add(Integer.parseInt(order[i]));
		}
//		System.out.println("generated ArrayList: "+ret.toString());
		return ret;
	}
	public void setColumnOrderString(String columnOrderString)
	{
		this.columnOrderString = columnOrderString;
	}
	public String getColumnOrderString()
	{
		return columnOrderString;
	}
	public void setSelectedRows(String selectedRows)
	{
		this.selectedRows = selectedRows;
	}
	public void setSelectedRowsArray(int[] selectedRows)
	{
		ArrayList<Integer> r = new ArrayList<Integer>();
		for(int i = 0 ; i < selectedRows.length ; i++){
			r.add(new Integer(selectedRows[i]));
		}
		this.selectedRows = r.toString();
	}
	public int[] getSelectedRowsArray(){
		int[] ret = null;
		String[] s = selectedRows.substring(1, selectedRows.length()).split(", ");
		ret = new int[s.length];
		for(int i = 0 ; i < s.length ; i++){
			ret[i] = Integer.parseInt(s[i]);
		}
		return ret;
	}
	public String getSelectedRows()
	{
		return selectedRows;
	}
	public void setColumnVisibilityString(String columnVisibilityString)
	{
		this.columnVisibilityString = columnVisibilityString;
	}
	public String getColumnVisibilityString()
	{
		return columnVisibilityString;
	}
	public ArrayList<Integer> getColumnVisibility() {
		return columnVisibility;
	}
	public void setColumnVisibility(ArrayList<Integer> columnVisibility) {
		this.columnVisibility = columnVisibility;
		columnVisibilityString = this.columnVisibility.toString();
	}
	public void setColumnOrder(ArrayList<Integer> columnOrder) {
		this.columnOrder = columnOrder;
		columnOrderString = this.columnOrder.toString();
	}
	public Typ getTyp(){
		Typ ret = Typ.UNDEFINED;
		if(selectedTab.equals(Typ.SENDER_V4.toString())){
			ret = Typ.SENDER_V4;
		}
		else if(selectedTab.equals(Typ.SENDER_V6.toString())){
			ret = Typ.SENDER_V6;
		}
		else if(selectedTab.equals(Typ.RECEIVER_V4.toString())){
			ret = Typ.RECEIVER_V4;
		}
		else if(selectedTab.equals(Typ.RECEIVER_V6.toString())){
			ret = Typ.RECEIVER_V6;
		}
		return ret;
	}
	public Userlevel getUserLevel(){
		Userlevel ret = Userlevel.UNDEFINED;
		if(selectedUserlevel.equals(Userlevel.BEGINNER.toString())){
			ret = Userlevel.BEGINNER;
		}
		else if(selectedUserlevel.equals(Userlevel.EXPERT.toString())){
			ret = Userlevel.EXPERT;
		}
		else if(selectedUserlevel.equals(Userlevel.CUSTOM.toString())){
			ret = Userlevel.CUSTOM;
		}
		else{
			ret = Userlevel.EXPERT;
		}
		return ret;
	}
	public boolean isActive(){
		//System.out.println("isactive: "+activeButton);
		boolean ret = false;
		if(activeButton.equals("true")){
			ret=true;
		}
		return ret;
	}
	public boolean isAutoSaveEnabled(){
		boolean ret = false;
		if(isAutoSaveEnabled.equals("true")){
			ret=true;
		}
		return ret;
	}
	public int getSourceAdressIndex(){
		return Integer.parseInt(networkInterface);
	}
	public void setIsAutoSaveEnabled(String isAutoSaveEnabled)
	{
		this.isAutoSaveEnabled = isAutoSaveEnabled;
	}
	public String getIsAutoSaveEnabled()
	{
		return isAutoSaveEnabled;
	}
}