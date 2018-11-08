package es.uma.lcc.gui.appgenerator.process;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * Manager de recursos
 *
 * @author ajifernandez
 *
 */
public class ResourceManager {
	private final String BUNDLE_NAME_LANGUAGE = "es.uma.lcc.gui.appgenerator.messages";

	public ResourceBundle resourceBundleLanguage;

	/**
	 * Constructor
	 */
	public ResourceManager() {
		String property = System.getProperty("user.language");
		resourceBundleLanguage = ResourceBundle.getBundle(this.BUNDLE_NAME_LANGUAGE + "_" + property);
	}

	/**
	 * Obtiene una cadena internacionalizada sin valor por defecto
	 *
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return getString(key, '!' + key + '!', this.resourceBundleLanguage);
	}

	/**
	 * Obtiene una cadena internacionalizada
	 *
	 * @param key
	 *            clave de la cadena
	 * @param defaultValue
	 *            valor por defecto en caso de no encontrarse la cedana
	 * @return cadena internacionalizada
	 */
	public String getString(String key, String defaultValue, ResourceBundle resourceBundle) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return defaultValue;
		}
	}

	/**
	 * Obtiene una cadena internacionalizada con parámetros
	 *
	 * @param key
	 * @param params
	 * @return
	 */
	public String getString(String key, Object[] params) {
		return MessageFormat.format(getString(key), params);
	}

	/**
	 * Obtiene el path de un icon dado su key
	 *
	 * @param id
	 * @return
	 */
	private String getPath(String key) {
		return "/es/uma/lcc/gui/appgenerator/" + key + ".png";
	}

	/**
	 * Obtiene el icon según el key de la imagen
	 *
	 * @param path
	 * @param description
	 * @return
	 */
	public ImageIcon getImageIcon(String key) {
		String path = getPath(key);
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
