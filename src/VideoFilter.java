import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class VideoFilter implements FilenameFilter 
{

	private ArrayList<String> accepted;
	
	public VideoFilter()
	{
		accepted = new ArrayList<String>();
	}
	
	public void setAccepted(ArrayList<String> accepted)
	{
		this.accepted = accepted;
	}
	
	public void addAccepted(String name)
	{
		this.accepted.add(name);
	}

	@Override
	public boolean accept(File dir, String name) 
	{
		boolean found = false;
		for (int i = 0; i < accepted.size(); i++) 
		{
			if (name.endsWith(accepted.get(i))) 
			{
				found = true;
			}
		}
		return found;
	}

}
