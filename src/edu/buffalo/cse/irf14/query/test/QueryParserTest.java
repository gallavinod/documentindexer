package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.buffalo.cse.irf14.Runner;
import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.query.Pair;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParserException;
import edu.buffalo.cse.irf14.query.QueryParser;

public class QueryParserTest {
	
	private File file; 
	SearchRunner sr;
	
	@Before
	public void initialise() {
		file = new File("test");
	}
	
	@Test
	public void testParse() throws QueryParserException {
		Query query = QueryParser.parse("hello", "AND");
		assertEquals("{ Term:hello }",query.toString());
		query = QueryParser.parse("hello world", "OR");
		assertEquals("{ Term:hello OR Term:world }",query.toString());
		query = QueryParser.parse("\"hello world\"", "OR");
		assertEquals("{ Term:\"hello world\" }",query.toString());
		query = QueryParser.parse("orange AND yellow", "OR");
		assertEquals("{ Term:orange AND Term:yellow }",query.toString());
		query = QueryParser.parse("(black OR blue) AND bruises", "OR");
		assertEquals("{ [ Term:black OR Term:blue ] AND Term:bruises }",query.toString());
		query = QueryParser.parse("Author:rushdie NOT jihad", "OR");
		assertEquals("{ Author:rushdie AND <Term:jihad> }",query.toString());
		query = QueryParser.parse("(Love NOT War) AND Category:(movies NOT crime)", "OR");
		assertEquals("{ [ Term:Love AND <Term:War> ] AND [ Category:movies AND <Category:crime> ] }",query.toString());
		query = QueryParser.parse("Category:War AND Author:Dutt AND Place:Baghdad AND prisoners detainees rebels", "OR");
		assertEquals("{ Category:War AND Author:Dutt AND Place:Baghdad AND [ Term:prisoners OR Term:detainees OR Term:rebels ] }", query.toString());
		query = QueryParser.parse("Category:oil AND place:Dubai AND (price OR cost)", "OR");
        assertEquals("{ Category:oil AND Place:Dubai AND [ Term:price OR Term:cost ] }", query.toString());
        query = QueryParser.parse("long query with several words","OR");
        assertEquals("{ Term:long OR Term:query OR Term:with OR Term:several OR Term:words }", query.toString());
        //Runner.main(new String[]{"/home/jagadeesh/workspace/training", "/home/jagadeesh/workspace/wergroot"});

		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("/home/jagadeesh/workspace/wergroot/query1"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    sr = new SearchRunner("/home/jagadeesh/workspace/wergroot", "/home/jagadeesh/workspace/wergroot/training", 'Q', System.out);
	    sr.query("takeovers", ScoringModel.TFIDF);
        sr.query("takeovers", ScoringModel.OKAPI);
        sr.query("takeovers", ScoringModel.CUSTOM);
	    System.out.println("\n\n");
        sr.query("controlling interest", ScoringModel.TFIDF);
        sr.query("controlling interest", ScoringModel.OKAPI);
        sr.query("controlling interest", ScoringModel.CUSTOM);
        System.out.println();
        sr.query("word*s*", ScoringModel.OKAPI);
        /*sr.query("laser", ScoringModel.OKAPI);
        System.out.println();
        sr.query("hello world", ScoringModel.TFIDF);
        System.out.println();*/
        //----sr.query("author:torday AND (debt OR currency)", ScoringModel.TFIDF);
        out.println();
        //System.out.println("\n\n\n\n\n2");
        //-----sr.query("french economy employment government policies", ScoringModel.OKAPI);
        out.println();
        //System.out.println("\n\n\n\n\n2");
        //---sr.query("place:tokyo NOT bank", ScoringModel.OKAPI);
        out.println();
        //System.out.println("\n\n\n\n\n2");
        //sr.query("fully convertible non convertible optionally convertible pct convertible debentures", ScoringModel.OKAPI);
        out.println();
        //sr.query("zenith NOT zenith", ScoringModel.OKAPI);
        //System.out.println("\n\n\n\n\n2");
        //--sr.query("blah blah blah", ScoringModel.OKAPI);
        /*sr.query("laser", ScoringModel.TFIDF);
        System.out.println();
        sr.query("laser", ScoringModel.OKAPI);
        System.out.println();*/
        
        
        /*sr.query("lase*", ScoringModel.OKAPI);
        for(Entry<String, List<String>> entry: sr.getQueryTerms().entrySet()){
        	System.out.println(entry.getKey());
        	for(String s: entry.getValue()) {
        		System.out.println(s);
        	}
        }*/
        
        
        /*sr.query("lase", ScoringModel.OKAPI);
        for(String s: sr.getCorrections()){
        	System.out.println(s);
        }
        sr.query("author:\"peter torday\" AND (debt OR currency)", ScoringModel.TFIDF);
        System.out.println();
        sr.query("LaSEr", ScoringModel.OKAPI);*/
	}
	
	@Test
	public void testParseInvalidQuery(){
		try {
			QueryParser.parse("((Fail)", "OR");
			fail("ParserException not thrown.");
		} catch (QueryParserException e) {
			assertNotNull(e);
		}
	}
	
	@Test
	public void testQuery() {
		try {
			PrintStream in = new PrintStream(new FileOutputStream(file));			
		    in.println("numQueries=5");
		    in.println("Q_1A63C:{hello world}");
		    in.println("Q_6V87S:{Category:oil AND place:Dubai AND ( price OR cost )}");
		    in.println("Q_4K66L:{long query with several words}");
		    in.println("Q_4K66M:{word*s*}");
		    in.println("Q_4K66N:{words}");
		    in.close();
			PrintStream out = new PrintStream(new FileOutputStream("/home/jagadeesh/workspace/wergroot/file"));
		    sr = new SearchRunner("/home/jagadeesh/workspace/wergroot", "/home/jagadeesh/workspace/wergroot/training", 'Q', out);
		    sr.query(file);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@After
	public void tearDown() {
		file.delete();
	}

}
