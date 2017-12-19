# HyperVerge Documents - API Documentation

## Overview

This documentation describes hyperdocs API v1. If you have any queries please contact support. The postman collection can be found at this [link](https://www.getpostman.com/collections/2841e059b5c4e2457090)

1. Schema
1. Parameters
1. Root Endpoint
1. Authentication
1. Media Types
3. Supported Endpoints
3. Supported kyc_types 
2. Optional Parameters
3. API wrappers and sample code snippets (Beta) 


## Schema

We recommend using HTTPS for all API access. All data is received as JSON, and all image uploads are to be performed as form-data (POST request). Incase of a pdf file input, the key name has to be `pdf` and in all other cases, the key name for the image could be anything apart from `pdf`.

## Parameters
All optional and compulsory parameters are passed as part of the request body.

## Root Endpoint
A `GET` request can be issued to the root endpoint to check for successful connection : 

	 curl https://docs.hyperverge.co/v1 

The `plain/text` reponse of `"AoK!"` should be received.

## Authentication

Currently, a simple appId, appKey combination is passed in the request header. The appId and appKey are provided on request by the HyperVerge team. If you would like to try the API, please reach out to contact@hyperverge.co

	curl -X POST http://docs.hyperverge.co/v1/readKYC \
	  -H 'appid: xxx' \
	  -H 'appkey: yyy' \
	  -H 'content-type: multipart/form-data;' \
	  -F 'image=@abc.png' 


On failed attempt with invalid credentials or unauthorized access the following error message should be received :

	{
	  "status": "failure",
	  "statusCode": "401",
	  "error": {
	    "developerMessage": "unauthorized",
	  }
	}

Please donot expose the appid and appkey on browser applications. In case of a browser application, set up the API calls from the server side.

## Media Types

Currently, `jpeg, png and tiff` images and `pdf` are supported by the HyperDocs image extraction APIs. 

1. `/readKYC` on an image.
	
		curl -X POST http://docs.hyperverge.co/v1/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'image=@image_path.png'

2. `/readKYC` on a pdf.
	
		curl -X POST http://docs.hyperverge.co/v1/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'pdf=@image_path.pdf'


## Supported APIs



Can be used to extract information from any or one of the supported documents depending on the endpoint.

* **URL**

  - /readKYC : used for any of the supported documents
  - /readPAN : used for pan cards alone, would have a higher accuracy than readKYC on PAN
  - /readPassport : used for Indian passports alone, would have a higher accuracy than readKYC on Indian Passports
  - /readAadhaar : used for aadhaar cards alone, would have a higher accuracy than readKYC on Aadhaar cards
  
* **Method:**

    `POST`

* **Header**
	
	- content-type : 'formdata'
	- appid 
	- appkey
	
* **Request Body**

	- image or pdf
  
* **Success Response:**

  * **Code:** 200 <br />
  * Incase of a properly made request, the response would follow schema.

		
		```
		{
			"status" : "success",
			"statusCode" : "200",
			"result" : <resultObject>
		}
		```
		
		The `resultObject` has the following Schema : 

			[{
				details : {
					"field-1" : "value-1",
					"field-2" : "value-2",
					"field-3" : "value-3",
					..
				},
				type : "kyc_type"
			}]
	
* **Error Response:**

	There are 3 types of request errors and `HTTP Status Code 400` is returned in all 3 cases:
	
	1. No Image input
		
			{
			  "status": "failure",
			  "statusCode": "400",
			  "error": "API call requires one input image"
			}
			
	
	2. More than 1 image input
	
			{
			  "status": "failure",
			  "statusCode": "400",
			  "error": "API call handles only one input image"
			}
	
	3. Larger than allowed image input
			
			{
			  "status": "failure",
			  "statusCode": "400",
			  "error": "image size cannot be greater than 6MB"
			}
			
	All error messages follow the same syntax with the statusCode and status also being a part of the response body, and `string` error message with the description of the error.
	
	**Server Errors**
	We try our best to avoid these errors, but if by chance they do occur the response code will be 5xx.


* **Sample Calls:**

 - readKYC
    
    ```
    curl -X POST http://docs.hyperverge.co/v1/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'image=@image_path.png'
    ```
  

 - readPAN

    ```
    curl -X POST http://docs.hyperverge.co/v1/readPAN \
	  -H 'appid: xxx' \
	  -H 'appkey: yyyy' \
	  -H 'content-type: multipart/form-data;\
	  -F 'image=@image_path.png'
    ```

 - readAadhaar	
	
	```
	curl -X POST http://docs.hyperverge.co/v1/readAadhaar \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'image=@image_path.png'
	```

 - readPassport
	
	```
	curl -X POST http://docs.hyperverge.co/v1/readPassport \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'image=@image_path.png'
	```	  
		  
## Supported kyc_types 

|Types|Fields|
---|---
|pan| date, father, name, pan_no
|old_pan| date, father, name, pan_no
|aadhaar_front\_bottom| name, aadhar, gender, father, mother, dob, yob
|aadhaar_front\_top| aadhaar, address, name, phone, pin,father, husband
|aadhaar_back| address, pin, aadhaar, husband, father
|passport_front| country_code, dob, doe, doi, gender, given\_name, nationality, passport\_num, place\_of\_birth, place\_of\_issue, surname, type
|passport_back| old\_doi, old\_pasport\_num, old\_place\_of\_issue, pin, address, father, mother, spouse, file_num
|voterid_front| voterid, name, gender, relation, father, husband, mother, dob, doc, age, dob-calculated
|voterid\_front\_new| voterid, name, father, husband, mother, relation
|voterid_back| voterid, type ,pin , address, gender, date, dob, age

## Optional parameters

Strongly advice users to not set the parameter to true unless required. HyperVerge does not want to store user's data beyond the processing time. 

| parameter | value| default| description |
|---|:---:|:---:|---|
| outputImageUrl| yes/no | no | If set to "yes" `string`, a signed temporary url be will generated. The output image will be cropped to contain only the region of interest in the image, the document will also be aligned.|

### API wrappers and sample code snippets (Beta)
1. [Python](samples/python/)
2. [Node.js](samples/node.js/)
3. [Java](samples/java)
4. [PHP](samples/php)
