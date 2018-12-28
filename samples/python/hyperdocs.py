import requests
import os

import json

with open('config.json', 'r') as f:
 config = json.load(f)

url = "https://docs.hyperverge.co/v2.0/"


appId = config['appId']
appKey = config['appKey']


#requestFn("./sample.jpg", "image", "readKYC")
#requestFn("./sample.pdf", "pdf", "readKYC")
# valid file types : image, pdf
# valid endPoints : readKYC, readPAN, readPassport, readAadhaar
# Though readKYC works on all documents. Use the appropriate endPoint if the document
# type is known as this would provide a marginally higher accuracy and better 
# performance.
def requestFn(filePath, fileType, endPoint):
 if(not os.path.exists(filePath)):
  return dict(error="Invalid file path")
 headers = {
     'appid':  appId,
     'appkey': appKey
     }
 if(fileType == "image"):
  response = requests.post(url+endPoint, files=dict(image=open(filePath)), headers=headers);
 elif(fileType == "pdf"):
  response = requests.post(url+endPoint, files=dict(pdf=open(filePath)), headers=headers);
 else:
  return dict(error='Invalid file type')
 return dict(response=response.text)

