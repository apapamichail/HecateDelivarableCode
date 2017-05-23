package gr.uoi.cs.daintiness.hecate.differencedetection;

import java.util.Iterator;

import gr.uoi.cs.daintiness.hecate.metrics.tables.ChangeType;
import gr.uoi.cs.daintiness.hecate.sql.Attribute;
import gr.uoi.cs.daintiness.hecate.sql.Schema;
import gr.uoi.cs.daintiness.hecate.sql.SqlItem;
import gr.uoi.cs.daintiness.hecate.sql.Table;
import gr.uoi.cs.daintiness.hecate.transitions.*;


/**
 * This class is responsible for performing the diff algorithm between two SQL
 * schemas. It then stores some metrics about the performed diff.
 * 
 * @author giskou
 *
 */
public class DifferencesAlgorithmSkoulis implements DifferencesAlgorithm {

	/**
	 * This function performs the main diff algorithm for
	 * finding the differences between the schemas that are 
	 * given as parameters. The algorithm is a modification of
	 * the SortMergeJoin algorithm found at DBMS's for joining
	 * two tables. The tables and attributes are stored on TreeMaps
	 * thus sorted according their name. Starting from the top of
	 * each Map we check the items for matches. If the original is
	 * larger lexicographically then the item of the modified Map does
	 * not exist in the original and so it's inserted and we move to
	 * the next item on the modified Map. Likewise, if the modified
	 * is larger lexicographically then the item on the original has been
	 * deleted and we move to the next item on the original Map. If a
	 * Map reaches at an end then the remaining items on the other Map
	 * are marked as inserted or deleted accordingly.
	 * @param schemaA
	 *   The original schema
	 * @param schemaB
	 *   The modified version of the original schema
	 */
	
	private String oldTableKey = null, newTableKey = null;
	private final String insertAction = "insert";
	private final String deleteAction = "delete";
	private Table oldTable;
	private Table newTable;
	public Insertion in;
	public Deletion out;
	public Update up;

	public Iterator<String> oldAttributeKeys;
	public Iterator<Attribute> oldAttributeValues;

	public Iterator<String> newAttributeKeys;
	public Iterator<Attribute> newAttributeValues;

	public Iterator<String> oldTableKeys;
	public Iterator<Table> oldTableValues;
	public Iterator<String> newTableKeys;
	public Iterator<Table> newTableValues;
	public DifferencesBetweenTwoSchemataData results;

	@Override
	public DifferencesBetweenTwoSchemataData getDifferencesBetweenTwoSchemata(Schema schemaA, Schema schemaB) {

		setUp(schemaA, schemaB);

		if (oldTableKeys.hasNext() && newTableKeys.hasNext()) {
			moveTableKeyIterators();
			moveTableValueIterators();
			while (true) {
				in = null;
				out = null;
				up = null;
				if (oldTableKey.compareTo(newTableKey) == 0) { // ** Matched
																// tables
					match(oldTable, newTable);

					findSameTablesDifferences(oldTable, newTable);

					if (oldTableKeys.hasNext() && newTableKeys.hasNext()) { // move
																			// both
																			// tables
						moveTableKeyIterators();
						moveTableValueIterators();
						continue;
					} else { // one list is empty
						break;
					}
				} else if (oldTableKey.compareTo(newTableKey) < 0) { // ** Table
																		// Deleted
					deleteTable(oldTable);
					if (oldTableKeys.hasNext()) { // move old only
						oldTableKey = oldTableKeys.next();
						oldTable = (Table) oldTableValues.next();
						continue;
					} else {
						insertTable(newTable);
						break;
					}
				} else { // ** Table Inserted
					insertTable(newTable);
					if (newTableKeys.hasNext()) { // move new only
						newTableKey = newTableKeys.next();
						newTable = (Table) newTableValues.next();
						continue;
					} else {
						deleteTable(oldTable);
						break;
					}
				}
			}
		}

		checkRemainingTableKeys(oldTableKeys, oldTableValues, "delete");
		checkRemainingTableKeys(newTableKeys, newTableValues, "insert");

		return results;
	}

	private void moveTableValueIterators() {
		oldTable = (Table) oldTableValues.next();
		newTable = (Table) newTableValues.next();
	}

	private void moveTableKeyIterators() {
		oldTableKey = oldTableKeys.next();
		newTableKey = newTableKeys.next();
	}

