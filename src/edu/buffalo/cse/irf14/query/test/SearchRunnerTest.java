package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.query.Pair;
import edu.buffalo.cse.irf14.query.QueryParser;

public class SearchRunnerTest {
	private File file; 
	
	@Before
	public void initialise() {
		file = new File("test");
	}
	
	@Test
	public void testQuery() {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			ArrayList<Pair> arrayList = new ArrayList<Pair>();
		    out.println("numQueries=3");
		    out.println("Q_1A63C:{hello world}");
		    out.println("Q_6V87S:{Category:oil AND place:Dubai AND ( price OR cost )}");
		    out.println("Q_4K66L:{long query with several words}");
		    out.close();
		    SearchRunner searchRunner = new SearchRunner();
		    arrayList = searchRunner.parseQueryFile(file);
		    assertEquals("Q_1A63C", arrayList.get(0).getQueryID());
		    assertEquals("{ Term:hello OR Term:world }", arrayList.get(0).getQuery().toString());
		    assertEquals("Q_6V87S", arrayList.get(1).getQueryID());
		    assertEquals("{ Category:oil AND Place:Dubai AND [ Term:price OR Term:cost ] }", arrayList.get(1).getQuery().toString());
		    assertEquals("Q_4K66L", arrayList.get(2).getQueryID());
		    assertEquals("{ Term:long OR Term:query OR Term:with OR Term:several OR Term:words }", arrayList.get(2).getQuery().toString());
		    
		} catch(Exception e){
			e.printStackTrace();
			fail("Failed parseQueryFileTest.");
		}
	}
	
	@Test
	public void testGetQueryTerms() {
		SearchRunner sr = new SearchRunner();
	}

	@After
	public void tearDown() {
		file.delete();
	}
}
