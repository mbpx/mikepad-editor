package mikedit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class EditingFile {
	private File file;
	private String fileName;
	private String dirPath;
	private String mimeType;
	
	public EditingFile(File file) {
		this.file = file;
		
		String path = file.getAbsolutePath();
		for (int i = path.length() - 1; i > 0; i--) {
			if (path.charAt(i) == '/') {
				i++;
				fileName = path.substring(i);
				dirPath = path.substring(0, i);
				break;
			}
		}
		
		try {
			mimeType = Files.probeContentType(file.toPath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public String getDirPath() {
		return dirPath;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	public String getFullPath() {
		return file.getAbsolutePath();
	}
}
