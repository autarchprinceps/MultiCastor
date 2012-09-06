   
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
 
 package dhbw.multicastor.testcases.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import dhbw.multicastor.program.controller.CLIController;
import dhbw.multicastor.program.controller.MulticastController;
import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.model.MulticastReceiverLayer3;
import dhbw.multicastor.program.model.MulticastSenderIgmpMld;


public class MulticastControllerTest {
	private ViewController viewControllerMock;
	private MulticastController mcController;
	private CLIController cliCtrlMock;
	
	@Before
	public void setup(){
		mcController = spy(new MulticastController());
		viewControllerMock = mock(ViewController.class);
		cliCtrlMock = mock(CLIController.class);		
	}
	
	//*************************
	// General
	//*************************
	
	@Test
	public void constructorTest(){
		assertNotNull(mcController);
		assertEquals(Modus.SENDER, mcController.getCurrentModus());
		assertNotNull(mcController.getMcDataVectorSender());
		assertNotNull(mcController.getMcDataVectorReceiver());
		assertNotNull(mcController.getMcMap());
		assertNotNull(mcController);
	}
	
	@Test
	public void registerUIControllerTestView(){
		mcController.registerUIController(viewControllerMock);
		assertNotNull(mcController.getView_controller());
		assertNull(mcController.getCli_controller());
		verify(viewControllerMock).initialize(mcController);
		assertNotNull(mcController.getConfig());
//		assertNotNull(mcController.getUpdateTask());
	}
	
	@Test
	public void registerUIControllerTestCli(){
		mcController.registerUIController(cliCtrlMock);
		assertNull(mcController.getView_controller());
		assertNotNull(mcController.getCli_controller());
		verify(cliCtrlMock).initialize(mcController);
		assertNotNull(mcController.getConfig());
//		assertNotNull(mcController.getUpdateTask());
	}
	
	@Test
	public void stopMCTest(){
		MulticastData mcDataMock = mock(IgmpMldData.class);
		addMCTestHelper(mcDataMock);
		mcController.startMC(mcDataMock);
		
		int threadSizeBefore = mcController.getThreads().size();
		
		mcController.stopMC(mcDataMock);
		
		assertEquals(threadSizeBefore - 1, mcController.getThreads().size());
	}

	//*************************
	// Sender
	//*************************
	
	@Test
	public void addMCTestSenderIGMP(){
		MulticastData mcDataMock = mock(IgmpMldData.class);

		int mcDateSizeBefore = mcController.getMcDataVectorSender().size();
		int mcMapSizeBefore = mcController.getMcMap().size();
		addMCTestHelper(mcDataMock);
		assertEquals(mcDateSizeBefore + 1 , mcController.getMcDataVectorSender().size());
		assertEquals(mcDataMock, mcController.getMcDataVectorSender().get(0));
		assertEquals(mcMapSizeBefore + 1, mcController.getMcMap().size());
	}
	
	@Test
	public void startMCTestSender(){
		MulticastData mcDataMock = mock(IgmpMldData.class);
		addMCTestHelper(mcDataMock);
		mcController.startMC(mcDataMock);
		
		assertEquals(1, mcController.getThreads().size());
	}
	
	@Test
	public void deleteMCTestSender(){
		MulticastData mcDataMock = mock(IgmpMldData.class);
		addMCTestHelper(mcDataMock);
		int mcMapSizeBefore = mcController.getMcMap().size();
		int mcVectorSizeBefore = mcController.getMcDataVectorSender().size();
		
		mcController.deleteMC(mcDataMock);
		
		assertFalse(mcController.getMcDataVectorSender().contains(mcDataMock));
		assertFalse(mcController.getMcMap().containsKey(mcDataMock));
		assertEquals(mcMapSizeBefore-1, mcController.getMcMap().size());
		assertEquals(mcVectorSizeBefore-1, mcController.getMcDataVectorSender().size());
	}
	
	//*************************
	// Receiver
	//*************************
	
	@Test
	public void addMCTestReceiverIGMP(){
		MulticastData mcDataMock = mock(IgmpMldData.class);
		int mcDateSizeBefore = mcController.getMcDataVectorReceiver().size();
		int mcMapSizeBefore = mcController.getMcMap().size();
		
		mcController.setCurrentModus(Modus.RECEIVER);
		addMCTestHelper(mcDataMock);
		
		assertEquals(mcDateSizeBefore + 1 , mcController.getMcDataVectorReceiver().size());
		assertEquals(mcDataMock, mcController.getMcDataVectorReceiver().get(0));
		assertEquals(mcMapSizeBefore + 1, mcController.getMcMap().size());
	}
	
	@Test
	public void startMCTestReceiver(){
		MulticastData mcDataMock = mock(IgmpMldData.class);
		
		MulticastReceiverLayer3 receiverMock = mock(MulticastReceiverLayer3.class);
		when(receiverMock.joinGroup()).thenReturn(false);
		
		mcController.setCurrentModus(Modus.RECEIVER);
		addMCTestHelper(mcDataMock);
		mcController.startMC(mcDataMock);
		
		assertEquals(1, mcController.getThreads().size());
	}
	
	@Test
	public void deleteMCTestReceiver(){
		MulticastData mcDataMock = mock(IgmpMldData.class);
		mcController.setCurrentModus(Modus.RECEIVER);
		addMCTestHelper(mcDataMock);
		int mcMapSizeBefore = mcController.getMcMap().size();
		int mcVectorSizeBefore = mcController.getMcDataVectorReceiver().size();
		
		mcController.deleteMC(mcDataMock);
		
		assertFalse(mcController.getMcDataVectorReceiver().contains(mcDataMock));
		assertFalse(mcController.getMcMap().containsKey(mcDataMock));
		assertEquals(mcMapSizeBefore-1, mcController.getMcMap().size());
		assertEquals(mcVectorSizeBefore-1, mcController.getMcDataVectorReceiver().size());
	}
	
	//*************************
	// Helper
	//*************************
	
	public void addMCTestHelper(MulticastData m){
		MulticastThreadSuper mcThreadMock;
		if(mcController.getCurrentModus() == Modus.SENDER){
			mcThreadMock = mock(MulticastSenderIgmpMld.class);
		}else {
			mcThreadMock = mock(MulticastReceiverLayer3.class);
		}		
		doReturn(mcThreadMock).when(mcController).getMulticastThreadSuper(m);
		mcController.addMC(m);
	}
}
