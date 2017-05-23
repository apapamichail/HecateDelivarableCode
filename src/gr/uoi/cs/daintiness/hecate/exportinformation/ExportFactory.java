package gr.uoi.cs.daintiness.hecate.exportinformation;

public class ExportFactory {
	public Export getExport(String type){
		if (type.equals("csv")){
			return new csvExport();
		}
		else if(type.equals("xml")){
			return new xmlExport();
		}
		else if (type.equals("metrics")){
			return new MetricsExport();
		}
		return null;
	}
}
