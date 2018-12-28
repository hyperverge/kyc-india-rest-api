import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;


public class HVMultipartPost {

    private static final String LOG_TAG = "HVMultipartPost";
    /**
     * Constants related to multipart entity.
     */
    private static final String lineEnd = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "*****";
    /**
     * Transmission variables
     */
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    /**
     * Setup URL connection objects.
     */
    private HttpURLConnection conn;
    private DataOutputStream dos;
//    private FileOutputStream fos;


    /**
     * Constructor for the multipart post. Need to provide the url of the post request here.
     *
     * @param mUrl URL for the post request
     * @throws IOException
     */
    public HVMultipartPost(String mUrl) throws IOException {
        URL url = new URL(mUrl);
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true); // Allow Inputs
        conn.setDoOutput(true); // Allow Outputs
        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        /**
         * Start the outputstream
         */
        dos = new DataOutputStream(conn.getOutputStream());
//        dos = new DataOutputStream(fos);
    }

    /**
     * Constructor for the multipart post. Need to provide the url of the post request here.
     *
     * @param mUrl URL for the post request
     * @throws IOException
     */
    public HVMultipartPost(String mUrl, JSONObject headers) throws IOException, JSONException {
        URL url = new URL(mUrl);
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true); // Allow Inputs
        conn.setDoOutput(true); // Allow Outputs
        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setChunkedStreamingMode(0);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");

        /**
         * Adding headers from the request.
         */
        Iterator<?> keys = headers.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            conn.setRequestProperty(key, headers.getString(key));
        }


        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        /**
         * Start the outputstream
         */
        dos = new DataOutputStream(conn.getOutputStream());
//        dos = new DataOutputStream(fos);
    }

    /**
     * Method to add File entity to the post request
     *
     * @param key      Key name of the file
     * @param filePath Path to required file on storage
     * @throws IOException
     */
    public void addFileEntity(String key, String contentType, String filePath) throws IOException {
        /**
         * Extracting file name from the given path.
         */
        String[] splits = filePath.split("/");
        String fileName;
        if (splits.length > 1) {
            fileName = splits[splits.length - 1];
        } else {
            fileName = filePath;
        }

        /**
         * Adding image file parameter
         */
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\";filename=\"" + fileName + "\"" + lineEnd);
        dos.writeBytes("Content-Type: " + contentType + lineEnd);
        dos.writeBytes(lineEnd);

        /**
         * Adding the image file byte stream
         */
        FileInputStream fileInputStream = new FileInputStream(filePath);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        fileInputStream.close();

        dos.writeBytes(lineEnd);
    }

    /**
     * Method to add text entity to the post
     *
     * @param key   Key of the entity
     * @param value Value of the entity
     * @throws IOException
     */
    public void addTextEntity(String key, String value) throws IOException {
        /**
         * Adding Text entity
         */
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(value);
        dos.writeBytes(lineEnd);


    }

    /**
     * This method executes the Post request and returns the response body as a string
     *
     * @return Response body
     * @throws IOException
     */
    public String executeRequest() throws IOException {
        /**
         * Flushing and closing the data stream
         */
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        dos.flush();
        dos.close();

        /**
         * Running the request
         */
        int serverResponseCode = conn.getResponseCode();
        String serverResponseMessage = conn.getResponseMessage();
        String responseBody = "";

        //TODO Handling of response codes
//        if (serverResponseCode == 200) {
        InputStream inputStream = null;
        /**
         * If the request is successful, read the response from the body
         */
        try {
            inputStream = conn.getInputStream();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            inputStream = conn.getErrorStream();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((inputStream)));


        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
            responseBody = sb.toString();
        }
        return responseBody;
    }
}