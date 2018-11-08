package es.uma.lcc.gui.appgenerator.data;

/**
 * Modelo de datos de un atributo de la tabla, que compondrá el modelo de datos
 * de la aplicación
 *
 * @author ajifernandez
 *
 */
public class TableData {
	/** Tipo del atributo */
	TypeEnum type;
	/** Nombre del atributo */
	String name;
	/** Descripción/comentario javadoc */
	String description;

	/**
	 * Devuelve el valor del atributo type
	 *
	 * @return atributo type
	 */
	public TypeEnum getType() {
		return type;
	}

	/**
	 * Establece el atributo type
	 *
	 * @param type
	 *            atributo type a establecer
	 */
	public void setType(TypeEnum type) {
		this.type = type;
	}

	/**
	 * Devuelve el valor del atributo name
	 *
	 * @return atributo name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece el atributo name
	 *
	 * @param name
	 *            atributo name a establecer
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve el valor del atributo description
	 *
	 * @return atributo description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Establece el atributo description
	 *
	 * @param description
	 *            atributo description a establecer
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableData other = (TableData) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TableData [type=" + type + ", name=" + name + ", description=" + description + "]";
	}

}
