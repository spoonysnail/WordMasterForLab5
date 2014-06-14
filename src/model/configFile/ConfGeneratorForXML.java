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
			
			
			readerConf.writeLong(lexiconPoint);		/**�ʿ����*/
			readerConf.writeLong(0);				/**�ʿ��һ���������*/
			readerConf.writeLong(0);				/**�ʿ��ϴα������ʵ����*/
			readerConf.writeInt(size);				/**�ʿ⺬�еĵ��ʸ���*/
			readerConf.writeInt(0);					/**�ʿ��Ѿ����ĵ��ʸ���*/
			readerConf.writeInt(0);					/**�ʿ��Ѿ����Եĵ��ʸ���*/
			readerConf.writeUTF((String) key);		/**�ʿ����*/
			
		}
		
		long tempPoint = readerConf.getFilePointer(); 
		readerConf.seek(0);
		readerConf.writeLong(tempPoint);
		
		//��ʼд������Ϣ
		readerConf.seek(tempPoint);
		
		
		Iterator<Entry<String, LexiconTemp>>  it2 = lexMap.entrySet().iterator();
		while (it2.hasNext())
		{
			Entry<String, LexiconTemp> entry = (Entry<String, LexiconTemp>) it2.next();
			String key = entry.getKey();
			LexiconTemp lexicon = entry.getValue();
			
			tempPoint = readerConf.getFilePointer();
			
			readerConf.seek(lexiconPointerMap.get(key)+8);
				
			readerConf.writeLong(tempPoint);	/**�ʿ��һ���������д��*/
			readerConf.writeLong(tempPoint);	/**�ʿ����һ�α��еĵ������д��*/
				
				
			readerConf.seek(tempPoint);			/**���ص�ǰ���ʵ���ڣ���������*/
			lexiconPointerMap.keySet().remove(lexiconPointerMap.get(key));
			
			System.out.println(key);
			
			List<WordTemp> wordList = lexicon.wordList;
			
			for(WordTemp wt : wordList)
			{
				readerConf.writeLong(readerConf.getFilePointer());	/**д��entry*/
				readerConf.writeInt(0);								/**д��״̬*/
				readerConf.writeUTF(wt.english);						/**д��Ӣ������*/
				readerConf.writeUTF(wt.chinese);					/**д����������*/
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