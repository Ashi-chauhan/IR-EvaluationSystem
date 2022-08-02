package com.maynooth.project.IRSystem.Service;

import javax.servlet.http.HttpServletResponse;

import com.maynooth.project.IRSystem.Model.EvaluationDataRequest;

public interface TrecEvalService {
	
	public String IREvaluation(Boolean isGraph, EvaluationDataRequest resultRequest, Boolean isSummary, String bucketName, String filePath,String accesskey, String secretKey);
	public void download(String id, HttpServletResponse response);
}
