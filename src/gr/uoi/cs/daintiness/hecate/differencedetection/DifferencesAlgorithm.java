package gr.uoi.cs.daintiness.hecate.differencedetection;

import gr.uoi.cs.daintiness.hecate.sql.Schema;

public interface DifferencesAlgorithm {

	

	DifferencesBetweenTwoSchemataData getDifferencesBetweenTwoSchemata(Schema schemaA, Schema schemaB);


}