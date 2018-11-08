package es.uma.lcc.gui.template.view.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.exception.ClientException;
import es.uma.lcc.gui.template.process.ClientProcess;
import es.uma.lcc.gui.template.process.ResourceManager;
import es.uma.lcc.gui.template.view.AbstractView;

/**
 * Clase de edición y creación
 *
 * @author ajifernandez
 *
 */
public class DataView extends AbstractView {
	/** Serial version */
	private static final long serialVersionUID = 1L;

	public static final boolean EDIT_MODE = true;

	private String TRUE = "TRUE";
	private ClientData data;
	private JLabel hostsLabel;
	private JLabel descriptionLabel;
	private JLabel isActiveLabel;
	private JTextField hostsField;
	private JTextField descriptionField;
	private JTextField isActiveField;
	private JPanel centerPanel;
	private JPanel southPanel;
	private JButton saveButton;
	private ClientProcess process;
	private boolean mode;

	private ResourceManager resourceManager;

	/**
	 * Constructor
	 */
	public DataView(ClientProcess process, ResourceManager resourceManager) {
		this.process = process;
		this.resourceManager = resourceManager;
		setLayout(new BorderLayout());
		initComponents();

	}

	/**
	 * Obtiene el dato de pantalla
	 *
	 * @return dato de pantalla
	 */
	public ClientData getGuiData() {
		ClientData data = getData();
		if (data == null) {
			data = new ClientData();
		}

		data.setHost(hostsField.getText());
		data.setDescription(descriptionField.getText());
		data.setIsActive(TRUE.equals(isActiveField.getText()) ? Boolean.TRUE
				: Boolean.FALSE);
		return data;
	}

	/**
	 * Inicializa los componentes de la vista
	 */
	private void initComponents() {

		southPanel = new JPanel();

		createCenterPanel();
		createSouthPanel();

		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);

	}

	/**
	 * Crea el panel sur
	 */
	private void createSouthPanel() {
		southPanel = new JPanel();

		// Inicializa los componentes
		saveButton = new JButton(
				resourceManager.getString("process.view.data.button.save"));
		saveButton.setPreferredSize(new Dimension(100, 30));
		saveButton.setEnabled(true);
		saveButton.addActionListener(new SaveAction());

		southPanel.add(saveButton);

	}

	/**
	 * Crea el panel central
	 */
	private void createCenterPanel() {
		centerPanel = new JPanel(new GridBagLayout());
		// Inicializa los componentes
		hostsLabel = new JLabel(
				resourceManager.getString("process.view.data.label.hosts"));
		descriptionLabel = new JLabel(
				resourceManager
						.getString("process.view.data.label.description"));
		isActiveLabel = new JLabel(
				resourceManager.getString("process.view.data.label.isactive"));

		hostsField = new JTextField();
		hostsField.setPreferredSize(new Dimension(100, 30));
		descriptionField = new JTextField();
		descriptionField.setPreferredSize(new Dimension(100, 30));
		isActiveField = new JTextField();
		isActiveField.setPreferredSize(new Dimension(100, 30));
		isActiveField.setToolTipText(resourceManager
				.getString("process.view.data.label.tooltip.isactive"));

		GridBagConstraints gbc = new GridBagConstraints();
		// Colocamos los labels
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		centerPanel.add(hostsLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		centerPanel.add(descriptionLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		centerPanel.add(isActiveLabel, gbc);

		// Colocamos los textfields
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		centerPanel.add(hostsField, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		centerPanel.add(descriptionField, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1;
		centerPanel.add(isActiveField, gbc);
	}

	/**
	 * Obtiene el valor de data
	 *
	 * @return valor de data
	 */
	public ClientData getData() {
		return data;
	}

	/**
	 * Establece el valor de data
	 *
	 * @param data
	 *            a establecer
	 */
	public void setData(ClientData data) {
		this.data = data;
	}

	/**
	 * Muestra un mensaje de error
	 *
	 * @param e
	 */
	public void showErrorMessage(ClientException e) {
		JOptionPane.showMessageDialog(this, e.getMessage(),
				resourceManager.getString("view.message.title.error"),
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un mensaje
	 *
	 * @param msg
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg,
				resourceManager.getString("view.message.title"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean isMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	public void updateDataGUI() {
		this.hostsField.setText(data.getHost());
		this.descriptionField.setText(data.getDescription());
		this.isActiveField.setText(data.getIsActive().toString());
	}

	public class SaveAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			process.save(getGuiData());
		}
	}

}
