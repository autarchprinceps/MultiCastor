package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * Panel welche die Statusbar des jeweiligen Programmteils beinhaltet.
 */
@SuppressWarnings("serial")
public class PanelStatusBar extends JPanel {
	private final LanguageManager lang;
	private final JLabel lb_multicast_count;
	private final JLabel lb_multicast_selected;
	private final JLabel lb_trafficDown;
	private final JLabel lb_trafficUP;
	private final JPanel pan_east;
	private final JPanel pan_west;

	/**
	 * Standardkonstruktor welcher die komplette Statusbar initialisiert.
	 */
	public PanelStatusBar() {
		lang = LanguageManager.getInstance();

		pan_east = new JPanel();
		pan_west = new JPanel();

		pan_east.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		pan_west.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		lb_multicast_selected = new JLabel("0 "
				+ lang.getProperty("status.mcSelected"));
		lb_multicast_selected.setFont(MiscFont.getFont());
		lb_multicast_selected.setPreferredSize(new Dimension(125, 20));
		lb_multicast_selected.setHorizontalAlignment(SwingConstants.RIGHT);
		lb_multicast_count = new JLabel("0 "
				+ lang.getProperty("status.mcTotal"));
		lb_multicast_count.setFont(MiscFont.getFont());
		lb_multicast_count.setPreferredSize(new Dimension(125, 20));
		lb_multicast_count.setHorizontalAlignment(SwingConstants.RIGHT);
		lb_trafficUP = new JLabel(lang.getProperty("status.traffic") + " "
				+ lang.getProperty("status.out") + ": 0,000 Mbps");
		lb_trafficUP.setFont(MiscFont.getFont());
		lb_trafficUP.setPreferredSize(new Dimension(150, 20));
		lb_trafficUP.setHorizontalAlignment(SwingConstants.LEFT);
		lb_trafficDown = new JLabel(lang.getProperty("status.in")
				+ ": 0,000 Mbps");
		lb_trafficDown.setFont(MiscFont.getFont());
		lb_trafficDown.setPreferredSize(new Dimension(105, 20));
		lb_trafficDown.setHorizontalAlignment(SwingConstants.LEFT);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, 20));
		pan_east.add(lb_multicast_selected);
		pan_east.add(lb_multicast_count);
		pan_west.add(lb_trafficUP);
		pan_west.add(lb_trafficDown);

		add(pan_east, BorderLayout.EAST);
		add(pan_west, BorderLayout.WEST);
	}

	public JLabel getLb_multicast_count() {
		return lb_multicast_count;
	}

	public JLabel getLb_multicasts_selected() {
		return lb_multicast_selected;
	}

	/**
	 * Funktion welche angezeigten Traffic in der Statusbar neu anfordert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 */
	public void updateTraffic(final ViewController ctrl) {
		lb_trafficUP.setText(lang.getProperty("status.traffic") + " "
				+ lang.getProperty("status.out") + ": "
				+ ctrl.getTotalTrafficUP() + " Mbps");
		lb_trafficDown.setText(lang.getProperty("status.in") + ": "
				+ ctrl.getTotalTrafficDown() + " Mbps");
	}

}
