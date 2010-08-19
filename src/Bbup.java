import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import sun.tools.tree.LengthExpression;


public class Bbup 
{
	public ImageUploader uploader;
	public String fileName = "White Collar - 2x05 - Unfinished Business HDTV XviD FQM.avi";
	public File theFile;
	public String mediaInfoURI;
	public String announceURL;
	public String makeTorrentURI;
	public String screenGrabber;
	
	public String API_KEY;
	public String nameStripped;

	public File tempFolder; //where everything is copied to /temp/MOVIENAME
	public File mainTemp; //main temp file as in just /tmp/
	public File descriptionFile;
	public File givenDir; //originaldirname
	
	public File[] allFiles;
	public boolean moveFiles = false;
	public String moveTorrentTo;
	public String moveFilesTo;
	
	public boolean isDir;
	
	private boolean uploadImages = false;
	public int imageCount =3;
	public double duration; //preferably in seconds
	
	public VideoFilter filter;
	
	
	public Bbup(String[] args) 
	{
		this.fileName = args[0];
		load();
		
		if (args.length > 1)
		{
			for (int i = 1; i < args.length; i++) 
			{
				if (args[i].equals("-c")) moveFiles = true;
				else if (args[i].equals("-u")) uploadImages = true;
			}
		}
		System.out.println("BBUP! \n[ARGS] Given Filename: " + args[0]);
		System.out.println("[ARGS] Move Files: " + moveFiles +"\n" + "[ARGS] Upload Images: " + uploadImages);
		
		System.out.println("[INFO] Using file: " + checkFile(args[0]));
		setUpFolders();
	}

	public static void main(String[] args) 
	{
		
		if (args.length == 0) System.out.println("Usage: <filename> <options>");
		

		
		Bbup bbup = new Bbup(args);
		bbup.run();
		
		//Takes 1 arg file name
		//if (args.length > 1) return;
		//Load various info 
		
	}

	public void load()
	{
		loadSettings();
	}
	
	
	public void runTasks()
	{
		long timer = System.currentTimeMillis();

		copyAllFiles();
		System.out.println("[INFO] File Copy: " + (System.currentTimeMillis() - timer)/1000 + " seconds");
		buildDescription();
		
		
		timer = System.currentTimeMillis();
		File test = new File(tempFolder.getAbsoluteFile() + File.separator + theFile.getName() );
		ArrayList<String> images = generateAllImages(theFile);
		if (uploadImages) 
		{
			System.out.println("[INFO] Uploading images to IMGUR -- This could take some time depending on your connection");
			uploadAllImages(images);
			System.out.println("[INFO] Imgur took: " + (System.currentTimeMillis() - timer)/1000);
		}
		
		
		//System.out.println("Generating Torrent File for " + tempFolder.getAbsolutePath());
		generateTorrentFile(tempFolder);
		
		if (moveFiles)
		{
			moveAllFiles();
		}
		
		spewOutDescription();
	}

