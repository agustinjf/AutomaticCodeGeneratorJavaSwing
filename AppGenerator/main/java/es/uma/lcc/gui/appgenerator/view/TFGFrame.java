package es.uma.lcc.gui.appgenerator.view;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import es.uma.lcc.gui.appgenerator.data.ClientData;
import es.uma.lcc.gui.appgenerator.data.GeneralData;
import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.data.TypeEnum;
import es.uma.lcc.gui.appgenerator.process.ResourceManager;
import es.uma.lcc.gui.appgenerator.process.TFGProcess;
import es.uma.lcc.gui.appgenerator.view.action.AddToTableAction;
import es.uma.lcc.gui.appgenerator.view.action.CleanAction;
import es.uma.lcc.gui.appgenerator.view.action.ClearTableAction;
import es.uma.lcc.gui.appgenerator.view.action.DeleteAction;
import es.uma.lcc.gui.appgenerator.view.action.EditAction;
import es.uma.lcc.gui.appgenerator.view.action.EnumClientAction;
import es.uma.lcc.gui.appgenerator.view.action.GenerateAction;

/**
 * Clase que modela el IHM de la aplicación
 *
 * @author ajifernandez
 *
 */
public class TFGFrame extends JPanel implements ActionListener {

	static Logger logger = LogManager.getLogger(TFGFrame.class);

	/** Serial version */
	private static final long serialVersionUID = 1L;

	// Constantes para exportación e importación
	private static final String ENCODING = "UTF-8";
	private static final String XML_HEADER_TAG = "<?xml version=\"1.0\"?>\n";

	// Action's commands
	private static final String ACTION_COMMAND_EXPORT = "E";
	private static final String ACTION_COMMAND_IMPORT = "I";
	private static final String ACTION_COMMAND_HELP = "H";

	private ResourceManager resourceManager;

	private TFGProcess process;

	private JTable table;

	private JPopupMenu popupMenu;

	private MyTableModel model;

	// Atributos generales
	private JTextField appNameField;
	private JTextField packageField;
	private JTextField userField;

	// Atributos para la tabla
	private JComboBox typeCombo;
	private JTextField nameAttributeField;
	private JTextField descriptionField;

	// Nombre del modelo de datos
	private JTextField dataModelField;

	private JDialog secundaria;

	private JMenuItem help;

	private JPanel centerPanel;

	/**
	 * @param process
	 * @param resourceManager2
	 * @throws HeadlessException
	 */
	public TFGFrame(TFGProcess process, ResourceManager resourceManager) throws HeadlessException {
		super();
		this.process = process;
		this.resourceManager = resourceManager;
		setLayout(new BorderLayout());
		// Establecemos el tamaño por defecto
		setPreferredSize(new Dimension(800, 600));

		add(createMenuBar(), BorderLayout.NORTH);
		centerPanel = createCenterPanel();
		add(centerPanel, BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Crea el panel con los botones del panel
	 *
	 * @return JPanel
	 */
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new FlowLayout());

		JButton generateButton = new JButton(resourceManager.getString("tfgframe.button.generate"));
		generateButton.setPreferredSize(new Dimension(100, 30));
		generateButton.setEnabled(true);
		generateButton.addActionListener(new GenerateAction(process, this, resourceManager));
		JButton cleanButton = new JButton(resourceManager.getString("tfgframe.button.clean"));
		cleanButton.setPreferredSize(new Dimension(100, 30));
		cleanButton.setEnabled(true);
		cleanButton.addActionListener(new CleanAction(this));

		buttonsPanel.add(generateButton);
		buttonsPanel.add(cleanButton);
		return buttonsPanel;
	}

