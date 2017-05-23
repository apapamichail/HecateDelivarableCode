package gr.uoi.cs.daintiness.hecate.differencedetection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import gr.uoi.cs.daintiness.hecate.exportinformation.Export;
import gr.uoi.cs.daintiness.hecate.exportinformation.ExportFactory;
import gr.uoi.cs.daintiness.hecate.metrics.tables.TablesInfo;
import gr.uoi.cs.daintiness.hecate.parser.HecateParser;
import gr.uoi.cs.daintiness.hecate.sql.Schema;
import gr.uoi.cs.daintiness.hecate.sql.Table;
import gr.uoi.cs.daintiness.hecate.transitions.Transitions;

public class SchemataDifferencesManager {

	public String path;
	public Transitions transitions;
	public int versions;
	public TablesInfo tableinfo;
	public DifferencesBetweenTwoSchemataData result;
	public ArrayList<DifferencesBetweenTwoSchemataData> resultList;

	public DifferencesBetweenTwoSchemataData getDifferencesBetweenTwoRevisions(Schema oldSchema, Schema newSchema) {
		AlgorithmFactory algorithmFactory = new AlgorithmFactory();

		DifferencesAlgorithm differencesAlgorithm = algorithmFactory.getAlgorithm("DifferencesAlgorithmSkoulis");

		DifferencesBetweenTwoSchemataData result = new DifferencesBetweenTwoSchemataData();
		result.clear();

		result = differencesAlgorithm.getDifferencesBetweenTwoSchemata(oldSchema, newSchema);

		return result;
	}

	/**
	 * @param result
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public DifferencesBetweenTwoSchemataData checkDifferencesInSchemataHistoryAndExport(File folder) throws IOException {
		result = new DifferencesBetweenTwoSchemataData();
		AlgorithmFactory algorithmFactory = new AlgorithmFactory();
		transitions = new Transitions();
		DifferencesAlgorithm differencesAlgorithm = algorithmFactory.getAlgorithm("DifferencesAlgorithmSkoulis");

		// MetricsExport metricsExport = new MetricsExport();
		// MetricsExport metricsExport2 = new MetricsExport();
		// csvExport exportToCSV = new csvExport();
		// xmlExport exportToXML = new xmlExport();

		ExportFactory exportFactory = new ExportFactory();
		Export csv = exportFactory.getExport("csv");
		Export xml = exportFactory.getExport("xml");
		Export metrics = exportFactory.getExport("metrics");
		String[] folders = folder.list();

		path = folder.getAbsolutePath();
		java.util.Arrays.sort(folders);
		resultList = new ArrayList<DifferencesBetweenTwoSchemataData>();

		// metricsExport.initMetrics(path);

		result.clear();

		for (int i = 0; i < folders.length - 1; i++) {
			// result.clear();
			System.out.println(path + File.separator + folders[i]);
			Schema schemaA = getSchema(path + File.separator + folders[i]);

			for (Entry<String, Table> e : schemaA.getTables().entrySet()) {

				String tablename = e.getKey();
				int attributes = e.getValue().getSize();
				result.tablesInfo.addTable(tablename, i, attributes);
			}

			Schema schemaB = getSchema(path + File.separator + folders[i + 1]);
			if (i == folders.length - 2) {
				for (Entry<String, Table> e : schemaB.getTables().entrySet()) {
					String tablename = e.getKey();
					int attributes = e.getValue().getSize();
					result.tablesInfo.addTable(tablename, i + 1, attributes);
				}
			}

			result = differencesAlgorithm.getDifferencesBetweenTwoSchemata(schemaA, schemaB);

			transitions.add(result.myTransformationList);
			resultList.add(result);
			// metricsExport.metrics(result, path);
			tableinfo = result.tablesInfo;
			versions = result.myMetrics.getNumRevisions() + 1;
		}
		doExport(csv, xml, metrics);

		Schema oldSchema = HecateParser.parse(path + File.separator + folders[0]);
		Schema newSchema = HecateParser.parse(path + File.separator + folders[folders.length - 1]);
		result.clear();
		result = differencesAlgorithm.getDifferencesBetweenTwoSchemata(oldSchema, newSchema);
		// for(int i=0; i < resultList.size(); i++){
		// metricsExport.metrics(resultList.get(i), path);
		// }
		// metrics.exportInformation(resultList, path);
		return result;
	}

	/**
	 * @param csv
	 * @param xml
	 * @param metrics
	 * @throws IOException
	 */
	private void doExport(Export csv, Export xml, Export metrics) throws IOException {
		try {
			csv.exportInformation(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// xml.exportInformation(transitions, path);
		xml.exportInformation(this);
		metrics.exportInformation(this);
	}

	public Schema getSchema(String path) {
		return HecateParser.parse(path);
	}
}
