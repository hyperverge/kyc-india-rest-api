<?php

	/**
	*	@param filePath: local path of the input image/pdf on which OCR needs to be run
	*   @param inputType: the type of input i.e. image or pdf
	*   @param endPoint: the API endpoint that should be used to run the OCR. This value is dependent on the type of document(PAN, Passport or Aadhaar). Possible Values: readKYC, readPAN, readAADHAAR, readPassport
	*	@return String response from the server
	**/
	function request_method($filePath, $inputType, $endPoint){
		$CONFIG_FILE_PATH = "./config.json";
		$SERVER_PATH_PREFIX = "https://docs.hyperverge.co/v2.0/";
		$configStr = file_get_contents($CONFIG_FILE_PATH);
		$config = json_decode($configStr, true);
		$appId = $config["appId"];
		$appKey = $config["appKey"];
		$headers = array(
			"Content-Type:multipart/form-data",
			"appid: " . $appId,
		    "appkey: ". $appKey
		); // cURL headers for file uploading


		if($inputType == "image"){
			$postfields = array("image" => new CURLFile("$filePath"));
		}
		elseif($inputType == "pdf"){
			$postfields = array("pdf" => new CURLFile("$filePath"));
		}
		else{
			echo "Invalid inputType. It can be either an 'image' or 'pdf'";
			return;
		}
		
		$ch = curl_init();
		$url = $SERVER_PATH_PREFIX . $endPoint;
		$options = array(
		    CURLOPT_URL => $url,
		    CURLOPT_HEADER => true,
		    CURLOPT_POST => 1,
		    CURLOPT_HTTPHEADER => $headers,
		    CURLOPT_POSTFIELDS => $postfields,
		    // CURLOPT_INFILESIZE => $filesize,
		    CURLOPT_RETURNTRANSFER => true
		); // cURL options
		curl_setopt_array($ch, $options);
		$resp = curl_exec($ch);
		if(!curl_errno($ch))
		{
		    $info = curl_getinfo($ch);
		    // if ($info['http_code'] == 200)
		    //     $errmsg = "File uploaded successfully";
		}
		else
		{
		    $errmsg = curl_error($ch);
		    echo $errmsg;
		}
		$header_size = curl_getinfo($ch, CURLINFO_HEADER_SIZE);
		$header = substr($resp, 0, $header_size);
		$body = substr($resp, $header_size);
		curl_close($ch);
		return $body;
	}
?>