	/**
	 * @param oldTable
	 * @param newTable
	 */
	public void findSameTablesDifferences(Table oldTable, Table newTable) {
		initializeAttributesKeys(oldTable, newTable);

		initializeAttributesValues(oldTable, newTable);

		computeAttributesDifferences(oldTable, newTable);
		// check remaining attributes
		while (oldAttributeKeys.hasNext()) { // delete remaining old (not found
												// in new)

			updateAttributesInList(newTable, oldAttributeKeys, oldAttributeValues, deleteAction);
		}
		while (newAttributeKeys.hasNext()) { // insert remaining new (not found
												// in old)
			// updateAttributesInList(oldTable);
			updateAttributesInList(oldTable, newAttributeKeys, newAttributeValues, insertAction);

		}
		// ** Done with attributes **
		if (newTable.getMode() == SqlItem.UPDATED) {
			alterTable(newTable);
		}
	}

	/**
	 * @param oldTable
	 * @param newTable
	 */
	private void computeAttributesDifferences(Table oldTable, Table newTable) {
		String oldAttrKey;
		String newAttrKey;
		if (oldAttributeKeys.hasNext() && newAttributeKeys.hasNext()) {
			oldAttrKey = oldAttributeKeys.next();
			Attribute oldAttr = oldAttributeValues.next();
			newAttrKey = newAttributeKeys.next();
			Attribute newAttr = newAttributeValues.next();
			while (true) {

				if (oldAttrKey.compareTo(newAttrKey) == 0) { // possible
																// attribute
																// match
					if (oldAttr.getType().compareTo(newAttr.getType()) == 0) { // check
																				// attribute
																				// type
						matchedAttributes(oldAttr, newAttr);
					} else { // attribute type changed
						attributeTypeChange(oldAttr, newAttr);
					}
					// move both attributes
					if (oldAttributeKeys.hasNext() && newAttributeKeys.hasNext()) {
						oldAttrKey = oldAttributeKeys.next();
						oldAttr = oldAttributeValues.next();
						newAttrKey = newAttributeKeys.next();
						newAttr = newAttributeValues.next();
						continue;
					} else { // one of the lists is empty, must process the rest
								// of the other
						break;
					}
				} else if (oldAttrKey.compareTo(newAttrKey) < 0) { // ** Deleted
																	// attributes
					attributeDelete(newTable, oldAttr);
					// move old only attributes
					if (oldAttributeKeys.hasNext()) {
						oldAttrKey = oldAttributeKeys.next();
						oldAttr = oldAttributeValues.next();
						continue;
					} else { // no more old
						attributeInsert(oldTable, newAttr);
						break;
					}
				} else { // ** Inserted attributes
					attributeInsert(oldTable, newAttr);
					// move new only
					if (newAttributeKeys.hasNext()) {
						newAttrKey = newAttributeKeys.next();
						newAttr = newAttributeValues.next();
						continue;
					} else { // no more new
						attributeDelete(newTable, oldAttr);
						break;
					}
				}

			}

		}
	}

	private void matchedAttributes(Attribute oldAttr, Attribute newAttr) {
		if (oldAttr.isKey() == newAttr.isKey()) { // ** Matched
													// attributes
			match(oldAttr, newAttr);
		} else { // * attribute key changed
			attributeKeyChange(oldAttr, newAttr);
		}
	}

	public void setUp(Schema schemaA, Schema schemaB) {
		results = new DifferencesBetweenTwoSchemataData();

		results.myMetrics.newRevision();
		results.setVersionNames(schemaA.getName(), schemaB.getName());
		oldTableKeys = schemaA.getTables().keySet().iterator();
		oldTableValues = schemaA.getTables().values().iterator();
		newTableKeys = schemaB.getTables().keySet().iterator();
		newTableValues = schemaB.getTables().values().iterator();
		setOriginalSizes(schemaA.getSize(), schemaB.getSize());

	}

	public void checkRemainingTableKeys(Iterator<String> tableKeys, Iterator<Table> tableValues, String action) {
		String tableKey;
		while (tableKeys.hasNext()) {
			tableKey = (String) tableKeys.next();
			Table table = (Table) tableValues.next();
			if (action.equals(insertAction)) {
				insertTable(table);
			} else if (action.equals(deleteAction)) {
				deleteTable(table);
			}
		}
	}

	public void updateAttributesInList(Table table, Iterator<String> attributeKeys, Iterator<Attribute> attributeValues,
			String action) {
		String attrKey;
		attrKey = (String) attributeKeys.next();
		Attribute attr = attributeValues.next();
		if (action.equals("insert"))
			attributeInsert(table, attr);
		else if (action.equals("delete"))
			attributeDelete(table, attr);
	}

	public void initializeAttributesValues(Table oldTable, Table newTable) {
		oldAttributeValues = oldTable.getAttrs().values().iterator();
		newAttributeValues = newTable.getAttrs().values().iterator();
	}

