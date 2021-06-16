package pta;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 日志及结果输出
 * @author xinjiang
 *
 */
public class Printer {
	
	public static void printResult(String filename, String result) {
		try {
			FileWriter writer = new FileWriter(filename);
			writer.write(result);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
