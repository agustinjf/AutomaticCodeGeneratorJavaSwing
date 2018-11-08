package es.uma.lcc.gui.appgenerator.view.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.uma.lcc.gui.appgenerator.data.GeneralData;
import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.process.ResourceManager;
import es.uma.lcc.gui.appgenerator.process.TFGProcess;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;

/**
 * Clase encargada de la acción de generación
 *
 * Dependecias!!!! Librerías ant y xercesimpl
 * http://www.srccodes.com/p/article/9
 * /Invoke-and-Execute-Hello-World-Ant-Script-Programmatically-using-Java-Code
 *
 * build.xml debe seguir esto al inicio <project name="Server_Interface"
 * default="compile">
 *
 * @author ajifernandez
 *
 */
public class GenerateAction implements ActionListener {

	private TFGProcess process;
	private TFGFrame frame;
	private ResourceManager resourceManager;

	private List<String> messageList = new ArrayList<String>();

	/**
	 * Constructor
	 *
	 * @param process
	 *            Procesador principal de la aplicación
	 * @param tfgFrame
	 *            Vista principal de la aplicación
	 * @param resourceManager
	 */
	public GenerateAction(TFGProcess process, TFGFrame tfgFrame, ResourceManager resourceManager) {
		this.process = process;
		this.frame = tfgFrame;
		this.resourceManager = resourceManager;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		messageList = new ArrayList<String>();

		if (isPanelValid()) {
			process.generate();
		} else {
			frame.showResultMessage(resourceManager.getString("tfgframe.generate.title"), messageList);
		}
	}

	/**
	 * Retorna el estado de validez del panel
	 *
	 * @return
	 */
	private boolean isPanelValid() {

		GeneralData generalData = frame.getGeneralData();
		List<TableData> tableDataList = frame.getTableDataList();

		if (generalData != null) {
			if (generalData.getAppName() != null && !"".equals(generalData.getAppName())) {
			} else {
				messageList.add(resourceManager.getString("tfgframe.generate.error.generaldata.name"));
			}

			if (generalData.getPackageName() != null && !"".equals(generalData.getPackageName())) {
			} else {
				messageList.add(resourceManager.getString("tfgframe.generate.error.generaldata.package"));
			}
			if (generalData.getUserName() != null && !"".equals(generalData.getUserName())) {
			} else {
				messageList.add(resourceManager.getString("tfgframe.generate.error.generaldata.user"));
			}
			if (generalData.getDataModelName() != null && !"".equals(generalData.getDataModelName())) {
			} else {
				messageList.add(resourceManager.getString("tfgframe.generate.error.generaldata.datamodel"));
			}
		} else {
			messageList.add(resourceManager.getString("tfgframe.generate.error.generaldata"));
		}

		if (tableDataList != null && !tableDataList.isEmpty() && allTableDataIsValid(tableDataList)) {
		} else {
			messageList.add(resourceManager.getString("tfgframe.generate.error.tabledata"));
		}

		return messageList.isEmpty();
	}

	/**
	 * Evaluamos si todos los elemenos de la tabla son válidos o no
	 *
	 * @param tableDataList
	 * @return
	 */
	private boolean allTableDataIsValid(List<TableData> tableDataList) {
		// Purgamos la lista de atributos que se llamen igual, por si llegan
		// iguales
		Set<TableData> tableDataSet = new HashSet<TableData>();
		for (TableData tableData : tableDataList) {
			tableDataSet.add(tableData);
		}

		boolean isValid = true;
		if (tableDataSet.size() != tableDataList.size()) {
			messageList.add(resourceManager.getString("tfgframe.generate.error.tabledata.repeated"));

			isValid = false;
		} else {

			for (TableData tableData : tableDataSet) {
				if (!isTableDataValid(tableData)) {
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}

	/**
	 * Evalua si un elemento de la tabla es válido
	 *
	 * @param tableData
	 *
	 * @return
	 */
	private boolean isTableDataValid(TableData tableData) {
		return tableData != null && tableData.getDescription() != null && !"".equals(tableData.getDescription()) && tableData.getName() != null
				&& !"".equals(tableData.getName()) && tableData.getType() != null && !"".equals(tableData.getType());
	}
}
