/**
 * @author ajifernandez
 */
package es.uma.lcc.gui.appgenerator.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author ajifernandez
 *
 */
public class Compilator {
	static Logger logger = LogManager.getLogger(Compilator.class);

	public static void printLines(String name, InputStream ins) throws Exception {
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			logger.log(Level.INFO,name + " " + line);
		}
	}

	public static void runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		printLines(command + " stdout:", pro.getInputStream());
		printLines(command + " stderr:", pro.getErrorStream());
		pro.waitFor();
		logger.log(Level.INFO,command + " exitValue() " + pro.exitValue());
	}

}
