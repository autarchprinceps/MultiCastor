package program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import program.controller.Messages;
import program.controller.ViewController;
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
		lb_multicast_selected = new JLabel(Messages.getString("PanelStatusBar.0")); //$NON-NLS-1$
		lb_multicast_selected.setFont(MiscFont.getFont());
		lb_multicast_selected.setHorizontalAlignment(SwingConstants.RIGHT);
		lb_multicast_count = new JLabel(Messages.getString("PanelStatusBar.1")); //$NON-NLS-1$
		lb_multicast_count.setFont(MiscFont.getFont());
		lb_multicast_count.setHorizontalAlignment(SwingConstants.RIGHT);
		lb_trafficUP = new JLabel(Messages.getString("PanelStatusBar.2") + ": 0,000 Mbps"); //$NON-NLS-1$
		lb_trafficUP.setFont(MiscFont.getFont());
		lb_trafficUP.setHorizontalAlignment(SwingConstants.LEFT);
		lb_trafficDown = new JLabel(Messages.getString("PanelStatusBar.3")+ ": 0,000 Mbps"); //$NON-NLS-1$
		lb_trafficDown.setFont(MiscFont.getFont());
		lb_trafficDown.setHorizontalAlignment(SwingConstants.LEFT);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, 18));
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
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 */
	public void updateTraffic(ViewController ctrl){
		lb_trafficUP.setText(Messages.getString("PanelStatusBar.2") + ": " + ctrl.getTotalTrafficUP()+" Mbps"); //$NON-NLS-2$
		lb_trafficDown.setText(Messages.getString("PanelStatusBar.3") + ": " + ctrl.getTotalTrafficDown()+" Mbps");  //$NON-NLS-2$
	}


}
