package mikepad;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EditingFile {
	private File file;
	private String directory;
	private String fileName;
	private String mimeType;

	public EditingFile(File file) {
		this.file = file;

		String path = file.getAbsolutePath();

		if (file.isFile()) {
			if (path.contains("/")) {
				int index = 1 + path.lastIndexOf('/');
				fileName = path.substring(index);
				directory = path.substring(0, index);
			}
		} else {
			fileName = path;
			directory = path;
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

	public String getDirectory() {
		return directory;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getPath() {
		return file.getAbsolutePath();
	}
}
