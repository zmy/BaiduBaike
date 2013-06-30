import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

/*
 * Warning!: this class will overwrite the file "baidu-article.dat"
 */
public class CrossValidation extends Classifier {

	static final int SPLIT_SET = 10;
	ArrayList<Arcticle>[] splitSet;
	ArrayList<Arcticle> trainingSet, testSet;
	private static String ontPath = "etc/ontology.owl";
	private static String dataPath="etc/";
	
	public void createValidationSets() throws IOException {
		FileInputStream is=new FileInputStream(dataPath+"baidu-article.dat");
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);

		String line;
		Arcticle article=new Arcticle();
		article.index="start";
		int index=0;
		
		Random rand = new Random();

		while((line=br.readLine())!=null){
			if(line.startsWith("I:")){
				index++;
				article.index=line;
				if(index==1000000)
					break;
				if(!article.index.equals("start")){
					//put into one set;
					int vIdx = rand.nextInt(SPLIT_SET);
					splitSet[vIdx].add(article);
				}
				article.abstact="A:";
				article.catogory="C:";
				article.name="T:";
				article.index="I:";
				article.infobox="IB:";
				article.root=null;
				article.link="L:";
			}
			if(line.startsWith("T:")){
				article.name=line;
			}
			else if(line.startsWith("C:")){
				//keypoint
				article.catogory=line;
				String []classes=line.substring(2).split("::;");
				String root=null;
				for(String subclass:classes){
					root=GetRoot(subclass);
					if(root!=null)
						break;
				}
				if(root!=null)
					article.root=root;
				else
					article.root=null;
			}
			else if(line.startsWith("IB:")){
				article.infobox=line;
			}
			else if(line.startsWith("A:")){
				article.abstact=line;
			}
			else if(line.startsWith("L")){
				article.link=line;
			}
		}
		br.close();
	}
	
	/*
	 * This function overwrites baidu-article.dat
	 */
	public void createTrainingFile() throws IOException {
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"baidu-article.dat")));
		for(Arcticle a: trainingSet)
			bw.write(a.toString());
		bw.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			CrossValidation cv=new CrossValidation();
			cv.ReadTaxomony();
			cv.createValidationSets();
			for(int i=0; i<SPLIT_SET; i++) {
				cv.trainingSet = new ArrayList<Arcticle>();
				for(int j=0; j<SPLIT_SET; j++) if(j!=i)
					cv.trainingSet.addAll(cv.splitSet[j]);
				cv.testSet=cv.splitSet[i];
				cv.createTrainingFile();
				/* */
				cv.SplitFiles();
				cv.getWordList();
				cv.getInfoboxProp();
				cv.ClassifyWithInfobox();
				//TODO: calc precision, recall, F1 scores
				cv.ClassifyWithLink();
				//TODO: calc precision, recall, F1 scores
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
