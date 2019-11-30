package edu.sjsu.codepad.utils;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class AwsUtils {

	public static final String CHECK_IP_AWS_ENDPOINT = 
			"http://checkip.amazonaws.com";
	
    private static String doGetRequest(String url) throws IOException {
    	OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    
	public static String getMyPublicIP() {
		String result = "0.0.0.0";
		try {
			result = doGetRequest(CHECK_IP_AWS_ENDPOINT);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
}
