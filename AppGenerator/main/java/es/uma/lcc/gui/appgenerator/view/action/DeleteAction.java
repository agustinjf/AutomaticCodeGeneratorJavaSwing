package es.uma.lcc.gui.appgenerator.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;
import es.uma.lcc.gui.appgenerator.view.TableDataModel;

/**
 * Borra un elemento de la tabla
 *
 * @author ajifernandez
 *
 */
public class DeleteAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private TFGFrame frame;
	private JTable table;

	public DeleteAction(String title, TFGFrame tfgFrame, JTable table) {
		super(title);
		this.frame = tfgFrame;
		this.table = table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();
		TableDataModel<TableData> model = (TableDataModel<TableData>) table.getModel();
		TableData tableData = model.getDataList().get(row);
		frame.getModel().removeDataList(tableData);

	}

}
