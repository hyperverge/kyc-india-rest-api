import java.util.HashMap;

public class Test{

	public static void main(String[] args){

      HashMap<String, String> headers = new HashMap<String, String>();
      headers.put("referenceId", "my-reference-id");

      HashMap<String, String> body = new HashMap<String, String>();
      body.put("enableDashboard", "yes");
	    System.out.println(new HyperDocs().requestMethod("./sample.jpg", HyperDocs.InputType.IMAGE, headers, body, HyperDocs.EndPoint.READ_KYC));
	}
}
