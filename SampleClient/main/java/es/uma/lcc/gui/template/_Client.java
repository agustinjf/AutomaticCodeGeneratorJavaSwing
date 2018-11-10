package es.uma.lcc.gui.template;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import es.uma.lcc.gui.template.process.ClientProcess;
import es.uma.lcc.gui.template.process.ResourceManager;

/**
 * Clase principal del cliente
 *
 * @author ajifernandez
 *
 */
public class _Client extends JPanel implements ActionListener {
	/** Serial version */
	private static final long serialVersionUID = 1L;

	// Action's commands
	private static final String ACTION_COMMAND_CREATE = "C";
	private static final String ACTION_COMMAND_LIST = "L";

	// Variables
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem mi1, mi2;
	private static JLabel infoLabel;
	private static ClientProcess process = null;
	private static JTabbedPane tabbedPane;
	private static JFrame frame;
	private static JPanel infoPanel;

	private static ResourceManager resourceManager;

	/**
	 * Constructor
	 */
	public _Client() {
		super(new BorderLayout());

		// Inicializamos el menú
		this.menuBar = new JMenuBar();
		this.menu = new JMenu(resourceManager.getString("main.menu"));
		this.menuBar.add(this.menu);
		this.mi1 = new JMenuItem(resourceManager.getString("main.menu.list"));
		this.mi1.setActionCommand(ACTION_COMMAND_LIST);
		this.mi1.addActionListener(this);
		this.menu.add(this.mi1);
		this.mi2 = new JMenuItem(resourceManager.getString("main.menu.create"));
		this.mi2.setActionCommand(ACTION_COMMAND_CREATE);
		this.mi2.addActionListener(this);
		this.menu.add(this.mi2);

		// Creamos el panel de pestañas
		tabbedPane = new JTabbedPane();

		// Creamos el panel de información
		infoPanel = createInfoPanel();

		// Añadimos los objetos en el layout
		add(this.menuBar, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
		add(infoPanel, BorderLayout.SOUTH);

		// Utilizamos scroll de tabs
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		// Establecemos el tamaño por defecto
		setPreferredSize(new Dimension(600, 600));

		tabbedPane.putClientProperty(
				SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY,
				Boolean.TRUE);
		tabbedPane.revalidate();
		tabbedPane.repaint();
	}

	/**
	 * Crea el panel de información
	 *
	 * @return JPanel
	 */
	private JPanel createInfoPanel() {
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoLabel = new JLabel();
		infoPanel.add(infoLabel, BorderLayout.NORTH);

		return infoPanel;
	}

	/**
	 * Crea el gui y lo muestra.
	 */
	private static void createAndShowGUI() {
		// Crea el frame principal
		frame = new JFrame(resourceManager.getString("appName"));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Añadimos el contenido
		frame.add(new _Client(), BorderLayout.CENTER);

		// Mostramos la ventana
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (ACTION_COMMAND_LIST.equals(event.getActionCommand())) {
			process.openList();
		} else if (ACTION_COMMAND_CREATE.equals(event.getActionCommand())) {
			process.openCreate();
		} else {
			Logger.getLogger(_Client.class.getName()).log(
					Level.INFO,
					resourceManager.getString("main.menu.error")
							+ event.getActionCommand());
		}

	}

	/**
	 * Devuelve el valor de infoLabel
	 *
	 * @return valor de infoLabel
	 */
	public JLabel getInfoLabel() {
		return infoLabel;
	}

	/**
	 * Establece el valor de infoLabel
	 *
	 * @param infoLabel
	 *            a establecer
	 */
	public void setInfoLabel(JLabel infoLabel) {
		this.infoLabel = infoLabel;
	}

	/**
	 * Metodo principal del módulo
	 *
	 * @param args
	 *            Argumentos
	 */
	public static void main(String[] args) {
		// Obtenemos el fichero de log para configurarlo
		try {
			InputStream stream = _Client.class.getClassLoader().getResourceAsStream("logging.properties");
			LogManager.getLogManager().readConfiguration(stream);

			resourceManager = new ResourceManager();
			JFrame.setDefaultLookAndFeelDecorated(true);
			// Schedule a job for the event dispatch thread:
			// creating and showing this application's GUI.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
					process = new ClientProcess(frame, tabbedPane, infoLabel,
							resourceManager);
				}
			});
		} catch (SecurityException e) {
			Logger.getLogger("_Client").log(Level.SEVERE,
					"Error starting client", e);
		} catch (FileNotFoundException e) {
			Logger.getLogger("_Client").log(Level.SEVERE,
					"Error starting client", e);
		} catch (IOException e) {
			Logger.getLogger("_Client").log(Level.SEVERE,
					"Error starting client", e);
		}
	}
}
