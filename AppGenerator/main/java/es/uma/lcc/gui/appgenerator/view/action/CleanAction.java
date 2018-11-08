package es.uma.lcc.gui.appgenerator.view.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import es.uma.lcc.gui.appgenerator.data.ClientData;
import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;

/**
 * Clase encargada de la acción de limpieza de la vista
 *
 * @author ajifernandez
 *
 */
public class CleanAction implements ActionListener {

	private TFGFrame frame;

	/**
	 * Constructor
	 *
	 * @param process
	 *            Procesador principal de la aplicación
	 * @param tfgFrame
	 *            Vista principal de la aplicación
	 */
	public CleanAction(TFGFrame tfgFrame) {
		this.frame = tfgFrame;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		ClientData clientData = new ClientData();
		clientData.setTableDataList(new ArrayList<TableData>());
		frame.setClientData(clientData);

	}

}
