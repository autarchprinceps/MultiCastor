package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import zisko.multicastor.program.controller.ViewController;
/**
 * FileChooser für Speichern und Laden von Config Files
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class FrameFileChooser extends JFrame {
	/**
	 * JFileChooser Menu welches angepasst wird je nach dem ob es sich um einen Speichern oder Laden Dialog handelt.
	 */
	private JFileChooser chooser;
	/**
	 * Filter welcher bestimmt das nur .xml Dateien gespeichert und geladen werden.
	 */
	private FileNameExtensionFilter filter;
	/**
	 * Hilfsvariable welche bestimmt um was für eine Art von Dialog es sich handelt (Speichern/Laden).
	 */
	private boolean typeSave;
	/**
	 * Checkbox welche angibt ob der SenderV4 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_senderV4;
	/**
	 * Checkbox welche angibt ob der SenderV6 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_senderV6;
	/**
	 * Checkbox welche angibt ob der ReceiverV4 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_receiverV4;
	/**
	 * Checkbox welche angibt ob der ReceiverV6 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_receiverV6;
	/**
	 * Checkbox welche angibt ob inkrementell geladen werden soll oder nicht.
	 */
	private JCheckBox cb_incremental;
	/**
	 * Kontruktir des FileChoosers
	 * @param ctrl Benöigte Referenz zum GUI Controller
	 * @param save gibt an ob es sich um einen Datei speichern Dialog handelt
	 */
	public FrameFileChooser(ViewController ctrl, boolean save){
		typeSave = save;
		initWindow();
		initFileChooser(ctrl);
		initSelection();
	}
	/**
	 * Initialisiert die Checkboxen des Filechoosers
	 */
	private void initSelection() {
		JPanel selection = new JPanel();
		selection.setLayout(null);
		selection.setPreferredSize(new Dimension(600,60));
		cb_senderV4 = new JCheckBox("Sender IPv4",true);
		cb_senderV6 = new JCheckBox("Sender IPv6",true);
		cb_receiverV4 = new JCheckBox("Receiver IPv4",true);
		cb_receiverV6 = new JCheckBox("Receiver IPv6",true);
		
		cb_senderV4.setFont(MiscFont.getFont());
		cb_senderV6.setFont(MiscFont.getFont());
		cb_receiverV4.setFont(MiscFont.getFont());
		cb_receiverV6.setFont(MiscFont.getFont());
		
		cb_senderV4.setFocusable(false);
		cb_senderV6.setFocusable(false);
		cb_receiverV4.setFocusable(false);
		cb_receiverV6.setFocusable(false);
		
		cb_senderV4.setBounds(10,0,90,20);
		cb_senderV6.setBounds(10,30,90,20);
		cb_receiverV4.setBounds(110,0,100,20);
		cb_receiverV6.setBounds(110,30,100,20);
		
		
		selection.add(cb_senderV4);
		selection.add(cb_senderV6);
		selection.add(cb_receiverV4);
		selection.add(cb_receiverV6);
		if(!typeSave){
			cb_incremental = new JCheckBox("Incremental Load");
			cb_incremental.setFont(MiscFont.getFont());
			cb_incremental.setFocusable(false);
			cb_incremental.setBounds(220,0,120,20);
			selection.add(cb_incremental);
		}
		add(selection,BorderLayout.SOUTH);
	}
	/**
	 * Initialisiert die Dateiauswahl.
	 * @param ctrl Benötigte Referenz zum GUI Controller.
	 */
	private void initFileChooser(ViewController ctrl) {
		filter = new FileNameExtensionFilter("XML Config Files", "xml");
		chooser = new JFileChooser();
		if(typeSave){
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}
		else{
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		}
		//chooser.setControlButtonsAreShown(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		chooser.addActionListener(ctrl);
		add(chooser, BorderLayout.CENTER);
	}
	/**
	 * Setzt den Fenstertitel für Laden
	 */
	private void initLoad() {
		setTitle("Load Configuration");
		
	}
	/**
	 * Setzt den Fenstertitel für Speichern
	 */
	private void initSave() {
		setTitle("Save Configuration");
		
	}
	/**
	 * Konfiguriert das Frame in welchem sich der FileChooser befindet
	 */
	private void initWindow(){
		setLayout(new BorderLayout());
		setSize(600,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		if(typeSave){
			initSave();
		}
		else{
			initLoad();
		}
	}
	/**
	 * Funktion zum anzeigen und verbergen des FileChoosers
	 */
	public void toggle(){
		if(this.isVisible()){
			setVisible(false);
		}
		else{
			setVisible(true);
		}
	}
	public JFileChooser getChooser() {
		return chooser;
	}
	public FileNameExtensionFilter getFilter() {
		return filter;
	}
	public boolean isTypeSave() {
		return typeSave;
	}
	public JCheckBox getCb_senderV4() {
		return cb_senderV4;
	}
	public JCheckBox getCb_senderV6() {
		return cb_senderV6;
	}
	public JCheckBox getCb_receiverV4() {
		return cb_receiverV4;
	}
	public JCheckBox getCb_receiverV6() {
		return cb_receiverV6;
	}
	public JCheckBox getCb_incremental() {
		return cb_incremental;
	}
	public boolean isCbSenderV4Selected(){
		return cb_senderV4.isSelected();
	}
	public boolean isCbSenderV6Selected(){
		return cb_senderV6.isSelected();
	}
	public boolean isCbReceiverV4Selected(){
		return cb_receiverV4.isSelected();
	}
	public boolean isCbReceiverV6Selected(){
		return cb_receiverV6.isSelected();
	}
	public boolean isCbIncrementalSelected(){
		return cb_incremental.isSelected();
	}
	public String getSelectedFile(){
		if(chooser.getSelectedFile().getAbsolutePath().endsWith(".xml")){
			return ""+chooser.getSelectedFile().getAbsolutePath();
		}
		else{
			return ""+chooser.getSelectedFile().getAbsolutePath()+".xml";
		}
		
	}
}
