package es.uma.lcc.impl.template.converter;

import java.util.ArrayList;
import java.util.List;

import es.uma.lcc.iface.template.data.SPIData;
import es.uma.lcc.iface.template.result.SPIErrorCode;
import es.uma.lcc.impl.template.data.ServerData;
import es.uma.lcc.impl.template.exception.ServerException;

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
	public SPIData convert(ServerData data) {
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
	 * Convierte la lista del modelo interno al modelo de intercambio
	 *
	 * @param list
	 * @return
	 * @throws ServerException
	 */
	public List<SPIData> convert(List<ServerData> list) throws ServerException {
		if (list == null) {
			throw new ServerException(SPIErrorCode.CONVERTER_ERROR,
					"cannot convert null data");
		}
		List<SPIData> resultList = new ArrayList<SPIData>();
		for (ServerData data : list) {
			resultList.add(convert(data));
		}
		return resultList;
	}

	/**
	 * Convierte del modelo de intercambio al modelo interno
	 *
	 * @param SPIData
	 * @return
	 * @throws ServerException
	 */
	public ServerData convert(SPIData data) throws ServerException {
		if (data == null) {
			throw new ServerException(SPIErrorCode.CONVERTER_ERROR,
					"cannot convert null data");
		}
		ServerData result = null;

		if (data != null) {
			result = new ServerData();
			result.setId(data.getId());
			result.setHost(data.getHost());
			result.setDescription(data.getDescription());
			result.setIsActive(data.getIsActive());
		}

		return result;
	}
}
