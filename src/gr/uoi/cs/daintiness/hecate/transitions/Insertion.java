/**
 * 
 */
package gr.uoi.cs.daintiness.hecate.transitions;

import gr.uoi.cs.daintiness.hecate.sql.Table;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author iskoulis
 *
 */
@XmlRootElement
public class Insertion extends Change {

	public Insertion() {
		super();
	}
	
	public void table(Table newTable) {
		super.table(newTable);
		this.type = "NewTable";
	}
}
