package es.uma.lcc.gui.appgenerator.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class TableDataModel<T> extends AbstractTableModel {

	/** Serial Version */
	private static final long serialVersionUID = 1L;

	public abstract List<T> getDataList();

}