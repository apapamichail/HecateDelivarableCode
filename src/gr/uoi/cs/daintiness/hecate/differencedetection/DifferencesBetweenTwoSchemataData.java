/**
 * 
 */
package gr.uoi.cs.daintiness.hecate.differencedetection;

import gr.uoi.cs.daintiness.hecate.metrics.Metrics;
import gr.uoi.cs.daintiness.hecate.metrics.tables.TablesInfo;
import gr.uoi.cs.daintiness.hecate.transitions.ChangesList;

/**
 * @author iskoulis
 *
	

 */
public class DifferencesBetweenTwoSchemataData {

	final public ChangesList myTransformationList;
	final public Metrics myMetrics;
	final public TablesInfo tablesInfo;
	/**
	 * 
	 */
	public DifferencesBetweenTwoSchemataData() {
		this.myTransformationList = new ChangesList();
		this.myMetrics = new Metrics();
		this.tablesInfo = new TablesInfo();
	}
	
	public void setVersionNames(String oldVersion, String newVersion) {
		this.myTransformationList.setVersionNames(oldVersion, newVersion);
		this.myMetrics.setVersionNames(oldVersion, newVersion);
	}
	
	public void clear() {
		this.tablesInfo.clear();
		myMetrics.resetRevisions();
	}
}
