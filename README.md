# HyperVerge Documents - API Documentation

## Overview

This documentation describes hyperdocs API v1-1. If you have any queries please contact support. The postman collection can be found at this [link](https://www.getpostman.com/collections/d4ed2fc6f5f2469479a2).

1. Schema
2. Parameters
3. Root Endpoint
4. Authentication
5. Media Types
6. Input Image Constraints
7. Supported Endpoints
8. Supported kyc_types 
9. Optional Parameters
10. API wrappers and sample code snippets (Beta) 


## Schema

We recommend using HTTPS for all API access. All data is received as JSON, and all image uploads are to be performed as form-data (POST request). Incase of a pdf file input, the key name has to be `pdf` and in all other cases, the key name for the image could be anything apart from `pdf`.

## Parameters
All optional and compulsory parameters are passed as part of the request body.

## Root Endpoint
A `GET` request can be issued to the root endpoint to check for successful connection: 

	 curl https://docs.hyperverge.co/v1-1 

The `plain/text` reponse of `"AoK!"` should be received.

## Authentication

Currently, a simple appId, appKey combination is passed in the request header. The appId and appKey are provided on request by the HyperVerge team. If you would like to try the API, please reach out to contact@hyperverge.co

	curl -X POST http://docs.hyperverge.co/v1-1/readKYC \
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

Please do not expose the appid and appkey on browser applications. In case of a browser application, set up the API calls from the server side.

## Media Types

Currently, `jpeg, png and tiff` images and `pdf` documents are supported by the HyperDocs image extraction APIs. 

1. `/readKYC` on an image
	
		curl -X POST http://docs.hyperverge.co/v1-1/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'image=@image_path.png'

2. `/readKYC` on a pdf
	
		curl -X POST http://docs.hyperverge.co/v1-1/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'pdf=@image_path.pdf'
		  
3. `/readKYC` using a public url

		curl -X POST http://docs.hyperverge.co/v1-1/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'url=https://public_url_of_document'

Please note that while using public url mechanism for extraction, the input image type (pdf or image) need not be specified

## Input Image Constraints
- For the DL servers to extract text out of an image, the rule of thumb that needs to be followed is that the text in the image should be legible
- Higher size of image will lead to higher upload and processing time. Hence the size of image should be reduced as much as possible while ensuring that above condition is met
- A general guideline that can be followed is to keep the width of the ID card image atleast 800 pixels and to keep JPEG compression quality factor above 80%
- For our DL server to extract text from the document, the aspect ratio of the input image should be same as the aspect ratio of the original document. Hence utmost care should be taken while resizing the image to maintain the aspect ratio
- If integrating into a native Android or iOS app, please use the HyperSnap SDK for [Android](https://github.com/hyperverge/capture-android-sdk) and [iOS](https://github.com/hyperverge/capture-ios-sdk). This SDK provides camera functionality for capturing an image of the ID card while taking care all of the above points 

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

	1. image or pdf: content of the image/pdf; OR
	2. url: public url for image or pdf 
  
* **Success Response:**

  * **Code:** 200 <br />
  * Incase of a properly made request, the response would follow schema.

		{
			"status" : "success",
			"statusCode" : "200",
			"result" : <resultObject>
		}
		
	The `resultObject` has the following Schema: 
		
		[{
			"details" : {
				"field-1" : "value-1",
				"field-2" : "value-2",
				"field-3" : "value-3",
				..
			},
			"type" : "kyc_type"
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
	4. KYC Document not detected from Image
		- readKYC:
		
			```
			{
			  "status": "failure",
			  "statusCode": 400,
			  "error": "No supported KYC documents detected"
			}
			```
			
		- readPAN:
		
			```
			{
			  "status": "failure",
			  "statusCode": 400,
			  "error": "No Pancard detected"
			}
			```
			
		- readAadhaar:
		
			```
			{
			  "status": "failure",
			  "statusCode": 400,
			  "error": "No Aadhaar detected"
			}
			```
			
		- readPassport:
		
			```
			{
			  "status": "failure",
			  "statusCode": 400,
			  "error": "No Passport detected"
			}
			```
			
	All error messages follow the same syntax with the statusCode and status also being a part of the response body, and `string` error message with the description of the error.
	
	**Server Errors**
	We try our best to avoid these errors, but if by chance they do occur the response code will be 5xx.


* **Sample Calls:**

 - readKYC
    
	    curl -X POST http://docs.hyperverge.co/v1-1/readKYC \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

	    curl -X POST http://docs.hyperverge.co/v1-1/readKYC \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://document_pdf_or_image_url'
  

 - readPAN

	    curl -X POST http://docs.hyperverge.co/v1-1/readPAN \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;' \
		  -F 'image=@image_path.png'

	    curl -X POST http://docs.hyperverge.co/v1-1/readPAN \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;' \
		  -F 'url=https://pan_pdf_or_image_url'

 - readAadhaar	
	
		curl -X POST http://docs.hyperverge.co/v1-1/readAadhaar \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

		curl -X POST http://docs.hyperverge.co/v1-1/readAadhaar \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://aadhaar_image_or_pdf_url'

 - readPassport
	
		curl -X POST http://docs.hyperverge.co/v1-1/readPassport \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

		curl -X POST http://docs.hyperverge.co/v1-1/readPassport \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://passport_image_or_pdf_url'
		  
## Supported kyc_types 
|Types|Fields|
---|---
|pan| date, father, name, pan_no
|old_pan| date, father, name, pan_no
|aadhaar_front\_bottom| aadhaar, dob, father, gender, mother, name, yob
|aadhaar_front\_top| aadhaar, address, address_split, father, husband, name, phone, pin
|aadhaar_back| aadhaar, address, address_split, father, husband, pin
|passport_front| country_code, dob, doe, doi, gender, given\_name, nationality, passport\_num, place\_of\_birth, place\_of\_issue, surname, type
|passport_back| address, address\_split, father, file\_num, mother, old\_doi, old\_pasport\_num, old\_place\_of\_issue, pin, spouse

### Explanation of Fields for Each Type:

- #### Pan
	- type: **pan**

	  ```
	  date: <type: String, description: Date of birth of the holder>,
	  father: <type: String, description: Father's name of the holder>,
	  name: <type: String, description: Name of the Holder>,
	  pan_no: <type: String, description: PAN No of the Holder>
	  ```
	- type: **old_pan**
	
	  ```
	  date: <type: String, description: Date of birth of the holder>,
	  father: <type: String, description: Father's name of the holder>,
	  name: <type: String, description: Name of the Holder>,
	  pan_no: <type: String, description: PAN No of the Holder>
	  ```
- #### Aadhaar
	- type: **aadhaar\_front\_bottom**
	
	  ```
	  aadhaar: <type: String, description: Aadhaar Number of the holder>,
	  dob: <type: String, description: Date of Birth of the Holder>
	  father: <type: String, description: Father's name of the holder>,
	  gender: <type: String, description: Gender of the holder>,
	  mother: <type: String, description: Mother's name of the holder>,
	  name: <type: String, description: Name of the holder>,
	  yob: <type: String, description: Year of Birth of the holder>
	  ```
	- type: **aadhaar\_front\_top**
		 
	  ```
	  aadhaar: <type: String, description: Aadhaar Number of the holder>,
	  address: <type: String, description: Address of the holder>,
	  address_split: {
	  	"care_of": <type: String, description: Care Of section of the address of the holder>,
	  	"city": <type: String, description: City of the address of the holder>,
	  	"line1": <type: String, description: Line1 of the address of the holder>,
	  	"line2": <type: String, description: Line2 of the address of the holder>,
	  	"pin": <type: String, description: Pincode of the address of the holder>,
	  	"state": <type: String, description: State of the address of the holder>
	  },
	  father: <type: String, description: Father's name of the holder>,
	  husband: <type: String, description: Husband's name of the holder>,
	  name: <type: String, description: Name of the holder>,
	  phone: <type: String, description: Phone of the holder>,
	  pin: <type: String, description: Pincode of the holder's Address>
	  ```
	- type: **aadhaar\_back**
	  
	  ```
	  aadhaar: <type: String, description: Aadhaar Number of the holder>,
	  address: <type: String, description: Address of the holder>,
	  address_split: {
	  	"care_of": <type: String, description: Care Of section of the address of the holder>,
	  	"city": <type: String, description: City of the address of the holder>,
	  	"line1": <type: String, description: Line1 of the address of the holder>,
	  	"line2": <type: String, description: Line2 of the address of the holder>,
	  	"pin": <type: String, description: Pincode of the address of the holder>,
	  	"state": <type: String, description: State of the address of the holder>
	  },
	  father: <type: String, description: Father's name of the holder>,
	  husband: <type: String, description: Husband's name of the holder>,
	  pin: <type: String, description: Pincode of the holder's Address>
	  ```
- #### Passport
	- type: **passport_front**
	
	  ```
	  country_code: <type: String, description: Country Code of the holder's Passport>,
	  dob: <type: String, description: Date of Birth of the holder>,
	  doe: <type: String, description: Date of Expiry of the Passport>,
	  doi: <type: String, description: Date of Issue of the Passport>,
	  gender: <type: String, description: Gender of the holder>,
	  given_name: <type: String, description: Given Name of the holder>,
	  nationality: <type: String, description: Nationality of the holder>,
	  passport_num: <type: String, description: Passport Number of the holder>,
	  place_of_birth: <type: String, description: Place of birth of the holder>,
	  place_of_issue: <type: String, description: Place of issue of the Passport>,
	  surname: <type: String, description: Surname of the holder>,
	  type: <type: String, description: Type of Passport>
	  ```
	- type: **passport_back**
	
	  ```
	  address: <type: String, description: Address of the holder>,
	  address_split: {
		"city": <type: String, description: City of the address of the holder>,
		"line1": <type: String, description: Line1 of the address of the holder>,
		"line2": <type: String, description: Line2 of the address of the holder>,
		"pin": <type: String, description: Pincode of the address of the holder>,
		"state": <type: String, description: State of the address of the holder>
	  },
	  father: <type: String, description: Father's name of the holder>,
	  file_num: <type: String, description: File number of the Passport>
	  mother: <type: String, description: Mother's name of the holder>,
	  old_doi: <type: String, description: Date of issue of the old passport>,
	  old_passport_num: <type: String, description: Passport Number of the old Passport>,
	  old_place_of_issue: <type: String, description: Place of Issue of old Passport>,
	  pin: <type: String, description: Pincode of the Address of the holder>,
	  spouse: <type: String, description: Spouse name of the holder>
	  ```
		
- ### Understanding Confidence
	For any field with key \<field-name> extracted from the document, the confidence score would be reported with the key as  \<field-name>_conf. The score would be a float value between 0 and 1. The optimal threshold for the confidence score is 0.7, if confidence is reported to be less than this threshold value, manual review might be required based on the use case.

## Optional parameters

| parameter | value| default| description |
|---|:---:|:---:|---|
| outputImageUrl| yes/no | no | If set to "yes" `string`, a signed temporary url be will generated. The output image will be cropped to contain only the region of interest in the image, the document will also be aligned.Strongly advice users to not set the parameter to true unless required. HyperVerge does not want to store user's data beyond the processing time. |

## API wrappers and sample code snippets (Beta)
1. [Python](samples/python/)
2. [Node.js](samples/node.js/)
3. [Java](samples/java)
4. [PHP](samples/php)

