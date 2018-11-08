package es.uma.lcc.gui.appgenerator.view.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.process.ResourceManager;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;

/**
 * Acción para añadir un elemento a la tabla
 *
 * @author ajifernandez
 *
 */
public class AddToTableAction extends AbstractAction {

	/** SerialVersion UID */
	private static final long serialVersionUID = 1L;

	private TFGFrame tfgFrame;

	private ResourceManager resourceManager;

	/**
	 * Constructor
	 *
	 * @param tfgFrame
	 */
	public AddToTableAction(String title, TFGFrame tfgFrame, ResourceManager resourceManager) {
		super(title);
		this.tfgFrame = tfgFrame;
		this.resourceManager = resourceManager;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		TableData tableData = tfgFrame.getTableData();
		if (tableData != null && tableData.getDescription() != null && !"".equals(tableData.getDescription()) && tableData.getName() != null
				&& !"".equals(tableData.getName())) {
			tfgFrame.getModel().addDataList(tfgFrame.getTableData());
			ClearTableAction action = new ClearTableAction("", tfgFrame);
			action.actionPerformed(null);
		} else {
			List<String> messageList = new ArrayList<String>();
			messageList.add(resourceManager.getString("tfgframe.table.panel.table.error.data"));
			tfgFrame.showResultMessage(resourceManager.getString("tfgframe.table.panel.table.error.title"), messageList);
		}

	}

}
