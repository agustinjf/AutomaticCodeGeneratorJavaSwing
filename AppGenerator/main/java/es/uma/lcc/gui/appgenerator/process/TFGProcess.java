package es.uma.lcc.gui.appgenerator.process;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import es.uma.lcc.gui.appgenerator.data.DataModelType;
import es.uma.lcc.gui.appgenerator.data.GeneralData;
import es.uma.lcc.gui.appgenerator.data.TableData;
import es.uma.lcc.gui.appgenerator.data.TypeEnum;
import es.uma.lcc.gui.appgenerator.process.data.ChangeMode;
import es.uma.lcc.gui.appgenerator.view.GlassPanel;
import es.uma.lcc.gui.appgenerator.view.TFGFrame;

/**
 * Clase procesadora del TFG
 *
 * @author ajifernandez
 *
 */
public class TFGProcess {
	static Logger logger = LogManager.getLogger(TFGProcess.class);

	
	private ResourceManager resourceManager;
	private GlassPanel glassPanel;
	private TFGFrame tfgFrame;

	Exception ex;
	String message;

	private GeneralData generalData;
	private List<TableData> tableDataList;

	/**
	 * Constructor
	 */
	public TFGProcess(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.glassPanel = new GlassPanel("", 14, 0.70f, 15.0f, 300);
	}

