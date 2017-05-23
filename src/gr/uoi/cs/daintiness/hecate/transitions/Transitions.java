/**
 * 
 */
package gr.uoi.cs.daintiness.hecate.transitions;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author iskoulis
 *
 */
@XmlRootElement(name="transitions")
public class Transitions {
	
	@XmlElement(name="transition")
	private Collection<ChangesList> list;
	
	public Transitions() {
		list = new ArrayList<ChangesList>();
	}
	
	public void add(ChangesList in) {
		this.list.add(in);
	}
	
	Collection<ChangesList> getList() {
		return list;
	}
}
