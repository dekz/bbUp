import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class TMDBScraper implements MovieScraper {

	private String name;
	private Document data;
	private String API_KEY;
	private String baseURI = "http://api.themoviedb.org/2.1/";
	private Movie possibleMovie;
	
	public TMDBScraper(String name) 
	{
		this.name = name;
	}

	@Override
	public Movie scrape()
	{
		Movie movie = null;
		try 
		{
			System.out.println(name);
			data = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(baseURI + "Movie.search/en/xml/" + "1afbe4c16d2a153a4cd1b1fba94165c0" + "/" + name);
			Element documentElement = data.getDocumentElement();
			NodeList nodeList = documentElement.getElementsByTagName("movie");
			if (nodeList.getLength() > 0)
			{
				Element element = (Element) nodeList.item(0);
                movie = parse(element);
                return movie;
			}
			
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Element element = (Element) nodeList.item(i);
                movie = parse(element);
			}
		} catch (SAXException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		} catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		return movie;
	}
	
	
	private Movie parse(Element element)
	{
		Movie movie = new Movie();
		movie.setIMDB(getTagValue(element, "imdb_id"));
		movie.setOverview(getTagValue(element, "overview"));
		movie.setName(getTagValue(element, "name"));
		if (!getTagValue(element, "rating").equalsIgnoreCase("")) {
            movie.setRating(Float.parseFloat(getTagValue(element, "rating")));
        }
		movie.setTrailer(getTagValue(element, "trailer"));
		return movie;
	}
	
	
	 private String getTagValue(Element element, String tagName) {
	        String value = "";
	        NodeList nodeList = element.getElementsByTagName(tagName);
	        if (nodeList != null && nodeList.getLength() > 0) {
	            Element el = (Element) nodeList.item(0);
	            if (el.getFirstChild() != null) {
	                value = el.getFirstChild().getNodeValue();
	            }
	        }
	        return value;
	   }
	
	@Override
	public String getOverview() 
	{
		return possibleMovie.getOverview();
	}

	@Override
	public void setName(String name) 
	{
		this.name = name;

	}

	@Override
	public void setAPIKey(String key) 
	{
		this.API_KEY = key;
	}

	@Override
	public String getIMDBTitle() 
	{
	
		return possibleMovie.getIMDB();
	}

	@Override
	public String getCategories() 
	{
		return null;
	}

	@Override
	public String getRating() 
	{
		return Float.toString((possibleMovie.getRating()));
	}

	@Override
	public List<Movie> movieList() 
	{
		
		return null;
	}

}
