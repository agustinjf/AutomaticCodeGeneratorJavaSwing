package es.uma.lcc.gui.template.view.list;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.exception.ClientException;
import es.uma.lcc.gui.template.process.ClientProcess;
import es.uma.lcc.gui.template.process.ResourceManager;
import es.uma.lcc.gui.template.process.action.ConsultAction;
import es.uma.lcc.gui.template.process.action.DeleteAction;
import es.uma.lcc.gui.template.process.action.EditAction;
import es.uma.lcc.gui.template.process.action.EnumClientAction;
import es.uma.lcc.gui.template.view.AbstractView;
import es.uma.lcc.gui.template.view.list.model.ClientTableModel;

public class ListView extends AbstractView {

	/** Serial version */
	private static final long serialVersionUID = 1L;

	private JTable table;

	private JPopupMenu popupMenu;

	private ClientProcess process;

	private ConsultAction consultAction;

	private ResourceManager resourceManager;

	/**
	 * Constructor
	 *
	 * @param clientProcess
	 */
	public ListView(ClientProcess clientProcess, ResourceManager resourceManager) {
		setLayout(new GridLayout(1, 0));
		this.process = clientProcess;
		this.resourceManager = resourceManager;
		table = new JTable(new MyTableModel());
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		popupMenu = new JPopupMenu();
		consultAction = new ConsultAction(
				resourceManager.getString("listview.action.consult"), table,
				process);
		JMenuItem menuItemConsult = new JMenuItem(consultAction);
		menuItemConsult.setActionCommand(EnumClientAction.CONSULT.name());
		JMenuItem menuItemEdit = new JMenuItem(new EditAction(
				resourceManager.getString("listview.action.edit"), table,
				process));
		menuItemEdit.setActionCommand(EnumClientAction.EDIT.name());
		JMenuItem menuItemRemove = new JMenuItem(new DeleteAction(
				resourceManager.getString("listview.action.delete"), table,
				process));
		menuItemRemove.setActionCommand(EnumClientAction.DELETE.name());

		popupMenu.add(menuItemConsult);
		popupMenu.add(menuItemEdit);
		popupMenu.add(menuItemRemove);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		MouseListener popupListener = new PopupListener();
		table.addMouseListener(popupListener);

		// table.setComponentPopupMenu(popupMenu);
	}

	class MyTableModel extends ClientTableModel<ClientData> {
		/** Serial version */
		private static final long serialVersionUID = 1L;

		private String[] columnNames = {
				resourceManager.getString("listview.header.isactive"),
				resourceManager.getString("listview.header.hosts"),
				resourceManager.getString("listview.header.description") };
		private List<ClientData> data = new ArrayList<ClientData>();

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			Object result = null;
			switch (col) {
			case 0:
				result = data.get(row).getIsActive();
				break;
			case 1:
				result = data.get(row).getHost();
				break;
			case 2:
				result = data.get(row).getDescription();
				break;
			}
			return result;
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		public Class<?> getColumnClass(int c) {
			Class<?> clazz = null;
			switch (c) {
			case 0:
				clazz = data.get(0).getIsActive().getClass();
				break;
			case 1:
				clazz = data.get(0).getHost().getClass();
				break;
			case 2:
				clazz = data.get(0).getDescription().getClass();
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
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		public void setValueAt(Object value, int row, int col) {
			data.set(row, (ClientData) value);
			fireTableCellUpdated(row, col);
		}

		public List<ClientData> getDataList() {
			return data;
		}

		public void setDataList(List<ClientData> dataList) {
			data = dataList;
			fireTableDataChanged();
		}

		public void addDataList(ClientData newData) {
			// Comprobamos si existe o no ya en la lista, para actualizar dicho
			// dato
			boolean found = false;
			for (ClientData innerData : data) {
				if (innerData.getId().equals(newData.getId())) {
					innerData.setDescription(newData.getDescription());
					innerData.setHost(newData.getHost());
					innerData.setIsActive(newData.getIsActive());
					found = true;
				}
			}
			if (!found) {
				this.data.add(data.size(), newData);
			}

		}

		public void removeDataList(ClientData oldData) {
			// Comprobamos si existe o no ya en la lista, para actualizar dicho
			// dato
			for (int i = data.size() - 1; i >= 0; i--) {
				ClientData innerData = data.get(i);
				if (innerData.getId().equals(oldData.getId())) {
					data.remove(i);
				}
			}
		}
	}

	public void insertOrUpdate(ClientData data) {
		List<ClientData> dataList = ((MyTableModel) table.getModel())
				.getDataList();
		boolean found = false;
		for (ClientData dataL : dataList) {
			if (dataL.getId().equals(data.getId())) {
				dataL.setDescription(data.getDescription());
				dataL.setHost(data.getHost());
				dataL.setIsActive(data.getIsActive());
				found = true;
			}
		}

		if (!found) {
			dataList.add(data);
		}

		((MyTableModel) table.getModel()).setDataList(dataList);
	}

	public void updateDataView(List<ClientData> list) {
		((MyTableModel) table.getModel()).setDataList(list);
	}

	public void addData(ClientData data) {
		((MyTableModel) table.getModel()).addDataList(data);
	}

	public void showErrorMessage(ClientException e) {
		JOptionPane.showMessageDialog(this, e.getMessage());
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public void removeData(ClientData data) {
		((MyTableModel) table.getModel()).removeDataList(data);

	}

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			JTable table = (JTable) e.getSource();
			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			if (row != -1) {
				if (e.isPopupTrigger()) {
					if (SwingUtilities.isRightMouseButton(e)) {
						// Get the ListSelectionModel of the JTable
						ListSelectionModel model = table.getSelectionModel();

						// set the selected interval of rows. Using the
						// "rowNumber"
						// variable for the beginning and end selects only that
						// one
						// row.
						model.setSelectionInterval(row, row);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				} else {
					if (e.getClickCount() == 2
							&& e.getButton() == MouseEvent.BUTTON1) {
						// your valueChanged overridden method
						consultAction.actionPerformed(new ActionEvent(this,
								ActionEvent.ACTION_PERFORMED,
								EnumClientAction.CONSULT.name()));
					}
				}
			}
		}
	}

}
