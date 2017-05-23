package gr.uoi.cs.daintiness.hecate.exportinformation;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import gr.uoi.cs.daintiness.hecate.differencedetection.SchemataDifferencesManager;
import gr.uoi.cs.daintiness.hecate.transitions.Deletion;
import gr.uoi.cs.daintiness.hecate.transitions.Insertion;
import gr.uoi.cs.daintiness.hecate.transitions.ChangesList;
import gr.uoi.cs.daintiness.hecate.transitions.Transitions;
import gr.uoi.cs.daintiness.hecate.transitions.Update;

public  class xmlExport extends Export {
	public  void exportInformation(SchemataDifferencesManager manager) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Update.class,
					Deletion.class, Insertion.class,
					ChangesList.class, Transitions.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			String filePath = getDirectory(manager.path) + File.separator + "transitions.xml";
			jaxbMarshaller.marshal(manager.transitions, new FileOutputStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
