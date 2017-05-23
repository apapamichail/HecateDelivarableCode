package gr.uoi.cs.daintiness.hecate.transitions;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="transition")
public class ChangesList {
	
	@XmlAttribute(name="oldVersion")
	private String oldVersion;
	@XmlAttribute(name="newVersion")
	private String newVersion;
	@XmlAnyElement(lax=true)
	private ArrayList<Change> list;
	
	public ChangesList() {
		this.oldVersion = "not set";
		this.newVersion = "not set";
		list = new ArrayList<Change>();
	}
	
	public ChangesList(String oldVersion, String newVersion) {
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		list = new ArrayList<Change>();
	}
	
	public void add(Change in) {
		this.list.add(in);
	}
	
	public Change get(int index) {
		return list.get(index);
	}

	/**
	 * @return the oldVersion
	 */
	public String getOldVersion() {
		return oldVersion;
	}

	/**
	 * @return the newVersion
	 */
	public String getNewVersion() {
		return newVersion;
	}
	
	public void setVersionNames(String oldVersion, String newVersion) {
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
	}
}
