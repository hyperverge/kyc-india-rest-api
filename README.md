
# HyperVerge India KYC API Documentation

## Overview

This documentation describes KYC India API v2.0. The postman collection can be found at this [link](https://www.getpostman.com/collections/e83a04bf0e906d5f1581).

- [HyperVerge India KYC API Documentation](#hyperverge-india-kyc-api-documentation)
	- [Overview](#overview)
	- [Schema](#schema)
	- [Root Endpoint](#root-endpoint)
	- [Authentication](#authentication)
	- [Media Types](#media-types)
	- [Input Image Constraints](#input-image-constraints)
	- [API Call Structure](#api-call-structure)
	- [Response Structure](#response-structure)
	- [Sample Code](#sample-code)
	- [Supported Document Types](#supported-document-types)
		- [Response Details Schema](#response-details-schema)
	- [Optional parameters](#optional-parameters)
	- [API wrappers and sample code (Beta)](#api-wrappers-and-sample-code-beta)


## Schema
We recommend using HTTPS for all API access. All data is received as JSON, and all image uploads are to be performed as form-data (POST request). Incase of a pdf file input, the key name has to be `pdf` and in all other cases, the key name for the image could be anything apart from `pdf`.

## Root Endpoint
A `GET` request can be issued to the root endpoint to check for successful connection: 

	 curl https://ind-docs.hyperverge.co/v2.0 

The `plain/text` reponse of `"AoK!"` should be received.

## Authentication

Currently, a simple appId, appKey combination is passed in the request header. The appId and appKey are provided on request by the HyperVerge team. If you would like to try the API, please reach out to contact@hyperverge.co

	curl -X POST https://ind-docs.hyperverge.co/v2.0/readKYC \
	  -H 'appid: xxx' \
	  -H 'appkey: yyy' \
	  -H 'content-type: multipart/form-data;' \
	  -F 'image=@abc.png' 


On failed attempt with invalid credentials or unauthorized access the following error message will be received :

	{
	  "status": "failure",
	  "statusCode": "401",
	  "error": "Missing/Invalid credentials"
	}

Please do not expose the appId and appKey on browser applications. In case of a browser application, set up the API calls from the server side.

## Media Types

Currently, `jpeg, png and tiff` images and `pdf` documents are supported by the APIs. 

1. `/readKYC` on an image
	
		curl -X POST https://ind-docs.hyperverge.co/v2.0/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'image=@image_path.png'

2. `/readKYC` on a pdf
	
		curl -X POST https://ind-docs.hyperverge.co/v2.0/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'pdf=@image_path.pdf'
		  
3. `/readKYC` using a public url

		curl -X POST https://ind-docs.hyperverge.co/v2.0/readKYC \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;\
		  -F 'url=https://public_url_of_document'

Please note that while using public url mechanism for extraction, the input image type (pdf or image) need not be specified

## Input Image Constraints
- For the deep learning servers to extract text out of an image, the rule of thumb that needs to be followed is that the text in the image should be legible
- Higher size of image will lead to higher upload and processing time. Hence the size of image should be reduced as much as possible while ensuring that above condition is met
- A general guideline that can be followed is to keep the width of the ID card image atleast 800 pixels and to keep JPEG compression quality factor above 80%
- The aspect ratio of the input image should be same as the aspect ratio of the original document. Hence utmost care should be taken while resizing the image to maintain the aspect ratio
- If integrating into an Android or iOS app, please use the HyperSnap SDK for [Android](https://github.com/hyperverge/capture-android-sdk) and [iOS](https://github.com/hyperverge/capture-ios-sdk). This SDK provides camera functionality for capturing images of ID cards while taking care of all the above requirements.

## API Call Structure

* **URL**

  - /readKYC : used for any of the supported documents
  - /readPAN : used for pan cards alone.
  - /readPassport : used for Indian passports alone
  - /readAadhaar : used for aadhaar cards alone
  - /readVoterID : used for Indian VoterID cards alone
  
* **Method:**

    `POST`

* **Header**
	
	- content-type : 'formdata'
	- appId 
	- appKey
	
* **Request Body**

	1. image or pdf: content of the image/pdf; OR
	2. url: public url for image or pdf 

  
## Response Structure
  
* **Success Response:**

  * **Code:** 200 <br />
  * Incase of a properly made request, the response would follow this schema.

		{
			"status" : "success",
			"statusCode" : "200",
			"result" : <resultObject>
		}
		
	The `resultObject` has the following Schema: 
		
		[{
			"details" : {
				"field-1" : {
					"value": <value extracted>(required),
					"conf": <confidence with which value is extracted>(optional)
				},
				"field-2" : {
					"value": <value extracted>(required),
					"conf": <confidence with which value is extracted>(optional)
				},
				"field-3" : {
					"value": <value extracted>(required),
					"conf": <confidence with which value is extracted>(optional)
				},
				..
			},
			"type" : "kyc_type"
		}]
	Apart from the schema mentioned above, `fields` in  `details` object might have some extra information based on niche information of the field. For more details, please refer to the [**Response Details Schema**](#response-details-schema) section.
	
* **Error Response:**

Following are the various errors possible. Please note that cases 1, 3 and 4 are applicable only for API integration and don't happen when the SDK is used.

1. `HTTP Status Code 400` is returned for request errors. The following are the request errors possible:
	
	- No Image input
		
			{
			  "status": "failure",
			  "statusCode": "400",
			  "error": "API call requires one input image"
			}
			
	- More than 1 image input
	
			{
			  "status": "failure",
			  "statusCode": "400",
			  "error": "API call handles only one input image"
			}
	
	- Larger than allowed image input
			
			{
			  "status": "failure",
			  "statusCode": "400",
			  "error": "image size cannot be greater than 6MB"
			}
			
2. KYC Document not detected from Image. HTTP Status code - 422
	- readKYC:
		
			{
			  "status": "failure",
			  "statusCode": 422,
			  "error": "No supported KYC document detected"
			}
			
	- readPAN:
		
			{
			  "status": "failure",
			  "statusCode": 422,
			  "error": "No Pancard detected"
			}
			
	- readAadhaar:
		
			{
			  "status": "failure",
			  "statusCode": 422,
			  "error": "No Aadhaar detected"
			}
			
	- readPassport:
		
			{
			  "status": "failure",
			  "statusCode": 422,
			  "error": "No Passport detected"
			}
	- readVoterID:
		
			{
			  "status": "failure",
			  "statusCode": 422,
			  "error": "No VoterID detected"
			}
3. Downloading input image/pdf from URL Failed
	
			{
			  "status": "failure",
			  "statusCode": "424",
			  "error": "Download from URL Failed"
			}
	This error response is returned only when `url` input is provided. This is returned when
		 - Url contains an invalid domain
		 - Invalid `url` is provided.
		 - Remote end is unreachable.
		 - Remote end resets connection etc.		
. 
4. Downloading input image/pdf from URL timed out 
	
			{
			  "status": "failure",
			  "statusCode": "424",
			  "error": "Download from URL timed out"
			}
			
	This error response is returned only when `url` input is provided. Currently, timeout is set as **5 seconds**. 

	

5. Server Errors
We try our best to avoid these errors, but if by chance they do occur the response code will be 5xx.

All error messages follow the same syntax with the statusCode, status and error(string) being part of the body.

## Sample Code

 - **readKYC**
    
	    curl -X POST https://ind-docs.hyperverge.co/v2.0/readKYC \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

	    curl -X POST https://ind-docs.hyperverge.co/v2.0/readKYC \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://document_pdf_or_image_url'
  

 - **readPAN**

	    curl -X POST https://ind-docs.hyperverge.co/v2.0/readPAN \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;' \
		  -F 'image=@image_path.png'

	    curl -X POST https://ind-docs.hyperverge.co/v2.0/readPAN \
		  -H 'appid: xxx' \
		  -H 'appkey: yyyy' \
		  -H 'content-type: multipart/form-data;' \
		  -F 'url=https://pan_pdf_or_image_url'

 - **readAadhaar**	
	
		curl -X POST https://ind-docs.hyperverge.co/v2.0/readAadhaar \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

		curl -X POST https://ind-docs.hyperverge.co/v2.0/readAadhaar \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://aadhaar_image_or_pdf_url'

 - **readPassport**
	
		curl -X POST https://ind-docs.hyperverge.co/v2.0/readPassport \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

		curl -X POST https://ind-docs.hyperverge.co/v2.0/readPassport \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://passport_image_or_pdf_url'
			  
 - **readVoterID**
	
		curl -X POST https://ind-docs.hyperverge.co/v2.0/readVoterID \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'image=@image_path.png'

		curl -X POST https://ind-docs.hyperverge.co/v2.0/readVoterID \
			  -H 'appid: xxx' \
			  -H 'appkey: yyyy' \
			  -H 'content-type: multipart/form-data;' \
			  -F 'url=https://passport_image_or_pdf_url'
		  
## Supported Document Types
|Types|Fields|
---|---
|pan| date, father, name, pan_no, date_of_issue
|old_pan| date, father, name, pan_no
|aadhaar_front\_bottom| aadhaar, dob, father, gender, mother, name, yob, qr
|aadhaar_front\_top| aadhaar, address, father, husband, name, phone, pin
|aadhaar_back| aadhaar, address, father, husband, pin, qr
|passport_front| country_code, dob, doe, doi, gender, given\_name, nationality, passport\_num, place\_of\_birth, place\_of\_issue, surname, type
|passport_back| address, father, file\_num, mother, old\_doi, old\_pasport\_num, old\_place\_of\_issue, passport\_num, pin, spouse
|voterid_front|name, voterid, dob, gender, doc, relation, age
|voterid_front_new|name, voterid, relation
|voterid_back|voterid, pin, address, type, gender, date, dob, age

### Response Details Schema
Each details object for the supported document types will have below mentioned schema ordered by document type and sub type. Kindly note that this schema will remain the same in a version of the API.
- #### Pan
	- type: **pan**

	  ```
	  "date": {
		  "value": <type: String, description: Date of birth of the holder>,
          "conf": <type: Number, description: Confidence for extracted Date of Birth>
	  },
	  "father": {
	      "value": <type: String, description: Father's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Father's name>
	  },
	  "name": {
	      "value": <type: String, description: Name of the Holder>,
	      "conf": <type: Number, description: Confidence for extracted Name>
	  },
	  "pan_no": {
	      "value": <type: String, description: PAN Number of the Holder>,
	      "conf": <type: Number, description: Confidence for extracted PAN Number>
	  },
	  "date_of_issue": {
	      "value": <type:String description: Date of Issue of PAN card>,
	      "conf": <type: Number, description: Confidence for extracted Date of Issue>
	  }
      "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```
	- type: **old_pan**
	
	  ```
	  "date": {
		  "value": <type: String, description: Date of birth of the holder>,
          "conf": <type: Number, description: Confidence for extracted Date of Birth>
	  },
	  "father": {
	      "value": <type: String, description: Father's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Father's name>
	  },
	  "name": {
	      "value": <type: String, description: Name of the Holder>,
	      "conf": <type: Number, description: Confidence for extracted Name>
	  },
	  "pan_no": {
	      "value": <type: String, description: PAN Number of the Holder>,
	      "conf": <type: Number, description: Confidence for extracted PAN Number>
	  }
      "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```
- #### Aadhaar
	- type: **aadhaar\_front\_bottom**
	
	  ```
	  "aadhaar": {
	      "value": <type: String, description: Aadhaar Number of the holder>,
	      "ismasked": <type: String, values: [yes or no], description: yes if the Aadhaar number extracted is of masked type and no if it is not of masked type>
	      "conf": <type: Number, description: Confidence for extracted Aadhaar Number>
	  },
	  "dob": {
	      "value": <type: String, description: Date of Birth of the Holder>,
	      "conf": <type: Number, description: Confidence for extracted Date of Birth>
	  },
	  "father": {
	      "value": <type: String, description: Father's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Father's name>
	  },
	  "gender": {
	      "value": <type: String, description: Gender of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Gender>
	  },
	  "mother": {
	      "value": <type: String, description: Mother's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Mother's name>
	  },
	  "name": {
	      "value": <type: String, description: Name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Name>
	  },
	  "yob": {
	      "value": <type: String, description: Year of Birth of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Year of Birth>
	  },
	  "qr": {
          "value": <type: String, description: QR code XML extracted from the Input Aadhaar Image>
      },
      "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```
	- type: **aadhaar\_front\_top**
		 
	  ```
	  "aadhaar": {
	      "value": <type: String, description: Aadhaar Number of the holder>,
	      "ismasked": <type: String, values: [yes or no], description: yes if the Aadhaar number extracted is of masked type and no if it is not of masked type>,
	      "conf": <type: Number, description: Confidence for extracted Aadhaar Number>
	  },
	  "address": {
	  	"care_of": <type: String, description: Care Of section of the address of the holder>,
	  	"district": <type: String, description: District of the address of the holder>,
	  	"city": <type: String, description: City of the address of the holder>,
	  	"locality": <type: String, description: Locality of the address of the holder>,
	  	"landmark": <type: String, description: Landmark of the address of the holder>,
	  	"street": <type: String, description: Street of the address of the holder>,
	  	"line1": <type: String, description: Line1 of the address of the holder>,
	  	"line2": <type: String, description: Line2 of the address of the holder>,
	  	"house_number": <type: String, description: House Number of the address of the holder>,
	  	"pin": <type: String, description: Pincode of the address of the holder>,
	  	"state": <type: String, description: State of the address of the holder>,
	  	"value": <type: String, description: Address of the holder>
	  	"conf": <type: Number, description: Confidence for extracted Address>
	  },
	  "father": {
	      "value": <type: String, description: Father's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Father's name>
	  },
	  "husband": {
	      "value": <type: String, description: Husband's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Husband's name>
	  },
	  "name": {
	      "value": <type: String, description: Name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Name>
	  },
	  "phone": {
	      "value": <type: String, description: Phone number of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Phone number>
	  },
	  "pin": {
	      "value": <type: String, description: Pincode of the holder's Address>,
	      "conf": <type: Number, description: Confidence for extracted Pincode>
	  },
      "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```
	- type: **aadhaar\_back**
	  
	  ```
	  "aadhaar": {
	      "value": <type: String, description: Aadhaar Number of the holder>,
	      "ismasked": <type: String, values: [yes or no], description: yes if the Aadhaar number extracted is of masked type and no if it is not of masked type>,
	      "conf": <type: Number, description: Confidence for extracted Aadhaar Number>
	  },
	  "address": {
	  	"care_of": <type: String, description: Care Of section of the address of the holder>,
	  	"district": <type: String, description: District of the address of the holder>,
	  	"city": <type: String, description: City of the address of the holder>,
	  	"locality": <type: String, description: Locality of the address of the holder>,
	  	"landmark": <type: String, description: Landmark of the address of the holder>,
	  	"street": <type: String, description: Street of the address of the holder>,
	  	"line1": <type: String, description: Line1 of the address of the holder>,
	  	"line2": <type: String, description: Line2 of the address of the holder>,
	  	"house_number": <type: String, description: House Number of the address of the holder>,
	  	"pin": <type: String, description: Pincode of the address of the holder>,
	  	"state": <type: String, description: State of the address of the holder>,
	  	"value": <type: String, description: Address of the holder>
	  	"conf": <type: Number, description: Confidence for extracted Address>
	  },
	  "father": {
	      "value": <type: String, description: Father's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Father's name>
	  },
	  "husband": {
	      "value": <type: String, description: Husband's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Husband's name>
	  },
	  "pin": {
	      "value": <type: String, description: Pincode of the holder's Address>,
	      "conf": <type: Number, description: Confidence for extracted Pincode>
	  },
	  "qr": {
          "value": <type: String, description: QR code XML extracted from the Input Aadhaar Image>
      },
      "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```
- #### Passport
	- type: **passport\_front**
	
	  ```
	  "country_code": {
	      "value": <type: String, description: Country Code of the holder's Passport>,
	      "conf": <type: Number, description: Confidence for extracted Country Code>
	  },
	  "dob": {
	      "value": <type: String, description: Date of Birth of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Date of Birth>
	  },
	  "doe": {
	      "value": <type: String, description: Date of Expiry of the Passport>,
	      "conf": <type: Number, description: Confidence for extracted Date of Expiry>
	  },
	  "doi": {
	      "value": <type: String, description: Date of Issue of the Passport>,
	      "conf": <type: Number, description: Confidence for extracted Date of Issue>
	  },
	  "gender": {
	      "value": <type: String, description: Gender of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Gender>
	  },
	  "given_name": {
	      "value": <type: String, description: Given Name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Given Name>
	  },
	  "nationality": {
	      "value": <type: String, description: Nationality of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Nationality>
	  },
	  "passport_num": {
	      "value": <type: String, description: Passport Number of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Passport Number>
	  },
	  "place_of_birth": {
	      "value": <type: String, description: Place of birth of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Place of Birth>
	  },
	  "place_of_issue": {
	      "value": <type: String, description: Place of issue of the Passport>,
	      "conf": <type: Number, description: Confidence for extracted Place of Issue>
	  },
	  "surname": {
	      "value": <type: String, description: Surname of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Surname>
	  },
	  "mrz": {
	      "line1": <type: String, description: Line 1 of MRZ in the Passport>,
	      "line2": <type: String, description: Line 2 of MRZ in the Passport>
	      "conf": <type: Number, description: Confidence for extracted MRZ>
	  },
	  "type": {
	      "value": <type: String, description: Type of Passport>
	      "conf": <type: Number, description: Confidence for extracted Type of Passport>
	  },
	  "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```
	- type: **passport\_back**
	
	  ```
	  "address": {
	  	"district": <type: String, description: District of the address of the holder>,
	  	"city": <type: String, description: City of the address of the holder>,
	  	"locality": <type: String, description: Locality of the address of the holder>,
	  	"landmark": <type: String, description: Landmark of the address of the holder>,
	  	"street": <type: String, description: Street of the address of the holder>,
	  	"line1": <type: String, description: Line1 of the address of the holder>,
	  	"line2": <type: String, description: Line2 of the address of the holder>,
	  	"house_number": <type: String, description: House Number of the address of the holder>,
	  	"pin": <type: String, description: Pincode of the address of the holder>,
	  	"state": <type: String, description: State of the address of the holder>,
	  	"value": <type: String, description: Address of the holder>
	  	"conf": <type: Number, description: Confidence for extracted Address>
	  },
	  "father": {
	      "value": <type: String, description: Father's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Father's name>
	  },
	  "mother": {
	      "value": <type: String, description: Mother's name of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Mother's name>
	  },
	  "file_num": {
	      "value": <type: String, description: File number of the Passport>,
	      "conf": <type: Number, description: Confidence for extracted File number>
	  },
	  "old_doi": {
	      "value": <type: String, description: Date of Issue of the Old Passport>,
	      "conf": <type: Number, description: Confidence for extracted Old Date of Issue>
	  },
	  "old_passport_num": {
	      "value": <type: String, description: Old Passport Number of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Old Passport Number>
	  },
	  "old_place_of_issue": {
	      "value": <type: String, description: Old Place of issue of the Passport>,
	      "conf": <type: Number, description: Confidence for extracted Old Place of Issue>
	  },
	  "passport_num": {
	      "value": <type: String, description: Passport Number of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Passport Number>
	  },
	  "pin": {
	      "value": <type: String, description: Pincode of the Address of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Pincode>
	  },
	  "spouse": {
	      "value": <type: String, description: Spouse name of the holder>
	      "conf": <type: Number, description: Confidence for extracted Spouse's name>
	  },
	  "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```

- #### Voter ID
	- type: **voterid\_front**
	
	  ```
	  "voterid": {
	      "value": <type:String, description: VoterID of Holder>,
	      "conf": <type: Number, description: Confidence for extracted VoterID>
	  },
	  "name": {
	      "value": <type:String, description: Name of Holder>,
	      "conf": <type: Number, description: Confidence for extracted Name>
	  },
	  "gender": {
	      "value": <type:String, description:Gender of Holder>,
	      "conf": <type: Number, description: Confidence for extracted Gender>
	  },
	  "relation": {
	      "value": <type:String, description: Name of relative's Holder>,
	      "conf": <type: Number, description: Confidence for extracted Relative's Name>
	  },
	  "dob": {
	      "value": <type:String, description: Date of Birth of Holder >,
	      "conf": <type: Number, description: Confidence for extracted Date of Birth>
	  },
	  "doc": {
	      "value": <type:String, description: Date for Calculation of Age>,
	      "conf": <type: Number, description: Confidence for extracted Date for Calculation of Age>
	  },
	  "age": {
	      "value": <type:String, description: Age of the Holder>
	      "conf": <type: Number, description: Confidence for extracted Age>
	  },
	  "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```

	- type: **voterid\_front\_new**
	
	  ```
	  "voterid": {
	      "value": <type:String, description: VoterID of Holder>,
	      "conf": <type: Number, description: Confidence for extracted VoterID>
	  },
	  "name": {
	      "value": <type:String, description: Name of Holder>,
	      "conf": <type: Number, description: Confidence for extracted Name>
	  },
	  "relation": {
	      "value": <type:String, description: Name of relative's Holder>,
	      "conf": <type: Number, description: Confidence for extracted Relative's Name>
	  },
	  "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```

	- type: **voterid\_back**
	
	  ```
	  "voterid": {
	      "value": <type:String, description: VoterID of Holder>,
	      "conf": <type: Number, description: Confidence for extracted VoterID>
	  },
	  "gender": {
	      "value": <type:String, description:Gender of Holder>,
	      "conf": <type: Number, description: Confidence for extracted Gender>
	  },
      "pin": {
	      "value": <type: String, description: Pincode of the Address of the holder>,
	      "conf": <type: Number, description: Confidence for extracted Pincode>
	  },
	  "dob": {
	      "value": <type:String, description: Date of Birth of Holder >,
	      "conf": <type: Number, description: Confidence for extracted Date of Birth>
	  },
	  "age": {
	      "value": <type:String, description: Age of the Holder>
	      "conf": <type: Number, description: Confidence for extracted Age>
	  },
	  "date": {
	      "value": <type:String, description: Date of Issue of VoterID>,
	      "conf": <type: Number, description: Confidence for extracted Date of Issue>
	  },
	  "type": {
	      "value": <type:String, description: Type of VoterID: Old/New>,
	      "conf": <type: Number, description: Confidence for extracted Type of VoterId>
	  },
	  "address": {
	  	"district": <type: String, description: District of the address of the holder>,
	  	"city": <type: String, description: City of the address of the holder>,
	  	"locality": <type: String, description: Locality of the address of the holder>,
	  	"landmark": <type: String, description: Landmark of the address of the holder>,
	  	"street": <type: String, description: Street of the address of the holder>,
	  	"line1": <type: String, description: Line1 of the address of the holder>,
	  	"line2": <type: String, description: Line2 of the address of the holder>,
	  	"house_number": <type: String, description: House Number of the address of the holder>,
	  	"pin": <type: String, description: Pincode of the address of the holder>,
	  	"state": <type: String, description: State of the address of the holder>,
	  	"value": <type: String, description: Address of the holder>
	  	"conf": <type: Number, description: Confidence for extracted Address>
	  },
	  "tag": <type: String, description: Unique identifier for extracted document sub-type>
	  ```

## Optional parameters

Following are optional parameters that could be set as part of the request body.

| parameter | Body or Header | value| default| description | Works on | 
|---|---|:---:|:---:|---|---|
| referenceId | Header |String|null|`referenceId` is an identifier for the API call and is echoed back by the server in the response. This referenceId can be used to create logical grouping of single/multiple API calls based on business logic of the consumer of API. HyperVerge's QC dashboard can be used to fetch logs grouped by the Reference ID| Aadhar, PAN, Passport, Driving License, Voter ID. |
| uuid | Header | String | null | This is an advanced feature designed to prevent tampering of response by a man in the middle. If `uuid` is set, the response will include an `X-Response-Signature` header which will have a checksum of the response body signed with the `uuid` and a private key| Aadhar, PAN, Passport, Driving License, Voter ID. |
| enableDashboard| Body | "yes"/"no" | "no" | This will give access to an exclusive dashboard built by HyperVerge for Quality Check and debugging purpose. This dashboard will enable realtime usage monitoring and can be used to pin-point integration issues in POC/production(if any) | Aadhar, PAN, Passport, Driving License, Voter ID. |
|qualityCheck|Body|"yes"/"no"|"no"|This will detect if an ID card is black and white. Currently, this feature is available only for PAN and Aadhaar cards|Aadhar, PAN|
|faceCheck|Body|"yes"/"no"|"no"|This will check if a face is present in the document. This works for the front side of all 4 documents supported. The body would have an extra 'face' parameter| Aadhar, PAN, Passport, Driving License, Voter ID. |
|outputImageUrl|Body|"yes"/"no"|"no"|When set to "yes", the response body would have an extra "url" parameter which contains a cropped and aligned image of the document. The url expires in 15 minutes.| Aadhar, PAN, Passport, Driving License, Voter ID. |
|maskAadhaarComplete|Body|"yes"/"no"|"no"|If set to "yes" along with 'outputImageUrl', the image in the url would also have the Aadhaar number masked| Aadhar |
|maskAadhaar|Body|"yes"/"no"|"no"|If set to "yes" along with 'outputImageUrl', the image in the url would also have the last 8 characters of Aadhaar number masked| Aadhar |
| disableQR         | Body           | "yes"/"no" |  "no"   | This will disable the QR present on the document.                                                                                                                                                           | Aadhar |
| maskAadhaarText         | Body           | "yes"/"no" |  "no"   | This will mask the text present in Aadhaar in additon to masking the image.                                                                                                                                                            | Aadhar |
| outputImageBase64         | Body           | "yes"/"no" |  "no"   | This will output the image in format Base64. It's an alternate parameter to outputImageUrl.                                                                                                                                                | Aadhar, PAN, Passport, Driving License, Voter ID. |
| expandQR         | Body           | "yes"/"no" |  "no"   | This will read the QR code present in the document and append the output in response.                                                                                                                                                            | Aadhar |
| allowOnlyHorizontal         | Body           | "yes"/"no" |  "no"   | This will allow only horizontal images .                                                                                                                                   | Aadhar, PAN, Passport, Driving License, Voter ID. |
| allowOnlyCompleteCard         | Body           | "yes"/"no" |  "no"   | This will allow only complete cards to be processed. Allows only entire image.                                                                                                                                          | Aadhar |
| allowOnlyLiveDocument         | Body           | "yes"/"no" |  "no"   |  This will allow only live document to be processed                                                                                                                                    |  Aadhar |
| rejectPhotoOnPhoto         | Body           | "yes"/"no" |  "no"   | This will reject if photo is present upon another photo |  Aadhar |
| detectMinor         | Body           | "yes"/"no" |  "no"   | This will detect if the card belongs to a minor                                                                                                                                                  | PAN |
| rejectBlur         | Body           | "yes"/"no" |  "no"   | This will reject if the image is blurred.                                                                                                                       | Aadhar, PAN, Passport, Driving License, Voter ID. |
| detectPANSignature         | Body           | "yes"/"no" |  "no"   | This will detect the Pan signature present of the card.                                                                                                                                                 | PAN |
| readBarcode         | Body           | "yes"/"no" |  "no"   | This will read the barcode present on the card.                                                                                                                                                       | Aadhar |


For more details regarding any of the above features, please contact your POC from HyperVerge.

## API wrappers and sample code (Beta)
1. [Python](samples/python/)
2. [Node.js](samples/node.js/)
3. [Java](samples/java)
4. [PHP](samples/php)
