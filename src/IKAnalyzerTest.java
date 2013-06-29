import java.io.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyzerTest {
	public static void main(String[] args) throws Exception{
		String text="最近朋友都推荐使用github管理自己的项目,而且免费用户可以有5个仓库,恰好我也想了解下git,借此机会学习一下";
		IKAnalyzer analyzer=new IKAnalyzer(true);
		analyzer.setUseSmart(true);
		StringReader reader=new StringReader(text);
		//�ִ�
		TokenStream ts=analyzer.tokenStream("", reader);
		CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
		//����ִ����
		while(ts.incrementToken()){
			System.out.print(term.toString()+"|");
		}
		reader.close();
		analyzer.close();
		System.out.println();
	}
}