	/**
	 * Crea el panel central de la aplicación
	 *
	 * @return
	 */
	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(createGeneralDataPanel(), BorderLayout.NORTH);
		centerPanel.add(createTableDataPanel(), BorderLayout.CENTER);
		centerPanel.add(createConfigDataPanel(), BorderLayout.SOUTH);
		return centerPanel;
	}

	/**
	 * Crea el panel de datos generales
	 *
	 * @return
	 */
	private JPanel createGeneralDataPanel() {
		JPanel generalDataPanel = new JPanel(new GridBagLayout());
		generalDataPanel.setBorder(BorderFactory.createTitledBorder(LineBorder.createBlackLineBorder(),
				resourceManager.getString("tfgframe.general.panel.border.title")));

		// Nombre aplicación
		JLabel nameLabel = new JLabel(resourceManager.getString("tfgframe.general.panel.name.label"));

		appNameField = createTextField("[a-zA-Z]");

		// Paquetería
		JLabel packageLabel = new JLabel(resourceManager.getString("tfgframe.general.panel.package.label"));

		packageField = createTextField("[a-z.]");

		// Usuario creación
		JLabel userLabel = new JLabel(resourceManager.getString("tfgframe.general.panel.user.label"));

		userField = createTextField("[a-zA-Z0-9 ._-]");

		// Colocamos los elementos en el panel
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridy = 0;
		gbc.gridx = 0;
		generalDataPanel.add(nameLabel, gbc);
		gbc.gridy = 0;
		gbc.gridx = 1;
		generalDataPanel.add(appNameField, gbc);
		gbc.gridy = 1;
		gbc.gridx = 0;
		generalDataPanel.add(packageLabel, gbc);
		gbc.gridy = 1;
		gbc.gridx = 1;
		generalDataPanel.add(packageField, gbc);
		gbc.gridy = 2;
		gbc.gridx = 0;
		generalDataPanel.add(userLabel, gbc);
		gbc.gridy = 2;
		gbc.gridx = 1;
		generalDataPanel.add(userField, gbc);

		return generalDataPanel;
	}

	/**
	 * Crea el panel con la tabla de atributos
	 *
	 * @return
	 */
	private JPanel createTableDataPanel() {
		JPanel tableDataPanel = new JPanel(new BorderLayout());

		JPanel dataModelPanel = new JPanel(new FlowLayout());
		JLabel dataModelLabel = new JLabel(resourceManager.getString("tfgframe.datamodel.label"));
		dataModelField = createTextField("[a-zA-Z]");
		dataModelPanel.add(dataModelLabel);
		dataModelPanel.add(dataModelField);
		tableDataPanel.add(dataModelPanel, BorderLayout.NORTH);

		tableDataPanel.setBorder(BorderFactory.createTitledBorder(LineBorder.createBlackLineBorder(),
				resourceManager.getString("tfgframe.table.panel.border.title")));

		setModel(new MyTableModel(resourceManager));
		table = new JTable(getModel());
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		tableDataPanel.add(scrollPane, BorderLayout.CENTER);

		popupMenu = new JPopupMenu();
		JMenuItem menuItemEdit = new JMenuItem(new EditAction(resourceManager.getString("tfgframe.table.panel.table.action.edit"), this, table));
		menuItemEdit.setActionCommand(EnumClientAction.EDIT.name());
		JMenuItem menuItemRemove = new JMenuItem(new DeleteAction(resourceManager.getString("tfgframe.table.panel.table.action.delete"), this, table));
		menuItemRemove.setActionCommand(EnumClientAction.DELETE.name());

		popupMenu.add(menuItemEdit);
		popupMenu.add(menuItemRemove);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		MouseListener popupListener = new PopupListener(popupMenu);
		table.addMouseListener(popupListener);

		return tableDataPanel;
	}

	/**
	 * Crea el panel de configuración de atributos
	 *
	 * @return
	 */
	private JPanel createConfigDataPanel() {
		JPanel configDataPanel = new JPanel(new GridBagLayout());
		configDataPanel.setBorder(BorderFactory.createTitledBorder(LineBorder.createBlackLineBorder(),
				resourceManager.getString("tfgframe.config.panel.border.title")));
		// Tipo (String)
		JLabel typeLabel = new JLabel(resourceManager.getString("tfgframe.config.panel.type.label"));

		typeCombo = new JComboBox(TypeEnum.values());

		// Nombre
		JLabel nameAttributeLabel = new JLabel(resourceManager.getString("tfgframe.config.panel.name.label"));

		nameAttributeField = createTextField("[a-zA-Z]");

		// Descripción
		JLabel descriptionLabel = new JLabel(resourceManager.getString("tfgframe.config.panel.description.label"));

		descriptionField = createTextField("[a-zA-Z ']");

		// Botones (Añadir/Limpiar)
		JButton addButton = new JButton(new AddToTableAction(resourceManager.getString("tfgframe.config.panel.add.button"), this, resourceManager));
		JButton cleanButton = new JButton(new ClearTableAction(resourceManager.getString("tfgframe.config.panel.clean.button"), this));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridy = 0;
		gbc.gridx = 0;
		configDataPanel.add(typeLabel, gbc);
		gbc.gridy = 0;
		gbc.gridx = 1;
		configDataPanel.add(typeCombo, gbc);
		gbc.gridy = 1;
		gbc.gridx = 0;
		configDataPanel.add(nameAttributeLabel, gbc);
		gbc.gridy = 1;
		gbc.gridx = 1;
		configDataPanel.add(nameAttributeField, gbc);
		gbc.gridy = 2;
		gbc.gridx = 0;
		configDataPanel.add(descriptionLabel, gbc);
		gbc.gridy = 2;
		gbc.gridx = 1;
		configDataPanel.add(descriptionField, gbc);

		gbc.gridy = 3;
		gbc.gridx = 0;
		configDataPanel.add(addButton, gbc);
		gbc.gridy = 3;
		gbc.gridx = 1;
		configDataPanel.add(cleanButton, gbc);

		return configDataPanel;
	}

	/**
	 * Crea un textField y le asigna parámetros generales
	 *
	 * @return
	 */
	private JTextField createTextField(String regex) {
		final String reg = regex;
		final JTextField result = new JTextField();
		String[] params = { regex };
		result.setToolTipText(resourceManager.getString("tfgframe.tooltip.textfield", params));
		result.setPreferredSize(new Dimension(400, 20));
		result.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
				String aux = "" + keyEvent.getKeyChar();
				if (aux.matches(reg)) {
					super.keyTyped(keyEvent);
				} else {
					keyEvent.consume();
					result.setText(result.getText());
				}
			}
		});
		return result;
	}

	/**
	 * Crea la barra de menu
	 *
	 * @return
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu(resourceManager.getString("main.menu"));

		JMenuItem mi1 = new JMenuItem(resourceManager.getString("main.menu.export"));
		mi1.setActionCommand(ACTION_COMMAND_EXPORT);
		mi1.addActionListener(this);
		mi1.setIcon(resourceManager.getImageIcon("export"));
		menu.add(mi1);

		JMenuItem mi2 = new JMenuItem(resourceManager.getString("main.menu.import"));
		mi2.setActionCommand(ACTION_COMMAND_IMPORT);
		mi2.addActionListener(this);
		mi2.setIcon(resourceManager.getImageIcon("import"));
		menu.add(mi2);

		menuBar.add(menu);

		// menuBar.add(new JSeparator());
		menuBar.add(Box.createHorizontalGlue());
		help = new JMenuItem();
		help.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		// help.setActionCommand(ACTION_COMMAND_HELP);
		// help.addActionListener(this);
		help.setIcon(resourceManager.getImageIcon("help"));
		putHelp();
		menuBar.add(help);

		return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// Variable donde almacenar la excepción ocurrida durante el proceso de
		// exportación o importación
		Exception exception = null;

		if (ACTION_COMMAND_EXPORT.equals(event.getActionCommand())) {
			ClientData clientData = new ClientData();
			clientData.setGeneralData(getGeneralData());
			clientData.setTableDataList(getTableDataList());

			XStream xstream = new XStream(new DomDriver());
			String xml = xstream.toXML(clientData);

			// Obtenemos la ruta del fichero
			JFileChooser chooser = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter(resourceManager.getString("tfgframe.filter.file"),
					resourceManager.getString("tfgframe.filter.file.xml.extension"));
			chooser.setFileFilter(filter);
			int retrival = chooser.showSaveDialog(null);
			if (retrival == JFileChooser.APPROVE_OPTION) {
				try {
					// Escribimos el contenido
					FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile().toString().contains(".xml") ? chooser.getSelectedFile()
							.toString() : chooser.getSelectedFile() + "." + resourceManager.getString("tfgframe.filter.file.xml.extension"));
					fos.write(XML_HEADER_TAG.getBytes(ENCODING));
					byte[] bytes = xml.getBytes(ENCODING);
					fos.write(bytes);
					fos.close();
				} catch (FileNotFoundException e) {
					exception = e;

					logger.log(Level.ERROR, resourceManager.getString("tfgframe.export.ko"), e);
				} catch (UnsupportedEncodingException e) {
					exception = e;

					logger.log(Level.ERROR, resourceManager.getString("tfgframe.export.ko"), e);
				} catch (IOException e) {
					exception = e;

					logger.log(Level.ERROR, resourceManager.getString("tfgframe.export.ko"), e);
				} catch (Exception e) {
					exception = e;

					logger.log(Level.ERROR, resourceManager.getString("tfgframe.export.ko"), e);
				}

				if (exception == null) {
					showResultMessage(resourceManager.getString("tfgframe.export.title"), resourceManager.getString("tfgframe.export.ok"), null);
				} else {
					showResultMessage(resourceManager.getString("tfgframe.export.title"), resourceManager.getString("tfgframe.export.ko"), exception);
				}
			}

		} else if (ACTION_COMMAND_IMPORT.equals(event.getActionCommand())) {
			// Obtenemos el fichero
			JFileChooser chooser = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter(resourceManager.getString("tfgframe.filter.file"),
					resourceManager.getString("tfgframe.filter.file.xml.extension"));
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int dialogResult = chooser.showOpenDialog(null);
			if (dialogResult == JFileChooser.APPROVE_OPTION) {

				File selectedFile = chooser.getSelectedFile();
				XStream xstream = new XStream(new DomDriver());
				try {
					ClientData clientData = (ClientData) xstream.fromXML(new FileInputStream(selectedFile.getAbsolutePath()));
					setClientData(clientData);
				} catch (FileNotFoundException e) {
					exception = e;

					logger.log(Level.ERROR, resourceManager.getString("tfgframe.import.ko"), e);
				}
			}

			if (exception == null) {
				showResultMessage(resourceManager.getString("tfgframe.import.title"), resourceManager.getString("tfgframe.import.ok"), null);
			} else {
				showResultMessage(resourceManager.getString("tfgframe.import.title"), resourceManager.getString("tfgframe.import.ko"), exception);
			}
		} else if (ACTION_COMMAND_HELP.equals(event.getActionCommand())) {
		} else {
			Logger.getLogger(TFGFrame.class.getName()).log(Level.INFO, resourceManager.getString("main.menu.error") + event.getActionCommand());
		}
	}

	/**
	 * Establece en el IHM los datos del argumento
	 *
	 * @param clientData
	 */
	public void setClientData(ClientData clientData) {
		appNameField.setText(clientData.getGeneralData().getAppName());
		packageField.setText(clientData.getGeneralData().getPackageName());
		userField.setText(clientData.getGeneralData().getUserName());
		dataModelField.setText(clientData.getGeneralData().getDataModelName());

		model.setDataList(clientData.getTableDataList());

	}

	/**
	 * Muestra por pantalla el resultado de la operación
	 *
	 * @param message
	 *            Mensaje a mostrar
	 * @param e
	 *            Excepción del error
	 */
	public void showResultMessage(String title, String message, Exception e) {
		if (e == null) {
			JOptionPane.showMessageDialog(TFGFrame.this, message, title, JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(TFGFrame.this, message + "\n" + e.getMessage(), title, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Obtiene de la vista los datos de información general
	 *
	 * @return generalData con la información de la vista
	 */
	public GeneralData getGeneralData() {
		GeneralData data = new GeneralData();
		data.setAppName(appNameField.getText());
		data.setPackageName(packageField.getText());
		data.setUserName(userField.getText());
		data.setDataModelName(dataModelField.getText());
		return data;
	}

	/**
	 * Obtenemos el objeto que representa a un atributo del modelo de datos
	 *
	 * @return representación de un atributo del modelo de datos
	 */
	public TableData getTableData() {
		TableData tableData = new TableData();
		tableData.setDescription(descriptionField.getText());
		tableData.setName(nameAttributeField.getText());
		tableData.setType((TypeEnum) typeCombo.getSelectedItem());
		return tableData;
	}

	/**
	 * Obtiene los elementos que formarán el modelo de datos de la aplicación
	 *
	 * @return Lista de atributos del modelo de datos
	 */
	public List<TableData> getTableDataList() {
		return getModel().getDataList();
	}

	/**
	 * Devuelve el valor del atributo model
	 *
	 * @return atributo model
	 */
	public MyTableModel getModel() {
		return model;
	}

	/**
	 * Establece el atributo model
	 *
	 * @param model
	 *            atributo model a establecer
	 */
	public void setModel(MyTableModel model) {
		this.model = model;
	}

	

	

	/**
	 * Establece el process al frame
	 *
	 * @param process
	 */
	public void setProcess(TFGProcess process) {
		this.process = process;
	}

	/**
	 * Mostramos una lista de mensajes de error
	 *
	 * @param title
	 * @param messageList
	 */
	public void showResultMessage(String title, List<String> messageList) {
		String finalMessage = "";
		for (String message : messageList) {
			finalMessage += message + "\n";
		}

		JOptionPane.showMessageDialog(TFGFrame.this, finalMessage, title, JOptionPane.ERROR_MESSAGE);

	}

	/**
	 * Devuelve el valor del atributo nameAttributeField
	 *
	 * @return atributo nameAttributeField
	 */
	public JTextField getNameAttributeField() {
		return nameAttributeField;
	}

	/**
	 * Establece el atributo nameAttributeField
	 *
	 * @param nameAttributeField
	 *            atributo nameAttributeField a establecer
	 */
	public void setNameAttributeField(JTextField nameAttributeField) {
		this.nameAttributeField = nameAttributeField;
	}

	/**
	 * Devuelve el valor del atributo descriptionField
	 *
	 * @return atributo descriptionField
	 */
	public JTextField getDescriptionField() {
		return descriptionField;
	}

	/**
	 * Establece el atributo descriptionField
	 *
	 * @param descriptionField
	 *            atributo descriptionField a establecer
	 */
	public void setDescriptionField(JTextField descriptionField) {
		this.descriptionField = descriptionField;
	}

	/**
	 * Devuelve el valor del atributo typeCombo
	 *
	 * @return atributo typeCombo
	 */
	public JComboBox getTypeCombo() {
		return typeCombo;
	}

	/**
	 * Establece el atributo typeCombo
	 *
	 * @param typeCombo
	 *            atributo typeCombo a establecer
	 */
	public void setTypeCombo(JComboBox typeCombo) {
		this.typeCombo = typeCombo;
	}

	/**
	 * Devuelve el valor del atributo dataModelField
	 *
	 * @return atributo dataModelField
	 */
	public JTextField getDataModelField() {
		return dataModelField;
	}

	/**
	 * Establece el atributo dataModelField
	 *
	 * @param dataModelField
	 *            atributo dataModelField a establecer
	 */
	public void setDataModelField(JTextField dataModelField) {
		this.dataModelField = dataModelField;
	}

	/**
	 * Hace que el item del menu y la pulsacion de F1 visualicen la ayuda.
	 */
	private void putHelp() {
		try {
			// Carga el fichero de ayuda
			String helpHS = "es/uma/lcc/gui/appgenerator/help_set.hs";
			logger.log(Level.INFO,helpHS);
			ClassLoader cl = TFGFrame.class.getClassLoader();
			logger.log(Level.INFO,cl);
			// File fichero = new
			// File("es/uma/lcc/gui/appgenerator/help_set.hs");
			URL hsURL = HelpSet.findHelpSet(cl, helpHS);
			logger.log(Level.INFO,hsURL);
			// URL hsURL = fichero.toURI().toURL();

			// Crea el HelpSet y el HelpBroker
			HelpSet helpset = new HelpSet(getClass().getClassLoader(), hsURL);
			HelpBroker hb = helpset.createHelpBroker();

			// Pone ayuda a item de menu al pulsarlo y a F1 en ventana principal
			hb.enableHelpOnButton(help, "aplicacion", helpset);
			hb.enableHelpKey(this, "aplicacion", helpset);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
