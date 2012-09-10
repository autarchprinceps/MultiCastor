   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.data;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;

/**
 * Diese Klasse enthält die Informationen was genau in dem User 
 * Interface angezeigt wird. Dabei entspricht jede Variable einem Element in der GUI.
 *
 */
public class UserlevelData {
	//********************************************
	// Daten, die gehalten werden
	//********************************************
	// Buttons
	private boolean startButton = true;
	private boolean stopButton = true;
	private boolean newButton = true;
	private boolean selectAllButton = true;
	private boolean deselectAllButton = true; 
	private boolean deleteButton = true;
	// Panels
	private boolean statusBar = true; 
	private boolean controlPanel = true; 
	private boolean configPanel = true; 
	// EingabeFelder
	private boolean groupIpField = true;
	private boolean sourceIpField = true;
	private boolean portField = true;
	private boolean packetLengthField = true;
	private boolean ttlField = true;
	private boolean packetRateField = true;
	private boolean activeField = true;
	private boolean enterField = true;
	// MenuItems
	private boolean saveConfigDialog = true; 
	private boolean loadConfigDialog = true; 
	private boolean userLevelRadioGrp = true; 
	private boolean autoSaveCheckbox = true; 
	private boolean snakeGame = true; 
	// Tabelle
	private boolean popupsEnabled = true; 
	private boolean startStopCheckBox = true;  
	// Graph und Konsole
	private boolean graph = true;
	private Graph graphTyp = Graph.UNDEFINED;
	private boolean console = true;
	private ProtocolType typ = ProtocolType.UNDEFINED;
	private Userlevel userlevel = Userlevel.UNDEFINED;
	
	//********************************************
	// Eigene Datentypen
	//********************************************	
	
	public enum Userlevel{
		UNDEFINED, BEGINNER, EXPERT, CUSTOM
	}
	
	public enum Graph{
		UNDEFINED, JITTER, LOST_PACKETS, MEASURED_PACKET_RATE;
	}
	
	//********************************************
	// Constructors
	//********************************************
	public UserlevelData(){
	}
	
	public UserlevelData(boolean startButton, boolean stopButton,
			boolean newButton, boolean selectAllButton, boolean deleteButton,
			boolean groupIpField, boolean sourceIpField, boolean portField,
			boolean packetLengthField, boolean ttlField,
			boolean packetRateField, boolean activeField, boolean enterField,
			boolean stateColumn, boolean idColumn,
			boolean groupIpAddressColumn, boolean sourceIpAddressColumn,
			boolean portColumn, boolean desiredPacketRateColumn,
			boolean measuredPacketRateColumn, boolean ttlColumn,
			boolean packetLengthColumn, boolean totalPacketsSentColumn,
			boolean numberOfInterruptionsColumn,
			boolean averageInterruptionTimeColumn,
			boolean packetLossPerSecondColumn, boolean graph, Graph graphTyp, 
			boolean console, ProtocolType typ, Userlevel userlevel) {
		super();
		this.startButton = startButton;
		this.stopButton = stopButton;
		this.newButton = newButton;
		this.selectAllButton = selectAllButton;
		this.deleteButton = deleteButton;
		this.groupIpField = groupIpField;
		this.sourceIpField = sourceIpField;
		this.portField = portField;
		this.packetLengthField = packetLengthField;
		this.ttlField = ttlField;
		this.packetRateField = packetRateField;
		this.activeField = activeField;
		this.enterField = enterField;
		this.graph = graph;
		this.graphTyp = graphTyp;
		this.console = console;
		this.typ = typ;
		this.userlevel = userlevel;
	}
	
