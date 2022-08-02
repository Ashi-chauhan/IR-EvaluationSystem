package com.maynooth.project.IRSystem.ServiceImpl;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.maynooth.project.IRSystem.Model.EvaluationDataRequest;
import com.maynooth.project.IRSystem.Model.FileDetails;
import com.maynooth.project.IRSystem.Model.GraphData;
import com.maynooth.project.IRSystem.Model.TableData;
import com.maynooth.project.IRSystem.Service.TrecEvalService;

import uk.ac.gla.terrier.jtreceval.trec_eval;

@Component
public class TrecEvalServiceImpl implements TrecEvalService {

	String[][] output;
	private String bucketName;
	private String filePath;
	private String accessKey;
	private String secretKey;

	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	//trec_eval t = new trec_eval();
	enum graph1Xaxis{P_5, P_10, P_15, P_100, P_200};
	enum graph2Xaxis{num_q, num_rel, num_rel_ret, map};

	@Override
	public String IREvaluation(Boolean isGraph, EvaluationDataRequest resultRequest, Boolean isSummary, String bucketName, String filePath
			,String accesskey, String secretKey) {
		setBucketName(bucketName);
		setFilePath(filePath);
		setAccessKey(accesskey);
		setSecretKey(secretKey);
		saveFiles(resultRequest);
		
		return EvaluateResponse(isGraph , resultRequest,isSummary);
	}
	public String[][] summaryResultEval(String qrel, String resFile, String id) {
		System.out.println("evaluate start");
		String[] args = new String[] { qrel, resFile };
		output = new trec_eval().runAndGetOutput(args);
		generateDownloadFiles(id, resFile.substring(resFile.lastIndexOf(File.separator)+1));
		System.out.println("evaluate end");
		return output;
	}
	  public String[][] detailedResultEval(String qrel, String resFile, String id) {
		  //logger.info("Evaluating result file: "+resultFilename);
		  System.out.println("evaluate start"); 
		  String[] args = new String[]{"-q",qrel, resFile}; 
		  output = new trec_eval().runAndGetOutput(args); 
		  generateDownloadFiles(id, resFile.substring(resFile.lastIndexOf(File.separator)+1));
		  
		  System.out.println("evaluate end");
		  return output;
		  }
		  
		
		  public void generateDownloadFiles(String id, String fileName) {
			 
			 // S3Client client =getAwsClient();
			  String path = filePath + File.separator + id +File.separator +"downloads"+File.separator;
				File file = new File(path);
				file = new File(path);
				try {
					if (file.mkdirs() || file.isDirectory()) {
						BufferedWriter writer = new BufferedWriter(
									new FileWriter(getFilePath() + File.separator + id + File.separator+"downloads"+File.separator + fileName));

							System.out.println("evaluate try start");
							for (String[] line : output) {
								for (String word : line) {

									writer.append(word + " ");// save the string representation of the board
								}
								writer.append(",\n");
							}
							writer.close();

						}
					//PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(id+"/download/"+fileName).build();
					//	client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(path+ fileName)));
				} catch (FileNotFoundException e) {
					
					System.out.println("evaluate exception");
					e.printStackTrace();
				} catch (IOException e) {
					
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
		  
		  public AmazonS3 getAwsClient() {
			  
			  AWSCredentials credentials = new BasicAWSCredentials(
					  getAccessKey(), 
					  getSecretKey()
					);
			  AmazonS3 client = AmazonS3ClientBuilder
					  .standard()
					  .withCredentials(new AWSStaticCredentialsProvider(credentials))
					  .withRegion(Regions.EU_WEST_1)
					  .build();
				return client;
		  }
		  
		  public void saveFiles( EvaluationDataRequest summaryDetailQuery) {
				System.out.println("save file :");
				List<FileDetails> fileDetailsList = summaryDetailQuery.getFileDetailsList();
				AmazonS3 client =getAwsClient();
				
				try {
					for (FileDetails fd : fileDetailsList) {
						S3Object s3object = client.getObject(bucketName, summaryDetailQuery.getId()+"/"+fd.getFileType()+"/"+fd.getFileName());
						S3ObjectInputStream inputStream = s3object.getObjectContent();
						
						File targetFile = new File(getFilePath() +File.separator+ summaryDetailQuery.getId() + File.separator+fd.getFileType());

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
		  public String EvaluateResponse(Boolean isGraph, EvaluationDataRequest resultRequest, Boolean isSummary) {
			  File qrelfile = new File(getFilePath() + File.separator + resultRequest.getId() + File.separator+"qrel"+File.separator);
				File resultfile = new File(getFilePath() + File.separator + resultRequest.getId()+File.separator + "result"+File.separator);
				File[] fileList = resultfile.listFiles();
				File qrel = qrelfile.listFiles()[0];
				System.out.println("result file length : " + fileList.length);
				System.out.println(qrel.getPath());
				// String qrelFilePath = qrel.getPath();
				List<String> header = new ArrayList<>();
				List<GraphData> graph1Data = new ArrayList<>();
				List<GraphData> graph2Data = new ArrayList<>();
				List<HashMap<String, List<TableData>>> metaData = new ArrayList<>();
				int dataCount = 1;
				for (File f : fileList) {
					System.out.println(f.getPath());
					String[][] result ;
					if(isSummary)
							result = summaryResultEval(qrel.getPath(), f.getPath(), resultRequest.getId());
					
					else
						result = detailedResultEval(qrel.getPath(), f.getPath(), resultRequest.getId());
					if (dataCount == 1) {
						header.add("Measure");

					}
					header.add(f.getName());
					List<Double> data1 = new ArrayList<>();
					List<Double> data2 = new ArrayList<>();
					for (int i = 1; i < result.length; i++) {
						List<TableData> tableData = new ArrayList<>();
						HashMap<String, List<TableData>> row = new HashMap<>();
						if (dataCount == 1) {
							tableData.add(new TableData(result[i][2]));
							row.put(result[i][0], tableData);
							metaData.add(row);
							if(EnumUtils.isValidEnum(graph1Xaxis.class, result[i][0]) && isGraph) {
								data1.add(Double.parseDouble(result[i][2]));
							}
							if(EnumUtils.isValidEnum(graph2Xaxis.class, result[i][0]) && isGraph) {
								data2.add(Double.parseDouble(result[i][2]));
							}
						} else {
							tableData.addAll(metaData.get(i - 1).get(result[i][0]));
							tableData.add(new TableData(result[i][2]));
							if(dataCount == fileList.length) {
								tableData.stream().min(Comparator.comparing(TableData::getValue)).get().setMinValue(true);
								tableData.stream().max(Comparator.comparing(TableData::getValue)).get().setMaxValue(true);;
							}
							row.put(result[i][0], tableData);
							metaData.get(i - 1).replace(result[i][0], tableData);
							if(EnumUtils.isValidEnum(graph1Xaxis.class, result[i][0]) && isGraph) {
								data1.add(Double.parseDouble(result[i][2]));
							}
							if(EnumUtils.isValidEnum(graph2Xaxis.class, result[i][0]) && isGraph) {
								data2.add(Double.parseDouble(result[i][2]));
							}

						}

					}
					if(isGraph) {
					GraphData gd1 =  new GraphData(f.getName(), data1);
					GraphData gd2 =  new GraphData(f.getName(), data2);
					graph1Data.add(gd1);
					graph2Data.add(gd2);
					}
					dataCount++;

				}
				// System.out.println(metaData);
				JSONObject finalResponse = new JSONObject();
				finalResponse.put("header", header);
				finalResponse.put("metaData", metaData);
				if(isGraph) {
				finalResponse.put("graph1Data", graph1Data);
				finalResponse.put("graph2Data", graph2Data);
				}
				return finalResponse.toString();
		  }
		  
		  @Override
		  public void download(String id, HttpServletResponse response) {
			  response.setContentType("application/zip");
		        response.setHeader("Content-Disposition", "attachment; filename=download.zip");
		        System.out.print(id);
		        File file = new File(getFilePath() + File.separator + id + File.separator +"downloads"+File.separator);
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
