/**
 * 
 */
package gr.uoi.cs.daintiness.hecate.gui.swing;

import java.io.File;
import java.io.IOException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import gr.uoi.cs.daintiness.hecate.differencedetection.AlgorithmFactory;
import gr.uoi.cs.daintiness.hecate.differencedetection.DifferencesAlgorithm;
import gr.uoi.cs.daintiness.hecate.differencedetection.DifferencesBetweenTwoSchemataData;
import gr.uoi.cs.daintiness.hecate.differencedetection.SchemataDifferencesManager;
import gr.uoi.cs.daintiness.hecate.parser.HecateParser;
import gr.uoi.cs.daintiness.hecate.sql.Schema;

/**
 * @author iskoulis
 *
 */
public class DifferencesWorker extends SwingWorker<DifferencesBetweenTwoSchemataData, Integer> {
	
	MainPanel mainpanel;
	ProgressMonitor progressmonitor;
	File oldFile = null;
	File newFile = null;
	File folder = null;
	Schema oldSchema;
	Schema newSchema;
	
	public DifferencesWorker(MainPanel mainpanel,
			          File oldFile, File newFile) {
		this.mainpanel = mainpanel;
		this.oldFile = oldFile;
		this.newFile = newFile;
	}
	
	public DifferencesWorker(MainPanel mainpanel, File folder) {
		this.mainpanel = mainpanel;
		this.folder = folder;
	}

	@Override
	protected DifferencesBetweenTwoSchemataData doInBackground() throws Exception {
		SchemataDifferencesManager differencesManager = new SchemataDifferencesManager();
		AlgorithmFactory factory = new AlgorithmFactory();
		DifferencesAlgorithm algo = factory.getAlgorithm("DifferencesAlgorithmSkoulis");
		progressmonitor = new ProgressMonitor(mainpanel.getRootPane(), "Working...", null, 0, 100);
		DifferencesBetweenTwoSchemataData result = new DifferencesBetweenTwoSchemataData();
		
		if (oldFile != null && newFile != null) {

			oldSchema = HecateParser.parse(oldFile.getAbsolutePath());
			newSchema = HecateParser.parse(newFile.getAbsolutePath());
			
			
			result = differencesManager.getDifferencesBetweenTwoRevisions(oldSchema, newSchema);
			
		} else if (folder != null){
			/* Here i want to export everything
			 * by checking all the schematas for differences
			 */
			checkTheDifferencesInHistory(differencesManager);
			
			oldSchema = HecateParser.parse(folder.getAbsolutePath() + File.separator + folder.list()[0]);
			newSchema = HecateParser.parse(folder.getAbsolutePath() + File.separator + folder.list()[folder.list().length-1]);
			result = differencesManager.getDifferencesBetweenTwoRevisions(oldSchema, newSchema);


			
		}



		return result;
	}

	/**
	 * @param differencesManager
	 * @return
	 * @throws IOException
	 */
	private void checkTheDifferencesInHistory(SchemataDifferencesManager differencesManager) throws IOException {

		differencesManager.checkDifferencesInSchemataHistoryAndExport(folder);
		

	}

	

	@Override
	protected void done() {
		mainpanel.drawSchema(oldSchema, "old");
		mainpanel.drawSchema(newSchema, "new");
		//progressmonitor.setProgress(progressmonitor.getMaximum());
		super.done();
	}
}
