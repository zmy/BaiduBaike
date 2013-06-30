import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.wltea.analyzer.lucene.IKAnalyzer;  


public class Classifier {
	
	private static String ontPath = "etc/ontology.owl";
	private static String dataPath="etc/";
	Map<String,String> catogory=new HashMap<String,String>();
	Map<String,Integer> wordList=new HashMap<String,Integer>();
	Map<String,Integer> infoboxList=new HashMap<String,Integer>();
	ArrayList<String> props=new ArrayList<String>();
	
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
				System.out.println(root+"\t"+child);
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
	
	//其实根据infobox去分类应该还会蛮准的
	public void getInfoboxProp() throws IOException{
		FileInputStream is=new FileInputStream(dataPath+"baidu-article.dat");
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);
		String line;
		int index=0;
		while((line=br.readLine())!=null){
			if(line.startsWith("IB:")){
				index++;
				if(index>1000000)
					break;
				String []attrVals=line.substring(3).split("::;");
				for(String attrVal:attrVals){
					String parts[]=attrVal.split("::=");
					if(parts.length<2)
						continue;
					//就要第一个，即key
					String key=parts[0];
					if(infoboxList.containsKey(key)){
						infoboxList.put(key, infoboxList.get(key)+1);
					}
					else{
						infoboxList.put(key,1);
					}
				}
			}
		}
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/infoboxList.txt")));
		Set<Map.Entry<String,Integer>> wordSet=infoboxList.entrySet();
		Iterator<Map.Entry<String, Integer>> itr=wordSet.iterator();
		while(itr.hasNext()){
			Map.Entry<String, Integer> entry=itr.next();
			bw.write(entry.getKey()+"\t"+entry.getValue()+"\n");
		}
		bw.close();
		
	}
	
	
	
	
	
	public void getWordList(){
		//
		try{
			FileInputStream is=new FileInputStream(dataPath+"baidu-article.dat");
			InputStreamReader isr=new InputStreamReader(is,"UTF-8");
			BufferedReader br=new BufferedReader(isr);
			String line;
			IKAnalyzer analyzer=new IKAnalyzer(true);
			
			int index=0;
			while((line=br.readLine())!=null){
				if(line.startsWith("I:")){
					index++;
				}
				if(index>100000){
					break;
				}
				
				StringReader reader=new StringReader(line);
				
				TokenStream ts=analyzer.tokenStream("", reader);
				if(ts==null)
					continue;
				CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
				//System.out.println(line);
				if(ts==null||term==null)
					continue;
				try{
				while(ts.incrementToken()){
					String word=term.toString();
					if(wordList.containsKey(word)){
						wordList.put(word,wordList.get(word)+1);
					}
					else{
						wordList.put(word,1);
					}
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
				
			br.close();
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/wordList.txt")));
			Set<Map.Entry<String,Integer>> wordSet=wordList.entrySet();
			Iterator<Map.Entry<String, Integer>> itr=wordSet.iterator();
			while(itr.hasNext()){
				Map.Entry<String, Integer> entry=itr.next();
				bw.write(entry.getKey()+"\t"+entry.getValue()+"\n");
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public void readProps() throws IOException{
		FileInputStream is=new FileInputStream(dataPath+"infobox.txt");
		InputStreamReader isr=new InputStreamReader(is,"GBK");
		BufferedReader br=new BufferedReader(isr);
		String line;
		while((line=br.readLine())!=null){
			String parts[]=line.split("\t");
			//System.out.println(parts[0]);
			props.add(parts[0]);
		}
		br.close();
		isr.close();
		is.close();
	}
	
	public Integer[] fileToVector(String filename)throws IOException{
		FileInputStream is=new FileInputStream(filename);
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);
		String line;
		Integer vec[]=new Integer[2000];
		for(int i=0;i<2000;i++){
			vec[i]=0;
		}
		for(int i=0;i<2000;i++){
			System.out.println(props.get(i));			
		}
		while((line=br.readLine())!=null){
			if(!line.startsWith("IB:"))
				continue;
			String []attrVals=line.substring(3).split("::;");
			for(String attrVal:attrVals){
				String parts[]=attrVal.split("::=");
				if(parts.length<2)
					continue;
				//System.out.println("parts:"+parts[0]);
				
				int index=props.indexOf(parts[0]);
				//System.out.println(index);
				if(index<0||index>=2000)
					continue;
				vec[index]=vec[index]+1;
			}
		}
		return vec;
	}
	
	public Integer[] string2Vector(String infobox){
		Integer vec[]=new Integer[2000];
		for(int i=0;i<2000;i++){
			vec[i]=0;
		}
		if(infobox.startsWith("IB:")){
			String []attrVals=infobox.substring(3).split("::;");
			for(String attrVal:attrVals){
				String parts[]=attrVal.split("::=");
				if(parts.length<2)
					continue;
				int index=props.indexOf(parts[0]);
				//System.out.println(index);
				if(index<0||index>=2000)
					continue;
				vec[index]=vec[index]+1;
			}
		}
		return vec;
	}
	
	public double VectorSimilarity(Integer vec1[],Integer vec2[]){
		double similarity=0;
		double a=0,b=0,c=0;
		for(int i=0;i<2000;i++){
			a=a+vec1[i]*vec2[i];
			b=b+vec1[i]*vec1[i];
			c=c+vec2[i]*vec2[i];
		}
		if(b==0){
			return 0;
		}
		similarity=(double)a/(Math.sqrt((double)b)*Math.sqrt((double)c));
		return similarity;
	}
	
	public int getMaxIndex(Double vec[]){
		int maxIndex=0;
		for(int i=0;i<vec.length;i++){
			if(vec[i]>vec[maxIndex])
				maxIndex=i;
		}
		return maxIndex;
	}
	
	public void ClassifyWithInfobox()throws Exception{
		readProps();
		
		
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/艺术_infobox.txt")));
		Integer[] vec1=fileToVector(dataPath+"/艺术.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec1[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/技术_infobox.txt")));
		Integer[] vec2=fileToVector(dataPath+"/技术.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec2[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/文化_infobox.txt")));
		Integer[] vec3=fileToVector(dataPath+"/文化.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec3[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/生活_infobox.txt")));
		Integer[] vec4=fileToVector(dataPath+"/生活.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec4[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/地理_infobox.txt")));
		Integer[] vec5=fileToVector(dataPath+"/地理.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec5[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/社会_infobox.txt")));
		Integer[] vec6=fileToVector(dataPath+"/社会.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec6[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/人物_infobox.txt")));
		Integer[] vec7=fileToVector(dataPath+"/人物.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec7[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/经济_infobox.txt")));
		Integer[] vec8=fileToVector(dataPath+"/经济.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec8[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/历史_infobox.txt")));
		Integer[] vec9=fileToVector(dataPath+"/历史.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec9[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/自然_infobox.txt")));
		Integer[] vec10=fileToVector(dataPath+"/自然.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec10[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/体育_infobox.txt")));
		Integer[] vec11=fileToVector(dataPath+"/体育.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec11[i]+"\n");
		}
		bw.close();
		bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"/科学_infobox.txt")));
		Integer[] vec12=fileToVector(dataPath+"/科学.txt");
		for(int i=0;i<2000;i++){
			bw.write(i+"\t"+vec12[i]+"\n");
		}
		bw.close();
		
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(dataPath+"/无类别.txt")));
		String line;
		
		BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"艺术_predict.txt")));
		BufferedWriter bw2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"技术_predict.txt")));
		BufferedWriter bw3=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"文化_predict.txt")));
		BufferedWriter bw4=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"生活_predict.txt")));
		BufferedWriter bw5=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"地理_predict.txt")));
		BufferedWriter bw6=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"社会_predict.txt")));
		BufferedWriter bw7=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"人物_predict.txt")));
		BufferedWriter bw8=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"经济_predict.txt")));
		BufferedWriter bw9=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"科学_predict.txt")));
		BufferedWriter bw10=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"历史_predict.txt")));
		BufferedWriter bw11=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"自然_predict.txt")));
		BufferedWriter bw12=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"体育_predict.txt")));
		BufferedWriter bw13=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"无法区分_predict.txt")));
		
		Arcticle article=new  Arcticle();
		article.index="start";
		int maxIndex=0;
		while((line=br.readLine())!=null){
			if(line.startsWith("I:")){
				article.index=line;
				if(!article.index.equals("start")){
					if(maxIndex==0){
						bw1.write(article.toString());
					}
					else if(maxIndex==1){
						bw2.write(article.toString());
					}
					else if(maxIndex==2){
						bw3.write(article.toString());
					}
					else if(maxIndex==3){
						bw4.write(article.toString());
					}
					else if(maxIndex==4){
						bw5.write(article.toString());
					}
					else if(maxIndex==5){
						bw6.write(article.toString());
					}
					else if(maxIndex==6){
						bw7.write(article.toString());
					}
					else if(maxIndex==7){
						bw8.write(article.toString());
					}
					else if(maxIndex==8){
						bw9.write(article.toString());
					}
					else if(maxIndex==9){
						bw10.write(article.toString());
					}
					else if(maxIndex==10){
						bw11.write(article.toString());
					}
					else if(maxIndex==11){
						bw12.write(article.toString());
					}
					else{
						bw13.write(article.toString());
					}
					
				}
				article.abstact="A:";
				article.catogory="C:";
				article.name="T:";
				article.index="IB:";
				article.root=null;
				article.link="L:";
			}
			else if(line.startsWith("T:")){
				article.name=line;
			}
			else if(line.startsWith("IB:")){
				article.infobox=line;
				Integer myvec[]=string2Vector(line);
				//计算是是哪一个类别
				Double sim[]=new Double[12];
				sim[0]=VectorSimilarity(myvec, vec1);
				sim[1]=VectorSimilarity(myvec, vec2);
				sim[2]=VectorSimilarity(myvec, vec3);
				sim[3]=VectorSimilarity(myvec, vec4);
				sim[4]=VectorSimilarity(myvec, vec5);
				sim[5]=VectorSimilarity(myvec, vec6);
				sim[6]=VectorSimilarity(myvec, vec7);
				sim[7]=VectorSimilarity(myvec, vec8);
				sim[8]=VectorSimilarity(myvec, vec9);
				sim[9]=VectorSimilarity(myvec, vec10);
				sim[10]=VectorSimilarity(myvec, vec11);
				sim[11]=VectorSimilarity(myvec, vec12);
				maxIndex=getMaxIndex(sim);
				if(sim[maxIndex]==0)
					maxIndex=-1;
				System.out.println(maxIndex);
				System.out.println(line);
			}
			else if(line.startsWith("A:")){
				article.abstact=line;
			}
			else if(line.startsWith("L")){
				article.link=line;
			}
		
		}
		br.close();
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
	
	//大概30万实例无法区分的实例里面，有12万只有名字，有16万有link,有5万有摘要abstract
	public void statWhoNoInfobox(){
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(dataPath+"/无法区分_predict.txt")));
			String line;
			int onlyName=0;
			int attributes=0;
			int hasLink=0;
			int total=0;
			int hasAbstract=0;
			while((line=br.readLine())!=null){
				
				if(line.startsWith("I:")){
					if(attributes==1)
						onlyName=onlyName+1;
					attributes=1;
					total=total+1;
				}
				else if(line.startsWith("C:")){
					if(line.length()>2)
						attributes=attributes+1;
				}
				else if(line.startsWith("L:")){
					if(line.length()>2){
						attributes=attributes+1;
						hasLink=hasLink+1;
					}
				}
				else if(line.startsWith("A:")){
					if(line.length()>2){
						attributes=attributes+1;
						hasAbstract=hasAbstract+1;
					}
				}
			}
			System.out.println(total+"\t"+hasLink+"\t"+hasAbstract+"\t"+onlyName+"\n");
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//那就简单这么认为吧，看它link到的是哪一类更多一点
	//就归入哪一类
	public void  ClassifyWithLink ()throws Exception{
		//首先要拿到一个100万实例哪些分好的都属于哪些类别这个对应表
		FileInputStream is=new FileInputStream(dataPath+"baidu-article.dat");
		InputStreamReader isr=new InputStreamReader(is,"UTF-8");
		BufferedReader br=new BufferedReader(isr);
		String line;
		Arcticle article=new Arcticle();
		article.index="start";
		int index=0;
		HashMap<String,String> child2Root=new HashMap<String,String>();
		
		while((line=br.readLine())!=null){
			if(line.startsWith("T:")){
				article.name=line.substring(3);
			}
			else if(line.startsWith("C:")){
				//keypoint
				article.catogory=line;
				String []classes=line.substring(2).split("::;");
				String root=null;
				for(String subclass:classes)
				{
					root=GetRoot(subclass);
					if(root!=null)
						break;
				}
				if(root!=null){
					article.root=root;
					child2Root.put(article.name, root);
				}
			}
		}
		System.out.println(child2Root.size());
		br.close();
		//
		Map<String,Integer> root2Index=new HashMap<String,Integer>();
		root2Index.put("艺术", 0);
		root2Index.put("技术", 1);
		root2Index.put("文化", 2);
		root2Index.put("生活", 3);
		root2Index.put("地理", 4);
		root2Index.put("社会", 5);
		root2Index.put("人物", 6);
		root2Index.put("经济", 7);
		root2Index.put("科学", 8);
		root2Index.put("历史", 9);
		root2Index.put("自然", 10);
		root2Index.put("体育", 11);
		
		br=new BufferedReader(new InputStreamReader(new FileInputStream(dataPath+"/无法区分_predict.txt")));
		
		BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"艺术_predict2.txt")));
		BufferedWriter bw2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"技术_predict2.txt")));
		BufferedWriter bw3=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"文化_predict2.txt")));
		BufferedWriter bw4=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"生活_predict2.txt")));
		BufferedWriter bw5=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"地理_predict2.txt")));
		BufferedWriter bw6=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"社会_predict2.txt")));
		BufferedWriter bw7=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"人物_predict2.txt")));
		BufferedWriter bw8=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"经济_predict2.txt")));
		BufferedWriter bw9=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"科学_predict2.txt")));
		BufferedWriter bw10=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"历史_predict2.txt")));
		BufferedWriter bw11=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"自然_predict2.txt")));
		BufferedWriter bw12=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"体育_predict2.txt")));
		BufferedWriter bw13=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPath+"无法区分_predict2.txt")));
	
		
		article.index="start";
		int maxIndex=0;
		while((line=br.readLine())!=null){
			if(line.startsWith("I:")){
				article.index=line;
				if(!article.index.equals("start")){
					if(maxIndex==0){
						bw1.write(article.toString());
					}
					else if(maxIndex==1){
						bw2.write(article.toString());
					}
					else if(maxIndex==2){
						bw3.write(article.toString());
					}
					else if(maxIndex==3){
						bw4.write(article.toString());
					}
					else if(maxIndex==4){
						bw5.write(article.toString());
					}
					else if(maxIndex==5){
						bw6.write(article.toString());
					}
					else if(maxIndex==6){
						bw7.write(article.toString());
					}
					else if(maxIndex==7){
						bw8.write(article.toString());
					}
					else if(maxIndex==8){
						bw9.write(article.toString());
					}
					else if(maxIndex==9){
						bw10.write(article.toString());
					}
					else if(maxIndex==10){
						bw11.write(article.toString());
					}
					else if(maxIndex==11){
						bw12.write(article.toString());
					}
					else{
						bw13.write(article.toString());
					}
					
				}
				article.abstact="A:";
				article.catogory="C:";
				article.name="T:";
				article.index="IB:";
				article.root=null;
				article.link="L:";
				maxIndex=-1;
			}
			else if(line.startsWith("T:")){
				article.name=line;
			}
			else if(line.startsWith("IB:")){
				article.infobox=line;
				
			}
			else if(line.startsWith("A:")){
				article.abstact=line;
			}
			else if(line.startsWith("L:")){
				article.link=line;
				if(line.length()<=2)
					continue;
				String parts[]=line.substring(3).split("::;");
				Double vec[]=new Double[12];
				for(int i=0;i<12;i++){
					vec[i]=0.0;
				}
				for(String part:parts){
					if(child2Root.containsKey(part)){
						int cur=root2Index.get(child2Root.get(part));
						vec[cur]=vec[cur]+1;
						System.out.println(child2Root.get(part));
					}
				}
				maxIndex=getMaxIndex(vec);
				if(vec[maxIndex]==0.0)
					maxIndex=-1;
				
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
			//cl.SplitFiles();
			//cl.getWordList();
			//cl.getInfoboxProp();
			//cl.ClassifyWithInfobox();
			//cl.ClassifyWithLink();
			
			//
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
