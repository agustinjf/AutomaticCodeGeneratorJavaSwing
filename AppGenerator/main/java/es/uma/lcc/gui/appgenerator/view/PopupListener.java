package es.uma.lcc.gui.appgenerator.view;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 * Listener del popup de la tabla
 *
 * @author ajifernandez
 *
 */
class PopupListener extends MouseAdapter {
	private JPopupMenu popupMenu;

	public PopupListener(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

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
					// one row.
					model.setSelectionInterval(row, row);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}
}