	/**
	 * Devuelve el valor del atributo resourceManager
	 *
	 * @return atributo resourceManager
	 */
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * Establece el atributo resourceManager
	 *
	 * @param resourceManager
	 *            atributo resourceManager a establecer
	 */
	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	/**
	 * Genera la aplicación
	 */
	public void generate() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				glassPanel.start();
				message = resourceManager.getString("tfgframe.generate.ok");
				Thread performer = new Thread(new Runnable() {

					public void run() {
						// Recuperamos la información de la vista
						try {
							ex = null;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
							String date = sdf.format(new Date());

							generalData = tfgFrame.getGeneralData();
							tableDataList = tfgFrame.getTableDataList();

							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							fileChooser.setDialogTitle("Specify a file to save " + generalData.getAppName() + "_" + date);

							int userSelection = fileChooser.showSaveDialog(tfgFrame);

							if (userSelection == JFileChooser.APPROVE_OPTION) {
								File fileToSave = fileChooser.getCurrentDirectory();
								// Comprobar que tenemos permisos de escritura
								// en destino
								File testWrite = new File(fileToSave.getAbsolutePath());
								if (testWrite.canWrite()) {

									// Copiamos los ficheros a una ruta temporal
									FileUtils.copyDirectory(new File("resources/code/"), new File(fileToSave.getAbsolutePath() + "/temp"));
									// Una vez copiado todo, eliminamos todo el
									// contenido de main/java de todos los
									// proyectos
									for (DataModelType project : DataModelType.values()) {
										FileUtils.deleteDirectory(new File(fileToSave.getAbsolutePath() + "/temp/" + project + "/main/java/es"));
										FileUtils.deleteDirectory(new File(fileToSave.getAbsolutePath() + "/temp/" + project + "/resources/es"));
										
										logger.log(Level.INFO,"Deleted: " + fileToSave.getAbsolutePath() + "/temp/" + project + "/main/java/es");
										logger.log(Level.INFO,"Deleted if exists: " + fileToSave.getAbsolutePath() + "/temp/" + project
												+ "/resources/es");
									}

									String folderTempName = fileToSave.getAbsolutePath() + "/temp";

									for (DataModelType project : DataModelType.values()) {
										createPackageStructureFolders(project, generalData.getPackageName(), folderTempName, generalData.getAppName());
									}

									// Creamos ficheros temporales de
									// build.xml y modificamos lo que
									// queramos del
									// temporal
									for (DataModelType project : DataModelType.values()) {
										List<String> readLines = FileUtils.readLines(new File(folderTempName + "/" + project + "/build.xml"));
										String updateLine = "";
										boolean mustUpdate = false;
										int pos = 0;
										for (int i = 0; i < readLines.size(); i++) {
											String line = readLines.get(i);
											if (line.contains("<property name=\"main-class\" value=")) {
												switch (project) {
												case Client:
													updateLine = "	<property name=\"main-class\" value=\"" + generalData.getPackageName() + ".gui."
															+ generalData.getAppName().toLowerCase() + "._Client\" />";
													mustUpdate = true;
													pos = i;
													break;
												case Server:
													updateLine = "	<property name=\"main-class\" value=\"" + generalData.getPackageName() + ".impl."
															+ generalData.getAppName().toLowerCase() + "._Server\" />";
													mustUpdate = true;
													pos = i;
													break;
												case Server_Interface:
													// Do nothing
													break;
												}
											}
											if (mustUpdate) {
												// Paramos el bucle, ya no es
												// necesario seguir
												readLines.set(pos, updateLine);
												break;
											}
										}
										FileUtils.writeLines(new File(folderTempName + "/" + project + "/build_temp.xml"), readLines);

										// Reemplazamos el original por el
										// modificado
										FileUtils.copyFile(new File(folderTempName + "/" + project + "/build_temp.xml"), new File(folderTempName
												+ "/" + project + "/build.xml"));

										FileUtils.forceDelete(new File(folderTempName + "/" + project + "/build_temp.xml"));
									}

									// Copiamos las carpetas y ficheros java en
									// las rutas adecuadas, modificandolos datos
									// necesarios
									String originalFolderPath = "";
									String destDirPath = "";
									for (DataModelType project : DataModelType.values()) {
										originalFolderPath = "";
										destDirPath = "";
										switch (project) {
										case Client:
											originalFolderPath = "resources/code/" + project + "/main/java/es/uma/lcc/gui/template";
											destDirPath = folderTempName + "/Client/main/java/" + generalData.getPackageName().replace('.', '/')
													+ "/gui/" + generalData.getAppName().toLowerCase();
											break;
										case Server:
											originalFolderPath = "resources/code/" + project + "/main/java/es/uma/lcc/impl/template";
											destDirPath = folderTempName + "/Server/main/java/" + generalData.getPackageName().replace('.', '/')
													+ "/impl/" + generalData.getAppName().toLowerCase();
											break;
										case Server_Interface:
											originalFolderPath = "resources/code/" + project + "/main/java/es/uma/lcc/iface/template";
											destDirPath = folderTempName + "/Server_Interface/main/java/"
													+ generalData.getPackageName().replace('.', '/') + "/iface/"
													+ generalData.getAppName().toLowerCase();
											break;
										}
										generateImpl(originalFolderPath, destDirPath, project);

									}
									originalFolderPath = "resources/code/" + DataModelType.Client + "/resources/es/uma/lcc/gui/template";
									destDirPath = folderTempName + "/" + DataModelType.Client + "/main/java/"
											+ generalData.getPackageName().replace('.', '/') + "/gui/" + generalData.getAppName().toLowerCase();
									generateImpl(originalFolderPath, destDirPath, DataModelType.Client);

									originalFolderPath = "resources/code/" + DataModelType.Client + "/resources/logging.properties";
									destDirPath = folderTempName + "/" + DataModelType.Client + "/main/java";
									FileUtils.copyFileToDirectory(new File(originalFolderPath), new File(destDirPath));

									generateProject(DataModelType.Server_Interface, fileToSave);
									copyServerInterface(fileToSave.getPath());

									generateProject(DataModelType.Server, fileToSave);

									generateProject(DataModelType.Client, fileToSave);
									logger.log(Level.INFO,"Writing files in: " + fileToSave.getAbsolutePath() + "/" + generalData.getAppName() + "_"
											+ date);

									String serverJar = fileToSave.getPath() + "/temp/Server/build/jar/Server.jar";
									String serverLibs = fileToSave.getPath() + "/temp/Server/build/jar/lib/";
									String serverResources = fileToSave.getPath() + "/temp/Server/build/jar/resources/";
									String clientJar = fileToSave.getPath() + "/temp/Client/build/jar/Client.jar";
									String clientLibs = fileToSave.getPath() + "/temp/Client/build/jar/lib/";
									// String clientResources =
									// fileToSave.getPath() +
									// "/temp/Client/build/jar/resources/";

									FileUtils.forceMkdir(new File(fileToSave.getAbsolutePath() + "/" + generalData.getAppName() + "_" + date));
									File file = new File(fileToSave.getAbsolutePath() + "/" + generalData.getAppName() + "_" + date + "/Server");
									FileUtils.forceMkdir(file);
									FileUtils.copyFileToDirectory(new File(serverJar), file);
									FileUtils.copyDirectoryToDirectory(new File(serverLibs), file);
									FileUtils.copyDirectoryToDirectory(new File(serverResources), file);

									file = new File(fileToSave.getAbsolutePath() + "/" + generalData.getAppName() + "_" + date + "/Client");
									FileUtils.forceMkdir(file);
									FileUtils.copyFileToDirectory(new File(clientJar), file);
									FileUtils.copyDirectoryToDirectory(new File(clientLibs), file);
									// FileUtils.copyDirectoryToDirectory(new
									// File(clientResources), file);
									// Borramos la carpeta temporal
									FileUtils.deleteDirectory(new File(fileToSave.getPath() + "/temp"));
								} else {
									message = resourceManager.getString("tfgframe.generate.error.permissions");
									logger.log(Level.INFO,"Can not write in selected directory");
								}
							} else {
								message = resourceManager.getString("tfgframe.generate.cancel");
								logger.log(Level.INFO,"User cancelled generation");
							}
						} catch (Exception e) {
							message = resourceManager.getString("tfgframe.generate.error.creatingfile");
							ex = e;
							logger.log(Level.ERROR, "Error Generating", e);
						} finally {
							glassPanel.stop();

							tfgFrame.showResultMessage(resourceManager.getString("tfgframe.generate.title"), message, ex);
						}
					}

					/**
					 * Implementación de la generación
					 *
					 * @param originalFolderPath
					 * @param destDirPath
					 * @param project
					 * @throws IOException
					 */
					private void generateImpl(String originalFolderPath, String destDirPath, DataModelType project) throws IOException {
						File destDir;
						String finalDestDirPath = "";
						File originalFolder = new File(originalFolderPath);
						File[] listFiles = originalFolder.listFiles();
						for (File file : listFiles) {
							if (file.isDirectory()) {
								// crear el mismo directorio en el directorio
								// temporal
								String folderName = file.getName();
								// Estamos en una carpeta de primer nivel
								finalDestDirPath = destDirPath + "/" + folderName;
								FileUtils.forceMkdir(new File(finalDestDirPath));

								generateImpl(file.getPath(), finalDestDirPath, project);
							} else {
								ChangeMode mode = ChangeMode.Normal;
								if (file.getName().equals("DaoManager.java")) {
									mode = ChangeMode.DaoManager;
								} else if (file.getName().equals("ConsultDataView.java")) {
									mode = ChangeMode.ConsultView;
								} else if (file.getName().equals("DataView.java")) {
									mode = ChangeMode.EditView;
								} else if (file.getName().equals("ListView.java")) {
									mode = ChangeMode.ListView;
								} else if (file.getName().equals("ConverterManager.java")) {
									if (DataModelType.Client.equals(project)) {
										mode = ChangeMode.ConverterGui;
									} else {
										mode = ChangeMode.ConverterImpl;
									}
								} else if (file.getName().equals("ClientData.java")) {
									mode = ChangeMode.DataModelGui;
								} else if (file.getName().equals("SPIData.java")) {
									mode = ChangeMode.DataModelIface;
								} else if (file.getName().equals("ServerData.java")) {
									mode = ChangeMode.DataModelImpl;
								} else if (file.getName().equals("ClientProcess.java")) {
									mode = ChangeMode.ClientProcess;
								} else if (file.getName().equals("ResourceManager.java")) {
									mode = ChangeMode.ResourceManager;
								} else if (file.getName().equals("messages_es.properties")) {
									mode = ChangeMode.ResourcesES;
								} else if (file.getName().equals("messages_en.properties")) {
									mode = ChangeMode.ResourcesEN;
								} else if (file.getName().equals("messages.properties")) {
									mode = ChangeMode.Resources;
								}

								File finalFile = modifyFile(file, project, mode);
								// Copiamos el fichero a destino
								destDir = new File(destDirPath);
								FileUtils.moveFileToDirectory(finalFile, destDir, true);
							}
						}
					}
				}, "Performer");
				performer.start();
			}
		});
	}

	/**
	 * Realiza todas las modificaciones del mapa sobre el fichero indicado
	 *
	 * @param file
	 *            fichero sobre el que realizar las modificaciones
	 * @param changeList
	 *            mapa de cambios
	 * @param project
	 * @param changeMode
	 * @return
	 * @throws IOException
	 */
	private File modifyFile(File file, DataModelType project, ChangeMode changeMode) throws IOException {
		// Modificar todo lo que hay que
		// modificar:
		// paquetería
		// usuario creación
		// bean de datos
		// daomanager (xstream alias)
		// converterManager en ambos sentidos
		// vista de consulta
		// vista de edición
		// vista de listado
		// process (recuperación y
		// establecimiento de datos)

		List<String> readLines = FileUtils.readLines(file);
		List<String> finalLines = changePackage(readLines, project);
		finalLines = changeImports(finalLines);
		finalLines = changeUser(finalLines);

		switch (changeMode) {
		case ConsultView:
			finalLines = generateConsultView(generalData, tableDataList);
			break;
		case ConverterGui:
		case ConverterImpl:
			finalLines = generateConverterManager(generalData, tableDataList, project);
			break;
		case DaoManager:
			// Aparte de los cambios normales, debemos modificar el alias en el
			// establecimiento de datos de la capa dao.
			finalLines = changeDaoAlias(finalLines);
			finalLines = insertUpdateMethod(finalLines);
			break;
		case EditView:
			finalLines = generateEditView(generalData, tableDataList);
			break;
		case ListView:
			finalLines = generateListView(generalData, tableDataList);
			break;
		case DataModelGui:
		case DataModelImpl:
		case DataModelIface:
			finalLines = generateDataModelBean(generalData, tableDataList, project);
			break;
		case ClientProcess:
			finalLines = modifyEditTabName(tableDataList, finalLines);
			finalLines = modifyConsultTabName(tableDataList, finalLines);
			break;
		case Resources:
		case ResourcesES:
		case ResourcesEN:
			finalLines = generateResourcesMessages(generalData, tableDataList, changeMode);
			break;
		case ResourceManager:
			finalLines = modifyBundleName(generalData, finalLines);
			break;
		case Normal:
			break;
		default:
			break;
		}

		FileUtils.writeLines(new File("temp_" + file.getName()), finalLines);

		// Reemplazamos el original por el
		// modificado
		File finalFile = new File(file.getName());
		FileUtils.copyFile(new File("temp_" + file.getName()), finalFile);

		FileUtils.forceDelete(new File("temp_" + file.getName()));
		return finalFile;
	}

	/**
	 * Modifica el bundle name del resourceManager
	 *
	 * @param generalData
	 * @param readLines
	 * @return
	 */
	private List<String> modifyBundleName(GeneralData generalData, List<String> readLines) {
		List<String> finalLines = new ArrayList<String>();
		for (String line : readLines) {
			if (line.contains("es.uma.lcc.gui.template.messages")) {
				line = "\tprivate final String BUNDLE_NAME = \"" + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase()
						+ ".messages\";";
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Modifica la parte encargada de poner el nombre a la pestaña de consulta
	 *
	 * @param tableDataList
	 * @param readLines
	 * @return
	 */
	private List<String> modifyConsultTabName(List<TableData> tableDataList, List<String> readLines) {
		List<String> finalLines = new ArrayList<String>();
		String tableDataName = "";
		for (TableData tableData : tableDataList) {
			if (!tableData.getName().equals("id")) {
				tableDataName = tableData.getName();
				break;
			}
		}
		for (String line : readLines) {
			if (line.contains("FILL_IN_GENERATOR_CONSULT_TITLE_DATA")) {
				line = "data.get" + tableDataName.substring(0, 1).toUpperCase() + tableDataName.substring(1) + "()";
			} else if (line.contains("FILL_IN_GENERATOR_CONSULT_TITLE")) {
				line = "resultSave.get" + tableDataName.substring(0, 1).toUpperCase() + tableDataName.substring(1) + "()";
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Modifica la parte encargada de poner el nombre a la pestaña de edición
	 *
	 * @param tableDataList
	 * @param finalLines2
	 * @return
	 */
	private List<String> modifyEditTabName(List<TableData> tableDataList, List<String> readLines) {
		List<String> finalLines = new ArrayList<String>();
		String tableDataName = "";
		for (TableData tableData : tableDataList) {
			if (!tableData.getName().equals("id")) {
				tableDataName = tableData.getName();
				break;
			}
		}
		for (String line : readLines) {
			if (line.contains("FILL_IN_GENERATOR_EDIT_TITLE_DATA")) {
				line = "data.get" + tableDataName.substring(0, 1).toUpperCase() + tableDataName.substring(1) + "()";
			} else if (line.contains("FILL_IN_GENERATOR_EDIT_TITLE")) {
				line = "resultSave.get" + tableDataName.substring(0, 1).toUpperCase() + tableDataName.substring(1) + "()";
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Inserta el método update en el daoManager
	 *
	 * @param finalLines
	 * @return
	 */
	private List<String> insertUpdateMethod(List<String> readLines) {

		List<String> finalLines = new ArrayList<String>();

		for (int i = 0; i < readLines.size(); i++) {
			String line = readLines.get(i);
			if (line.contains("FILL_IN_GENERATOR")) {
				for (TableData tableData : tableDataList) {
					if (!"id".equals(tableData.getName())) {
						line = "dataL.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "(data.get"
								+ tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());";
						finalLines.add(line);
					}
				}
			} else {
				finalLines.add(line);
			}
		}
		return finalLines;
	}

	/**
	 * Genera el fichero de mensajes internacionalizado (excepto los atributos
	 * nuevos, que los pone tal cual se han introducido)
	 *
	 * @param generalData
	 * @param tableDataList
	 * @param changeMode
	 * @return
	 */
	private List<String> generateResourcesMessages(GeneralData generalData, List<TableData> tableDataList, ChangeMode changeMode) {
		List<String> result = new ArrayList<String>();
		result.add("#Datos comunes");
		result.add("appName=" + generalData.getAppName());
		switch (changeMode) {
		case ResourcesES:
			result.add(StringEscapeUtils.escapeJava("main.menu=Men\u00fa"));
			result.add("main.menu.create=Crear");
			result.add(StringEscapeUtils.escapeJava("main.menu.error=No hay opci\u00f3n para el evento"));
			result.add("main.menu.list=Listado");
			result.add("");
			result.add(StringEscapeUtils.escapeJava("view.message.title=Informaci\u00f3n"));
			result.add("view.message.title.error=Mensaje de error");
			result.add("");
			result.add("#Vista de listado");
			result.add("listview.action.consult=Consultar");
			result.add("listview.action.delete=Borrar");
			result.add(StringEscapeUtils.escapeJava("listview.action.edit=Edici\u00f3n"));
			result.add("");
			for (TableData tableData : tableDataList) {
				result.add(StringEscapeUtils.escapeJava("listview.header." + tableData.getName().toLowerCase() + "=" + tableData.getDescription()));
			}
			result.add("");
			result.add("#Vista de consulta");
			for (TableData tableData : tableDataList) {
				result.add(StringEscapeUtils.escapeJava("view.consult.label.text." + tableData.getName().toLowerCase() + "="
						+ tableData.getDescription()));
				result.add(StringEscapeUtils.escapeJava("view.consult.label.tooltip." + tableData.getName().toLowerCase() + "="
						+ tableData.getDescription()));
			}
			result.add("");
			result.add(StringEscapeUtils.escapeJava("#Vista de edición"));
			result.add("process.view.data.button.save=Guardar");
			for (TableData tableData : tableDataList) {
				result.add(StringEscapeUtils.escapeJava("process.view.data.label." + tableData.getName().toLowerCase() + "="
						+ tableData.getDescription()));
			}
			result.add("");
			result.add("#Mensajes de aviso/error");
			result.add(StringEscapeUtils.escapeJava("process.warning.dialog.title=T\u00edtulo Warning"));
			result.add(StringEscapeUtils
					.escapeJava("process.already.open.edit.error=Ya existe una ventana de creaci\u00f3n o edici\u00f3n,\n\u00bfEst\u00e1 seguro de cerrarla y perder los cambios?"));
			result.add("process.delete.message.ko=Error borrando datos");
			result.add("process.delete.message.lock=No se puede borrar un dato bloqueado");
			result.add("process.delete.message.ok=Borrado correctamente");
			result.add("process.get.message.error=Error obteniendo datos");
			result.add("process.save.message.error=Error guardando datos");
			result.add("process.error.server.connection=Error conectando al servidor server, reintentando en {0} ms");
			result.add("process.error.server.connection=Error arrancando el servidor");
			result.add("process.save.message.ok=Guaradado correctamente");
			result.add("");
			result.add(StringEscapeUtils.escapeJava("#Mensajes panel información"));
			result.add("process.server.connected=Conectado al servidor!! a las: {0}");
			result.add("process.server.disconnected=Desconectado del servidor!! a las: {0}");
			result.add("process.server.reconnected=Reconectado al servidor!! a las: {0}");
			result.add("");
			result.add("#Títulos de vistas");
			result.add("process.view.list.title=Listado");
			result.add("process.view.create.title=Crear");
			result.add("process.view.consult.title=Consulta - {0}");
			result.add(StringEscapeUtils.escapeJava("process.view.edit.title=Edici\u00f3n - {0}"));
			result.add("");
			result.add("#Opciones de dialogos");
			result.add("process.view.option.cancel=Cancelar");
			result.add("process.view.option.ok=OK");
			result.add("");
			result.add("#Pestaña");
			result.add(StringEscapeUtils.escapeJava("tab.close=Cerrar esta pesta\u00f1a"));
			break;
		case ResourcesEN:
		case Resources:
			result.add("main.menu=Menu");
			result.add("main.menu.create=Create");
			result.add("main.menu.error=No option for event command");
			result.add("main.menu.list=List");
			result.add("");
			result.add("view.message.title=Information");
			result.add("view.message.title.error=Error Message");
			result.add("");
			result.add("#Vista de listado");
			result.add("listview.action.consult=Consult");
			result.add("listview.action.delete=Delete");
			result.add("listview.action.edit=Edit");
			result.add("");
			for (TableData tableData : tableDataList) {
				result.add(StringEscapeUtils.escapeJava("listview.header." + tableData.getName().toLowerCase() + "=" + tableData.getDescription()));
			}
			result.add("");
			result.add("#Vista de consulta");
			for (TableData tableData : tableDataList) {
				result.add(StringEscapeUtils.escapeJava("view.consult.label.text." + tableData.getName().toLowerCase() + "="
						+ tableData.getDescription()));
				result.add(StringEscapeUtils.escapeJava("view.consult.label.tooltip." + tableData.getName().toLowerCase() + "="
						+ tableData.getDescription()));
			}
			result.add("");
			result.add("#Vista de edición");
			result.add("process.view.data.button.save=Save");
			for (TableData tableData : tableDataList) {
				result.add(StringEscapeUtils.escapeJava("process.view.data.label." + tableData.getName().toLowerCase() + "="
						+ tableData.getDescription()));
			}
			result.add("");
			result.add("#Mensajes de aviso/error");
			result.add("process.warning.dialog.title=Warning Title");
			result.add("process.already.open.edit.error=Already exist and edit or creation view,\ndo you want to close other and lose changes?");
			result.add("process.delete.message.ko=Error deleting data");
			result.add("process.delete.message.lock=Can not delete a lock data");
			result.add("process.delete.message.ok=Delete successful");
			result.add("process.get.message.error=Error getting data");
			result.add("process.save.message.error=Error saving data");
			result.add("process.error.server.connection=Error connecting server, trying in {0} ms");
			result.add("process.error.server.connection=Error running server");
			result.add("process.save.message.ok=Saved correctly");
			result.add("");
			result.add("#Mensajes panel información");
			result.add("process.server.connected=Server connected!! at: {0}");
			result.add("process.server.disconnected=Server disconnected!! at: {0}");
			result.add("process.server.reconnected=Server reconnected!! at: {0}");
			result.add("");
			result.add("#Títulos de vistas");
			result.add("process.view.list.title=List");
			result.add("process.view.create.title=Create");
			result.add("process.view.consult.title=Consult - {0}");
			result.add("process.view.edit.title=Edit - {0}");
			result.add("");
			result.add("#Opciones de dialogos");
			result.add("process.view.option.cancel=Cancel");
			result.add("process.view.option.ok=OK");
			result.add("");
			result.add("#Pestaña");
			result.add("tab.close=Close this tab");
			break;
		}

		return result;
	}

	/**
	 * Genera la clase de listado de datos
	 *
	 * @param generalData
	 * @param tableDataList
	 * @return
	 */
	private List<String> generateListView(GeneralData generalData, List<TableData> tableDataList) {
		List<String> result = new ArrayList<String>();
		String fileName = "ListView";
		String packageName = "package " + generalData.getPackageName().toLowerCase() + ".gui." + generalData.getAppName().toLowerCase()
				+ ".view.list" + ";";
		result.add(packageName);
		result.add("");
		result.add("import java.awt.Dimension;");
		result.add("import java.awt.GridLayout;");
		result.add("import java.awt.Point;");
		result.add("import java.awt.event.ActionEvent;");
		result.add("import java.awt.event.MouseAdapter;");
		result.add("import java.awt.event.MouseEvent;");
		result.add("import java.awt.event.MouseListener;");
		result.add("import java.util.ArrayList;");
		result.add("import java.util.List;");
		result.add("");
		result.add("import javax.swing.JMenuItem;");
		result.add("import javax.swing.JOptionPane;");
		result.add("import javax.swing.JPopupMenu;");
		result.add("import javax.swing.JScrollPane;");
		result.add("import javax.swing.JTable;");
		result.add("import javax.swing.ListSelectionModel;");
		result.add("import javax.swing.SwingUtilities;");
		result.add("");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".data.ClientData;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".exception.ClientException;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.ClientProcess;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.ResourceManager;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.action.ConsultAction;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.action.DeleteAction;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.action.EditAction;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.action.EnumClientAction;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".view.AbstractView;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".view.list.model.ClientTableModel;");
		result.add("");
		result.add("/**");
		result.add(" * Clase de listado");
		result.add(" *");
		result.add(" * @author " + generalData.getUserName());
		result.add(" *");
		result.add(" */");
		result.add("public class " + fileName + " extends AbstractView {");
		result.add("");
		result.add("\t/** Serial version */");
		result.add("\tprivate static final long serialVersionUID = 1L;");
		result.add("");
		result.add("\tprivate JTable table;");
		result.add("");
		result.add("\tprivate JPopupMenu popupMenu;");
		result.add("");
		result.add("\tprivate ClientProcess process;");
		result.add("");
		result.add("\tprivate ConsultAction consultAction;");
		result.add("");
		result.add("\tprivate ResourceManager resourceManager;");
		result.add("");
		result.add("\t/**");
		result.add("\t * Constructor");
		result.add("\t *");
		result.add("\t * @param clientProcess");
		result.add("\t */");
		result.add("\tpublic ListView(ClientProcess clientProcess, ResourceManager resourceManager) {");
		result.add("\t\tsetLayout(new GridLayout(1, 0));");
		result.add("\t\tthis.process = clientProcess;");
		result.add("\t\tthis.resourceManager = resourceManager;");
		result.add("\t\ttable = new JTable(new MyTableModel());");
		result.add("\t\ttable.setPreferredScrollableViewportSize(new Dimension(500, 70));");
		result.add("\t\ttable.setFillsViewportHeight(true);");
		result.add("\t\t// Create the scroll pane and add the table to it.");
		result.add("\t\tJScrollPane scrollPane = new JScrollPane(table);");
		result.add("\t\tadd(scrollPane);");
		result.add("");
		result.add("\t\tpopupMenu = new JPopupMenu();");
		result.add("\t\tconsultAction = new ConsultAction(resourceManager.getString(\"listview.action.consult\"), table, process);");
		result.add("\t\tJMenuItem menuItemConsult = new JMenuItem(consultAction);");
		result.add("\t\tmenuItemConsult.setActionCommand(EnumClientAction.CONSULT.name());");
		result.add("\t\tJMenuItem menuItemEdit = new JMenuItem(new EditAction(resourceManager.getString(\"listview.action.edit\"), table, process));");
		result.add("\t\tmenuItemEdit.setActionCommand(EnumClientAction.EDIT.name());");
		result.add("\t\tJMenuItem menuItemRemove = new JMenuItem(new DeleteAction(resourceManager.getString(\"listview.action.delete\"), table, process));");
		result.add("\t\tmenuItemRemove.setActionCommand(EnumClientAction.DELETE.name());");
		result.add("");
		result.add("\t\tpopupMenu.add(menuItemConsult);");
		result.add("\t\tpopupMenu.add(menuItemEdit);");
		result.add("\t\tpopupMenu.add(menuItemRemove);");
		result.add("");
		result.add("\t\ttable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
		result.add("");
		result.add("\t\tMouseListener popupListener = new PopupListener();");
		result.add("\t\ttable.addMouseListener(popupListener);");
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\tclass MyTableModel extends ClientTableModel<ClientData> {");
		result.add("\t\t/** Serial version */");
		result.add("\t\tprivate static final long serialVersionUID = 1L;");
		result.add("");
		result.add("\t\tprivate String[] columnNames = {");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				String line = "\t\t\tresourceManager.getString(\"listview.header." + tableData.getName().toLowerCase() + "\")";
				result.add(line + (i < tableDataList.size() - 1 ? "," : ""));
			}
		}
		result.add("\t\t};");
		result.add("\t\tprivate List<ClientData> data = new ArrayList<ClientData>();");
		result.add("");
		result.add("\t\tpublic int getColumnCount() {");
		result.add("\t\t\treturn columnNames.length;");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic int getRowCount() {");
		result.add("\t\t\treturn data.size();");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic String getColumnName(int col) {");
		result.add("\t\t\treturn columnNames[col];");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic Object getValueAt(int row, int col) {");
		result.add("\t\t\tObject result = null;");
		result.add("\t\t\tswitch (col) {");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				result.add("\t\t\tcase " + i + ":");
				result.add("\t\t\t\tresult = data.get(row).get" + tableData.getName().substring(0, 1).toUpperCase()
						+ tableData.getName().substring(1) + "();");
				result.add("\t\t\t\tbreak;");
			}
		}
		result.add("\t\t\t}");
		result.add("\t\t\treturn result;");
		result.add("\t\t}");
		result.add("");
		result.add("\t\t/*");
		result.add("\t\t * JTable uses this method to determine the default renderer/ editor for");
		result.add("\t\t * each cell. If we didn't implement this method, then the last column");
		result.add("\t\t * would contain text (\"true\"/\"false\"), rather than a check box.");
		result.add("\t\t */");
		result.add("\t\tpublic Class<?> getColumnClass(int c) {");
		result.add("\t\t\tClass<?> clazz = null;");
		result.add("\t\t\tswitch (c) {");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				result.add("\t\t\tcase " + i + ":");
				result.add("\t\t\t\tclazz = data.get(0).get" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
						+ "().getClass();");
				result.add("\t\t\t\tbreak;");
			}
		}
		result.add("\t\t\t}");
		result.add("\t\t\treturn clazz;");
		result.add("\t\t}");
		result.add("");
		result.add("\t\t/*");
		result.add("\t\t * Don't need to implement this method unless your table's editable.");
		result.add("\t\t */");
		result.add("\t\tpublic boolean isCellEditable(int row, int col) {");
		result.add("\t\t\treturn false;");
		result.add("\t\t}");
		result.add("");
		result.add("\t\t/*");
		result.add("\t\t * Don't need to implement this method unless your table's data can");
		result.add("\t\t * change.");
		result.add("\t\t*/");
		result.add("\t\tpublic void setValueAt(Object value, int row, int col) {");
		result.add("\t\t\tdata.set(row, (ClientData) value);");
		result.add("\t\t\tfireTableCellUpdated(row, col);");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic List<ClientData> getDataList() {");
		result.add("\t\t\treturn data;");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic void setDataList(List<ClientData> dataList) {");
		result.add("\t\t\tdata = dataList;");
		result.add("\t\t\tfireTableDataChanged();");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic void addDataList(ClientData newData) {");
		result.add("\t\t\t// Comprobamos si existe o no ya en la lista, para actualizar dicho");
		result.add("\t\t\t// dato");
		result.add("\t\t\tboolean found = false;");
		result.add("\t\t\tfor (ClientData innerData : data) {");
		result.add("\t\t\tif (innerData.getId().equals(newData.getId())) {");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\t\t\tinnerData.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
						+ "(newData.get" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());");
			}
		}
		result.add("\t\t\t\tfound = true;");
		result.add("\t\t\t}");
		result.add("\t\t}");
		result.add("\t\tif (!found) {");
		result.add("\t\t\tthis.data.add(data.size(), newData);");
		result.add("\t\t}");
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\t\tpublic void removeDataList(ClientData oldData) {");
		result.add("\t\t\t// Comprobamos si existe o no ya en la lista, para actualizar dicho dato");
		result.add("\t\t\tfor (int i = data.size() - 1; i >= 0; i--) {");
		result.add("\t\t\t\tClientData innerData = data.get(i);");
		result.add("\t\t\t\tif (innerData.getId().equals(oldData.getId())) {");
		result.add("\t\t\t\t\tdata.remove(i);");
		result.add("\t\t\t\t}");
		result.add("\t\t\t}");
		result.add("\t\t}");
		result.add("\t}");
		result.add("");
		result.add("\tpublic void insertOrUpdate(ClientData data) {");
		result.add("\t\tList<ClientData> dataList = ((MyTableModel) table.getModel()).getDataList();");
		result.add("\t\tboolean found = false;");
		result.add("\t\tfor (ClientData dataL : dataList) {");
		result.add("\t\t\tif (dataL.getId().equals(data.getId())) {");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\t\t\tdataL.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "(data.get"
						+ tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());");
			}
		}
		result.add("\t\t\t\tfound = true;");
		result.add("\t\t\t}");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tif (!found) {");
		result.add("\t\t\tdataList.add(data);");
		result.add("\t\t}");
		result.add("");
		result.add("\t\t((MyTableModel) table.getModel()).setDataList(dataList);");
		result.add("\t}");
		result.add("");
		result.add("\tpublic void updateDataView(List<ClientData> list) {");
		result.add("\t\t((MyTableModel) table.getModel()).setDataList(list);");
		result.add("\t}");
		result.add("");
		result.add("\tpublic void addData(ClientData data) {");
		result.add("\t\t((MyTableModel) table.getModel()).addDataList(data);");
		result.add("\t}");
		result.add("");
		result.add("\tpublic void showErrorMessage(ClientException e) {");
		result.add("\t\tJOptionPane.showMessageDialog(this, e.getMessage());");
		result.add("\t}");
		result.add("");
		result.add("\tpublic void showMessage(String message) {");
		result.add("\t\tJOptionPane.showMessageDialog(this, message);");
		result.add("\t}");
		result.add("");
		result.add("\tpublic void removeData(ClientData data) {");
		result.add("\t\t((MyTableModel) table.getModel()).removeDataList(data);");
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\tclass PopupListener extends MouseAdapter {");
		result.add("\t\tpublic void mousePressed(MouseEvent e) {");
		result.add("\t\t\tshowPopup(e);");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tpublic void mouseReleased(MouseEvent e) {");
		result.add("\t\t\tshowPopup(e);");
		result.add("\t\t}");
		result.add("");
		result.add("\t\tprivate void showPopup(MouseEvent e) {");
		result.add("\t\t\tJTable table = (JTable) e.getSource();");
		result.add("\t\t\tPoint p = e.getPoint();");
		result.add("\t\t\tint row = table.rowAtPoint(p);");
		result.add("\t\t\tif (row != -1) {");
		result.add("\t\t\t\tif (e.isPopupTrigger()) {");
		result.add("\t\t\t\t\tif (SwingUtilities.isRightMouseButton(e)) {");
		result.add("\t\t\t\t\t\t// Get the ListSelectionModel of the JTable");
		result.add("\t\t\t\t\t\tListSelectionModel model = table.getSelectionModel();");
		result.add("");
		result.add("\t\t\t\t\t\t// set the selected interval of rows. Using the");
		result.add("\t\t\t\t\t\t// \"rowNumber\"");
		result.add("\t\t\t\t\t\t// variable for the beginning and end selects only that");
		result.add("\t\t\t\t\t\t// one");
		result.add("\t\t\t\t\t\t// row.");
		result.add("\t\t\t\t\t\tmodel.setSelectionInterval(row, row);");
		result.add("\t\t\t\t\t\tpopupMenu.show(e.getComponent(), e.getX(), e.getY());");
		result.add("\t\t\t\t\t}");
		result.add("\t\t\t\t} else {");
		result.add("\t\t\t\t\tif (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {");
		result.add("\t\t\t\t\t\t// your valueChanged overridden method");
		result.add("\t\t\t\t\t\tconsultAction.actionPerformed(new ActionEvent(this,");
		result.add("\t\t\t\t\t\t\tActionEvent.ACTION_PERFORMED,");
		result.add("\t\t\t\t\t\t\tEnumClientAction.CONSULT.name()));");
		result.add("\t\t\t\t\t}");
		result.add("\t\t\t\t}");
		result.add("\t\t\t}");
		result.add("\t\t}");
		result.add("\t}");
		result.add("");
		result.add("}");

		return result;
	}

	/**
	 * Genera la clase de edición de datos
	 *
	 * @param generalData
	 * @param tableDataList
	 * @return
	 */
	private List<String> generateEditView(GeneralData generalData, List<TableData> tableDataList) {
		List<String> result = new ArrayList<String>();
		String fileName = "DataView";
		String packageName = "package " + generalData.getPackageName().toLowerCase() + ".gui." + generalData.getAppName().toLowerCase()
				+ ".view.edit" + ";";
		result.add(packageName);
		result.add("");
		result.add("import java.awt.BorderLayout;");
		result.add("import java.awt.Dimension;");
		result.add("import java.awt.GridBagConstraints;");
		result.add("import java.awt.GridBagLayout;");
		result.add("import java.awt.event.ActionEvent;");
		result.add("import java.awt.event.ActionListener;");
		result.add("");
		result.add("import javax.swing.JButton;");
		result.add("import javax.swing.JLabel;");
		result.add("import javax.swing.JOptionPane;");
		result.add("import javax.swing.JPanel;");
		result.add("import javax.swing.JTextField;");
		result.add("import javax.swing.JCheckBox;");
		result.add("");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".data.ClientData;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".exception.ClientException;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.ClientProcess;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.ResourceManager;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".view.AbstractView;");
		result.add("");

		result.add("/**");
		result.add(" * Clase de edición y creación");
		result.add(" *");
		result.add(" * @author " + generalData.getUserName());
		result.add(" *");
		result.add(" */");
		result.add("public class " + fileName + " extends AbstractView {");
		result.add("\t/** Serial version */");
		result.add("\tprivate static final long serialVersionUID = 1L;");
		result.add("");
		result.add("\tpublic static final boolean EDIT_MODE = true;");
		result.add("\tprivate String TRUE = \"TRUE\";");

		result.add("\tprivate ClientData data;");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\tprivate JLabel " + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1) + "Label;");
				switch (tableData.getType()) {
				case Boolean:
					result.add("\tprivate JCheckBox " + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check;");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
				case String:
					result.add("\tprivate JTextField " + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field;");
					break;
				default:
					break;

				}
			}
		}
		result.add("\tprivate JPanel centerPanel;");
		result.add("\tprivate JPanel southPanel;");
		result.add("\tprivate JButton saveButton;");
		result.add("\tprivate ClientProcess process;");
		result.add("\tprivate boolean mode;");
		result.add("");
		result.add("\tprivate ResourceManager resourceManager;");
		result.add("");
		result.add("\t/**");
		result.add("\t * Constructor");
		result.add("\t */");
		result.add("\tpublic DataView(ClientProcess process, ResourceManager resourceManager) {");
		result.add("\t\tthis.process = process;");
		result.add("\t\tthis.resourceManager = resourceManager;");
		result.add("");
		result.add("\t\tsetLayout(new BorderLayout());");
		result.add("\t\tinitComponents();");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Obtiene el dato de pantalla");
		result.add("\t *");
		result.add("\t * @return dato de pantalla");
		result.add("\t */");
		result.add("\tpublic ClientData getGuiData() {");
		result.add("\t\tClientData data = getData();");
		result.add("\t\tif (data == null) {");
		result.add("\t\t\tdata = new ClientData();");
		result.add("\t\t}");
		result.add("");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tdata.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "("
							+ tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1) + "Check.isSelected());");
					break;
				case Double:
					result.add("\t\tdata.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
							+ "(Double.valueOf(" + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.getText()));");
					break;
				case Float:
					result.add("\t\tdata.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
							+ "(Float.valueOf(" + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.getText()));");
					break;
				case Integer:
					result.add("\t\tdata.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
							+ "(Integer.valueOf(" + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.getText()));");
					break;
				case Long:
					result.add("\t\tdata.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
							+ "(Long.valueOf(" + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.getText()));");
					break;
				case String:
					result.add("\t\tdata.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "("
							+ tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1) + "Field.getText());");
					break;
				default:
					break;
				}
			}
		}
		result.add("\t\treturn data;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Inicializa los componentes de la vista");
		result.add("\t */");
		result.add("\tprivate void initComponents() {");
		result.add("");
		result.add("\t\tthis.southPanel = new JPanel();");
		result.add("");
		result.add("\t\tcreateCenterPanel();");
		result.add("\t\tcreateSouthPanel();");
		result.add("");
		result.add("\t\tadd(this.centerPanel, BorderLayout.CENTER);");
		result.add("\t\tadd(this.southPanel, BorderLayout.SOUTH);");
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Genera el panel sur");
		result.add("\t */");
		result.add("\tprivate void createSouthPanel() {");
		result.add("\t\tthis.southPanel = new JPanel();");
		result.add("\t\t// Inicializa los componentes");
		result.add("\t\tsaveButton = new JButton(resourceManager.getString(\"process.view.data.button.save\"));");
		result.add("\t\tsaveButton.setPreferredSize(new Dimension(100, 30));");
		result.add("\t\tsaveButton.setEnabled(true);");
		result.add("\t\tsaveButton.addActionListener(new SaveAction());");
		result.add("");
		result.add("\t\tsouthPanel.add(saveButton);");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Genera el panel central");
		result.add("\t */");
		result.add("\tprivate void createCenterPanel() {");
		result.add("\t\tthis.centerPanel = new JPanel(new GridBagLayout());");
		result.add("\t\t// Inicializamos los componentes");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
						+ "Label = new JLabel(this.resourceManager.getString(\"view.consult.label.text." + tableData.getName().toLowerCase()
						+ "\"));");
			}
		}

		result.add("\t\tGridBagConstraints gbc = new GridBagConstraints();");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
						+ "Label = new JLabel(this.resourceManager.getString(\"view.consult.label.text." + tableData.getName().toLowerCase()
						+ "\"));");
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check = new JCheckBox();");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check.setEnabled(true);");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check.setToolTipText(this.resourceManager.getString(\"view.consult.label.tooltip." + tableData.getName().toLowerCase()
							+ "\"));");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
				case String:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field = new JTextField();");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setPreferredSize(new Dimension(100, 30));");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setEditable(true);");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setToolTipText(this.resourceManager.getString(\"view.consult.label.tooltip." + tableData.getName().toLowerCase()
							+ "\"));");
					break;
				default:
					break;

				}
			}
		}
		result.add("\t\t// Colocamos los labels");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tgbc.gridx = 0;");
				result.add("\t\tgbc.gridy = " + i + ";");
				result.add("\t\tgbc.weightx = 1;");
				result.add("\t\tthis.centerPanel.add(this." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
						+ "Label, gbc);");
			}
		}
		result.add("\t\t// Colocamos los textfields");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tgbc.gridx = 1;");
				result.add("\t\tgbc.gridy = " + i + ";");
				result.add("\t\tgbc.weightx = 1;");
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tthis.centerPanel.add(this." + tableData.getName().substring(0, 1).toLowerCase()
							+ tableData.getName().substring(1) + "Check, gbc);");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
				case String:
					result.add("\t\tthis.centerPanel.add(this." + tableData.getName().substring(0, 1).toLowerCase()
							+ tableData.getName().substring(1) + "Field, gbc);");
					break;
				default:
					break;

				}
			}
		}
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Obtiene el valor de data");
		result.add("\t *");
		result.add("\t * @return valor de data");
		result.add("\t */");
		result.add("\tpublic ClientData getData() {");
		result.add("\t\treturn this.data;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Establece el valor de data");
		result.add("\t *");
		result.add("\t * @param data a establecer");
		result.add("\t */");
		result.add("\tpublic void setData(ClientData data) {");
		result.add("\t\tthis.data = data;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Actualiza los datos visuales del componente");
		result.add("\t */");
		result.add("\tpublic void updateDataGUI() {");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check.setSelected(this.data.get" + tableData.getName().substring(0, 1).toUpperCase()
							+ tableData.getName().substring(1) + "());");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setText(String.valueOf(this.data.get" + tableData.getName().substring(0, 1).toUpperCase()
							+ tableData.getName().substring(1) + "()));");

					break;
				case String:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setText(this.data.get" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
							+ "());");
					break;
				default:
					break;
				}
			}
		}
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Muestra un mensaje de error");
		result.add("\t *");
		result.add("\t * @param e");
		result.add("\t */");
		result.add("\tpublic void showErrorMessage(ClientException e) {");
		result.add("\t\tJOptionPane.showMessageDialog(this, e.getMessage(),");
		result.add("\t\t\tthis.resourceManager.getString(\"view.message.title.error\"),");
		result.add("\t\t\tJOptionPane.ERROR_MESSAGE);");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Muestra un mensaje");
		result.add("\t *");
		result.add("\t * @param msg");
		result.add("\t */");
		result.add("\tpublic void showMessage(String msg) {");
		result.add("\t\tJOptionPane.showMessageDialog(this, msg,");
		result.add("\t\t\tthis.resourceManager.getString(\"view.message.title\"),");
		result.add("\t\t\tJOptionPane.INFORMATION_MESSAGE);");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Obtiene el valor de mode");
		result.add("\t *");
		result.add("\t * @return valor de mode");
		result.add("\t */");
		result.add("\tpublic boolean isMode() {");
		result.add("\t\treturn this.mode;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Establece el valor de mode");
		result.add("\t *");
		result.add("\t * @param mode a establecer");
		result.add("\t */");
		result.add("\tpublic void setMode(boolean mode) {");
		result.add("\t\tthis.mode = mode;");
		result.add("\t}");
		result.add("");
		result.add("\tpublic class SaveAction implements ActionListener {");
		result.add("");
		result.add("\t\t@Override");
		result.add("\t\tpublic void actionPerformed(ActionEvent arg0) {");
		result.add("\t\t\tprocess.save(getGuiData());");
		result.add("\t\t}");
		result.add("\t}");
		result.add("}");
		return result;
	}

	/**
	 * Genera la clase de consulta de datos
	 *
	 * @param generalData
	 * @param tableDataList
	 * @param project
	 * @return
	 */
	private List<String> generateConsultView(GeneralData generalData, List<TableData> tableDataList) {
		List<String> result = new ArrayList<String>();
		String fileName = "ConsultDataView";
		String packageName = "package " + generalData.getPackageName().toLowerCase() + ".gui." + generalData.getAppName().toLowerCase()
				+ ".view.consult" + ";";
		result.add(packageName);
		result.add("");
		result.add("import java.awt.BorderLayout;");
		result.add("import java.awt.Dimension;");
		result.add("import java.awt.GridBagConstraints;");
		result.add("import java.awt.GridBagLayout;");
		result.add("");
		result.add("import javax.swing.JLabel;");
		result.add("import javax.swing.JOptionPane;");
		result.add("import javax.swing.JPanel;");
		result.add("import javax.swing.JTextField;");
		result.add("import javax.swing.JCheckBox;");
		result.add("");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".data.ClientData;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".exception.ClientException;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".process.ResourceManager;");
		result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".view.AbstractView;");
		result.add("");

		result.add("/**");
		result.add(" * Clase de consulta");
		result.add(" *");
		result.add(" * @author " + generalData.getUserName());
		result.add(" *");
		result.add(" */");
		result.add("public class " + fileName + " extends AbstractView {");
		result.add("\t/** Serial version */");
		result.add("\tprivate static final long serialVersionUID = 1L;");
		result.add("");
		result.add("\tpublic static final boolean EDIT_MODE = true;");
		result.add("\tprivate ClientData data;");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\tprivate JLabel " + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1) + "Label;");
				switch (tableData.getType()) {
				case Boolean:
					result.add("\tprivate JCheckBox " + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check;");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
				case String:
					result.add("\tprivate JTextField " + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field;");
					break;
				default:
					break;

				}
			}
		}
		result.add("\tprivate JPanel centerPanel;");
		result.add("\tprivate JPanel southPanel;");
		result.add("\tprivate boolean mode;");
		result.add("");
		result.add("\tprivate ResourceManager resourceManager;");
		result.add("");
		result.add("\t/**");
		result.add("\t * Constructor");
		result.add("\t */");
		result.add("\tpublic ConsultDataView(ResourceManager resourceManager) {");
		result.add("\t\tthis.resourceManager = resourceManager;");
		result.add("");
		result.add("\t\tsetLayout(new BorderLayout());");
		result.add("\t\tinitComponents();");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Inicializa los componentes de la vista");
		result.add("\t */");
		result.add("\tprivate void initComponents() {");
		result.add("");
		result.add("\t\tthis.southPanel = new JPanel();");
		result.add("");
		result.add("\t\tcreateCenterPanel();");
		result.add("\t\tcreateSouthPanel();");
		result.add("");
		result.add("\t\tadd(this.centerPanel, BorderLayout.CENTER);");
		result.add("\t\tadd(this.southPanel, BorderLayout.SOUTH);");
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Genera el panel sur");
		result.add("\t */");
		result.add("\tprivate void createSouthPanel() {");
		result.add("\t\tthis.southPanel = new JPanel();");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Genera el panel central");
		result.add("\t */");
		result.add("\tprivate void createCenterPanel() {");
		result.add("\t\tthis.centerPanel = new JPanel(new GridBagLayout());");
		result.add("\t\t// Inicializamos los componentes");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
						+ "Label = new JLabel(this.resourceManager.getString(\"view.consult.label.text." + tableData.getName().toLowerCase()
						+ "\"));");
			}
		}

		result.add("\t\tGridBagConstraints gbc = new GridBagConstraints();");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
						+ "Label = new JLabel(this.resourceManager.getString(\"view.consult.label.text." + tableData.getName().toLowerCase()
						+ "\"));");
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check = new JCheckBox();");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check.setEnabled(false);");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check.setToolTipText(this.resourceManager.getString(\"view.consult.label.tooltip." + tableData.getName().toLowerCase()
							+ "\"));");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
				case String:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field = new JTextField();");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setPreferredSize(new Dimension(100, 30));");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setEditable(false);");
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setToolTipText(this.resourceManager.getString(\"view.consult.label.tooltip." + tableData.getName().toLowerCase()
							+ "\"));");
					break;
				default:
					break;

				}
			}
		}
		result.add("\t\t// Colocamos los labels");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tgbc.gridx = 0;");
				result.add("\t\tgbc.gridy = " + i + ";");
				result.add("\t\tgbc.weightx = 1;");
				result.add("\t\tthis.centerPanel.add(this." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
						+ "Label, gbc);");
			}
		}
		result.add("\t\t// Colocamos los textfields");
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				result.add("\t\tgbc.gridx = 1;");
				result.add("\t\tgbc.gridy = " + i + ";");
				result.add("\t\tgbc.weightx = 1;");
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tthis.centerPanel.add(this." + tableData.getName().substring(0, 1).toLowerCase()
							+ tableData.getName().substring(1) + "Check, gbc);");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
				case String:
					result.add("\t\tthis.centerPanel.add(this." + tableData.getName().substring(0, 1).toLowerCase()
							+ tableData.getName().substring(1) + "Field, gbc);");
					break;
				default:
					break;

				}
			}
		}
		result.add("");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Obtiene el valor de data");
		result.add("\t *");
		result.add("\t * @return valor de data");
		result.add("\t */");
		result.add("\tpublic ClientData getData() {");
		result.add("\t\treturn this.data;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Establece el valor de data");
		result.add("\t *");
		result.add("\t * @param data a establecer");
		result.add("\t */");
		result.add("\tpublic void setData(ClientData data) {");
		result.add("\t\tthis.data = data;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Actualiza los datos visuales del componente");
		result.add("\t */");
		result.add("\tpublic void updateDataGUI() {");
		for (TableData tableData : tableDataList) {
			if (!"id".equals(tableData.getName())) {
				switch (tableData.getType()) {
				case Boolean:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Check.setSelected(this.data.get" + tableData.getName().substring(0, 1).toUpperCase()
							+ tableData.getName().substring(1) + "());");
					break;
				case Double:
				case Float:
				case Integer:
				case Long:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setText(String.valueOf(this.data.get" + tableData.getName().substring(0, 1).toUpperCase()
							+ tableData.getName().substring(1) + "()));");

					break;
				case String:
					result.add("\t\tthis." + tableData.getName().substring(0, 1).toLowerCase() + tableData.getName().substring(1)
							+ "Field.setText(this.data.get" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1)
							+ "());");
					break;
				default:
					break;
				}
			}
		}
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Muestra un mensaje de error");
		result.add("\t *");
		result.add("\t * @param e");
		result.add("\t */");
		result.add("\tpublic void showErrorMessage(ClientException e) {");
		result.add("\t\tJOptionPane.showMessageDialog(this, e.getMessage(),");
		result.add("\t\t\tthis.resourceManager.getString(\"view.message.title.error\"),");
		result.add("\t\t\tJOptionPane.ERROR_MESSAGE);");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Muestra un mensaje");
		result.add("\t *");
		result.add("\t * @param msg");
		result.add("\t */");
		result.add("\tpublic void showMessage(String msg) {");
		result.add("\t\tJOptionPane.showMessageDialog(this, msg,");
		result.add("\t\t\tthis.resourceManager.getString(\"view.message.title\"),");
		result.add("\t\t\tJOptionPane.INFORMATION_MESSAGE);");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Obtiene el valor de mode");
		result.add("\t *");
		result.add("\t * @return valor de mode");
		result.add("\t */");
		result.add("\tpublic boolean isMode() {");
		result.add("\t\treturn this.mode;");
		result.add("\t}");
		result.add("");
		result.add("\t/**");
		result.add("\t * Establece el valor de mode");
		result.add("\t *");
		result.add("\t * @param mode a establecer");
		result.add("\t */");
		result.add("\tpublic void setMode(boolean mode) {");
		result.add("\t\tthis.mode = mode;");
		result.add("\t}");
		result.add("}");
		return result;
	}

	/**
	 * Modifica el alias de almacenaje
	 *
	 * @param finalLines
	 * @return
	 */
	private List<String> changeDaoAlias(List<String> readLines) {
		List<String> finalLines = new ArrayList<String>();

		for (int i = 0; i < readLines.size(); i++) {
			String line = readLines.get(i);
			if (line.contains("ServerData.class")) {
				line = "\t\tthis.xstream.alias(" + "\"" + generalData.getDataModelName() + "\"" + ", ServerData.class);";
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Modifica la paquetería de todas las clases
	 *
	 * @param readLines
	 * @param project
	 * @return
	 */
	private List<String> changePackage(List<String> readLines, DataModelType project) {
		List<String> finalLines = new ArrayList<String>();

		for (int i = 0; i < readLines.size(); i++) {
			String line = readLines.get(i);
			if (line.startsWith("package")) {
				String packageMode = "";
				switch (project) {
				case Client:
					packageMode = "gui";
					break;
				case Server:
					packageMode = "impl";
					break;
				case Server_Interface:
					packageMode = "iface";
					break;
				default:
					break;
				}
				String[] lineSplit = line.split("template");
				if (lineSplit[1].length() == 1) {
					// Raiz principal
					line = "package " + generalData.getPackageName() + "." + packageMode + "." + generalData.getAppName().toLowerCase() + ";";
				} else {
					// resto de paquetería
					line = "package " + generalData.getPackageName() + "." + packageMode + "." + generalData.getAppName().toLowerCase()
							+ lineSplit[1];
				}
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Modifica todos los imports de los ficheros
	 *
	 * @param readLines
	 * @param project
	 * @return
	 */
	private List<String> changeImports(List<String> readLines) {
		List<String> finalLines = new ArrayList<String>();
		for (int i = 0; i < readLines.size(); i++) {
			String line = readLines.get(i);
			if (line.startsWith("import es.uma.lcc.iface.template")) {
				String[] splitLine = line.split("import es.uma.lcc.iface.template");
				line = "import " + generalData.getPackageName() + ".iface." + generalData.getAppName().toLowerCase() + splitLine[1];
			} else if (line.startsWith("import es.uma.lcc.impl.template")) {
				String[] splitLine = line.split("import es.uma.lcc.impl.template");
				line = "import " + generalData.getPackageName() + ".impl." + generalData.getAppName().toLowerCase() + splitLine[1];
			} else if (line.startsWith("import es.uma.lcc.gui.template")) {
				String[] splitLine = line.split("import es.uma.lcc.gui.template");
				line = "import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + splitLine[1];
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Cambia el usuario de todos los ficheros
	 *
	 * @param finalLines
	 * @return
	 */
	private List<String> changeUser(List<String> readLines) {
		List<String> finalLines = new ArrayList<String>();
		for (int i = 0; i < readLines.size(); i++) {
			String line = readLines.get(i);
			if (line.contains("@author")) {
				String[] splitLine = line.split("@author");
				line = splitLine[0] + "@author " + generalData.getUserName();
			}
			finalLines.add(line);
		}
		return finalLines;
	}

	/**
	 * Crea la estructura de carpetas según la paquetería indicada
	 *
	 * @param project
	 *            proyecto
	 * @param packageName
	 *            paquetería
	 * @param folderTempName
	 */
	private void createPackageStructureFolders(DataModelType project, String packageName, String folderTempName, String appName) {
		String aux = folderTempName + "/" + project + "/main/java";
		String[] folders = packageName.replace('.', '/').split("/");

		File folder;
		for (int i = 0; i < folders.length; i++) {
			String newFolder = folders[i];
			aux += "/" + newFolder;
			folder = new File(aux);
			if (folder.mkdir()) {
				logger.log(Level.INFO,"Created " + folder.getAbsolutePath());
			} else {
				logger.log(Level.INFO,"Error creating " + folder.getAbsolutePath());
			}
		}

		// Creamos la última carpeta según el tipo de projecto
		String lastFolder = "";
		switch (project) {
		case Client:
			lastFolder = "gui";
			break;
		case Server:
			lastFolder = "impl";
			break;
		case Server_Interface:
			lastFolder = "iface";
			break;
		}
		aux += "/" + lastFolder;
		folder = new File(aux);
		if (folder.mkdir()) {
			logger.log(Level.INFO,"Created " + folder.getAbsolutePath());
		} else {
			logger.log(Level.INFO,"Error creating " + folder.getAbsolutePath());
		}

		aux += "/" + appName.toLowerCase();
		folder = new File(aux);
		if (folder.mkdir()) {
			logger.log(Level.INFO,"Created " + folder.getAbsolutePath());
		} else {
			logger.log(Level.INFO,"Error creating " + folder.getAbsolutePath());
		}
	}

	/**
	 * Genera la clase converterManager según el tipo (Cliente o servidor)
	 *
	 * @param generalData
	 * @param tableDataList
	 * @param client
	 * @return
	 */
	private List<String> generateConverterManager(GeneralData generalData, List<TableData> tableDataList, DataModelType modelType) {
		List<String> result = new ArrayList<String>();
		String fileName = "ConverterManager";
		String packageName = "";
		switch (modelType) {
		case Client:
			packageName = "package " + generalData.getPackageName().toLowerCase() + ".gui." + generalData.getAppName().toLowerCase() + ".converter"
					+ ";";
			break;
		case Server:
			packageName = "package " + generalData.getPackageName().toLowerCase() + ".impl." + generalData.getAppName().toLowerCase() + ".converter"
					+ ";";
			break;
		default:
			break;

		}
		result.add(packageName);
		result.add("");
		result.add("import java.util.ArrayList;");
		result.add("import java.util.List;");
		result.add("");
		switch (modelType) {
		case Client:
			result.add("import " + generalData.getPackageName() + ".gui." + generalData.getAppName().toLowerCase() + ".data.ClientData;");
			result.add("import " + generalData.getPackageName() + ".iface." + generalData.getAppName().toLowerCase() + ".data.SPIData;");
			break;
		case Server:
			result.add("import " + generalData.getPackageName() + ".iface." + generalData.getAppName().toLowerCase() + ".data.SPIData;");
			result.add("import " + generalData.getPackageName() + ".iface." + generalData.getAppName().toLowerCase() + ".result.SPIErrorCode;");
			result.add("import " + generalData.getPackageName() + ".impl." + generalData.getAppName().toLowerCase() + ".data.ServerData;");
			result.add("import " + generalData.getPackageName() + ".impl." + generalData.getAppName().toLowerCase() + ".exception.ServerException;");
			break;
		default:
			break;
		}
		result.add("");
		result.add("/**");
		result.add(" * Clase princial del manager");
		result.add(" *");
		result.add(" * @author " + generalData.getUserName());
		result.add(" *");
		result.add(" */");

		result.add("public class " + fileName + " {");
		result.add("");
		TableData data = null;
		switch (modelType) {
		case Client:
			// Método 1
			result.add("\t/**");
			result.add("\t * Convierte del modelo interno al modelo de intercambio");
			result.add("\t *");
			result.add("\t * @param data");
			result.add("\t * @return");
			result.add("\t */");
			result.add("\tpublic SPIData convert(ClientData data) {");
			result.add("\t\tSPIData result = null;");
			result.add("");
			result.add("\t\tif (data != null) {");
			result.add("\t\t\tresult = new SPIData();");
			for (TableData tableData : tableDataList) {
				result.add("\t\t\tresult.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "(data.get"
						+ tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());");
			}
			for (TableData tableData : tableDataList) {
				if ("id".equals(tableData.getName())) {
					data = tableData;
				}
			}

			if (data == null) {
				data = new TableData();
				data.setName("id");
				data.setType(TypeEnum.String);

				result.add("\t\t\tresult.set" + data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "(data.get"
						+ data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "());");
			}
			result.add("\t\t}");
			result.add("");
			result.add("\t\treturn result;");
			result.add("\t}");

			// Método 2
			result.add("\t/**");
			result.add("\t * Convierte del modelo de intercambio al modelo interno");
			result.add("\t *");
			result.add("\t * @param SPIData");
			result.add("\t * @return");
			result.add("\t * @throws ServerException");
			result.add("\t */");
			result.add("\tpublic ClientData convert(SPIData data){");
			result.add("\t\tClientData result = null;");
			result.add("");
			result.add("\t\tif (data != null) {");
			result.add("\t\t\tresult = new ClientData();");
			for (TableData tableData : tableDataList) {
				result.add("\t\t\tresult.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "(data.get"
						+ tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());");
			}
			data = null;
			for (TableData tableData : tableDataList) {
				if ("id".equals(tableData.getName())) {
					data = tableData;
				}
			}

			if (data == null) {
				data = new TableData();
				data.setName("id");
				data.setType(TypeEnum.String);

				result.add("\t\t\tresult.set" + data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "(data.get"
						+ data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "());");
			}
			result.add("\t\t}");
			result.add("");
			result.add("\t\treturn result;");
			result.add("\t}");

			// Método 3
			result.add("\t/**");
			result.add("\t * Convierte la lista del modelo interno al modelo de intercambio");
			result.add("\t *");
			result.add("\t * @param list");
			result.add("\t * @return");
			result.add("\t */");
			result.add("\tpublic List<ClientData> convertSPIList(List<SPIData> list) {");
			result.add("\t\tList<ClientData> resultList = new ArrayList<ClientData>();");
			result.add("\t\tfor (SPIData data : list) {");
			result.add("\t\t\tresultList.add(convert(data));");
			result.add("\t\t}");
			result.add("\t\treturn resultList;");
			result.add("\t}");

			break;
		case Server:
			// Método 1
			result.add("\t/**");
			result.add("\t * Convierte del modelo interno al modelo de intercambio");
			result.add("\t *");
			result.add("\t * @param data");
			result.add("\t * @return");
			result.add("\t */");
			result.add("\tpublic SPIData convert(ServerData data) {");
			result.add("\t\tSPIData result = null;");
			result.add("");
			result.add("\t\tif (data != null) {");
			result.add("\t\t\tresult = new SPIData();");
			for (TableData tableData : tableDataList) {
				result.add("\t\t\tresult.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "(data.get"
						+ tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());");
			}
			data = null;
			for (TableData tableData : tableDataList) {
				if ("id".equals(tableData.getName())) {
					data = tableData;
				}
			}

			if (data == null) {
				data = new TableData();
				data.setName("id");
				data.setType(TypeEnum.String);

				result.add("\t\t\tresult.set" + data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "(data.get"
						+ data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "());");
			}
			result.add("\t\t}");
			result.add("");
			result.add("\t\treturn result;");
			result.add("\t}");

			// Método 2
			result.add("\t/**");
			result.add("\t * Convierte del modelo de intercambio al modelo interno");
			result.add("\t *");
			result.add("\t * @param SPIData");
			result.add("\t * @return");
			result.add("\t * @throws ServerException");
			result.add("\t */");
			result.add("\tpublic ServerData convert(SPIData data) throws ServerException {");
			result.add("\t\tif (data == null) {");
			result.add("\t\t\tthrow new ServerException(SPIErrorCode.CONVERTER_ERROR,\"cannot convert null data\");");
			result.add("\t\t}");
			result.add("\t\tServerData result = null;");
			result.add("");
			result.add("\t\tif (data != null) {");
			result.add("\t\t\tresult = new ServerData();");
			for (TableData tableData : tableDataList) {
				result.add("\t\t\tresult.set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "(data.get"
						+ tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "());");
			}
			data = null;
			for (TableData tableData : tableDataList) {
				if ("id".equals(tableData.getName())) {
					data = tableData;
				}
			}

			if (data == null) {
				data = new TableData();
				data.setName("id");
				data.setType(TypeEnum.String);

				result.add("\t\t\tresult.set" + data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "(data.get"
						+ data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "());");
			}
			result.add("\t\t}");
			result.add("");
			result.add("\t\treturn result;");
			result.add("\t}");

			// Método 3
			result.add("\t/**");
			result.add("\t * Convierte la lista del modelo interno al modelo de intercambio");
			result.add("\t *");
			result.add("\t * @param list");
			result.add("\t * @return");
			result.add("\t * @throws ServerException");
			result.add("\t */");
			result.add("\tpublic List<SPIData> convert(List<ServerData> list) throws ServerException {");
			result.add("\t\tif (list == null) {");
			result.add("\t\t\tthrow new ServerException(SPIErrorCode.CONVERTER_ERROR,\"cannot convert null data\");");
			result.add("\t\t}");
			result.add("\t\tList<SPIData> resultList = new ArrayList<SPIData>();");
			result.add("\t\tfor (ServerData data : list) {");
			result.add("\t\t\tresultList.add(convert(data));");
			result.add("\t\t}");
			result.add("\t\treturn resultList;");
			result.add("\t}");
			break;
		default:
			break;

		}
		result.add("}");
		return result;
	}

	/**
	 * Genera la clase del modelo de datos
	 *
	 * @param generalData
	 *            Datos generales de la aplicación
	 * @param tableDataList
	 *            Listado de atributos
	 * @param modelType
	 * @return fichero generado
	 */
	private List<String> generateDataModelBean(GeneralData generalData, List<TableData> tableDataList, DataModelType modelType) {
		List<String> result = new ArrayList<String>();
		String fileName = "";
		String packageName = "";
		switch (modelType) {
		case Client:
			fileName = "ClientData";
			packageName = "package " + generalData.getPackageName().toLowerCase() + ".gui." + generalData.getAppName().toLowerCase() + ".data" + ";";
			break;
		case Server:
			fileName = "ServerData";
			packageName = "package " + generalData.getPackageName().toLowerCase() + ".impl." + generalData.getAppName().toLowerCase() + ".data" + ";";
			break;
		case Server_Interface:
			fileName = "SPIData";
			packageName = "package " + generalData.getPackageName().toLowerCase() + ".iface." + generalData.getAppName().toLowerCase() + ".data"
					+ ";";
			break;
		default:
			break;

		}
		result.add(packageName);
		result.add("");
		result.add("import java.io.Serializable;");
		result.add("");
		result.add("/**");
		result.add(" * Modelo de datos de " + modelType);
		result.add(" *");
		result.add(" * @author " + generalData.getUserName());
		result.add(" *");
		result.add(" */");

		result.add("public class " + fileName + " implements Serializable {");
		result.add("");
		result.add("\t/** Serial Version */");
		result.add("\tprivate static final long serialVersionUID = 1L;");
		result.add("");

		TableData data = null;
		for (TableData tableData : tableDataList) {
			if ("id".equals(tableData.getName())) {
				data = tableData;
			}
		}
		if (data == null) {
			data = new TableData();
			data.setName("id");
			data.setDescription("id");
			data.setType(TypeEnum.String);

			result.add("\t/** " + data.getDescription() + " */");
			result.add("\tprivate " + data.getType().toString() + " " + data.getName() + ";");
			result.add("");
		}

		for (TableData tableData : tableDataList) {
			result.add("\t/** " + tableData.getDescription() + " */");
			result.add("\tprivate " + tableData.getType().toString() + " " + tableData.getName() + ";");
			result.add("");
		}
		for (TableData tableData : tableDataList) {

			result.add("\t/**");
			result.add("\t * Obtiene el valor de " + tableData.getName());
			result.add("\t */");
			result.add("\tpublic " + tableData.getType().toString() + " get" + tableData.getName().substring(0, 1).toUpperCase()
					+ tableData.getName().substring(1) + "(){");
			result.add("\t\treturn this." + tableData.getName() + ";");
			result.add("\t}");
			result.add("");

			result.add("\t/**");
			result.add("\t * Establece el valor de " + tableData.getName());
			result.add("\t */");
			result.add("\tpublic void set" + tableData.getName().substring(0, 1).toUpperCase() + tableData.getName().substring(1) + "("
					+ tableData.getType().toString() + " " + tableData.getName() + "){");
			result.add("\t\tthis." + tableData.getName() + "=" + tableData.getName() + ";");
			result.add("\t}");
			result.add("");
		}

		data = null;
		for (TableData tableData : tableDataList) {
			if ("id".equals(tableData.getName())) {
				data = tableData;
			}
		}

		if (data == null) {
			data = new TableData();
			data.setName("id");
			data.setType(TypeEnum.String);

			result.add("\t/**");
			result.add("\t * Obtiene el valor de " + data.getName());
			result.add("\t */");
			result.add("\tpublic " + data.getType().toString() + " get" + data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1)
					+ "(){");
			result.add("\t\treturn this." + data.getName() + ";");
			result.add("\t}");
			result.add("");

			result.add("\t/**");
			result.add("\t * Establece el valor de " + data.getName());
			result.add("\t */");
			result.add("\tpublic void set" + data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1) + "("
					+ data.getType().toString() + " " + data.getName() + "){");
			result.add("\t\tthis." + data.getName() + "=" + data.getName() + ";");
			result.add("\t}");
			result.add("");
		}

		result.add("\t@Override");
		result.add("\tpublic String toString() {");
		String toString = "";
		toString += "return \"CLientData [id=\" + id + \", ";
		// Por seguridad, quitamos en caso de estar el atributo id
		List<TableData> tableDataNoId = new ArrayList<TableData>();
		for (int i = 0; i < tableDataList.size(); i++) {
			TableData tableData = tableDataList.get(i);
			if (!"id".equals(tableData.getName())) {
				tableDataNoId.add(tableData);
			}
		}
		// Trabajamos con la lista que no tiene el id
		for (int i = 0; i < tableDataNoId.size(); i++) {
			TableData tableData = tableDataNoId.get(i);
			if (i < tableDataNoId.size() - 1) {
				toString += tableData.getName() + "=\" + " + tableData.getName() + " + \", ";
			} else {
				toString += tableData.getName() + "=\" + " + tableData.getName() + " + \"]\";";
			}
		}
		result.add(toString);
		result.add("\t}");
		result.add("}");
		return result;
	}

	/**
	 * Copia el fichero de server_interface al cliente y servidor
	 *
	 * @throws IOException
	 */
	private void copyServerInterface(String initPath) throws IOException {
		FileUtils.copyFileToDirectory(new File(initPath + "/temp/Server_Interface/build/jar/Server_Interface.jar"), new File(initPath
				+ "/temp/Server/lib"), true);

		FileUtils.copyFileToDirectory(new File(initPath + "/temp/Server_Interface/build/jar/Server_Interface.jar"), new File(initPath
				+ "/temp/Client/lib"), true);
	}

	/**
	 * Realiza la compilación del proyecto en cuestión
	 *
	 * @param projectName
	 *            proyecto a generar
	 */
	private void generateProject(DataModelType projectName, File fileToSave) {
		logger.log(Level.INFO, "Generating " + projectName);

		Project project = new Project();
		// String target = "compile";

		File buildFile = new File(fileToSave.getAbsolutePath() + "/temp/" + projectName + "/build.xml");

		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		project.init();

		ProjectHelper helper = ProjectHelper.getProjectHelper();
		helper.parse(project, buildFile);

		project.addBuildListener(getDefaultLogger());
		project.addReference("ant.ProjectHelper", helper);

		// project.executeTarget("-post-jar");
		project.executeTarget("clean");
		project.executeTarget("compile");
		project.executeTarget("jar");
		project.log("=== Build Completed Successfully [" + projectName + "]===", Project.MSG_INFO);
	}

	/**
	 * Obtiene el logger por defecto
	 *
	 * @return DefaultLogger
	 */
	private static DefaultLogger getDefaultLogger() {
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
		return consoleLogger;
	}

	/**
	 * Devuelve el valor del atributo glassPanel
	 *
	 * @return atributo glassPanel
	 */
	public GlassPanel getGlassPanel() {
		return glassPanel;
	}

	/**
	 * Establece el atributo glassPanel
	 *
	 * @param glassPanel
	 *            atributo glassPanel a establecer
	 */
	public void setGlassPanel(GlassPanel glassPanel) {
		this.glassPanel = glassPanel;
	}

	/**
	 * Establece el frame principal de la aplicación
	 *
	 * @param tfgFrame
	 */
	public void setTfgFrame(TFGFrame tfgFrame) {
		this.tfgFrame = tfgFrame;
	}

}
