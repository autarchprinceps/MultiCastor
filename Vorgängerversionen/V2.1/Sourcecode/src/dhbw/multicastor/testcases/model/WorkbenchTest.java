   
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
 
 package dhbw.multicastor.testcases.model;

import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.FileNames;
import dhbw.multicastor.program.model.XmlParserWorkbench;


public class WorkbenchTest {

	private XmlParserWorkbench workbench;
	
	@Before
	public void generateParser(){
		workbench = 
				new XmlParserWorkbench(Logger.getLogger("dhbw.multicastor.testcases.model.WorkbenchTest"), null);
	}
	
	/**
	 * Testfile mit invaliden Daten laden.
	 * Alle Werte muessen den Defaultwerten entsprechen.
	 */
	@Test
	public void testLoadWorkbenchDefault(){
		
		workbench.loadWorkbench(FileNames.WORKBENCH_TEST_01);
		Assert.assertEquals("US", workbench.getV_country());
		Assert.assertEquals("en", workbench.getV_language());
		Assert.assertEquals("sender", workbench.getV_mode());
		Assert.assertEquals(ProtocolType.IGMP, workbench.getV_rb());
		Assert.assertEquals("graph", workbench.getV_GoC());
		Assert.assertEquals(100, workbench.getV_posx());
		Assert.assertEquals(100, workbench.getV_posy());
		Assert.assertEquals(694, workbench.getV_height());
		Assert.assertEquals(850, workbench.getV_width());
		
	}
	
	/**
	 * Testfile mit validen Daten.
	 * Alle Werte muessen den Werten aus dem File entsprechen.
	 */
	@Test
	public void testLoadWorkbench(){
		workbench.loadWorkbench(FileNames.WORKBENCH_TEST_02);
		Assert.assertEquals("DE", workbench.getV_country());
		Assert.assertEquals("de", workbench.getV_language());
		Assert.assertEquals("receiver", workbench.getV_mode());
		Assert.assertEquals(ProtocolType.MLD, workbench.getV_rb());
		Assert.assertEquals("console", workbench.getV_GoC());
		Assert.assertEquals(20, workbench.getV_posx());
		Assert.assertEquals(30, workbench.getV_posy());
		Assert.assertEquals(1000, workbench.getV_height());
		Assert.assertEquals(1200, workbench.getV_width());
	}
	
	

}
