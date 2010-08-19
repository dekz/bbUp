
public class Movie 
{
	private String name;
	private float rating;
	private String overview;
	private String trailer;
	private String imdb;
	private String type;
	
	public String getName()
	{
		return name;
	}
	
	public String getIMDB()
	{
		return imdb;
	}
	
	public void setIMDB(String imdb)
	{
		this.imdb = imdb;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getTrailer()
	{
		return trailer;
	}
	
	public String getOverview()
	{
		return overview;
	}
	
	public void setOverview(String overview)
	{
		this.overview = overview;
	}
	
	public float getRating()
	{
		return rating;
	}
	
	public void setRating(float rating)
	{
		this.rating = rating;
	}
	
	@Override
	public String toString()
	{
		return name + " - " + imdb;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public void setTrailer(String trailer)
	{
		this.trailer = trailer;
	}
}
