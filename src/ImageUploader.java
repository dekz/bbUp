import java.io.File;

public abstract interface ImageUploader 
{
	public String uploadImage(File file) throws Exception;
	public String parser(String response);
}
