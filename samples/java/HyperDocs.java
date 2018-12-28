import org.json.*;
import java.io.*;

public class HyperDocs{
	private final String SERVER_PATH = "https://docs.hyperverge.co/v2.0/";

	private String appId, appKey;

	enum InputType{
		IMAGE{
			@Override
            public String getInputTag() {
                return "image";
            }
            @Override
            public String getMIMEType() {
                return "image/*";
            }
		},
		PDF{
			@Override
            public String getInputTag() {
                return "pdf";
            }
            @Override
            public String getMIMEType() {
                return "application/pdf";
            }
		};

		public abstract String getInputTag();
		public abstract String getMIMEType();
	}

	enum EndPoint {
		READ_KYC{
			//generuc endpoint, will work with PAN, PASSPORT & AADHAAR
			@Override
            public String getEndpointString() {
                return "readKYC";
            }
		},
		READ_PAN{
			//end point for PAN Cards
			@Override
            public String getEndpointString() {
                return "readPAN";
            }
		},
		READ_AADHAAR{
			//end point for Aadhaar
			@Override
            public String getEndpointString() {
                return "readAadhaar";
            }
		},
		READ_PASSPORT{
			//end point for Passports
			@Override
            public String getEndpointString() {
                return "readPassport";
            }

		};
		public abstract String getEndpointString();

	}

	public HyperDocs(){
		try{
			File file = new File("./config.json");
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			String configString = new String(data, "UTF-8");

			JSONObject configs = new JSONObject(configString);
			appId = configs.getString("appId");
			appKey = configs.getString("appKey");

		} catch(FileNotFoundException e){
			e.printStackTrace();
			return;
		} catch(IOException e){
			e.printStackTrace();
			return;
		}
	}

	/**
	*	@param imagePath: local path of the input image/pdf on which OCR needs to be run
	*   @param inputType: the type of input i.e. image or pdf
	*   @param endPoint: the API endpoint that should be used to run the OCR. This value is dependent on the type of document(PAN, Passport or Aadhaar)
	*	@return String response from the server
	**/
	public String requestMethod(String inputPath, InputType inputType, EndPoint endPoint){
		if(appId.isEmpty() || appKey.isEmpty()){
			System.out.println("appId and appKey cannot be empty. Kindly add valid credentials in config.json");
			return null;
		}
		try{
			JSONObject headers = new JSONObject();
			headers.put("appId", appId);
			headers.put("appKey", appKey);
			HVMultipartPost multipartPost = new HVMultipartPost(SERVER_PATH + endPoint.getEndpointString(), headers);	
			multipartPost.addFileEntity(inputType.getInputTag(), inputType.getMIMEType(), inputPath);
			return multipartPost.executeRequest();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
