package gr.uoi.cs.daintiness.hecate.exportinformation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gr.uoi.cs.daintiness.hecate.differencedetection.DifferencesBetweenTwoSchemataData;
import gr.uoi.cs.daintiness.hecate.differencedetection.SchemataDifferencesManager;

public class MetricsExport extends Export {


	public  void exportInformation(SchemataDifferencesManager manager)
			throws IOException {
		DifferencesBetweenTwoSchemataData result;
		String filePath = getDirectory(manager.path) + File.separator + "metrics.csv";
		FileWriter filewriter = new FileWriter(filePath, true);
		BufferedWriter metrics = new BufferedWriter(filewriter);
		for(int i=0; i < manager.resultList.size(); i++){
			result = manager.resultList.get(i);
		String name = result.myMetrics.getVersionNames()[1];
		String time = name.substring(0, name.length()-4);
		metrics.write(
				result.myMetrics.getNumRevisions() + ";" +			//trID
				time + ";" +								//time
				result.myMetrics.getVersionNames()[0] + ";" +		//oldVer
				result.myMetrics.getVersionNames()[1] + ";" +		//newVer
				result.myMetrics.getOldSizes()[0] + ";" +			//#oldT
				result.myMetrics.getNewSizes()[0] + ";" +			//#newT
				result.myMetrics.getOldSizes()[1] + ";" +			//#oldA
				result.myMetrics.getNewSizes()[1] + ";" +			//#newA
				result.myMetrics.getTableMetrics()[0] + ";" +		//tIns
				result.myMetrics.getTableMetrics()[1] + ";" +		//tDel
				result.myMetrics.getAttributeMetrics()[0] + ";" +	//aIns
				result.myMetrics.getAttributeMetrics()[1] + ";" +	//aDel
				result.myMetrics.getAttributeMetrics()[2] + ";" +	//aTypeAlt
				result.myMetrics.getAttributeMetrics()[3] + ";" +	//keyAlt
				result.myMetrics.getAttributeMetrics()[4] + ";" +	//aTabIns
				result.myMetrics.getAttributeMetrics()[5] + "\n"		//aTabDel
			);
		}
		metrics.close();
	}
	public  void initMetrics(String path) throws IOException {

		String filePath = getDirectory(path) + File.separator + "metrics.csv";
		BufferedWriter metrics = new BufferedWriter(new FileWriter(filePath));
		metrics.write("trID;time;oldVer;newVer;#oldT;#newT;#oldA;#newA"
				+ ";tIns;tDel;aIns;aDel;aTypeAlt;keyAlt;aTabIns;aTabDel\n");
		metrics.close();
	}
	
}
