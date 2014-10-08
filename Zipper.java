import java.io.*;
import java.util.*;

public class Zipper {
	
	// Instance Variables
	int location;
	
	public static void main(String [] args) throws Exception {
		String method = args[0];
		String target = args[1];
		String destination = args[2];
		if (method.equals("zipper")){
			Zipper.zipper(target,destination);
		}else if (method.equals("unzipper")){
			Zipper.unzipper(target, destination);
		}else{
			throw new IllegalArgumentException("No method for "+ method);
		}
		
	}

	private Zipper() {
		location = 0;
	}
	
	public static File zipper(String targetDir, String destinationFile) throws Exception {
		Zipper z = new Zipper();
		File destination = new File(destinationFile);
		File startDir = new File(targetDir);
		if (!startDir.isDirectory()) {
			throw new IllegalArgumentException("encode must take in a directory");
		}
		StringBuilder toAdd = new StringBuilder();
		FileOutputStream output = new FileOutputStream(destination);
		//File[] files = startDir.listFiles();
		String toPrint = z.writeContents(startDir, output, toAdd, startDir.getName());
		output.write(10);
		FileOutputHelper.writeBinStrToFile(toPrint, destinationFile);
		output.close();
		return destination;
	}
	
	//TODO: implement unzipper and needed helper methods
	public static File unzipper(String targetFile, String destinationDir) throws IOException {
		File ret = new File(destinationDir);
		ret.mkdir();
		FileCharIterator iter = new FileCharIterator(targetFile);
		//HashMap<String, Integer> hash = new HashMap<String, Integer>();
		Zipper z = new Zipper();
		ArrayList<FileMap> fileLocations = new ArrayList<FileMap>();
		String prev = "";
		String curr = "";
		boolean topDirectoryRenamed = false;
		StringBuilder charString = new StringBuilder(); 
		while(!prev.equals("00001010") || !curr.equals("00001010")) {
			prev = curr;
			curr = iter.next();
			if (curr.equals("00101111") && !topDirectoryRenamed) {
				topDirectoryRenamed = true;
			} else if (curr.equals("00001010") && charString.length() > 0) {
				int commaLocation = charString.indexOf(",");
				String fileName = destinationDir + "/" + charString.substring(0, commaLocation);
				String fileLocation = charString.substring(commaLocation + 1);
				//hash.put(fileName, Integer.parseInt(fileLocation));
				fileLocations.add(z.new FileMap(fileName, Integer.parseInt(fileLocation)));
				charString = new StringBuilder();
				topDirectoryRenamed = false;
			} else if (topDirectoryRenamed) {
				charString.append((char)Integer.parseInt(curr, 2));
			}
		}
		long count = 0;
		for (int i = 0; i < fileLocations.size(); i++) {
			if (fileLocations.get(i).myLocation == -1) {
				File temp = new File(fileLocations.get(i).myName);
			} else {
				while (count < fileLocations.get(i).myLocation) {
					iter.next();
				}
				File toCopy = new File("toCopy.txt");
				int k = i + 1;
				long toStop = (new File(targetFile)).length();
				while (k < fileLocations.size()) {
					if (fileLocations.get(k).myLocation != -1) {
						toStop = fileLocations.get(k).myLocation;
						break;
					}
					k++;
				}
				StringBuilder toAdd = new StringBuilder();
				while (count < toStop) {
					toAdd.append(iter.next());
					count ++;
				}
				//FileOutputStream fos = new FileOutputStream("toCopy.txt");
				//fos.write(toAdd.toString().getBytes());
				FileOutputHelper.writeBinStrToFile(toAdd.toString(), "toCopy.txt");
				HuffmanEncoding.decode("toCopy.txt", fileLocations.get(i).myName);
				toCopy.delete();
			}
		}
		return ret;
	}
	
	
	String writeContents(File target, FileOutputStream output, StringBuilder toAdd, String parentDirs) 
			throws IOException{
		File[] files = target.listFiles();
		for(File myFile : files){
			if (myFile.isDirectory()) {
				output.write((parentDirs + "/" + myFile.getName() + "," + -1).getBytes());
				output.write(10);
				toAdd.append(writeContents(myFile, output, toAdd, parentDirs + "/" + myFile.getName()));
			} else {
				// writes the table of contents
				output.write((parentDirs + "/" + myFile.getName() + "," + location).getBytes());
				output.write(10);
				// builds up our string builder, which will be written to the file at the end.  
				HuffmanEncoding.encode(parentDirs + "/" + myFile.getName(), "temp.txt");
				FileCharIterator iter = new FileCharIterator("temp.txt");
				while (iter.hasNext()) {
					toAdd.append(iter.next());
				}
				File temp = new File("temp.txt");
				location += temp.length();
				temp.delete();
			}
		}
		return toAdd.toString();
	}
	
	class FileMap {
		String myName;
		int myLocation;
		
		FileMap(String name, int location) {
			myName = name;
			myLocation = location;
		}
	}
}
