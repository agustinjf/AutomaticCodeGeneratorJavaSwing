package es.uma.lcc.gui.appgenerator.view;

import java.util.ArrayList;
import java.util.List;

import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.process.ResourceManager;

/**
 * Modelo de datos de la tabla
 *
 * @author ajifernandez
 *
 */
public class MyTableModel extends TableDataModel<TableData> {
	/** Serial version */
	private static final long serialVersionUID = 1L;

	// Cabeceras de las columnas
	private String[] columnNames;

	private List<TableData> dataList = new ArrayList<TableData>();

	private ResourceManager resourceManager;

	public MyTableModel(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;

		initData();
	}

	private void initData() {
		columnNames = new String[] { resourceManager.getString("tfgframe.table.panel.table.type"),
				resourceManager.getString("tfgframe.table.panel.table.name"),
				resourceManager.getString("tfgframe.table.panel.table.description") };
	}

	/**
	 * Obtiene el número de columnas
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Obtiene el número de elementos (filas)
	 */
	public int getRowCount() {
		return dataList.size();
	}

	/**
	 * Obtiene el nombre de la columna
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Obtiene el valor de la celda indicada
	 */
	public Object getValueAt(int row, int col) {
		Object result = null;
		switch (col) {
		case 0:
			result = dataList.get(row).getType();
			break;
		case 1:
			result = dataList.get(row).getName();
			break;
		case 2:
			result = dataList.get(row).getDescription();
			break;
		}
		return result;
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for each
	 * cell. If we didn't implement this method, then the last column would contain
	 * text ("true"/"false"), rather than a check box.
	 */
	public Class<?> getColumnClass(int c) {
		Class<?> clazz = null;
		switch (c) {
		case 0:
			clazz = dataList.get(0).getType().getClass();
			break;
		case 1:
			clazz = dataList.get(0).getName().getClass();
			break;
		case 2:
			clazz = dataList.get(0).getDescription().getClass();
			break;
		}
		return clazz;
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		dataList.set(row, (TableData) value);
		fireTableCellUpdated(row, col);
	}

	/**
	 * Obtiene la lista de elementos de la tabla
	 */
	public List<TableData> getDataList() {
		return dataList;
	}

	/**
	 * Establece la lista de elementos de la tabla
	 *
	 * @param dataList
	 */
	public void setDataList(List<TableData> dataList) {
		this.dataList = dataList;
		fireTableDataChanged();
	}

	/**
	 * Añade un elemento a la tabla
	 *
	 * @param newData
	 */
	public void addDataList(TableData newData) {
		// Comprobamos si existe o no ya en la lista, para actualizar dicho
		// dato
		boolean found = false;
		for (TableData innerData : dataList) {
			if (innerData.getName().equals(newData.getName())) {
				innerData.setDescription(newData.getDescription());
				innerData.setName(newData.getName());
				innerData.setType(newData.getType());
				found = true;
			}
		}
		if (!found) {
			this.dataList.add(dataList.size(), newData);
		}

		fireTableDataChanged();

	}

	/**
	 * Elimina un elemento de la tabla
	 *
	 * @param oldData
	 */
	public void removeDataList(TableData oldData) {
		// Comprobamos si existe o no ya en la lista, para actualizar dicho
		// dato
		for (int i = dataList.size() - 1; i >= 0; i--) {
			TableData innerData = dataList.get(i);
			if (innerData.getName().equals(oldData.getName())) {
				dataList.remove(i);
			}
		}
	}
}