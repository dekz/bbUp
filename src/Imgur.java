import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;



public class Imgur implements ImageUploader {

	private String API_KEY;
	private URL uploadURL;
	
	public Imgur(String API_KEY)
	{
		this.API_KEY = API_KEY;
		try {
			this.uploadURL = new URL("http://imgur.com/api/upload");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
/*	
	public String uploadImageOLD(File file) throws Exception {

		FileInputStream fis = new FileInputStream(file);
		String APIKEY = "79b3a3a5897ef30fa999af5b909230b0";
		
		HttpPost hp = new HttpPost(uploadURL.toURI());
        MultipartEntity en = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
  
        HttpClient c = new DefaultHttpClient();
        HttpResponse r;
        String xmlResponse = null;
        try 
        {
	        en.addPart("key", new StringBody(APIKEY));
	        en.addPart("image", new InputStreamBody(fis, "test"));  // or use new FileBody(File . . . )
	        hp.setEntity(en);
	
	        r = c.execute(hp);
	        
	        // Read in the response
	        InputStream content = r.getEntity().getContent();
	        ByteArrayOutputStream response = new ByteArrayOutputStream();
	        final int BUF_SIZE = 1 << 8; // 1KiB buffer
	        byte[] buffer = new byte[BUF_SIZE];
	        int bytesRead = -1;
	        while ((bytesRead = content.read(buffer)) > -1) 
	        {
	                response.write(buffer, 0, bytesRead);
	        }
	        content.close();
	        c.getConnectionManager().closeExpiredConnections();
	        c.getConnectionManager().shutdown();
	        xmlResponse = response.toString();
	        System.out.println("Content uploaded");
	        System.out.println(xmlResponse);
	        return xmlResponse;

        } catch (ClientProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
		return null;
	}*/
	
	@Override
	public String uploadImage(File f) throws UnsupportedEncodingException
	{
		
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL("http://imgur.com/api/upload");

			//encodes picture with Base64 and inserts api key
			
			String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encodeFromFile(f.getAbsolutePath()), "UTF-8");
			data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode("79b3a3a5897ef30fa999af5b909230b0", "UTF-8");
			System.out.println("Generated base 64 string");
		
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			long timer = System.currentTimeMillis();
			System.out.println("Writing data " + data.length());
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data, 0, data.length());
			wr.flush();
			wr.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String reply;
			
			System.out.println("Reading data");
			while ((reply = in.readLine()) != null)
			{
				sb.append(reply);
			}
			
			in.close();
			System.out.println("Image took " + (System.currentTimeMillis() - timer)/1000);
			return sb.toString();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		return sb.toString();
	}
	
	public String parser(String response)
	{
		if (response.length() <=0) System.out.println("Problem");
		String document = response;
		String startTag = "<original_image>";
		String endTag = "</original_image>";
		int start = document.indexOf(startTag) + startTag.length();
		int end = document.indexOf(endTag);
		String result = document.substring(start,end);
		System.out.println("URL " + result + " " + result.length());
		return result;
		
	}

}
