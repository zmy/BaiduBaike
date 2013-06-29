package demo;

import java.io.*;
import java.util.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class NavigateOntology {
	private static OntModel model = null;
	private static String ontPath = "etc/ontology-demo.owl";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Read an ontology
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		model.read(new BufferedInputStream(new FileInputStream(ontPath)),
				"RDF/XML");

		System.out.println("------ List Classes ------");
		Iterator<OntClass> ontClasses = model.listClasses();
		while (ontClasses.hasNext()) {
			OntClass ontClass = ontClasses.next();
			System.out.println(ontClass.getURI());
		}

		System.out.println("\n------ List Individuals ------");
		Iterator<Individual> individuals = model.listIndividuals();
		while (individuals.hasNext()) {
			Individual individual = individuals.next();
			System.out.println(individual.getURI());
		}

		System.out.println("\n------ List ObjectProperties ------");
		Iterator<ObjectProperty> objectProperties = model
				.listObjectProperties();
		while (objectProperties.hasNext()) {
			ObjectProperty objectProperty = objectProperties.next();
			System.out.println(objectProperty.getURI());
		}

		System.out.println("\n------ List DatatypeProperties ------");
		Iterator<DatatypeProperty> datatypeProperties = model
				.listDatatypeProperties();
		while (datatypeProperties.hasNext()) {
			DatatypeProperty datatypeProperty = datatypeProperties.next();
			System.out.println(datatypeProperty.getURI());
		}

	}

}
