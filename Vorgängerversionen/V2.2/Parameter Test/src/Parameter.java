import java.util.HashMap;
import java.util.Map;


public class Parameter {

	/**
	 * @param args
	 * 
vorhandene Parameter
    Multicastor ohne GUI starten    |   -c configuration-file       |   --config
    Hilfe ausgeben                  |   -h                          |   --help

zusätzlich benötigte Parameter
    Sender                          |   -S                          |   --send
    Receiver                        |   -R                          |   --receive
    IPv4                            |   -4                          |   --ipv4
    IPv6                            |   -6                          |   --ipv6
    mmrp                            |   -m                          |   --mmrp
    
    groupIP                         |   -g                          |   --group-ip
    sourceIP                        |   -s                          |   --source-ip
    UDPport                         |   -p                          |   --port
    packetLength                    |   -l                          |   --length
    ttl                             |   -t                          |   --ttl
    packetRateDesired               |   -n                          |   --rate

	 * 
	 */
	
	
	
	public static void main(String[] args) {
		
		if(args.length == 0) {
			//Wenn keine Parameter angegen werden, startet der Multicastor mit der GUI
			System.out.println("start GUI...");
		}
		else {
			Map m = handleParameter(args);
			
		}
		
		
	}
	
	private static Map handleParameter(String[] args) {
		Map<String,String> r=new HashMap<String,String>();
		for (int i = 0; i < args.length; i++) {
		    
			/*
			 * Gibt die Hilfe zu den Parametern aus
			 */
			if(args[i].equals("-h") || args[i].equals("--help")) {
				help("");
				break;
			}
			
			/*
			 * Liest den Pfad des Configfiles ein
			 */
			if(args[i].equals("-c")) {
				if(++i < args.length) {
					System.out.println("config-file: " + args[i]);
					r.put("config",  args[i]);
				}
				else {
					help("no config-file given");
					System.exit(0);
				}
				break;
			}
			
			if(args[i].contains("--config=")) {
				String[] params = args[i].split("0");
				if(params[1].isEmpty()) {
					help("no config-file given");
					System.exit(0);
				}
				System.out.println("config-file: " + params[1]);
				r.put("config",  args[i]);
				break;
			}
			
			/*
			 * Anlegen eines Streams mit Parametern
			 */

			if(args[i].equals("-S")) {
		    	System.out.println("Sender!");
		    	r.put("mode", "sender");
		    	continue;
			}
			
			if(args[i].equals("-R")) {
		    	System.out.println("Receiver!");
		    	r.put("mode", "receiver");
		    	continue;
			}
			
			if(args[i].equals("-4")) {
		    	System.out.println("IPV4!");
		    	r.put("netmode", "ipv4");
		    	continue;
			}
			
			if(args[i].equals("-6")) {
		    	System.out.println("IPV6!");
		    	r.put("netmode", "ipv6");
		    	continue;
			}
			
			if(args[i].equals("-m")) {
		    	System.out.println("MMRP!");
		    	r.put("netmode", "mmrp");
		    	continue;
			}
			
			
			if(args[i].equals("-g")) {
				if(++i < args.length) {
					System.out.println("group-ip: " + args[i]);
					r.put("group-ip", args[i]);
				}
				else {
					help("no group IP given");
					System.exit(0);
				}		
				continue;
			}
			
			if(args[i].contains("--group-ip=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no group-ip given");
					System.exit(0);
				}
				System.out.println("group-ip: " + params[1]);
				r.put("group-ip", params[1]);
				continue;
			}
			
			if(args[i].equals("-s")) {
				if(++i < args.length) {
					System.out.println("source-ip: " + args[i]);
					r.put("source-ip", args[i]);
				}
				else {
					help("no source-ip given");
					System.exit(0);
				}		
				continue;
			}
			
			if(args[i].contains("--source-ip=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no source-ip given");
					System.exit(0);
				}
				System.out.println("source-ip: " + params[1]);
				r.put("source-ip", params[1]);
				continue;
			}

			if(args[i].equals("-p")) {
				if(++i < args.length) {
					System.out.println("port: " + args[i]);
					r.put("port", args[i]);
				}
				else {
					help("no port given");
					System.exit(0);
				}		
				continue;
			}
			
