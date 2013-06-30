import java.io.*;
import java.util.*;


public class JustForTest {
	private static String dataPath="etc/";
	public static void main(String args[]){
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
}
