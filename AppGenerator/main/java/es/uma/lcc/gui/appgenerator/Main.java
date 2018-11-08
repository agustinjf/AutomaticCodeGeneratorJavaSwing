package es.uma.lcc.gui.appgenerator;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.BasicConfigurator;

import es.uma.lcc.gui.appgenerator.process.ResourceManager;
import es.uma.lcc.gui.appgenerator.process.TFGProcess;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;

/**
 * Clase principal el proyecto
 *
 * @author ajifernandez
 *
 */
public class Main {
	static TFGProcess process;

	private static JFrame frame;

	private static ResourceManager resourceManager;

	private static TFGFrame tfgFrame;

	/**
	 * Método principal
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		resourceManager = new ResourceManager();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				process = new TFGProcess(resourceManager);
				createAndShowGUI();
				process.setTfgFrame(tfgFrame);
			}
		});
	}

	/**
	 * Crea el gui y lo muestra.
	 */
	private static void createAndShowGUI() {
		// Crea el frame principal
		frame = new JFrame(resourceManager.getString("appName"));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Añadimos el contenido
		tfgFrame = new TFGFrame(process, resourceManager);
		frame.add(tfgFrame, BorderLayout.CENTER);
		frame.setGlassPane(process.getGlassPanel());

		// Mostramos la ventana
		frame.pack();
		frame.setVisible(true);
	}

}
