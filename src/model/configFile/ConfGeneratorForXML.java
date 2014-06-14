package model.configFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfGeneratorForXML {
	
	private static DicXMLHandler dxHandler;
	
	public static void main(String []arg) throws Exception
	{
		generateConfig();
	}
	
	private static void initConfig()
	{
		Map<String, String> tagMap = new HashMap<String, String>();
		tagMap.put("list", "word");
		tagMap.put("english", "english");
		tagMap.put("chinese", "chinese");
		dxHandler = new DicXMLHandler("dictionary.xml",tagMap);
	}
	
	public static void generateConfig() throws Exception
	{
		initConfig();
		
		Map<String, LexiconTemp> lexMap = dxHandler.HandleXMLFlow();
		
		
		
		File confFile = new File("user.conf");
		
		RandomAccessFile readerConf = null;
		
		Map<String,Long> lexiconPointerMap = new HashMap<String,Long>(); 
		
		if(!confFile.exists())
		{
			confFile.createNewFile();
		}
		
		
		readerConf = new RandomAccessFile(confFile,"rws");
		readerConf.seek(0);
		
		readerConf.writeLong(0);/** The entry of dictionary */
		
		Iterator<Entry<String, LexiconTemp>>  it1 = lexMap.entrySet().iterator();
		while (it1.hasNext())
		{
			Entry<String, LexiconTemp> entry = (Entry<String, LexiconTemp>) it1.next();
			String key = entry.getKey();
			LexiconTemp lexicon = entry.getValue();
			Integer size = lexicon.wordList.size();
			
			long lexiconPoint = readerConf.getFilePointer();
			
			lexiconPointerMap.put((String) key,lexiconPoint);
			
			
			readerConf.writeLong(lexiconPoint);		/**词库入口*/
			readerConf.writeLong(0);				/**词库第一个单词入口*/
			readerConf.writeLong(0);				/**词库上次背到单词的入口*/
			readerConf.writeInt(size);				/**词库含有的单词个数*/
			readerConf.writeInt(0);					/**词库已经背的单词个数*/
			readerConf.writeInt(0);					/**词库已经背对的单词个数*/
			readerConf.writeUTF((String) key);		/**词库类别*/
			
		}
		
		long tempPoint = readerConf.getFilePointer(); 
		readerConf.seek(0);
		readerConf.writeLong(tempPoint);
		
		//开始写词语信息
		readerConf.seek(tempPoint);
		
		
		Iterator<Entry<String, LexiconTemp>>  it2 = lexMap.entrySet().iterator();
		while (it2.hasNext())
		{
			Entry<String, LexiconTemp> entry = (Entry<String, LexiconTemp>) it2.next();
			String key = entry.getKey();
			LexiconTemp lexicon = entry.getValue();
			
			tempPoint = readerConf.getFilePointer();
			
			readerConf.seek(lexiconPointerMap.get(key)+8);
				
			readerConf.writeLong(tempPoint);	/**词库第一个单词入口写入*/
			readerConf.writeLong(tempPoint);	/**词库最后一次背诵的单词入口写入*/
				
				
			readerConf.seek(tempPoint);			/**返回当前单词的入口，继续操作*/
			lexiconPointerMap.keySet().remove(lexiconPointerMap.get(key));
			
			System.out.println(key);
			
			List<WordTemp> wordList = lexicon.wordList;
			
			for(WordTemp wt : wordList)
			{
				readerConf.writeLong(readerConf.getFilePointer());	/**写入entry*/
				readerConf.writeInt(0);								/**写入状态*/
				readerConf.writeUTF(wt.english);						/**写入英文释义*/
				readerConf.writeUTF(wt.chinese);					/**写入中文释义*/
			}						
		} 
		
		readerConf.seek(0);
		/**
		 * @param testing_code
		 * 
		 * 	long temp = readerConf.readLong();
		 *	while(readerConf.getFilePointer()<temp)
		 *	{
		 *		System.out.print(readerConf.readLong()+"-");
		 *		System.out.print(readerConf.readLong()+"-");
		 *		System.out.print(readerConf.readLong()+"-");
		 *		System.out.print(readerConf.readInt()+"-");
		 *		System.out.print(readerConf.readInt()+"-");
		 *		System.out.print(readerConf.readInt()+"-");
		 *		System.out.println(readerConf.readUTF());
		 *	}
		 *	while(readerConf.getFilePointer()<readerConf.length())
		 *	{
		 *		System.out.print(readerConf.readLong()+"-");
		 *		System.out.print(readerConf.readInt()+"-");
		 *		System.out.print(readerConf.readUTF()+"-");
		 *		System.out.println(readerConf.readUTF());
		 *	}
		 *
		 */
		readerConf.close();
		
	}
}
