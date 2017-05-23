	package gr.uoi.cs.daintiness.hecate.exportinformation;

	import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import gr.uoi.cs.daintiness.hecate.differencedetection.SchemataDifferencesManager;
import gr.uoi.cs.daintiness.hecate.metrics.tables.Changes;
import gr.uoi.cs.daintiness.hecate.metrics.tables.MetricsOverVersion;
	
	public  class csvExport extends Export{

		public  void exportInformation(SchemataDifferencesManager manager) {
			//public  void exportInformation(String path, int versions, TablesInfo tableinfo) {
			String slashedPath = getDirectory(manager.path) + File.separator;
			String tables = slashedPath + "tables.csv";
			String tablesI = slashedPath + "table_ins.csv";
			String tablesD = slashedPath + "table_del.csv";
			String tablesT = slashedPath + "table_type.csv";
			String tablesK = slashedPath + "table_key.csv";
			String tablesAll = slashedPath + "all.csv";
			String tablesSt = slashedPath + "table_stats.csv";

			try {
				BufferedWriter tablesWriter = new BufferedWriter(new FileWriter(tables));
				BufferedWriter tablesWriterI = new BufferedWriter(new FileWriter(tablesI));
				BufferedWriter tablesWriterD = new BufferedWriter(new FileWriter(tablesD));
				BufferedWriter tablesWriterT = new BufferedWriter(new FileWriter(tablesT));
				BufferedWriter tablesWriterK = new BufferedWriter(new FileWriter(tablesK));
				BufferedWriter tablesWriterAll = new BufferedWriter(new FileWriter(tablesAll));
				BufferedWriter tablesWriterSt = new BufferedWriter(new FileWriter(tablesSt));

				writeVersionsLine(tablesWriter, manager.versions);
				writeVersionsLine(tablesWriterI, manager.versions);
				writeVersionsLine(tablesWriterD, manager.versions);
				writeVersionsLine(tablesWriterT, manager.versions);
				writeVersionsLine(tablesWriterK, manager.versions);
				writeVersionsLine(tablesWriterAll, manager.versions);
				tablesWriterSt.write("table;dur;birth;death;chngs;s@s;s@e;sAvg\n");

				for (String t : manager.tableinfo.getTables()){
					tablesWriter.write(t + ";");
					tablesWriterI.write(t + ";");
					tablesWriterD.write(t + ";");
					tablesWriterT.write(t + ";");
					tablesWriterK.write(t + ";");
					tablesWriterAll.write(t + ";");

					tablesWriterSt.write(t + ";");
					MetricsOverVersion mov = manager.tableinfo.getTableMetrics(t);
					tablesWriterSt.write(mov.getLife() + ";");
					tablesWriterSt.write(mov.getBirth() + ";");

					tablesWriterSt.write((mov.getDeath()==manager.versions-1 ? "-" : mov.getDeath())
							+ ";");

					tablesWriterSt.write(mov.getTotalChanges().getTotal() + ";");
					tablesWriterSt.write(mov.getBirthSize() + ";");
					tablesWriterSt.write(mov.getDeathSize() + ";");

					int sumSize = 0;
					int v = 0;
					for (int i = 0; i < manager.versions; i++) {
						if (mov != null && mov.containsKey(i)) {
							tablesWriter.write(mov.getSize(i) + ";");
							Changes c = mov.getChanges(i);
							tablesWriterI.write(c.getInsertions() + ";");
							tablesWriterD.write(c.getDeletions() + ";");
							tablesWriterT.write(c.getAttrTypeChange() + ";");
							tablesWriterK.write(c.getKeyChange() + ";");
							tablesWriterAll.write(mov.getSize(i) + "[" + c.toString() +
									"]" + ";");
							sumSize += mov.getSize(i);
							v++;

						} else {
							tablesWriter.write("0;");
							tablesWriterI.write("-;");
							tablesWriterD.write("-;");
							tablesWriterT.write("-;");
							tablesWriterK.write("-;");
							tablesWriterAll.write("0|-|-|-|-;");
						}
					}
					tablesWriterSt.write(Float.toString((sumSize/(float)v)));
					tablesWriterSt.write("\n");

					tablesWriter.write("\n");
					tablesWriterI.write("\n");
					tablesWriterD.write("\n");
					tablesWriterT.write("\n");
					tablesWriterK.write("\n");
					tablesWriterAll.write("\n");
				}
				tablesWriter.close();
				tablesWriterI.close();
				tablesWriterD.close();
				tablesWriterT.close();
				tablesWriterK.close();
				tablesWriterAll.close();
				tablesWriterSt.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Oups...",
						                      JOptionPane.ERROR_MESSAGE);
			}
		}
		private  void writeVersionsLine(BufferedWriter file, int versions)
				throws IOException {
			file.write(";");
			for (int i = 0; i < versions; i++) {
				file.write(i + ";");
			}
			file.write("\n");
		}
 	}


