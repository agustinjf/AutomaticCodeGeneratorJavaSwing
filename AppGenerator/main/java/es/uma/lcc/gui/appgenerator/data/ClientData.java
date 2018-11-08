package es.uma.lcc.gui.appgenerator.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos que servirá para exportar e importar lo que hay en el IHM
 *
 * @author ajifernandez
 *
 */
public class ClientData {

	/** Datos generales de la aplicación */
	GeneralData generalData;
	/** Atributos del modelo de datos */
	List<TableData> tableDataList;

	/**
	 * Constructor
	 */
	public ClientData() {
		super();
		generalData = new GeneralData();
		tableDataList = new ArrayList<TableData>();
	}

	/**
	 * Devuelve el valor del atributo generalData
	 *
	 * @return atributo generalData
	 */
	public GeneralData getGeneralData() {
		return generalData;
	}

	/**
	 * Establece el atributo generalData
	 *
	 * @param generalData
	 *            atributo generalData a establecer
	 */
	public void setGeneralData(GeneralData generalData) {
		this.generalData = generalData;
	}

	/**
	 * Devuelve el valor del atributo tableDataList
	 *
	 * @return atributo tableDataList
	 */
	public List<TableData> getTableDataList() {
		return tableDataList;
	}

	/**
	 * Establece el atributo tableDataList
	 *
	 * @param tableDataList
	 *            atributo tableDataList a establecer
	 */
	public void setTableDataList(List<TableData> tableDataList) {
		this.tableDataList = tableDataList;
	}

	@Override
	public String toString() {
		return "ClientData [generalData=" + generalData + ", tableDataList=" + tableDataList + "]";
	}

}
