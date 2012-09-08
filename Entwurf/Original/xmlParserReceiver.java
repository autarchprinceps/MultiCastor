   
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
 
  

import java.util.logging.Logger;

import dhbw.multicastor.program.interfaces.IgmpMldInterface;
import dhbw.multicastor.program.interfaces.MMRPInterface;

public class xmlParserReceiver extends xmlParser{

	
	public xmlParserReceiver(Logger logger) {
		super(logger);
		// TODO Auto-generated constructor stub
	}

	/**
	 * checks if the data contains all needed information to be a Multicastdata for a Receiver
	 * (IGMP/MLD)
	 */
	@Override
	protected boolean checkIgmpMld(IgmpMldInterface data) {
		if(data.getGroupIp() != null && data.getUdpPort() > 0){
			return true;
		}
		return false;
	}

	/**
	 * checks if the data contains all needed information to be a Multicastdata for a Receiver
	 * (MMRP)
	 */
	@Override
	protected boolean checkMmrp(MMRPInterface data) {
		if(data.getMacGroupId() != null && data.getTimerJoinMt() > -2){
			return true;
		}
		return false;
	}

	/**
	 * returns all informations for a error-message
	 */
	@Override
	protected String getErrorMessageIgmpMld(IgmpMldInterface data) {
		return "There is an error in the configuration-file:\n" +
				"\t\tMulticast: "+"protocolType: '"+data.getProtocolType()+"' "+
				"groupIp: '"+data.getGroupIp()+"' " +
				"udpPort: '"+data.getUdpPort()+"' ";
	}

	/**
	 * returns all informations for a error-message
	 */
	@Override
	protected String getErrorMessageMmrp(MMRPInterface data) {
		return "There is an error in the following configuration:\n" +
				"\t\tMulticast: "+"protocolType: '"+data.getProtocolType()+"' "+
				"macGroupId: '"+data.getMacGroupId()+"' "+"macSourceId: '"+data.getMacSourceId()+"' ";
	}

	
	@Override
	protected boolean checkNetworkInterface(IgmpMldInterface data) {
		return true;
	}

	@Override
	protected boolean checkNetworkInterface(MMRPInterface data) {
		return true;
	}
}
