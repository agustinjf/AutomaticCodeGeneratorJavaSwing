package es.uma.lcc.gui.appgenerator.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import es.uma.lcc.gui.appgenerator.view.TFGFrame;

/**
 * Limpia los elementos de los datos de atributo
 *
 * @author ajifernandez
 *
 */
public class ClearTableAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private TFGFrame tfgFrame;

	public ClearTableAction(String title, TFGFrame tfgFrame) {
		super(title);
		this.tfgFrame = tfgFrame;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.tfgFrame.getNameAttributeField().setText("");
		this.tfgFrame.getDescriptionField().setText("");
	}

}
