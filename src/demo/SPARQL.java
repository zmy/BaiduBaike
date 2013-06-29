package demo;

import java.io.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SPARQL {
	private static OntModel model = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		simpleQuery("陈吉宁");
	}

	private static void simpleQuery(String label) throws IOException {
		long startTime = System.currentTimeMillis();

		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		model.read(new BufferedInputStream(new FileInputStream(
				"etc/ontology-demo.owl")), "RDF/XML");

		String query = "PREFIX j.0: <http://keg.cs.tsinghua.edu.cn/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?subject ?property ?value " + "WHERE " + "{"
				+ "?subject rdfs:label \"" + label + "\" . "
				+ "?subject ?property ?value " + "}";
		Query q = QueryFactory.create(query);

		QueryExecution qexec = QueryExecutionFactory.create(q, model);
		ResultSet results = qexec.execSelect();

		ResultSetFormatter.out(System.out, results);
		qexec.close();

		long endTime = System.currentTimeMillis();
		System.out.println("Time: " + (double) (endTime - startTime) / 1000
				+ "s.");
	}

}
