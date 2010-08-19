import java.util.List;


public interface MovieScraper extends InfoScraper 
{
	public String getIMDBTitle();
	public String getCategories();
	public String getRating();
	public List<Movie> movieList();
	public Movie scrape();
}
