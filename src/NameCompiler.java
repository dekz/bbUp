import java.io.StreamTokenizer;
import java.util.StringTokenizer;


public class NameCompiler 
{
	private String name;
	public NameCompiler(String s)
	{
		this.name = s;
	}
	
	
	public String determineName()
	{
		
		int wordCount=0;
		boolean foundNumber = false;
		int length = name.length();
		
		//String[] split = name.split("[^\\w \\xC0-\\xFF]");
		String[] split = name.split("[^\\w]");
		//Split now contains tokenised words including year 
		StringBuilder sb = new StringBuilder();
		int possibleLength=0;
		if (split.length == 0) split = name.split(" ");
		System.out.println(split.length);
		for (int i = 0; i < split.length; i++) 
		{
			if (!checkForYear(split[i]))
			{
				sb.append(split[i]);
				sb.append(" ");
				possibleLength++;
			}
			else 
			{
				sb.append(split[i]);
				break;
			}
		}
		System.out.println("Possible Title: " + sb.toString());
		
		//Find words, if found a 4 digit number and a few words - this could be the year 
		//if no words only number this could be the name
		//if found words then found something along the lines of S01e02, letters and words with no seperator - could be tv info
		
		return sb.toString();
	}
	
	private boolean checkForYear(String s)
	{
		
		if (s == null || s.length() <= 0) return false;
		if (s.length() != 4) return false;
		for (int i = 0; i < s.length(); i++) 
		{
			if (!Character.isDigit(s.charAt(i))) return false;
		}
		
		return true;
	}
	
	private String[] tokenize(String theString, String splitter)
	{
		String[] tokens = null;
		StringTokenizer st = new StringTokenizer(theString, splitter);
		tokens = new String[st.countTokens()];
		for (int i = 0; i < tokens.length; i++) 
		{
			tokens[i] = st.nextToken();
		}
		
		return tokens;
	}
}
