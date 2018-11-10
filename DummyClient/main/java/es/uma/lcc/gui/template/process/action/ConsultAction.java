package es.uma.lcc.gui.template.process.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.process.ClientProcess;
import es.uma.lcc.gui.template.view.list.model.ClientTableModel;

/**
 * Acción de consulta
 *
 * @author ajifernandez
 *
 */
public class ConsultAction extends AbstractAction {

	/** Serial version */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private ClientProcess process;

	/**
	 * Constructor
	 *
	 * @param title
	 * @param table
	 * @param process
	 */
	public ConsultAction(String title, JTable table, ClientProcess process) {
		super(title);
		this.table = table;
		this.process = process;
	}

	/**
	 * Método encargado de realizar la acción
	 */
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent actionEvent) {
		if (EnumClientAction.CONSULT.name().equals(
				actionEvent.getActionCommand())) {
			int row = table.getSelectedRow();
			ClientTableModel<ClientData> model = (ClientTableModel<ClientData>) table
					.getModel();
			ClientData hostsData = model.getDataList().get(row);
			process.openConsult(hostsData);
		}
	}
}
