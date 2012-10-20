package program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import program.controller.Messages;
import program.controller.MulticastController;
import program.data.MulticastData;

public class StreamImport extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = -754176591985244966L;
	private final JPanel contentPanel = new JPanel();
	private Vector<MulticastData> ret;
	ImportPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); //$NON-NLS-1$
			StreamImport dialog = new StreamImport(null, new Vector<MulticastData>(), null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor for a ProfileImport Dialog.
	 * @param frame
	 * @param data
	 * @param mc
	 */
	public StreamImport(JFrame frame, Vector<MulticastData> data, MulticastController mc) {
		super(frame);
		ImageIcon icon = new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/icon.png")); //$NON-NLS-1$
		setIconImage(icon.getImage());
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setTitle(Messages.getString("StreamImport.1")); //$NON-NLS-1$
		setBounds(100, 100, 640, 431);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel panelTitle = new JPanel();
		panelTitle.setBackground(Color.WHITE);
		panelTitle.setBounds(0, 0, 634, 74);
		contentPanel.add(panelTitle);
		panelTitle.setLayout(null);

		JLabel lblImport = new JLabel(Messages.getString("StreamImport.1")); //$NON-NLS-1$
		lblImport.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		lblImport.setBounds(10, 11, 168, 27);
		panelTitle.add(lblImport);

		JLabel lblNewLabel = new JLabel(Messages.getString("StreamImport.4")); //$NON-NLS-1$
		lblNewLabel.setBounds(20, 40, 404, 23);
		panelTitle.add(lblNewLabel);
		{
			JSeparator separator = new JSeparator();
			separator.setBounds(0, 74, 634, 2);
			contentPanel.add(separator);
		}

		JSeparator separator = new JSeparator();
		separator.setName("test"); //$NON-NLS-1$
		separator.setBounds(0, 366, 634, 2);
		contentPanel.add(separator);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 86, 614, 232);
		contentPanel.add(scrollPane);

//		table = new JTable(mod);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		((JLabel)table.getDefaultRenderer(String.class)).setHorizontalAlignment(JLabel.RIGHT);
//		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
//		setTableWidth(table);
//		scrollPane.setViewportView(table);

		panel = new ImportPanel(data);
		scrollPane.setViewportView(panel);

		JButton btnSelAll = new JButton(Messages.getString("StreamImport.6")); //$NON-NLS-1$
		btnSelAll.setBounds(20, 330, 110, 23);
		btnSelAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.selectAll();
			}
		});
		contentPanel.add(btnSelAll);

		JButton btnDeselectAll = new JButton(Messages.getString("StreamImport.7")); //$NON-NLS-1$
		btnDeselectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.deselectAll();
			}
		});
		btnDeselectAll.setBounds(142, 330, 135, 23);
		contentPanel.add(btnDeselectAll);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(Messages.getString("StreamImport.8")); //$NON-NLS-1$
				buttonPane.add(okButton);
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent paramActionEvent) {
						ret = panel.getImportFields();
						dispose();
					}
				});
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(Messages.getString("StreamImport.9")); //$NON-NLS-1$
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent paramActionEvent) {
						ret = new Vector<MulticastData>();
						dispose();
					}
				});
				cancelButton.setActionCommand(Messages.getString("StreamImport.9")); //$NON-NLS-1$
				buttonPane.add(cancelButton);
			}
		}
	}

	public Vector<MulticastData> getRet() {
		return ret;
	}

	/**
	 * Static method with a Vector of MulticastData Streams as return Value. Generates a ProfileImport Dialog and waits for selection.
	 * Returns the Vector with selected fields if the ApproveSelection is chosen and else a Vector with zero entries.
	 * @param f
	 * 			JFrame - Parent Frame to get the icon.
	 * @param data
	 * 			Vector<MutlicastData> - Vector which holds the MulticastData
	 * @param mc
	 * 			MulticastController
	 * @return
	 * 			Vector<MulticastData> - on ApproveSelection returns selected values, else an empty Vector.
	 */
	public static Vector<MulticastData> showImportDialog(JFrame f, Vector<MulticastData> data, MulticastController mc) {
		StreamImport pi = new StreamImport(f, data, mc);
		pi.setVisible(true);
		pi.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		return pi.getRet();
	}
}