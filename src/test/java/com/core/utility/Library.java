package com.core.utility;

import com.core.framework.htmlreporter.Reporter;
import com.core.framework.listener.Listener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;


@Slf4j
public class Library {

	Reporter reporter = Listener.reporter;

	Properties globalConfig = Listener.property;

	public String extractPayloadToText(String fileName,String text) {
		String folderName = reporter.getAssetFolder() + "/";
		File f = (new File(folderName));
		if (!f.exists()) {
			log.debug((f.mkdirs() ? "Payload folder created" : "Payload folder creation failed"));
		}
		String filePath = folderName + fileName+".txt";
		try {
			// write file with given data
			FileWriter writer = new FileWriter(filePath);
			writer.write(text);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "assets/" + fileName+".txt";
	}
}
