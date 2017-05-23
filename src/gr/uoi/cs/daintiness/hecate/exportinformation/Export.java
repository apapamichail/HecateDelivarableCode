package gr.uoi.cs.daintiness.hecate.exportinformation;

import java.io.File;
import java.io.IOException;

import gr.uoi.cs.daintiness.hecate.differencedetection.SchemataDifferencesManager;

public abstract class Export {
	
	public String getDirectory(String path) {
		String parrent = (new File(path)).getParent();
		File directory = new File(parrent + File.separator + "results");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory.getPath();
	}

	public abstract void exportInformation(SchemataDifferencesManager manager) throws IOException;

	



}
