package es.uma.lcc.gui.template.view.list.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class ClientTableModel<T> extends AbstractTableModel {

	/** Serial Version */
	private static final long serialVersionUID = 1L;

	public abstract List<T> getDataList();

}