	public void loadSettings()
	{
		StringBuffer sb = new StringBuffer();
		FileInputStream fis;
		
		try 
		{
			fis = new FileInputStream(new File("config.txt"));
			BufferedReader br = new BufferedReader( new InputStreamReader(fis));
			Reader in = new InputStreamReader(fis);
			int c;
			while ((c = in.read()) != -1)
			{
				sb.append((char) c);
			}
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		mediaInfoURI = parse("mediainfo=", sb.toString());
		makeTorrentURI = parse("torrentgen=", sb.toString());
		API_KEY = parse("apikey=", sb.toString());
		announceURL = parse("announceurl=", sb.toString());
		screenGrabber = parse("screengrabber=", sb.toString());
		moveTorrentTo = parse("moveTorrentTo=", sb.toString());
		moveFilesTo = parse("moveFilesTo=", sb.toString());
		uploader = new Imgur(API_KEY);
		


	}
	
	public void setUpFolders()
	{
		nameStripped = stripName();
		//theFile = new File(fileName);
		System.out.println("[INFO] Name stripped: " + nameStripped);
		
	//	mainTemp = new File("tmp" + File.separator);
		//mainTemp.mkdirs();
	//	tempFolder = new File(mainTemp.getAbsolutePath() + File.separator + nameStripped + File.separator);
		
		System.out.println("[FILE] Temp Folder: " + tempFolder.getAbsolutePath());
		
		tempFolder.mkdirs();
		
		descriptionFile = new File(mainTemp.getAbsoluteFile() + File.separator + theFile.getName() + ".txt");

		
		//if (mainTemp.mkdirs() && tempFolder.mkdirs()); //System.out.println("Created tmp dir");
	}

	public void run()
	{
		this.load();
		this.runTasks();
	}
	
	public File findFile(File f)
	{
		if (!f.isDirectory()) return null;
		File[] possibles;
		if (filter == null) loadFilter();
		possibles = f.listFiles(filter);
		File found = null;
		if (possibles.length > 0)
		{
			found = possibles[0];
			for (int i = 0; i < possibles.length; i++) 
			{
				if (!possibles[i].getName().contains("sample")) found = possibles[i];
			}
		}
		else
		{
			File currentDir;
			possibles = f.listFiles();
			for (int i = 0; i < possibles.length; i++) 
			{
				if (possibles[i].isDirectory())
				{
					System.out.println("[INFO] Looking in: " + possibles[i].getName());

					found = findFile(possibles[i]);
					if (found != null) break;
				}
			}
		}
		//System.out.println("FOUND: " + found.getName());
		return found;
	}
	
	public String checkFile(String name)
	{
		mainTemp = new File("tmp" + File.separator);
		
		
		File f = new File(name);
		if (f.exists())
		{
			if (f.isDirectory())
			{
				allFiles = f.listFiles();
				
				theFile = findFile(f);
				
				//System.out.println("BLEH: " + theFile.getAbsolutePath());
				
				nameStripped = stripName();
				isDir = true;
				givenDir = f;
				tempFolder = new File(mainTemp.getAbsolutePath() + File.separator + f.getName() + File.separator);
				return theFile.getName();
			} else if (f.isFile())
			{
				//check for parental files
				theFile = new File(name);
				File parent = theFile.getParentFile();
				nameStripped = stripName();
				
				if (parent != null)
				{
					
					theFile = new File(parent.getAbsoluteFile() + File.separator + theFile.getName());
					nameStripped = stripName();
					tempFolder = new File(mainTemp.getAbsolutePath() + File.separator  + nameStripped + File.separator);
				} else
				{
					//System.out.println("Given a video file");
					//just a video file
					tempFolder = new File(mainTemp.getAbsolutePath() + File.separator + nameStripped + File.separator);
				}
				
				
				
				allFiles =  new File[] { theFile };
				return name;
			}
		}
		return "NONE";
	}
	
	public void loadFilter()
	{
		filter = new VideoFilter();
		
		filter.addAccepted(".avi");
		filter.addAccepted(".mkv");
	}
	
	private void copy(File src, File dest)
	{
		if (dest.isDirectory() && !dest.exists()) dest.mkdirs();
		
		
		if (src.isDirectory())
		{
			File[] localFiles = src.listFiles();
			String[] localNames = src.list();
			if (!dest.exists()) dest.mkdirs();
			for (int i = 0; i < localNames.length; i++) 
			{
				copy(new File(src, localNames[i]), new File(dest, localNames[i]));
			}
			
		} else
		{
			try 
			{
				int len;
				//if (!dest.exists()) System.out.println("NO dest: " + dest.getAbsolutePath());
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));
				byte[] buffer = new byte[65536];
				
				while ((len = bis.read(buffer)) != -1)
				{
					bos.write(buffer, 0, len);
				}
				
				bis.close();
				bos.close();
				
			} catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		

	}
	
	public void copyAllFiles()
	{
		File f;
		File f2;
		File temp;
		for (int i = 0; i < allFiles.length; i++) 
		{
			temp = allFiles[i];
			f = temp;
			
			f2 = new File(tempFolder.getAbsolutePath() + File.separator + allFiles[i].getName());
			copy(f, f2);
		}
	}
	
	public void moveAllFiles()
	{
		System.out.println("Moving files");
		File torrentFile = new File(tempFolder.getAbsolutePath() + ".torrent");
		File torrentDir = new File(moveTorrentTo);
		moveFile(torrentFile, torrentDir);
		File filesDir = new File(moveFilesTo);
		moveFile(tempFolder, filesDir);
		
	}
	
	private boolean moveFile(File f, File dir)
	{
		if (!dir.exists()) dir.mkdirs();
		return f.renameTo(new File(dir, f.getName()));
	}
	
	
	public ArrayList<String> generateAllImages(File in)
	{
		
		ArrayList<String> images = new ArrayList<String>();
		String name;
		for (int i = 0; i < imageCount; i++) 
		{
			//name = mainTemp.getAbsolutePath() + File.separator + theFile.getName() + Integer.toString(i) + ".png";
			name = mainTemp.getAbsolutePath() + File.separator  + nameStripped + Integer.toString(i) + ".png";
			System.out.println(name);
			
			int marker = (int) (duration * ((i+1)/10));
			int test =  (int)(duration/15) * (i+1);
			
			System.out.println("DUR " + duration + "marker " + test);
			if (marker <= 0) marker = i * 10 + 10;
			System.out.println("[FILE] IMAGE: " + name + " " + test + " seconds in");
			generateScreenCap(in.getAbsolutePath(), name, test);
			images.add(name);
		}
		return images;
		
	}
	