	public void initializeAttributesKeys(Table oldTable, Table newTable) {
		oldAttributeKeys = oldTable.getAttrs().keySet().iterator();
		newAttributeKeys = newTable.getAttrs().keySet().iterator();
	}

	public void attributeInsert(Table oldTable, Attribute newAttr) {
		results.myMetrics.insertAttr();
		insertItemInList(newAttr);
		newAttr.setMode(SqlItem.INSERTED);
		oldTable.setMode(SqlItem.UPDATED);
		newAttr.getTable().setMode(SqlItem.UPDATED);
		results.tablesInfo.addChange(oldTable.getName(), results.myMetrics.getNumRevisions(), ChangeType.Insertion);
	}

	public void attributeDelete(Table newTable, Attribute oldAttr) {
		results.myMetrics.deleteAttr();
		deleteItem(oldAttr);
		oldAttr.setMode(SqlItem.DELETED);
		oldAttr.getTable().setMode(SqlItem.UPDATED);
		newTable.setMode(SqlItem.UPDATED);
		results.tablesInfo.addChange(newTable.getName(), results.myMetrics.getNumRevisions(), ChangeType.Deletion);
	}

	public void attributeTypeChange(Attribute oldAttr, Attribute newAttr) {
		results.myMetrics.alterAttr();
		updateAttribute(newAttr, "TypeChange");
		oldAttr.getTable().setMode(SqlItem.UPDATED);
		newAttr.getTable().setMode(SqlItem.UPDATED);
		oldAttr.setMode(SqlItem.UPDATED);
		newAttr.setMode(SqlItem.UPDATED);
		results.tablesInfo.addChange(newAttr.getTable().getName(), results.myMetrics.getNumRevisions(),
				ChangeType.AttrTypeChange);
	}

	public void attributeKeyChange(Attribute oldAttr, Attribute newAttr) {
		results.myMetrics.alterKey();
		updateAttribute(newAttr, "KeyChange");
		oldAttr.getTable().setMode(SqlItem.UPDATED);
		newAttr.getTable().setMode(SqlItem.UPDATED);
		oldAttr.setMode(SqlItem.UPDATED);
		newAttr.setMode(SqlItem.UPDATED);
		results.tablesInfo.addChange(newAttr.getTable().getName(), results.myMetrics.getNumRevisions(),
				ChangeType.KeyChange);
	}

	public void deleteTable(Table t) {
		deleteItem(t);
		results.myMetrics.deleteTable();
		markAll(t, SqlItem.DELETED); // mark attributes deleted
	}

	public void insertTable(Table t) {
		insertItemInList(t);
		results.myMetrics.insetTable();
		markAll(t, SqlItem.INSERTED); // mark attributes inserted
	}

	public void alterTable(Table t) {
		results.myMetrics.alterTable();
	}

	public void match(SqlItem oldI, SqlItem newI) {
		oldI.setMode(SqlItem.MACHED);
		newI.setMode(SqlItem.MACHED);
	}

	public void markAll(Table t, int mode) {
		t.setMode(mode);
		for (Iterator<Attribute> i = t.getAttrs().values().iterator(); i.hasNext();) {
			i.next().setMode(mode);
			switch (mode) {
			case SqlItem.INSERTED:
				results.myMetrics.insertTabAttr();
				break;
			case SqlItem.DELETED:
				results.myMetrics.deleteTabAttr();
				break;
			default:
				;
			}
		}
	}

	public void insertItemInList(SqlItem item) {
		if (item.getClass() == Attribute.class) {
			if (in == null) {
				in = new Insertion();
				results.myTransformationList.add(in);
			}
			try {
				in.attribute((Attribute) item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (item.getClass() == Table.class) {
			in = new Insertion();
			results.myTransformationList.add(in);
			in.table((Table) item);
		}
	}

	public void deleteItem(SqlItem item) {
		if (item.getClass() == Attribute.class) {
			if (out == null) {
				out = new Deletion();
				results.myTransformationList.add(out);
			}
			try {
				out.attribute((Attribute) item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (item.getClass() == Table.class) {
			out = new Deletion();
			results.myTransformationList.add(out);
			out.table((Table) item);
		}
	}

	public void updateAttribute(Attribute item, String type) {
		if (up == null) {
			up = new Update();
			results.myTransformationList.add(up);
		}
		try {
			up.updateAttribute((Attribute) item, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOriginalSizes(int[] sizeA, int[] sizeB) {
		results.myMetrics.setOrigTables(sizeA[0]);
		results.myMetrics.setOrigAttrs(sizeA[1]);
		results.myMetrics.setNewTables(sizeB[0]);
		results.myMetrics.setNewAttrs(sizeB[1]);
	}
}