			if(args[i].contains("--port=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no port given");
					System.exit(0);
				}
				System.out.println("port: " + params[1]);
				r.put("port", params[1]);
				continue;
			}

            if(args[i].equals("-l")) {
				if(++i < args.length) {
					System.out.println("length: " + args[i]);
					r.put("length", args[i]);
				}
				else {
					help("no length given");
					System.exit(0);
				}		
				continue;
			}
			
			if(args[i].contains("--length=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no length given");
					System.exit(0);
				}
				System.out.println("length: " + params[1]);
				r.put("length", params[1]);
				continue;
			}

            if(args[i].equals("-t")) {
				if(++i < args.length) {
					System.out.println("ttl: " + args[i]);
					r.put("ttl", args[i]);
				}
				else {
					help("no ttl given");
					System.exit(0);
				}		
				continue;
			}
			
			if(args[i].contains("--ttl=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no ttl given");
					System.exit(0);
				}
				System.out.println("ttl: " + params[1]);
				r.put("ttl", params[1]);
				continue;
			}

            if(args[i].equals("-n")) {
				if(++i < args.length) {
					System.out.println("rate: " + args[i]);
					r.put("rate", args[i]);
				}
				else {
					help("no rate given");
					System.exit(0);
				}		
				continue;
			}
			
			if(args[i].contains("--rate=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no rate given");
					System.exit(0);
				}
				System.out.println("rate: " + params[1]);
				r.put("rate", params[1]);
				continue;
			}

			/*
			 * Beenden, wenn ein falscher Parameter angegeben wird.
			 */
			help("unrecognized option '" + args[i] + "'");
			System.exit(0);
			//String[] parts=arg.split("="); /* TODO: Fall parts.length!=2 berücksichtigen */
		    //arguments.put(parts[0],parts[1]);
		}

		//if(arguments.containsKey("foo")) {
		//    String fooValue=arguments.get("foo");
		    
		//}
		return r;
	}
	
	private static void help(String error) {
		/*
		 * Wenn ein unbekanter Parameter angegeben wird, wird ein Fehler in der ersten Zeile ausgegeben.
		 */
		if(!error.isEmpty())
			System.out.println("Error: " + error);
		
		/*
		 * Kopfzeilen ausgeben.
		 */
		System.out.println("Usage: multicastor [Options]...");
		System.out.println("Send and recive IPv4, IPv6 and MMRP multicastpackages.");
		System.out.println("");
		System.out.println("There are two ways to start multicasor preconfigurated:");
		System.out.println("\t" + "With a config-file");
		System.out.println("\t" + "With a options definded stream");
		System.out.println("");
		/*
		 * Optionen ausgeben.
		 */
		System.out.println("Options:");
		String[][] params =  
			{
				{"-h", "--help", "Print this help message"},
				{"-c", "--config", "Start program with a config-file"},
				{"-S", "--send", "Start program as a sender"},
				{"-R", "--recive", "Start program as a reciver"},
				{"-4", "--ipv4", "Set in IPv4 mode"},
				{"-6", "--ipv6", "Set in IPv6 mode"},
				{"-m", "--mmrp", "Set in MMRP mode"},
				{"-d", "--group-ip", "Set group IP"},
				{"-s", "--source-ip", "Set source IP"},
				{"-p", "--port", "Set (UDP)-port"},
				{"-l", "--length", "Set package length"},
				{"-t", "--ttl", "Set time to live"},
				{"-n", "--rate", "Set package rate desired"}
			};
		
		/*
		 * Maximum der ParameterLlänge ermitteln.
		 */
		int maxLengthParam = 0;
		for(String[] param : params)
			if(param[1].length() > maxLengthParam)
				maxLengthParam = param[1].length();
				
		/*
		 * Optionen formatiert ausgeben.
		 */
		for(String[] param : params) {
			System.out.print("   " + param[0] + ",   " + param[1] + "   ");
			for(int i = param[1].length(); i < maxLengthParam; i++)
				System.out.print(" ");
			System.out.println(param[2]);
		}
		
		/*
		 * Beispiele ausgeben.
		 */
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("\t" + "");
	}

}
