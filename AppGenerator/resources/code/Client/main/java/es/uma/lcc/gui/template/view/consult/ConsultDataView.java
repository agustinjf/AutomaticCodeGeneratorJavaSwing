package es.uma.lcc.gui.template.view.consult;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.exception.ClientException;
import es.uma.lcc.gui.template.process.ResourceManager;
import es.uma.lcc.gui.template.view.AbstractView;

/**
 * Clase de consulta
 * 
 * @author ajifernandez
 *
 */
public class ConsultDataView extends AbstractView {
	/** Serial version */
	private static final long serialVersionUID = 1L;

	public static final boolean EDIT_MODE = true;
	private ClientData data;
	private JLabel hostsLabel;
	private JLabel descriptionLabel;
	private JLabel isActiveLabel;
	private JTextField hostsField;
	private JTextField descriptionField;
	private JTextField isActiveField;
	private JPanel centerPanel;
	private JPanel southPanel;
	private boolean mode;

	private ResourceManager resourceManager;

	/**
	 * Constructor
	 */
	public ConsultDataView(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;

		setLayout(new BorderLayout());
		initComponents();
	}

	/**
	 * Inicializa los componentes de la vista
	 */
	private void initComponents() {

		this.southPanel = new JPanel();

		createCenterPanel();
		createSouthPanel();

		add(this.centerPanel, BorderLayout.CENTER);
		add(this.southPanel, BorderLayout.SOUTH);

	}

	/**
	 * Genera el panel sur
	 */
	private void createSouthPanel() {
		this.southPanel = new JPanel();
	}

	/**
	 * Genera el panel central
	 */
	private void createCenterPanel() {
		this.centerPanel = new JPanel(new GridBagLayout());
		// Inicializamos los componentes
		this.hostsLabel = new JLabel(
				this.resourceManager.getString("view.consult.label.text.hosts"));
		this.descriptionLabel = new JLabel(
				this.resourceManager
						.getString("view.consult.label.text.description"));
		this.isActiveLabel = new JLabel(
				this.resourceManager
						.getString("view.consult.label.text.isactive"));

		this.hostsField = new JTextField();
		this.hostsField.setPreferredSize(new Dimension(100, 30));
		this.hostsField.setEditable(false);
		this.hostsField.setToolTipText(this.resourceManager
				.getString("view.consult.label.tooltip.hosts"));
		this.descriptionField = new JTextField();
		this.descriptionField.setPreferredSize(new Dimension(100, 30));
		this.descriptionField.setEditable(false);
		this.descriptionField.setToolTipText(this.resourceManager
				.getString("view.consult.label.text.description"));
		this.isActiveField = new JTextField();
		this.isActiveField.setPreferredSize(new Dimension(100, 30));
		this.isActiveField.setToolTipText(this.resourceManager
				.getString("view.consult.label.tooltip.isactive"));
		this.isActiveField.setEditable(false);

		GridBagConstraints gbc = new GridBagConstraints();
		// Colocamos los labels
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		this.centerPanel.add(this.hostsLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		this.centerPanel.add(this.descriptionLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		this.centerPanel.add(this.isActiveLabel, gbc);

		// Colocamos los textfields
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		this.centerPanel.add(this.hostsField, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		this.centerPanel.add(this.descriptionField, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1;
		this.centerPanel.add(this.isActiveField, gbc);
	}

	/**
	 * Obtiene el valor de data
	 * 
	 * @return valor de data
	 */
	public ClientData getData() {
		return this.data;
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
	 * Actualiza los datos visuales del componente
	 */
	public void updateDataGUI() {
		this.hostsField.setText(this.data.getHost());
		this.descriptionField.setText(this.data.getDescription());
		this.isActiveField.setText(this.data.getIsActive().toString());
	}

	/**
	 * Muestra un mensaje de error
	 * 
	 * @param e
	 */
	public void showErrorMessage(ClientException e) {
		JOptionPane.showMessageDialog(this, e.getMessage(),
				this.resourceManager.getString("view.message.title.error"),
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un mensaje
	 * 
	 * @param msg
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg,
				this.resourceManager.getString("view.message.title"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Obtiene el valor de mode
	 * 
	 * @return valor de mode
	 */
	public boolean isMode() {
		return this.mode;
	}

	/**
	 * Establece el valor de mode
	 * 
	 * @param mode
	 *            a establecer
	 */
	public void setMode(boolean mode) {
		this.mode = mode;
	}
}
