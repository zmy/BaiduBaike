import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.wltea.analyzer.lucene.IKAnalyzer;  


public class Classifier {
	
	private static String ontPath = "etc/ontology.owl";
	private static String dataPath="etc/";
	Map<String,String> catogory;
	
	public void ReadTaxomony() throws IOException{
		FileInputStream is=new FileInputStream(dataPath+"baidu-taxonomy.dat");
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);
		String line;
		while((line=br.readLine())!=null){
			String segs[]=line.split(" ");
			String root=segs[0];
			String parts[]=root.split(";");
			for(String child:parts){
				catogory.put(child, root);
			}
		}
	}
	
	public String GetRoot(String node){
		while(catogory.containsKey(node)&&!catogory.get(node).equals("Root")){
			node=catogory.get(node);
		}
		if(catogory.containsKey(node)&&catogory.get(node).equals("Root")){
			return node;
		}
		else
			return null;
	}
	
	public void SplitFiles()throws IOException{
		FileInputStream is=new FileInputStream(dataPath+"baidu-article.dat");
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);
		String line;
		Arcticle article=new Arcticle();
		article.index="start";
		while((line=br.readLine())!=null){
			if(line.startsWith("I:")){
				if(article.index.equals("start")){
					//write to file;
				}
				article.index=line;
			}
			if(line.startsWith("T:")){
				
			}
			else if(line.startsWith("C:")){
				//keypoint
			}
			else if(line.startsWith("IB:")){
				
			}
		}
		
	}
}
