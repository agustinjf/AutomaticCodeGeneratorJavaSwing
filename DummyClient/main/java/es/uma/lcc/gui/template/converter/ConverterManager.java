package es.uma.lcc.gui.template.converter;

import java.util.ArrayList;
import java.util.List;

import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.iface.template.data.SPIData;

/**
 * Clase principal de manager
 *
 * @author ajifernandez
 *
 */
public class ConverterManager {
	/**
	 * Convierte del modelo interno al modelo de intercambio
	 *
	 * @param data
	 * @return
	 */
	public SPIData convert(ClientData data) {
		SPIData result = null;

		if (data != null) {
			result = new SPIData();
			result.setId(data.getId());
			result.setHost(data.getHost());
			result.setDescription(data.getDescription());
			result.setIsActive(data.getIsActive());
		}

		return result;
	}

	/**
	 * Convierte del modelo de intercambio al modelo interno
	 *
	 * @param spiHostsData
	 * @return
	 */
	public ClientData convert(SPIData data) {
		ClientData result = null;

		if (data != null) {
			result = new ClientData();
			result.setId(data.getId());
			result.setHost(data.getHost());
			result.setDescription(data.getDescription());
			result.setIsActive(data.getIsActive());
		}

		return result;
	}

	/**
	 * Convierte una lista del modelo de intercambio al modelo interno
	 *
	 * @param list
	 * @return
	 */
	public List<ClientData> convertSPIList(List<SPIData> list) {
		List<ClientData> resultList = new ArrayList<ClientData>();
		for (SPIData data : list) {
			resultList.add(convert(data));
		}
		return resultList;
	}

}
