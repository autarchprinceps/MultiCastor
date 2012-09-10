   
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
 
 package dhbw.multicastor.program.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LibraryHandler {

	public void checkLibrary(){
		if(new File("libs" + File.separator + "jnetpcap-1.3.0" + File.separator + "jnetpcap.jar").exists()){
			return;
		}
		if (isWindows()) {
			Process process = null;
			File checker = new File("bitness-checker.exe");
			
			if (!checker.exists()) {
				extractBitChecker();
			}
		    ProcessBuilder bitnessChecker = new ProcessBuilder("bitness-checker.exe");
		    try {
		    	process = bitnessChecker.start();
		    	InputStream is = process.getInputStream();
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(isr);
		        String line;
		        String output = "";
		        while ((line = br.readLine()) != null) {
		            output += line;
		        }
		        if (output.contains("64")) {
		        	extractLib("jnetpcap-1.3.0", "win64");
					System.out.println("64bit Windows");
				}else if (output.contains("32")) {
					extractLib("jnetpcap-1.3.0", "win32");
					System.out.println("32bit Windows");
				}
		        checker.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (isLinux()) {
			Process process = null;
		    try {
		    	process = Runtime.getRuntime().exec("uname -m");
		    	process.waitFor();
		    	InputStream is = process.getInputStream();
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(isr);
		        String line;
		        String output = "";
		        while ((line = br.readLine()) != null) {
		            output += line;
		        }
		        if (output.contains("x86_64")) {
		        	extractLib("jnetpcap-1.3.0", "ubuntu64");
					System.out.println("64bit Linux");
				}else if (output.contains("i686")) {
					extractLib("jnetpcap-1.3.0", "ubuntu32");
					System.out.println("32bit Linux");
				}
		        System.out.println(output);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void extractLib(String path, String os) {
		File jnetPcap = new File(path);
		if(!jnetPcap.exists()){
			BufferedInputStream input = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("dhbw/multicastor/resources/libs/" + path + os + ".zip"));
			
			FileOutputStream out;
			try {
				out = new FileOutputStream(path + os + ".zip");
			int i;
			while ((i=input.read()) >= 0) {
				out.write(i);
			}
			out.close();
			input.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				ZipFile pcap = new ZipFile(path + os + ".zip");
				Enumeration<? extends ZipEntry> e = pcap.entries();
				while (e.hasMoreElements()) {
				    ZipEntry file = (ZipEntry) e.nextElement();
				    File f = new File(file.getName());
				    if (file.isDirectory()) { // if its a directory, create it
				        f.mkdir();
				        continue;
				    }
				    java.io.InputStream is = pcap.getInputStream(file); // get the input stream
				    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
				    while (is.available() > 0) {
				        fos.write(is.read());
				    }
				    fos.close();
				    is.close();
				}
				pcap.close();
				File zipFile = new File(path + os + ".zip");zipFile.delete();
	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void extractBitChecker() {
		BufferedInputStream input = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("dhbw/multicastor/resources/libs/bitness-checker.exe"));		
		FileOutputStream out;
		try {
			out = new FileOutputStream("bitness-checker.exe");
		
		int i;
		while ((i=input.read()) >= 0) {
			out.write(i);
		}
		out.close();
		input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isLinux() {
		String OS = System.getProperty("os.name");
		if(OS.toUpperCase().contains("LINUX")){
			return true;
		}
		else{
			return false;
		}
	}

	private boolean isWindows() {
		String OS = System.getProperty("os.name");
		if(OS.contains("Windows")){
			return true;
		}
		else{
			return false;
		}
	}
}