	//********************************************
	// Getters und Setters
	//********************************************
	public boolean isStartButton() {
		return startButton;
	}
	public void setStartButton(boolean startButton) {
		this.startButton = startButton;
	}
	public boolean isStopButton() {
		return stopButton;
	}
	public void setStopButton(boolean stopButton) {
		this.stopButton = stopButton;
	}
	public boolean isNewButton() {
		return newButton;
	}
	public void setNewButton(boolean newButton) {
		this.newButton = newButton;
	}
	public boolean isSelectAllButton() {
		return selectAllButton;
	}
	public void setSelectAllButton(boolean selectAllButton) {
		this.selectAllButton = selectAllButton;
	}
	public boolean isDeleteButton() {
		return deleteButton;
	}
	public void setDeleteButton(boolean deleteButton) {
		this.deleteButton = deleteButton;
	}
	public boolean isGroupIpField() {
		return groupIpField;
	}
	public void setGroupIpField(boolean groupIpField) {
		this.groupIpField = groupIpField;
	}
	public boolean isSourceIpField() {
		return sourceIpField;
	}
	public void setSourceIpField(boolean sourceIpField) {
		this.sourceIpField = sourceIpField;
	}
	public boolean isPortField() {
		return portField;
	}
	public void setPortField(boolean portField) {
		this.portField = portField;
	}
	public boolean isPacketLengthField() {
		return packetLengthField;
	}
	public void setPacketLengthField(boolean packetLengthField) {
		this.packetLengthField = packetLengthField;
	}
	public boolean isTtlField() {
		return ttlField;
	}
	public void setTtlField(boolean ttlField) {
		this.ttlField = ttlField;
	}
	public boolean isPacketRateField() {
		return packetRateField;
	}
	public void setPacketRateField(boolean packetRateField) {
		this.packetRateField = packetRateField;
	}
	public boolean isActiveField() {
		return activeField;
	}
	public void setActiveField(boolean activeField) {
		this.activeField = activeField;
	}
	public boolean isEnterField() {
		return enterField;
	}
	public void setEnterField(boolean enterField) {
		this.enterField = enterField;
	}

	public boolean isStatusBar() {
		return statusBar;
	}

	public boolean isControlPanel() {
		return controlPanel;
	}

	public boolean isConfigPanel() {
		return configPanel;
	}
	public boolean isGraph() {
		return graph;
	}
	public void setGraph(boolean graph) {
		this.graph = graph;
	}
	public boolean isConsole() {
		return console;
	}
	public void setConsole(boolean console) {
		this.console = console;
	}
	public ProtocolType getTyp() {
		return typ;
	}
	public void setTyp(ProtocolType typ) {
		this.typ = typ;
	}
	public Graph getGraphTyp() {
		return graphTyp;
	}
	public void setGraphTyp(Graph graphTyp) {
		this.graphTyp = graphTyp;
	}
	public Userlevel getUserlevel(){
		return userlevel;
	}
	public void setUserlevel(Userlevel userlevel){
		this.userlevel = userlevel;
	}

	public boolean isDeselectAllButton() {
		return deselectAllButton;
	}

	public boolean isSaveConfigDialog() {
		return saveConfigDialog;
	}

	public boolean isLoadConfigDialog() {
		return loadConfigDialog;
	}

	public boolean isUserLevelRadioGrp() {
		return userLevelRadioGrp;
	}

	public boolean isAutoSaveCheckbox() {
		return autoSaveCheckbox;
	}

	public boolean isSnakeGame() {
		return snakeGame;
	}

	public boolean isPopupsEnabled() {
		return popupsEnabled;
	}

	public boolean isStartStopCheckBox() {
		return startStopCheckBox;
	}

	public void setDeselectAllButton(boolean deselectAllButton)
	{
		this.deselectAllButton = deselectAllButton;
	}

	public void setStatusBar(boolean statusBar)
	{
		this.statusBar = statusBar;
	}

	public void setControlPanel(boolean controlPanel)
	{
		this.controlPanel = controlPanel;
	}

	public void setConfigPanel(boolean configPanel)
	{
		this.configPanel = configPanel;
	}

	public void setSaveConfigDialog(boolean saveConfigDialog)
	{
		this.saveConfigDialog = saveConfigDialog;
	}

	public void setLoadConfigDialog(boolean loadConfigDialog)
	{
		this.loadConfigDialog = loadConfigDialog;
	}

	public void setUserLevelRadioGrp(boolean userLevelRadioGrp)
	{
		this.userLevelRadioGrp = userLevelRadioGrp;
	}

	public void setAutoSaveCheckbox(boolean autoSaveCheckbox)
	{
		this.autoSaveCheckbox = autoSaveCheckbox;
	}

	public void setSnakeGame(boolean snakeGame)
	{
		this.snakeGame = snakeGame;
	}

	public void setPopupsEnabled(boolean popupsEnabled)
	{
		this.popupsEnabled = popupsEnabled;
	}

	public void setStartStopCheckBox(boolean startStopCheckBox)
	{
		this.startStopCheckBox = startStopCheckBox;
	}
}
