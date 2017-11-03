var request  = require("request");
var fs = require("fs");
var credentials = require("./credentials.json");

var appId = credentials.appId
var appKey = credentials.appKey

var validEndpoints = ["readPAN", "readPassport", "readAadhaar", "readKYC"];
function requestFn(filePath, fileType, endPoint, cb){
    if(fileType != "image" && fileType != "pdf") return cb("unsupported file type");
    if(validEndpoints.indexOf(endPoint) == -1) return cb("invalid endPoint");
    try{
        fs.statSync(filePath);
    }catch(e){
        return cb("invalid file path");
    }
    var formData = {};
    formData[fileType] = fs.createReadStream(filePath);

	request({
		url: 'https://docs.hyperverge.co/v1/'+endPoint,
		method: 'POST',
		formData: formData,
		headers: {
			'appid' : appId,
			'appkey' : appKey,
            'Accept' : "application/json"
		},
        json: true
	}, function(err, response, body){
        cb(err, body)
	});
}

module.exports = {
    readKYCImage : function(filePath, cb){ requestFn(filePath, "image", "readKYC", cb) },
    readKYCpdf : function(filePath, cb){ requestFn(filePath, "pdf", "readKYC", cb) },
    readPanImage : function(filePath, cb){ requestFn(filePath, "image", "readPAN", cb) },
    readPanAadhaar : function(filePath, cb){ requestFn(filePath, "pdf", "readPAN", cb) },
    readPassportAadhaar : function(filePath, cb){ requestFn(filePath, "image", "readPassport", cb) },
    readPassportPdf : function(filePath, cb){ requestFn(filePath, "pdf", "readPassport", cb) },
    readAadhaarImage : function(filePath, cb){ requestFn(filePath, "image", "readAadhaar", cb) },
    readAadhaarPdf : function(filePath, cb){ requestFn(filePath, "pdf", "readAadhaar", cb) },
}
