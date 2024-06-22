package com.app.priorityone_uc.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.app.priorityone_uc.UpdateProfile;

public class UploadImageCall extends AsyncTask<String, String, Boolean> {
	
	Activity activity;
	ProgressDialog pd;
	
	String value, errorType = "", url;
	static JSONObject jsonObject = null,userInfoObject = null;

    SharedPreferences prefs;
	String msg, status;
	
	String imageName;
	
	public UploadImageCall(Activity activity) {
		this.activity = activity;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		pd = new ProgressDialog(activity);
//		pd.setTitle("Uploading image on server");
		//pd.setIcon(R.drawable.ic_launcher);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("Updating profile image...");
		pd.show();

			//url = "http://vegatechnologies.net/self/app2/lib/";
			//url = "http://vegatechnologies.net/self/app2/lib/";
	}

	
	
	@Override
	protected Boolean doInBackground(String... params) {
		

		
		url = DATA.baseUrl+"userProfileImg";

		try {

			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;

			String pathToOurFile = DATA.imagePath;
			System.out.println("--path: "+pathToOurFile);

			String urlServer = url;
			System.out.println("--urlServer: "+urlServer);

			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary =  "*****";

			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;

			try
			{
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method 
			connection.setRequestMethod("POST");

			 imageName = prefs.getString("id", "")+"_"+prefs.getString("subPatientID", "") + "_patient_image.jpg";
				System.out.println("--online care IMAGE NAME: "+imageName);
			
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imageName +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				System.out.println("--bytesRead: "+bytesRead);
				System.out.println("--bytesAvailable: "+bytesAvailable);

			outputStream.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
		 	int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			
			msg = serverResponseMessage;
			status = ""+serverResponseCode;
			
			System.out.println("--online care msg: "+msg);
			System.out.println("--online care status: "+status);
			
			if(msg.equals("OK")) {
				
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                value = sb.toString();
                
                System.out.println("--value in image upload response "+value);
                
                jsonObject = new JSONObject(value);
                				
				SharedPreferences.Editor ed = prefs.edit();
				ed.putString("image",jsonObject.getString("image"));
				ed.commit();
                
                                   
			}
			
			else {
				errorType = "j";
			}
			
			
			
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			}
			catch (Exception ex)
			{
			//Exception handling
				System.out.println("--exception ex: "+ex);

			}
		} catch (Exception e) {
			errorType = "j";

			System.out.println("--exception: "+e);
			e.printStackTrace();
		}		

		
		return true;
		
		
	}
	
	
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		
		
		System.out.println("--errorType: "+errorType);
		System.out.println("--value in onPostExecute: "+value);
		
	if(errorType.equals("I")) {
		
		DATA.imagePath = "";
		DATA.isImageCaptured = false;

		
		Toast.makeText(activity, "No Internet connection found", 0).show();
	}
	else if(errorType.equals("j")) {
		
		DATA.imagePath = "";
		DATA.isImageCaptured = false;

		
		Toast.makeText(activity, "Something went wrong, please try again", 0).show();
	}

	else {

		//Toast.makeText(activity, "Image uploaded...", 0).show();
		
		((UpdateProfile)activity).setResultsFromImageUpload();

	}	
		
		pd.dismiss();
	}

}
