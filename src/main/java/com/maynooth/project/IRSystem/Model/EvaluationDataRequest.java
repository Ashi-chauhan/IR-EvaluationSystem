package com.maynooth.project.IRSystem.Model;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EvaluationDataRequest {
	List<FileDetails> fileDetailsList;
	String id;

	public String getId() {
		return id;
	}
	public void setId(String uniqueId) {
		this.id = uniqueId;
	}
	
	public EvaluationDataRequest() {
		super();
	}
	public List<FileDetails> getFileDetailsList() {
		return fileDetailsList;
	}
	public void setFileDetailsList(List<FileDetails> fileDetailsList) {
		this.fileDetailsList = fileDetailsList;
	}
	@Override
	public String toString() {
		return "SummaryDetailQuery [fileDetailsList=" + fileDetailsList + ", id=" + id + "]";
	}

}
