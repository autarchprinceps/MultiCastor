package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import zisko.multicastor.program.controller.ViewController;
/**
 * Panel welche die Statusbar des jeweiligen Programmteils beinhaltet
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelStatusBar extends JPanel {
	private JPanel pan_east;
	private JPanel pan_west;
	private JLabel lb_multicast_selected;
	private JLabel lb_multicast_count;
	private JLabel lb_trafficUP;
	private JLabel lb_trafficDown;
	/**
	 * Standardkonstruktor welcher die komplette Statusbar initialisiert.
	 */
	public PanelStatusBar(){
		pan_east = new JPanel();
		pan_west = new JPanel();
		
		pan_east.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		pan_west.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		lb_multicast_selected = new JLabel("0 Multicasts Selected");
		lb_multicast_selected.setFont(MiscFont.getFont());
		lb_multicast_selected.setPreferredSize(new Dimension(125,20));
		lb_multicast_selected.setHorizontalAlignment(SwingConstants.RIGHT);
		lb_multicast_count = new JLabel("0 Multicasts Total");
		lb_multicast_count.setFont(MiscFont.getFont());
		lb_multicast_count.setPreferredSize(new Dimension(125,20));
		lb_multicast_count.setHorizontalAlignment(SwingConstants.RIGHT);
		lb_trafficUP = new JLabel("Traffic OUT: 0,000 Mbps");
		lb_trafficUP.setFont(MiscFont.getFont());
		lb_trafficUP.setPreferredSize(new Dimension(135,20));
		lb_trafficUP.setHorizontalAlignment(SwingConstants.LEFT);
		//lb_trafficUP.setBorder(new LineBorder(Color.black));
		lb_trafficDown = new JLabel("IN: 0,000 Mbps");
		lb_trafficDown.setFont(MiscFont.getFont());
		lb_trafficDown.setPreferredSize(new Dimension(90,20));
		lb_trafficDown.setHorizontalAlignment(SwingConstants.LEFT);
		//lb_trafficDown.setBorder(new LineBorder(Color.black));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, 20));
		pan_east.add(lb_multicast_selected);
		pan_east.add(lb_multicast_count);
		pan_west.add(lb_trafficUP);
		pan_west.add(lb_trafficDown);

		add(pan_east, BorderLayout.EAST);
		add(pan_west, BorderLayout.WEST);
	}

	public JLabel getLb_multicasts_selected() {
		return lb_multicast_selected;
	}
	public JLabel getLb_multicast_count() {
		return lb_multicast_count;
	}
	/**
	 * Funktion welche angezeigten Traffic in der Statusbar neu anfordert.
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public void updateTraffic(ViewController ctrl){
		lb_trafficUP.setText("Traffic OUT: "+ctrl.getTotalTrafficUP()+" Mbps");
		lb_trafficDown.setText("IN: "+ctrl.getTotalTrafficDown()+" Mbps");
	}


}
