package es.uma.lcc.gui.appgenerator.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;
import es.uma.lcc.gui.appgenerator.view.TableDataModel;

public class EditAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private TFGFrame tfgFrame;
	private JTable table;

	public EditAction(String title, TFGFrame tfgFrame, JTable table) {
		super(title);
		this.tfgFrame = tfgFrame;
		this.table = table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();
		TableDataModel<TableData> model = (TableDataModel<TableData>) table.getModel();
		TableData tableData = model.getDataList().get(row);

		this.tfgFrame.getNameAttributeField().setText(tableData.getName());
		this.tfgFrame.getDescriptionField().setText(tableData.getDescription());
		this.tfgFrame.getTypeCombo().setSelectedItem(tableData.getType());

	}

}
