package es.uma.lcc.gui.template.process;

import java.awt.Component;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import es.uma.lcc.gui.template._Client;
import es.uma.lcc.gui.template.connector.ClientConnector;
import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.exception.ClientException;
import es.uma.lcc.gui.template.view.AbstractView;
import es.uma.lcc.gui.template.view.ButtonTabComponent;
import es.uma.lcc.gui.template.view.GlassPanel;
import es.uma.lcc.gui.template.view.consult.ConsultDataView;
import es.uma.lcc.gui.template.view.edit.DataView;
import es.uma.lcc.gui.template.view.list.ListView;

/**
 * Clase procesadora del cliente
 *
 * @author ajifernandez
 *
 */
public class ClientProcess {

	JTabbedPane tabbedPane;
	static ListView lView;
	static DataView dView;
	ClientConnector connector;
	private GlassPanel glassPane;
	private Timer timer;
	private JLabel infoLabel;
	private Map<String, ClientData> locksMaps = new HashMap<String, ClientData>();
	private ResourceManager resourceManager;

	/**
	 * Constructor
	 *
	 * @param frame
	 * @param tabbedPane
	 * @param label
	 */
	public ClientProcess(JFrame frame, final JTabbedPane tabbedPane,
			JLabel label, final ResourceManager resourceManager) {
		this.tabbedPane = tabbedPane;
		this.infoLabel = label;

		this.resourceManager = resourceManager;
		this.glassPane = new GlassPanel();
		frame.setGlassPane(glassPane);
		connector = new ClientConnector(resourceManager);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				glassPane.start();
				Thread performer = new Thread(new Runnable() {

					public void run() {
						connector.remoteConnection();
						infoLabel.setText(resourceManager.getString(
								"process.server.connected",
								new Object[] { (new Date()).toString() }));
						glassPane.stop();
						tabbedPane.removeAll();

						TimerTask timerTask = new MyTimerTask();
						// running timer task as daemon thread
						timer = new Timer(true);
						timer.scheduleAtFixedRate(timerTask, 0, 1000);
					}
				}, "Performer");
				performer.start();
			}
		});
	}

	/**
	 * Abre la pantalla de listado
	 *
	 * @param tabbedPane
	 */
	public void openList() {
		String title = resourceManager.getString("process.view.list.title");
		int indexOfTab = tabbedPane.indexOfTab(title);
		if (indexOfTab >= 0) {
			tabbedPane.setSelectedIndex(indexOfTab);
		} else {
			lView = new ListView(this, resourceManager);
			tabbedPane.addTab(title, lView);

			tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title),
					new ButtonTabComponent(tabbedPane, this, lView,
							resourceManager));
			indexOfTab = tabbedPane.indexOfTab(title);
			tabbedPane.setSelectedIndex(indexOfTab);
			loadListView();
		}
	}

	/**
	 * Carga la pantalla de listado
	 */
	private void loadListView() {
		List<ClientData> list = new ArrayList<ClientData>();
		try {
			list = connector.get();
			lView.updateDataView(list);
		} catch (ClientException e) {
			lView.showErrorMessage(e);
		}

	}

	/**
	 * Abre la pantalla de creación
	 *
	 * @param tabbedPane
	 */
	public void openCreate() {
		String title = resourceManager.getString("process.view.create.title");
		if (dView == null) {
			dView = new DataView(this, resourceManager);
			tabbedPane.addTab(title, dView);
			tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title),
					new ButtonTabComponent(tabbedPane, this, dView,
							resourceManager));
			int indexOfTab = tabbedPane.indexOfTab(title);
			tabbedPane.setSelectedIndex(indexOfTab);
		} else {
			Object[] options = {
					resourceManager.getString("process.view.option.ok"),
					resourceManager.getString("process.view.option.cancel") };
			int n = JOptionPane.showOptionDialog(tabbedPane, resourceManager
					.getString("process.already.open.edit.error"),
					resourceManager.getString("process.warning.dialog.title"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, // do not use a
							// custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			switch (n) {
			case 0:
				removeTab(tabbedPane, tabbedPane.indexOfComponent(dView), dView);
				dView = new DataView(this, resourceManager);
				tabbedPane.addTab(title, dView);
				tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title),
						new ButtonTabComponent(tabbedPane, this, dView,
								resourceManager));
				int indexOfTab = tabbedPane.indexOfTab(title);
				tabbedPane.setSelectedIndex(indexOfTab);
				break;
			}
		}
	}

	/**
	 * Guarda un dato
	 *
	 * @param data
	 * @return
	 */
	public Boolean save(ClientData data) {
		glassPane.start();
		boolean result = false;
		try {
			ClientData resultSave = connector.save(data);
			if (resultSave != null) {
				int indexOfTab = tabbedPane.indexOfComponent(dView);

				// Cambiamos el modo de la vista a edicion
				dView.setMode(DataView.EDIT_MODE);
				tabbedPane.setTitleAt(indexOfTab, resourceManager.getString(
						"process.view.edit.title",
						new Object[] {
								//FILL_IN_GENERATOR_EDIT_TITLE
								}));
				dView.setData(resultSave);
				// Comprobamos si está abierta la vista de listado
				indexOfTab = tabbedPane.indexOfTab(resourceManager
						.getString("process.view.list.title"));
				if (indexOfTab >= 0) {
					JPanel listView = (JPanel) tabbedPane
							.getTabComponentAt(indexOfTab);
					lView.addData(resultSave);
				}
				for (int i = 0; i < tabbedPane.getComponents().length; i++) {
					Component c = tabbedPane.getComponents()[i];
					if (c instanceof ConsultDataView) {
						if (resultSave.getId().equals(
								((ConsultDataView) c).getData().getId())) {
							((ConsultDataView) c).setData(resultSave);

							tabbedPane
									.setTitleAt(
											tabbedPane.indexOfComponent(c),
											resourceManager
													.getString(
															"process.view.consult.title",
															new Object[] {
																	//FILL_IN_GENERATOR_CONSULT_TITLE
																	}));
							((ConsultDataView) c).updateDataGUI();
						}
					}
				}

				locksMaps.put(resultSave.getId(), resultSave);
				dView.showMessage(resourceManager
						.getString("process.save.message.ok"));

			} else {
				result = false;
			}
		} catch (ClientException e) {
			dView.showErrorMessage(e);
		}
		glassPane.stop();
		return result;
	}

	/**
	 * Tarea de heartbeat
	 *
	 * @author ajifernandez
	 *
	 */
	public class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			completeTask();
		}

		private void completeTask() {
			try {
				connector.isAlive();
			} catch (RemoteException e) {
				infoLabel.setText(resourceManager.getString(
						"process.server.disconnected",
						new Object[] { (new Date()).toString() }));
				timer.cancel();
				timer.purge();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getGlassPane().start();
						Thread performer = new Thread(new Runnable() {

							public void run() {
								connector.remoteConnection();
								infoLabel.setText(resourceManager
										.getString(
												"process.server.reconnected",
												new Object[] { (new Date())
														.toString() }));
								TimerTask timerTask = new MyTimerTask();
								// running timer task as daemon thread
								timer = new Timer(true);
								timer.scheduleAtFixedRate(timerTask, 0, 1000);

								getGlassPane().stop();
								// tabbedPane.removeAll();
							}
						}, "performer");
						performer.start();
					}
				});
			} catch (ClientException e) {
				Logger.getLogger(_Client.class.getName()).log(Level.SEVERE, e.getErrorMsg(), e);
			}
		}
	}

	/**
	 * Elimina un tab del frame principal
	 *
	 * @param pane
	 * @param i
	 * @param view
	 */
	public void removeTab(JTabbedPane pane, int i, AbstractView view) {
		Component[] components = pane.getComponents();
		for (Component c : components) {
			if (c.equals(view) && c instanceof ListView) {
				lView = null;
			} else if (c.equals(view) && c instanceof DataView) {
				dView = null;
				if (((DataView) view).getData() != null && ((DataView) view).getData().getId() != null) {
					locksMaps.remove(((DataView) view).getData().getId());
				}
			}
		}
		pane.remove(i);

	}

	/**
	 * Devuelve el valor del atributo glassPane
	 *
	 * @return atributo glassPane
	 */
	public GlassPanel getGlassPane() {
		return glassPane;
	}

	/**
	 * Establece el atributo glassPane
	 *
	 * @param glassPane
	 *            atributo glassPane a establecer
	 */
	public void setGlassPane(GlassPanel glassPane) {
		this.glassPane = glassPane;
	}

	/**
	 * Abre la vista de consulta
	 *
	 * @param data
	 */
	public void openConsult(ClientData data) {
		String title = resourceManager.getString("process.view.consult.title",
				new Object[] {
				//FILL_IN_GENERATOR_CONSULT_TITLE_DATA
				});

		// Buscamos por todas las vistas de consultas abiertas, si está la que
		// queremos abrir
		int indexOfTab = -1;
		for (Component c : tabbedPane.getComponents()) {
			if (c instanceof ConsultDataView) {
				if (((ConsultDataView) c).getData().equals(data)) {
					indexOfTab = tabbedPane.indexOfTab(title);
					break;
				}
			}
		}

		if (indexOfTab >= 0) {
			tabbedPane.setSelectedIndex(indexOfTab);
		} else {
			ConsultDataView cView = new ConsultDataView(resourceManager);
			tabbedPane.addTab(title, cView);

			tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title),
					new ButtonTabComponent(tabbedPane, this, cView,
							resourceManager));
			indexOfTab = tabbedPane.indexOfTab(title);
			tabbedPane.setSelectedIndex(indexOfTab);
			loadConsultView(cView, data);
		}
	}

	/**
	 * Carga la vista de consulta
	 *
	 * @param cView
	 * @param hostsData
	 */
	private void loadConsultView(ConsultDataView cView, ClientData hostsData) {
		cView.setData(hostsData);
		cView.updateDataGUI();
	}

	/**
	 * Abre la vista de edición
	 *
	 * @param data
	 */
	public void openEdit(ClientData data) {
		String title = resourceManager.getString("process.view.edit.title",
				new Object[] {
				//FILL_IN_GENERATOR_EDIT_TITLE_DATA
				});
		if (dView == null) {
			dView = new DataView(this, resourceManager);
			dView.setMode(DataView.EDIT_MODE);
			dView.setData(data);
			loadEditView(dView, data);

			tabbedPane.addTab(title, dView);
			tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title),
					new ButtonTabComponent(tabbedPane, this, dView,
							resourceManager));
			int indexOfTab = tabbedPane.indexOfTab(title);
			tabbedPane.setSelectedIndex(indexOfTab);
			locksMaps.put(data.getId(), data);
		} else {
			Object[] options = {
					resourceManager.getString("process.view.option.ok"),
					resourceManager.getString("process.view.option.cancel") };
			int n = JOptionPane.showOptionDialog(tabbedPane, resourceManager
					.getString("process.already.open.edit.error"),
					resourceManager.getString("process.warning.dialog.title"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, // do not use a
							// custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			switch (n) {
			case 0:
				removeTab(tabbedPane, tabbedPane.indexOfComponent(dView), dView);
				dView = new DataView(this, resourceManager);
				dView.setMode(DataView.EDIT_MODE);
				dView.setData(data);
				loadEditView(dView, data);

				tabbedPane.addTab(title, dView);
				tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title),
						new ButtonTabComponent(tabbedPane, this, dView,
								resourceManager));
				int indexOfTab = tabbedPane.indexOfTab(title);
				tabbedPane.setSelectedIndex(indexOfTab);
				locksMaps.put(data.getId(), data);
				break;
			}
		}
	}

	/**
	 * Carga la vista de edición
	 *
	 * @param dView
	 * @param hostsData
	 */
	private void loadEditView(DataView dView, ClientData hostsData) {
		dView.setData(hostsData);
		dView.updateDataGUI();
	}

	/**
	 * Borra un dato
	 *
	 * @param data
	 */
	public void delete(ClientData data) {
		glassPane.start();
		try {
			if (!locksMaps.containsKey(data.getId())) {

				ClientData deleteData = connector.delete(data);
				if (deleteData != null) {
					lView.showMessage(resourceManager
							.getString("process.delete.message.ok"));
					lView.removeData(deleteData);

					// Cerramos las vistas de consultas que esten abiertas
					for (int i = 0; i < tabbedPane.getComponents().length; i++) {
						Component c = tabbedPane.getComponents()[i];
						if (c instanceof ConsultDataView) {
							if (data.getId().equals(
									((ConsultDataView) c).getData().getId())) {
								removeTab(tabbedPane,
										tabbedPane.indexOfComponent(c),
										(AbstractView) c);
							}
						}
					}
				} else {
					lView.showMessage(resourceManager
							.getString("process.delete.message.ko"));
				}
			} else {
				lView.showMessage(resourceManager
						.getString("process.delete.message.lock"));
			}
		} catch (ClientException e) {
			lView.showErrorMessage(e);
		} finally {
			glassPane.stop();
		}
	}
}
