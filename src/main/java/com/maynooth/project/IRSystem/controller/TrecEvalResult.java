package com.maynooth.project.IRSystem.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.maynooth.project.IRSystem.Model.EvaluationDataRequest;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.maynooth.project.IRSystem.Model.FileDetails;
import com.maynooth.project.IRSystem.Model.TableData;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import uk.ac.gla.terrier.jtreceval.trec_eval;

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
	//trec_eval t = new trec_eval();

	@PostMapping("/summaryresult")
	@ResponseStatus(code = HttpStatus.OK)
	public String summaryResultFinal(@RequestBody EvaluationDataRequest resultRequest) {
		saveFiles(resultRequest);
		File qrelfile = new File(filePath + "\\" + resultRequest.getId() + "\\qrel\\");
		File resultfile = new File(filePath + "\\" + resultRequest.getId() + "\\result\\");
		File[] fileList = resultfile.listFiles();
		File qrel = qrelfile.listFiles()[0];
		System.out.println("result file length : " + fileList.length);
		System.out.println(qrel.getPath());
		// String qrelFilePath = qrel.getPath();
		List<String> header = new ArrayList<>();
		List<HashMap<String, List<TableData>>> metaData = new ArrayList<>();
		int dataCount = 1;
		for (File f : fileList) {
			System.out.println(f.getPath());
			String[][] result = summaryResultEval(qrel.getPath(), f.getPath(), resultRequest.getId());

			if (dataCount == 1) {
				header.add("Measure");

			}
			header.add(f.getName());
			for (int i = 1; i < result.length; i++) {
				List<TableData> tableData = new ArrayList<>();
				HashMap<String, List<TableData>> row = new HashMap<>();
				if (dataCount == 1) {
					tableData.add(new TableData(result[i][2]));
					row.put(result[i][0], tableData);
					metaData.add(row);
				} else {
					tableData.addAll(metaData.get(i - 1).get(result[i][0]));
					tableData.add(new TableData(result[i][2]));
					if(dataCount == fileList.length) {
						tableData.stream().min(Comparator.comparing(TableData::getValue)).get().setMinValue(true);
						tableData.stream().max(Comparator.comparing(TableData::getValue)).get().setMaxValue(true);;
					}
					row.put(result[i][0], tableData);
					metaData.get(i - 1).replace(result[i][0], tableData);

				}

			}
			dataCount++;

		}
		// System.out.println(metaData);
		JSONObject finalResponse = new JSONObject();
		finalResponse.put("header", header);
		finalResponse.put("metaData", metaData);
		return finalResponse.toString();
	}

	public String[][] summaryResultEval(String qrel, String resFile, String id) {
		System.out.println("evaluate start");
		String[] args = new String[] { qrel, resFile };
		output = new trec_eval().runAndGetOutput(args);
		generateDownloadFiles(id, resFile.substring(resFile.lastIndexOf('\\')+1));
		System.out.println("evaluate end");
		return output;
	}
	@PostMapping("/detailedresult")
	@ResponseStatus(code = HttpStatus.OK)
	public String detailedResultFinal(@RequestBody EvaluationDataRequest resultRequest) {
		saveFiles(resultRequest);
		File qrelfile = new File(filePath + "\\" + resultRequest.getId() + "\\qrel\\");
		File resultfile = new File(filePath + "\\" + resultRequest.getId() + "\\result\\");
		File[] fileList = resultfile.listFiles();
		File qrel = qrelfile.listFiles()[0];
		System.out.println("result file length : " + fileList.length);
		System.out.println(qrel.getPath());
		// String qrelFilePath = qrel.getPath();
		List<String> header = new ArrayList<>();
		List<HashMap<String, List<TableData>>> metaData = new ArrayList<>();
		int dataCount = 1;
		for (File f : fileList) {
			System.out.println(f.getPath());
			String[][] result = detailedResultEval(qrel.getPath(), f.getPath(), resultRequest.getId());

			if (dataCount == 1) {
				header.add("Measure");

			}
			header.add(f.getName());
			for (int i = 1; i < result.length; i++) {
				List<TableData> tableData = new ArrayList<>();
				HashMap<String, List<TableData>> row = new HashMap<>();
				if (dataCount == 1) {
					tableData.add(new TableData(result[i][2]));
					row.put(result[i][0], tableData);
					metaData.add(row);
				} else {
					tableData.addAll(metaData.get(i - 1).get(result[i][0]));
					tableData.add(new TableData(result[i][2]));
					
					if(dataCount == fileList.length) {
						tableData.stream().min(Comparator.comparing(TableData::getValue)).get().setMinValue(true);
						tableData.stream().max(Comparator.comparing(TableData::getValue)).get().setMaxValue(true);;
					}
					row.put(result[i][0], tableData);
					metaData.get(i - 1).replace(result[i][0], tableData);
				}

			}
			dataCount++;

		}
		// System.out.println(metaData);
		JSONObject finalResponse = new JSONObject();
		finalResponse.put("header", header);
		finalResponse.put("metaData", metaData);
		return finalResponse.toString();
	}

	  public String[][] detailedResultEval(String qrel, String resFile, String id) {
	  //logger.info("Evaluating result file: "+resultFilename);
	  System.out.println("evaluate start"); 
	  String[] args = new String[]{"-q",qrel, resFile}; 
	  output = new trec_eval().runAndGetOutput(args); 
	  generateDownloadFiles(id, resFile.substring(resFile.lastIndexOf('\\')+1));
	  
	  System.out.println("evaluate end");
	  return output;
	  }
	  
	
	  public void generateDownloadFiles(String id, String fileName) {
		 
		  S3Client client =getAwsClient();
		  String path = filePath + "\\" + id + "\\downloads\\";
			File file = new File(path);
			file = new File(path);
			try {
				if (file.mkdirs() || file.isDirectory()) {
					BufferedWriter writer = new BufferedWriter(
								new FileWriter(filePath + "\\" + id + "\\downloads\\" + fileName));

						System.out.println("evaluate try start");
						for (String[] line : output) {
							for (String word : line) {

								writer.append(word + " ");// save the string representation of the board
							}
							writer.append(",\n");
						}
						writer.close();

					}
				PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(id+"/download/"+fileName).build();
					client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(path+ fileName)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("evaluate exception");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e) {
				System.out.print(e.getMessage());
			}
			finally {
				//file.deleteOnExit();
			}
	  }
	  
	/*  public Path createTempFile(String fileName) {
		  try {
			Path path = Files.createTempFile(fileName, ".txt");
			System.out.print(path.toString());
			return path;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		  return null;
	  }*/
	  
	  public S3Client getAwsClient() {
		  
		  S3Client client = S3Client.builder().build();
			
			return client;
	  }
	  
	  public void saveFiles( EvaluationDataRequest summaryDetailQuery) {
			System.out.println("save file :");
			List<FileDetails> fileDetailsList = summaryDetailQuery.getFileDetailsList();
			S3Client client =getAwsClient();
			
			try {
				for (FileDetails fd : fileDetailsList) {
//					S3Object s3object = client.getObject(bucketName, summaryDetailQuery.getId()+"/"+fd.getFileType()+"/"+fd.getFileName());
//					S3ObjectInputStream inputStream = s3object.getObjectContent();
//					
					GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			                .bucket(bucketName)
			                .key(summaryDetailQuery.getId()+"/"+fd.getFileType()+"/"+fd.getFileName())
			                .build();
					ResponseInputStream<GetObjectResponse> inputStream = client.getObject(getObjectRequest);
					File targetFile = new File(filePath +"\\"+ summaryDetailQuery.getId() + "\\"+fd.getFileType());

					if(targetFile.mkdirs() || targetFile.isDirectory()) {
						targetFile = new File(targetFile.getPath()+"\\"+fd.getFileName());
						targetFile.createNewFile();
					//Path path = createTempFile(fd.getFileName());
					byte[] buf = new byte[1024];
				    OutputStream out = new FileOutputStream(targetFile);
				    int count;
				    while( (count = inputStream.read(buf)) != -1)
				    {
				       if( Thread.interrupted() )
				       {
				           throw new InterruptedException();
				       }
				       out.write(buf, 0, count);
				    }
				    out.close();
				    inputStream.close();

				}
				}
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}
			
		}
}
