import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.wltea.analyzer.lucene.IKAnalyzer;  


public class Classifier {
	
	private static String ontPath = "etc/ontology.owl";
	private static String dataPath="etc/";
	Map<String,String> catogory=new HashMap<String,String>();
	
	public void ReadTaxomony() throws IOException{
		FileInputStream is=new FileInputStream(dataPath+"baidu-taxonomy.dat");
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);
		String line;
		while((line=br.readLine())!=null){
			String segs[]=line.split("\t");
			String root=segs[0];
			String parts[]=segs[1].split(";");
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
		
		//
		BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"艺术.txt")));
		BufferedWriter bw2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"技术.txt")));
		BufferedWriter bw3=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"文化.txt")));
		BufferedWriter bw4=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"生活.txt")));
		BufferedWriter bw5=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"地理.txt")));
		BufferedWriter bw6=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"社会.txt")));
		BufferedWriter bw7=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"人物.txt")));
		BufferedWriter bw8=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"经济.txt")));
		BufferedWriter bw9=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"科学.txt")));
		BufferedWriter bw10=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"历史.txt")));
		BufferedWriter bw11=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"自然.txt")));
		BufferedWriter bw12=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"体育.txt")));
		BufferedWriter bw13=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"无类别.txt")));
		
		
		String line;
		Arcticle article=new Arcticle();
		article.index="start";
		int index=0;
		
		while((line=br.readLine())!=null){
			if(line.startsWith("I:")){
				index++;
				article.index=line;
				if(index==1000000)
					break;
				if(!article.index.equals("start")){
					//write to file;
					
					if(article.root==null){
						bw13.write(article.toString());
					}
					else if(article.root.equals("艺术")){
						bw1.write(article.toString());
					}
					else if(article.root.equals("技术")){
						bw2.write(article.toString());
					}
					else if(article.root.equals("文化")){
						bw3.write(article.toString());
					}
					else if(article.root.equals("生活")){
						bw4.write(article.toString());
					}
					else if(article.root.equals("地理")){
						bw5.write(article.toString());
					}
					else if(article.root.equals("社会")){
						bw6.write(article.toString());
					}
					else if(article.root.equals("人物")){
						bw7.write(article.toString());
					}
					else if(article.root.equals("经济")){
						bw8.write(article.toString());
					}
					else if(article.root.equals("科学")){
						bw9.write(article.toString());
					}
					else if(article.root.equals("历史")){
						bw10.write(article.toString());
					}
					else if(article.root.equals("自然")){
						bw11.write(article.toString());
					}
					else if(article.root.equals("体育")){
						bw12.write(article.toString());
					}
					
				}
				article.abstact="A:";
				article.catogory="C:";
				article.name="T:";
				article.index="IB:";
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
		
		bw1.close();
		bw2.close();
		bw3.close();
		bw4.close();
		bw5.close();
		bw6.close();
		bw7.close();
		bw8.close();
		bw9.close();
		bw10.close();
		bw11.close();
		bw12.close();
		bw13.close();
	}
	
	public static void main(String args[]){
		try{
			Classifier cl=new Classifier();
			cl.ReadTaxomony();
			cl.SplitFiles();
			System.out.println(cl.GetRoot("地理"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
