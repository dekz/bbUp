


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class bbTests extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public bbTests( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( bbTests.class );
    }

    public void testNameCompiler()
    {
    	String s = "Tropic.Thunder[2008]DvDrip-aXXo";
    	NameCompiler nm = new NameCompiler(s);
    	String determined = nm.determineName();
    	System.out.println("Determined:" + determined);
    	
    	MovieScraper ms = new TMDBScraper(determined);
    	Movie m = ms.scrape();
    	System.out.println(m.getOverview());
    	System.out.println(m.getRating());
	
    }
  
    
}
