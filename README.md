# IR-EvaluationSystem

Download the zip from git repository
Unzip the folder and open in any IDE - like ecplise

---------------------------------------
Set up application.properties file

spring.ir.base.file=local file path
spring.ir.aws.bucketname= amazon s3 bucket name
spring.ir.aws.accesskey= amazon s3 bucket access key
spring.ir.aws.secretkey=amazon s3 bucket secret key

--------------------------------------

Rest API request is received on the Controller file - TrecEvalResult.java
It passes the request to TrecEvalServiceImpl.java to perform the business logic
It has 3 Rest api
1. SUmmaryresult
2. detailresult
3. download
---------------------------------------
After receiving a request, API first download the files from Amazon S3 bucket to the local system. The local path of the system is fetched from ‘spring.ir.base.file’ property in the application.properties file. 
A new folder is created with folder name same as id value. There are 3 more folders created inside id folder – qrel, result and download. ‘qrel’ folder has qrel file for the request. ‘result’ folder has all the result files received in the request body. ‘download’ folder has the files generated by backend to store the evaluation result for each result file. 
Storing file in local system is an important step since the jtreceval.jar needs the file to be in local path to access it. It cannot access the file directly from cloud location. Local system doesn’t only mean laptop or PC. If it is deployed in a server for use, the local path will be the path in the Operating System where application is deployed and has read, create and write access too. 
After saving the file, the local path is passed in the parameter of “runAndGetOutput” function of the trec_eval class. 'trec_eval’ is a class in the jtreceval.jar and “runAndGetOutput” is the function returning the evaluation result of qrel and result file. Only one qrel and one result file can be evaluated using the function. If multiple result file is received in the request, it will be passed one by one to the “runAndGetOutput” function. When all the result files are evaluated then the output is transformed in the JSON response. 

------------------------------------------------

The differences between summary result API and detailed result API are parameters passed in runAndGetOutput function and graphs data in response. To get the detailed result i.e query by query evaluation result, ‘-q’ is passed in the parameter list. The response contains graph data for only summary result api.

----------------------------------------------

Graphs data are nothing but the only data that needs to be displayed on the graph. Backend is already processing all the data and formatting it in the response so to it was easier to do in backend and save processing time on client side. Java enums are used to store the measures for which data needs to be displayed on graphs.

----------------------------------------------
Application can be tested from UI application present on link - https://github.com/Ashi-chauhan/IR_EVAL_UI.git

-----------------------------------------
API Details :

1. summaryresult 
url - http://localhost:8080/summaryresult
Request Method - POST
Sample Request - {
    "id":"unique id generated by frontend",// it is also the folder name from amazon s3 where qrel and result file are uploaded
    "fileDetailsList":[
        {"fileName":"qrel filename",
        "fileType":"qrel"
        },
         {"fileName":"result file name1",
        "fileType":"result"
        },
         {"fileName":"result filename 2",
        "fileType":"result"
        }
    ]
}

2. detailresult - http://localhost:8080/detailedresult
3. download - http://localhost:8080/download?id=uuidvalue
Request Method - Get