	public void uploadAllImages(ArrayList<String> images)
	{
		FileOutputStream fos;

		try 
		{
			fos = new FileOutputStream(descriptionFile ,true);
			fos.write("[quote]".getBytes());

			ArrayList<String> urls = uploadImages(images);
			String url;
			for (int i = 0; i < urls.size(); i++) 
			{
				url = urls.get(i);
				System.out.println(url);
				fos.write("[img]".getBytes());
				fos.write(url.getBytes());
				fos.write("[/img]".getBytes());
				fos.flush();
			}

			fos.write("[/quote]".getBytes());
			fos.close();

			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
	}
	
	public String generateTorrentFile(File f)
	{
		System.out.println("[FILE] TORRENT: " + mainTemp.getAbsolutePath() + File.separator + nameStripped + ".torrent");
		String[] mkTorrentArgs = new String[] {makeTorrentURI, "-p", "-v" , "-a", announceURL, "-o", mainTemp.getAbsolutePath() + File.separator + nameStripped + ".torrent", f.getAbsolutePath() };
		return runCmd(mkTorrentArgs);
	}
	
	public String generateScreenCap(String in, String out, int seconds)
	{
		String[] arghhhh = new String[] {screenGrabber, "-ss", Integer.toString(seconds),"-i", in, "-f", "image2", "-vframes", "1", out};
		return runCmd(arghhhh);
	}
	
	public String grabMediaInfo(File mediaFile)
	{
		String[] arghhhh = new String[] {mediaInfoURI, "-pretty", mediaFile.getAbsolutePath(), "-show_streams"};
		return runCmd(arghhhh);
	}
	
	public ArrayList<String> uploadImages(ArrayList<String> filenames) throws Exception
	{
		ArrayList<String> al = new ArrayList<String>();
		
		for (String filename : filenames) 
		{
			File file = new File(filename);
			System.out.println("Attempting to upload " + filename);
			String url = uploader.parser(uploader.uploadImage(file));
			al.add(url);
		}
		return al;
	}
	
	
	public String runCmd(String[] args)
	{
		StringBuffer output = new StringBuffer();
		String s;

		//System.out.println("Running command " + args[0]);
		try 
		{
			Process p = Runtime.getRuntime().exec(args);

			BufferedReader stdInput = new BufferedReader(new 
			InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
			InputStreamReader(p.getErrorStream()));
			
			while ((s = stdInput.readLine()) != null) 
			{
				//System.out.println(s);
				output.append(s);
				output.append('\n');
			}
			
			while ((s = stdError.readLine()) != null) 
			{
				System.out.println(s);
			}
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return output.toString();

	}
	
	private void getDuration(String dura)
	{
		String[] culled = dura.split("\\.");
		StringTokenizer st = new StringTokenizer(culled[0], ":");
		String hours = st.nextToken();
		String mins = st.nextToken();
		String secs = st.nextToken();
		duration = Double.parseDouble(hours)*60*60 + Double.parseDouble(mins)*60 + Double.parseDouble(secs);
		//System.out.println(duration);
	}
	
	private void spewOutDescription()
	{
		System.out.println("############# DESCRIPTION ##############");
		byte[] buffer = new byte[1024];
		int l;
		try 
		{
			FileInputStream in = new FileInputStream(descriptionFile);
			BufferedInputStream bin = new BufferedInputStream(in);
			while((l = bin.read(buffer, 0, 1024)) != -1)
			{
				System.out.println(new String(buffer));
			}
			
		System.out.println("############# END ##############");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void buildDescription()
	{
		StringBuilder sb = new StringBuilder();
		try 
		{
			FileOutputStream fos = new FileOutputStream(descriptionFile);
			System.out.println("[FILE] Description: " + descriptionFile.getAbsolutePath() + " for " + tempFolder.getAbsolutePath() +  File.separator  + theFile.getName());
			fos.write("[quote]".getBytes());
			//sb.append(grabMediaInfo(new File(tempFolder.getAbsolutePath() +  File.separator  + theFile.getName())));
			sb.append(grabMediaInfo(theFile.getAbsoluteFile()));
			fos.write(sb.toString().getBytes());
			fos.write("[/quote]".getBytes());
			fos.flush();
			fos.close();
			
			String startTag = "duration=";
			String endTag = "nb_frames=";

			
			
			int start = sb.indexOf(startTag) + startTag.length();
			int end = sb.indexOf(endTag);
			String duration = sb.substring(start, end);
			getDuration(duration);
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	
	private String parse(String start, String document) 
	{
		int begin = document.indexOf(start) + start.length();
		int end = document.indexOf('\n', begin);
		int length = document.length();
		return document.substring(begin, (end <= length) ? end : length);
	}
	
	
	private String stripName() 
	{
		StringBuilder sp = new StringBuilder();
		String[] parts = fileName.split("\\.");
		
		String[] noFolders = parts[0].split("\\/");
		//parts[0] = parts[0].replace("/", "");
		return noFolders[noFolders.length-1];
	}

 class NoVideoException extends Exception
 {
	 
 }
	
}
