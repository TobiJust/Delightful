package de.thwildau.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DelightfulHttpClient {

	/** The message appears if there is no network connection */
	private static final String NO_NETWORK = "{'id': -1,'message': 'Please check your network connection!'}";
	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds
	/** Single instance of our HttpClient */
	private static HttpClient dHttpClient;
	/** The URL to connect to */
	private String url;
	/** List of key value pairs with transmission data */
	private List<NameValuePair> data;
	/** Context from wich the HTTPConnection was requested */
	private Context context;

	public DelightfulHttpClient(String url, List<NameValuePair> data, Context context){
		this.url = url;
		this.data = data;
		this.context = context;
	}
	public void executeConnection(){
		new UrlConnector().execute(url);
	}

	/**
	 * Performs an HTTP GET request to the specified url.
	 * 
	 * @param url
	 *            The web address to post the request to
	 * @return The result of the request
	 * @throws Exception
	 */

	public static String executeHttpGet(String url) throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = getHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");

			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String result = sb.toString();

			return result.trim();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e("network", "Error converting result " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Performs an HTTP Post request to the specified url with the specified
	 * parameters.
	 * 
	 * @param url
	 *            The web address to post the request to
	 * @param postParameters
	 *            The parameters to send via the request
	 * @return The result of the request
	 * @throws Exception
	 */

	public String executeHttpPost(String url) throws Exception {

		BufferedReader in = null;
		try {

			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					data);

			request.setEntity(formEntity);
			Log.i("DATABASE", "Try Request to " + url + " " + data);
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line;

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

			in.close();
			String result = sb.toString();
			return result.trim();

		} catch (Exception e) {
			Log.e("network", "Error in request " + e.toString());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e("network", "Error converting result " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get our single instance of our HttpClient object.
	 * 
	 * @return an HttpClient object with connection parameters set
	 */

	private static HttpClient getHttpClient() {

		if (dHttpClient == null) {
			dHttpClient = new DefaultHttpClient();

			final HttpParams params = dHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return dHttpClient;
	}

	public class UrlConnector extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... urls) {
			String response = null;
			try {
				response = executeHttpPost(urls[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String response){
			if(response != null)
				ResponseHandler.handleResponse(context, response);
		}
	}
}