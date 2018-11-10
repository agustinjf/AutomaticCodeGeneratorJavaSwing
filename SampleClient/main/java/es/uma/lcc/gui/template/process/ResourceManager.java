package es.uma.lcc.gui.template.process;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Manager de recursos
 *
 * @author ajifernandez
 *
 */
public class ResourceManager {
	private final String BUNDLE_NAME = "es.uma.lcc.gui.template.messages";

	private final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(this.BUNDLE_NAME);

	/**
	 * Constructor
	 */
	public ResourceManager() {
	}

	/**
	 * Obtiene una cadena internacionalizada sin valor por defecto
	 *
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return getString(key, '!' + key + '!');
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
	public String getString(String key, String defaultValue) {
		try {
			return this.RESOURCE_BUNDLE.getString(key);
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
}
