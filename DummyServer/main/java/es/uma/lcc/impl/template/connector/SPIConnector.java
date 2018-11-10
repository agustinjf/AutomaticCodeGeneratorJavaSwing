package es.uma.lcc.impl.template.connector;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uma.lcc.iface.template.connector.ISPIConnector;
import es.uma.lcc.iface.template.data.SPIData;
import es.uma.lcc.iface.template.result.SPIErrorCode;
import es.uma.lcc.iface.template.result.SPIResultData;
import es.uma.lcc.impl.template.converter.ConverterManager;
import es.uma.lcc.impl.template.data.ServerData;
import es.uma.lcc.impl.template.exception.ServerException;
import es.uma.lcc.impl.template.process.ProcessManager;

public class SPIConnector extends UnicastRemoteObject implements ISPIConnector {

	/** Serial version */
	private static final long serialVersionUID = 1L;
	ProcessManager processManager = null;
	ConverterManager converterManager = null;

	public SPIConnector() throws RemoteException {
		super();
		processManager = new ProcessManager();
		converterManager = new ConverterManager();
	}

	@Override
	public SPIResultData<SPIData> get(String id) {
		Logger.getLogger(getClass().getName()).log(Level.INFO, id);

		SPIResultData<SPIData> spiResultData = new SPIResultData<SPIData>(
				SPIErrorCode.OK);
		try {
			ServerData data = processManager.find(id);
			SPIData spiData = converterManager.convert(data);
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					spiData.toString());
			spiResultData.setData(spiData);
		} catch (ServerException e) {
			spiResultData.setCode(e.getCode());
			spiResultData.setEx(e);
		}
		return spiResultData;
	}

	@Override
	public SPIResultData<List<SPIData>> get() {
		Logger.getLogger(getClass().getName()).log(Level.INFO, "");
		SPIResultData<List<SPIData>> spiResultData = new SPIResultData<List<SPIData>>(
				SPIErrorCode.OK);
		try {
			List<ServerData> list = processManager.find();
			List<SPIData> spiList = converterManager.convert(list);
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					spiList.toString());
			spiResultData.setData(spiList);
		} catch (ServerException e) {
			spiResultData.setCode(e.getCode());
			spiResultData.setEx(e);
		}
		return spiResultData;
	}

	@Override
	public SPIResultData<SPIData> put(SPIData SPIData) {
		Logger.getLogger(getClass().getName()).log(Level.INFO,
				SPIData.toString());
		SPIResultData<SPIData> spiResultData = new SPIResultData<SPIData>(
				SPIErrorCode.OK);
		try {
			Boolean result = true;
			ServerData data = converterManager.convert(SPIData);
			processManager.save(data);
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					result.toString());
			spiResultData.setData(converterManager.convert(data));
		} catch (ServerException e) {
			spiResultData.setData(null);
			spiResultData.setCode(e.getCode());
			spiResultData.setEx(e);
		}
		return spiResultData;
	}

	@Override
	public Boolean isAlive() throws RemoteException {
		return true;
	}

	@Override
	public SPIResultData<SPIData> delete(SPIData SPIData)
			throws RemoteException {
		Logger.getLogger(getClass().getName()).log(Level.INFO,
				SPIData.toString());
		SPIResultData<SPIData> spiResultData = new SPIResultData<SPIData>(
				SPIErrorCode.OK);
		try {
			Boolean result = true;
			ServerData data = converterManager.convert(SPIData);
			result = processManager.delete(data);
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					result.toString());
			if (result) {
				spiResultData.setData(converterManager.convert(data));
			} else {
				spiResultData.setData(null);
			}
		} catch (ServerException e) {
			spiResultData.setData(null);
			spiResultData.setCode(e.getCode());
			spiResultData.setEx(e);
		}
		return spiResultData;
	}

}
