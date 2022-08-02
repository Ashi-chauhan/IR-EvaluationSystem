package com.maynooth.project.IRSystem.controller;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.maynooth.project.IRSystem.Model.EvaluationDataRequest;
import com.maynooth.project.IRSystem.Service.TrecEvalService;
import com.maynooth.project.IRSystem.ServiceImpl.TrecEvalServiceImpl;

@RestController
public class TrecEvalResult {

	String[][] output;
	@Value("${spring.ir.aws.bucketname}")
	String bucketName;
	@Value("${spring.ir.base.file}")
	String filePath;
	@Value("${spring.ir.aws.accesskey}")
	String accessKey;
	@Value("${spring.ir.aws.secretkey}")
	String secretKey;
	
	@Autowired
	TrecEvalService tes;
	
	@CrossOrigin
	@PostMapping("/summaryresult")
	@ResponseStatus(code = HttpStatus.OK)
	public String summaryResultFinal(@RequestBody EvaluationDataRequest resultRequest) {
		tes = new TrecEvalServiceImpl();
		return tes.IREvaluation(true, resultRequest, true, bucketName, filePath,accessKey,secretKey);
		
	}

	@PostMapping("/detailedresult")
	@CrossOrigin
	@ResponseStatus(code = HttpStatus.OK)
	public String detailedResultFinal(@RequestBody EvaluationDataRequest resultRequest) {
		tes = new TrecEvalServiceImpl();
		return tes.IREvaluation(false, resultRequest, false, bucketName, filePath,accessKey,secretKey);
	}

	  @CrossOrigin
		@GetMapping("/download")
		public void downloadEvaluationResult(@RequestParam String id, HttpServletResponse response) {
		  response.setContentType("application/zip");
	        response.setHeader("Content-Disposition", "attachment; filename=download.zip");
	        System.out.print(id);
	        File file = new File(filePath + File.separator + id + File.separator +"downloads"+File.separator);
			File[] fileList = file.listFiles();
	        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
	            for(File fileName : fileList) {
	            	System.out.print(fileName.getPath());
	                FileSystemResource fileSystemResource = new FileSystemResource(fileName.getPath());
	                ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
	                zipEntry.setSize(fileSystemResource.contentLength());
	                zipEntry.setTime(System.currentTimeMillis());

	                zipOutputStream.putNextEntry(zipEntry);

	                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
	                zipOutputStream.closeEntry();
	            }
	            zipOutputStream.finish();
	        } catch (IOException e) {
	            System.out.print(e.getMessage());
	        }
	  
	  }
	  
}
