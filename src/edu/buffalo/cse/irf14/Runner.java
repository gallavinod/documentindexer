/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		//long t = System.currentTimeMillis();
		
		try {
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						writer.addDocument(d);
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} 
					
				}
				
			}
			/*t = (System.currentTimeMillis()-t);
			System.out.println("Time taken for indexing: "+ t/100);
			t = System.currentTimeMillis();
			writer.close();
			t = (System.currentTimeMillis()-t);
			System.out.println("Time taken for writing: "+ t/100);
			t = System.currentTimeMillis();
			IndexReader ir = new IndexReader(indexDir, IndexType.TERM);
			System.out.println(ir.getTotalKeyTerms());
			System.out.println(ir.getPostings("shares").containsKey("0000005"));
			System.out.println(ir.getPostings("sale").containsKey("0000005"));
			ir.query("shares", "circumstances", "sale");
			ir.getTopK(10);

			IndexReader pir = new IndexReader(indexDir, IndexType.PLACE);
			IndexReader air = new IndexReader(indexDir, IndexType.AUTHOR);
			IndexReader cir = new IndexReader(indexDir, IndexType.CATEGORY);
			cir.getTopK(10);
			t = (System.currentTimeMillis()-t);
			System.out.println("Time taken for reading: "+ t/100);*/
			writer.close();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
