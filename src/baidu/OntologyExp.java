/**
 * 
 */
package baidu;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.URIref;


/**
 * @author moony
 *
 */
public class OntologyExp {

	private static String nameSpace = "http://baike.baidu.com/"; //search?word=
	private static OntModel model = null;
	private static String ontPath = "etc/ontology-baidu.owl";

	private static int maxArticle = 500000;
	static List<String> origTax;

	static String genFull(String title) throws UnsupportedEncodingException {
		return URIref.encode(nameSpace + title);
		//TODO: what's the right way to read and write owl file?
	}

	static String genPart(String title) {
		return nameSpace + title;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

		System.out.println("Reading...");

		BufferedReader reader = new BufferedReader(new FileReader("etc/baidu-taxonomy.dat"));
		readTaxonomy(reader, model);

		reader = new BufferedReader(new FileReader("etc/baidu-article.dat"));
		readArticle(reader, model, true);
		reader = new BufferedReader(new FileReader("etc/baidu-article.dat"));
		readArticle(reader, model, false);

		System.out.println("Finished Reading");

		//BufferedWriter writer = new BufferedWriter(new FileWriter(ontPath));
		//model.write(writer, "RDF/XML");
		//model.write(writer, "N-TRIPLE");
		//model.write(writer, "TURTLE");
		//writer.close();

		NavigateOntology();
		SPARQL();
	}

	static void NavigateOntology() throws IOException {
		System.out.println("There are " + model.listObjectProperties().toList().size() + "ObjectProperty.");
		System.out.println("There are " + model.listDatatypeProperties().toList().size() + "DataTypeProperty.");
		
		int cnt=0;
		/*
		HashMap<String, Integer> objCnt = new HashMap<String, Integer>();
		Iterator<ObjectProperty> objectProperties = model.listObjectProperties();
		while (objectProperties.hasNext()) {
			ObjectProperty objectProperty = objectProperties.next();
			System.out.println("[OBJC"+(++cnt)+"]"+objectProperty.getURI());
			ResIterator iter = model.listResourcesWithProperty(objectProperty);
			Iterator<? extends OntClass> iter = objectProperty.listDeclaringClasses();
			while (iter.hasNext()) {
				String clazz = iter.next().getURI();
				Integer val = objCnt.get(clazz);
				if (val == null) val = 0;
				objCnt.put(clazz, val+1);
			}
		}
		System.out.println("Object Properties Count Finished.");

		cnt=0;
		HashMap<String, Integer> dataCnt = new HashMap<String, Integer>();
		Iterator<DatatypeProperty> datatypeProperties = model
				.listDatatypeProperties();
		while (datatypeProperties.hasNext()) {
			DatatypeProperty datatypeProperty = datatypeProperties.next();
			System.out.println("[DATA"+(++cnt)+"]"+datatypeProperty.getURI());
			Iterator<? extends OntClass> iter = datatypeProperty.listDeclaringClasses();
			while (iter.hasNext()) {
				String clazz = iter.next().getURI();
				Integer val = dataCnt.get(clazz);
				if (val == null) val = 0;
				dataCnt.put(clazz, val+1);
			}
		}
		System.out.println("Data Properties Count Finished.");
		*/
		
		System.out.println("There are "+model.listClasses().toList().size()+" classes.");
		//int cnt=0;
		BufferedWriter writer = new BufferedWriter(new FileWriter("statics.log"));
		//Iterator<OntClass> ontClasses = model.listClasses();
		//while (ontClasses.hasNext()) {
		for (String clazz: origTax) {
			OntClass ontClass = model.getOntClass(clazz);//ontClasses.next();
			writer.write(ontClass.getURI());
			writer.newLine();
			//System.out.println("["+(++cnt)+"]"+ontClass.getURI()/*URLDecoder.decode(ontClass.getURI(), "UTF-8")*/);
			//int cnt=0;
			//Iterator<? extends OntClass> iter1 = ontClass.listSubClasses();
			//while (iter1.hasNext()) {iter1.next(); cnt++;}
			writer.write("\thas "+ontClass.listSubClasses().toList().size()+" Subclasses.");
			writer.newLine();
			//System.out.println("\thas "+cnt+" Subclasses.");
			//cnt=0;
			//Iterator<? extends OntResource> iter2 = ontClass.listInstances();
			//while (iter2.hasNext()) {iter2.next(); cnt++;}
			writer.write("\thas "+ontClass.listInstances().toList().size()+" Instances.");
			writer.newLine();
			//System.out.println("\thas "+cnt+" Instances.");
			//Integer val = objCnt.get(ontClass.getURI());
			//writer.write("\thas "+(val==null?0:val)+" ObjectProperties.");
			int dp=0, op=0;
			Iterator<OntProperty> iter = ontClass.listDeclaredProperties(true);
			while (iter.hasNext()) {
				OntProperty p = iter.next();
				if (p.isDatatypeProperty()) {
					dp++;
					//System.out.println("\t[D]"+p.getURI());
				}
				if (p.isObjectProperty()){
					op++;
					//System.out.println("\t[O]"+p.getURI());
				}
			}
			writer.write("\thas "+ op +" ObjectProperties.");
			writer.newLine();
			//System.out.println("\thas "+(val==null?0:val)+" ObjectProperties.");
			//val = dataCnt.get(ontClass.getURI());
			//writer.write("\thas "+(val==null?0:val)+" DataProperties.");
			writer.write("\thas "+ dp +" DataTypeProperties.");
			writer.newLine();
			//System.out.println("\thas "+(val==null?0:val)+" DataProperties.");
		}
		writer.close();
	}

