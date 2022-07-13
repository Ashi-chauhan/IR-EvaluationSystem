package com.maynooth.project.IRSystem.Model;

import org.springframework.stereotype.Component;

@Component
public class FileDetails {
String fileName;
String fileType;

public FileDetails() {
	super();
}

public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public String getFileType() {
	return fileType;
}

public void setFilePath(String fileType) {
	this.fileType = fileType;
}

@Override
public String toString() {
	return "FileDetails [fileName=" + fileName + ", fileType=" + fileType + "]";
}

}
