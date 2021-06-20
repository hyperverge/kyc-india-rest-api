var request  = require("request");
var fs = require("fs");
var credentials = require("./credentials.json");

var appId = credentials.appId
var appKey = credentials.appKey

var validEndpoints = ["readPAN", "readPassport", "readAadhaar", "readKYC"];
function requestFn(filePath, fileType, headerParams, bodyParams, endPoint, cb){
    if(fileType != "image" && fileType != "pdf") return cb("unsupported file type");
    if(validEndpoints.indexOf(endPoint) == -1) return cb("invalid endPoint");
    try{
        fs.statSync(filePath);
    }catch(e){
        return cb("invalid file path");
    }
    var formData = bodyParams;
    formData[fileType] = fs.createReadStream(filePath);

    request({
        url: 'https://ind-docs.hyperverge.co/v2.0/'+endPoint,
        method: 'POST',
        formData: formData,
        headers: {
            'appid' : appId,
            'appkey' : appKey,
            'Accept' : "application/json",
            ...headerParams
        },
        json: true
    }, function(err, response, body){
        cb(err, body)
    });
}

module.exports = {
    readKYCImage : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "image", headerParams, bodyParams, "readKYC", cb) },
    readKYCpdf : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "pdf", headerParams, bodyParams, "readKYC", cb) },
    readPanImage : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "image", headerParams, bodyParams, "readPAN", cb) },
    readPanAadhaar : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "pdf", headerParams, bodyParams, "readPAN", cb) },
    readPassportAadhaar : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "image", headerParams, bodyParams, "readPassport", cb) },
    readPassportPdf : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "pdf", headerParams, bodyParams, "readPassport", cb) },
    readAadhaarImage : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "image", headerParams, bodyParams, "readAadhaar", cb) },
    readAadhaarPdf : function(filePath, headerParams, bodyParams, cb){ requestFn(filePath, "pdf", headerParams, bodyParams, "readAadhaar", cb) },
}