	static void SPARQL() throws IOException {
		System.out.println("Start Query...");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("queries.log"));
		query("PREFIX j.0: <http://baike.baidu.com/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?subject ?property ?value" + " WHERE " + "{"
				+ "?subject rdfs:label \"大帅\" . "
				+ "?subject ?property ?value " + "}"
				+ "ORDER BY ?property", out);
		query("PREFIX j.0: <http://baike.baidu.com/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?subject" + " WHERE " + "{"
				+ "?subject rdfs:seeAlso ?value ."
				+ "?subject ?property ?value .}"
				+ "LIMIT 50", out);
		query("PREFIX j.0: <http://baike.baidu.com/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?subject ?value" + " WHERE " + "{"
				+ "?subject j.0:页数 ?value . FILTER regex(?value, \"^[0-9]+$\")"
				+ "}"
				+ "LIMIT 50", out);
		out.close();
		//TODO: IB data is dirty
	}

	private static void query(String query, BufferedOutputStream out) throws IOException {
		long startTime = System.currentTimeMillis();

		Query q = QueryFactory.create(query);

		QueryExecution qexec = QueryExecutionFactory.create(q, model);
		ResultSet results = qexec.execSelect();

		ResultSetFormatter.out(out, results);
		qexec.close();

		long endTime = System.currentTimeMillis();
		String spent = "Time: " + (double) (endTime - startTime) / 1000 + "s.";
		out.write(spent.getBytes());
	}

	static void readTaxonomy(BufferedReader reader, OntModel model) throws IOException {
		origTax = new ArrayList<String>();
		String line;
		while ((line=reader.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length == 2) {
				String superClass = elements[0];
				if (superClass.equals("Root")) {
					String[] classes = elements[1].split(";");
					for (String clazz: classes) {
						model.createClass(genPart(clazz));
						origTax.add(genPart(clazz));
					}
				} else {
					String[] subClasses = elements[1].split(";");
					OntClass superOntClass = model.createClass(genPart(elements[0]));
					for (String clazz: subClasses) {
						superOntClass.addSubClass(model.createClass(genPart(clazz)));
						origTax.add(genPart(clazz));
					}
				}
			}
		}
		reader.close();
	}

	static void readArticle(BufferedReader reader, OntModel model, boolean firstScan) throws IOException {
		OntClass others = model.createClass(genPart("Others"));
		OntClass notaxo = model.createClass(genPart("NotInTaxonomy"));
		DatatypeProperty hasID = model.createDatatypeProperty(genPart("hasID"));
		DatatypeProperty hasAbstract = model.createDatatypeProperty(genPart("hasAbstract"));
		ObjectProperty isRelatedTo = model.createObjectProperty(genPart("isRelatedTo"));
		int cnt = 0;
		reader.readLine();
		String line;
		do {
			if (cnt++ > maxArticle) break;
			//System.out.println(cnt);
			String id = null;
			String title = null;
			String[] related = {};
			String abstr = null;
			String[] links = {};
			String[] categories = {};
			String[] synonyms = {};
			HashMap<String, String> infobox = new HashMap<String, String>();

			while ((line=reader.readLine())!=null && !line.isEmpty()) {
				int split = line.indexOf(':');
				String flag = line.substring(0, split);
				String content = line.substring(split+1, line.length());
				if (flag.equals("I")) {
					//id
					id = content;
					//System.out.println("ID: "+id);
				} else if (flag.equals("T")) {
					//title
					title = content;
				} else if (flag.equals("R")) {
					//related entities // split by ::;
					related = content.split("::;");
				} else if (flag.equals("A")) {
					//abstract (first paragraph)
					abstr = content;
				} else if (flag.equals("L")) {
					//links // split by ::;
					links = content.split("::;");
				} else if (flag.equals("C")) {
					//categories
					categories = content.split("::;");
				} else if (flag.equals("IB")) {
					//infobox // each pair is split by ::; and attribute-value is split by ::=
					for (String pair: content.split("::;")) {
						String[] p = pair.split("::=");
						infobox.put(p[0], p[1]);
					}
				} else if (flag.equals("S")) {
					//synonym
					synonyms = content.split("::;");
				}
			}
			
			if (categories.length == 0) {
				cnt--;
			} else {
				if (firstScan) {
					Individual individual = null;
					//TODO: deal with things that are classes?
					for (String category: categories) {
						OntClass clazz = model.getOntClass(genPart(category));
						if (clazz == null) {
							clazz = model.createClass(genPart(category));
							clazz.addSuperClass(notaxo);
						}
						if (individual == null)
							individual = clazz.createIndividual(genPart(title));
						else
							individual.addOntClass(clazz);
						//TODO: belongs to a subclass and superclass at the same time
					}
					if (individual == null)
						individual = others.createIndividual(genPart(title));
					//System.out.println(title);
					individual.addLabel(model.createLiteral(title));
					if (id != null)
						individual.addProperty(hasID, model.createLiteral(id));
					if (abstr != null)
						individual.addProperty(hasAbstract, model.createLiteral(abstr));
				} else {
					Individual individual = model.getIndividual(genPart(title));
					for (String name: related) {
						OntResource nameInd = model.getOntResource(genPart(name));
						if (nameInd == null)
							nameInd = others.createIndividual(genPart(name));
						individual.addProperty(isRelatedTo, nameInd);
					} //TODO: bidirected?
					for (String link: links) {
						OntResource linkInd = model.getOntResource(genPart(link));
						if (linkInd == null)
							linkInd = others.createIndividual(genPart(link));
						individual.addSeeAlso(linkInd);
					} //TODO
					for (String synonym: synonyms) {
						OntResource synonymInd = model.getOntResource(genPart(synonym));
						if (synonymInd == null)
							synonymInd = others.createIndividual(genPart(synonym));
						individual.addSameAs(synonymInd);
					}
					for (Entry<String, String> entry: infobox.entrySet()) {
						//System.out.println(entry);
						OntResource object = model.getOntResource(genPart(entry.getValue()));
						if (object == null) { //TODO: same property can be both data and object type?
							DatatypeProperty property = model.createDatatypeProperty(genPart(entry.getKey()));
							// TODO: differentiate from normal properties?
							individual.addProperty(property, entry.getValue());
							// TODO: is it possible that some values are objects?
						} else {
							ObjectProperty property = model.createObjectProperty(genPart(entry.getKey()));
							individual.addProperty(property, object);
							//TODO: self-loop?
						}
					}
				}
			}
		} while (line!= null);
		reader.close();
	}

}
