package demo;

import java.io.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class CreateOntology {

	private static String nameSpace = "http://keg.cs.tsinghua.edu.cn/";
	private static OntModel model = null;
	private static String ontPath = "etc/ontology-demo.owl";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

		OntClass superClass = model.createClass(nameSpace + "人物");
		OntClass subClass = model.createClass(nameSpace + "教育人物");

		superClass.setSubClass(subClass);
		// subClass.setSuperClass(superClass);

		Individual individual = subClass.createIndividual(nameSpace + "陈吉宁");
		individual.addLabel(model.createLiteral("陈吉宁"));

		ObjectProperty presidentOf = model.createObjectProperty(nameSpace
				+ "presidentOf");
		individual.addProperty(presidentOf, model.createClass(nameSpace + "学校")
				.createIndividual(nameSpace + "清华大学"));

		DatatypeProperty hasBirthDay = model.createDatatypeProperty(nameSpace
				+ "hasBirthday");
		individual.addProperty(hasBirthDay, "1964年2月");

		BufferedWriter out = new BufferedWriter(new FileWriter(ontPath));
		model.write(out, "RDF/XML");
		// model.write(out, "N-TRIPLE");
		out.close();
	}

}
