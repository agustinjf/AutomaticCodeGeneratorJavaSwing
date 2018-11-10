package es.uma.lcc.gui.template.process.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.process.ClientProcess;
import es.uma.lcc.gui.template.view.list.model.ClientTableModel;

/**
 * Acción de editado
 *
 * @author ajifernandez
 *
 */
public class EditAction extends AbstractAction {

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
	public EditAction(String title, JTable table, ClientProcess process) {
		super(title);
		this.table = table;
		this.process = process;
	}

	/**
	 * Método que realiza la acción
	 */
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent actionEvent) {
		if (EnumClientAction.EDIT.name().equals(actionEvent.getActionCommand())) {
			// Pedir datos al servidor si es necesario
			int row = table.getSelectedRow();
			ClientTableModel<ClientData> model = (ClientTableModel<ClientData>) table
					.getModel();
			ClientData hostsData = model.getDataList().get(row);
			process.openEdit(hostsData);
		}
	}
}
