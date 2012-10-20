package program.view;

import javax.swing.JTabbedPane;

import program.controller.ViewController;
import program.data.MulticastData.Typ;

@SuppressWarnings("serial")
public class PanelTabpaneConfiguration extends JTabbedPane{

	PanelMulticastConfig ipv4;
//	PanelMulticastConfig ipv6;
	PanelMulticastConfig mmrp;


	public PanelTabpaneConfiguration(ViewController ctrl,Typ typ){
		/*
		 * Hier werden die COnfigurationspannels erzeugt, wenn der übergebenene Typ Sender.v4 ist werden alle Sender Panels angelegt, ansonsten die Receiver Panels
		 */
		if(typ == Typ.SENDER_V4){
//		System.out.println("DEBUG: Sender V4"); //$NON-NLS-1$
		ipv4 = new PanelMulticastConfig(ctrl, Typ.SENDER_V4);
//		ipv6 = new PanelMulticastConfig(ctrl, Typ.SENDER_V6);
		mmrp = new PanelMulticastConfig(ctrl, Typ.SENDER_MMRP);
		}else{
			ipv4 = new PanelMulticastConfig(ctrl, Typ.RECEIVER_V4);
//			ipv6 = new PanelMulticastConfig(ctrl, Typ.RECEIVER_V6);
			mmrp = new PanelMulticastConfig(ctrl, Typ.RECEIVER_MMRP);
		}

	//	mmrp = new PanelMulticastConfig(ctrl, Typ.RECEIVER_V6);


		this.addTab("Layer 2: MMRP",mmrp); //$NON-NLS-1$
		this.addTab("Layer 3: IGMP",ipv4); //$NON-NLS-1$
//		this.addTab("IPv6",ipv6);

		this.setSelectedIndex(1);
		this.setFont(MiscFont.getFont(0,12));
		this.setFocusable(false);
		this.addChangeListener(ctrl);
		setVisible(true);
	}

	public PanelMulticastConfig getPan(Typ typ){
		switch(typ){
		case RECEIVER_MMRP:;
		case SENDER_MMRP:return this.mmrp;
		case RECEIVER_V6:
		case SENDER_V6:
		case RECEIVER_V4:;
		case SENDER_V4:return this.ipv4;
		default: return null;

		}
	}

	public PanelMulticastConfig getIpv4() {
		return ipv4;
	}

	public void setIpv4(PanelMulticastConfig ipv4) {
		this.ipv4 = ipv4;
	}

	public PanelMulticastConfig getMmrp() {
		return mmrp;
	}

	public void setMmrp(PanelMulticastConfig mmrp) {
		this.mmrp = mmrp;
	}

}
