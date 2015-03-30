package com.example.roobusapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TextView;


public class BusDetails extends Activity {

	private TextView src_dst_dsply, dst_dst_dsply, src_Loc, dst_Loc;
	DBActivity dbActivity1;
	private String BUS_NAME, roobus_list = "";
	private String src_geoPoint, dst_geoPoint;
	private WebView webview;
	private ProgressDialog progressDialog; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			BUS_NAME = extras.getString("BUS_NAME");
			//Log.e("bus_name :", BUS_NAME + "");
			src_geoPoint = extras.getString("SrcLatLng");
			//Log.e("src_geopoint", src_geoPoint + "");
			dst_geoPoint = extras.getString("DstLatLng");
			//Log.e("dst_geoPoint", dst_geoPoint + "");
			BUS_NAME = extras.getString("BUS_NAME").replace(" ", "_");
			//Log.e("bus name", BUS_NAME + "");
		}
		
		setContentView(R.layout.buslistvalues);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");

		spec.setContent(R.id.map);
		spec.setIndicator("Google Maps");
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.contentLayout);
		spec.setIndicator("More Details");
		tabs.addTab(spec);

		src_dst_dsply = (TextView) findViewById(R.id.textView4);
		dst_dst_dsply = (TextView) findViewById(R.id.textView3);
		src_Loc = (TextView) findViewById(R.id.textView8);
		dst_Loc = (TextView) findViewById(R.id.textView11);
		webview = (WebView) findViewById(R.id.map);

		// ////Log.e("2nd Activity","In the Bus Details page");

		dbActivity1 = new DBActivity(this);
	    //Log.e("Before ","Access Data");
		webview.loadUrl("file:///android_asset/showroute.html");
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setJavaScriptEnabled(true);
		new AccessDBActivity().execute();

	}

	private class AccessDBActivity extends AsyncTask<String, String, String> {

		String selectQuery = null;
		SQLiteDatabase db1 = dbActivity1.getWritableDatabase();
		String intial_loc = "";
		private Map<Integer, String> route_location = new HashMap<Integer, String>();

		String[] src_loc_dtls = new String[2];
		String[] dst_loc_dtls = new String[2];

		@Override
		protected void onPreExecute() {
			
			 //Create a new progress dialog  
            progressDialog = new ProgressDialog(BusDetails.this);  
            //Set the progress dialog to display a horizontal progress bar  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
            //Set the dialog title to 'Loading...'  
            progressDialog.setTitle("Loading Google Maps");  
            //Set the dialog message to 'Loading application View, please wait...'  
            progressDialog.setMessage("Displaying user GPS location and destination, please wait...");  
            //This dialog can't be canceled by pressing the back key  
            progressDialog.setCancelable(false);  
            //This dialog isn't indeterminate  
            progressDialog.setIndeterminate(false);  
            //The maximum number of items is 100    
            //Set the current progress to zero  
            progressDialog.setProgress(0);  
            //Display the progress dialog  
            progressDialog.show();  

		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				
				//Log.e("bus_name",BUS_NAME+"");
				selectQuery = "SELECT  ROUTE_NO,LOCATION_NAME,LAT,LONG FROM "
						+ BUS_NAME + " ORDER BY ROUTE_NO ASC";
				

				Cursor cursor = db1.rawQuery(selectQuery, null);
				//Log.e("ccursor ","started");
				if (cursor.moveToFirst()) {
					do {
						int route_no = cursor.getInt(cursor
								.getColumnIndex("ROUTE_NO"));
						double tmp_lat = cursor.getDouble(cursor
								.getColumnIndex("LAT"));
						double tmp_long = cursor.getDouble(cursor
								.getColumnIndex("LONG"));
						String Location_name = cursor.getString(cursor
								.getColumnIndex("LOCATION_NAME"));

						roobus_list += tmp_lat + "," + tmp_long + "|";

						// //Log.e("roobus list ",roobus_list);

						if (intial_loc.isEmpty())
							intial_loc = tmp_lat + "," + tmp_long;

						route_location.put(route_no, Location_name);
					} while (cursor.moveToNext());

				}
				//Log.e("cursor ","ended");

				// //Log.e("route lcation val",route_location+"");
				String temp_src_data = GetDistance(src_geoPoint, roobus_list);
				 //Log.e("temp_src_data",temp_src_data);
				 src_loc_dtls = temp_src_data.split("\\|");
				 //Log.e("src_loc_dtls[0]",src_loc_dtls[0]);
				 //Log.e("src_loc_dtls[1]",src_loc_dtls[1]);

				temp_src_data = GetDistance(dst_geoPoint, roobus_list);
				 //Log.e("temp_src_data",temp_src_data);
				dst_loc_dtls = temp_src_data.split("\\|");
				 //Log.e("dst_loc_dtls[0]",dst_loc_dtls[0]);
				 //Log.e("dst_loc_dtls[1]",dst_loc_dtls[1]);

				roobus_list += intial_loc;

			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... progress) {

		}

		@Override
		protected void onPostExecute(String result) {
           
			// //Log.e("route name",route_location.get(Integer.parseInt(src_loc_dtls[1])));
			src_dst_dsply.setText(src_loc_dtls[0] + " mi");
			src_Loc.setText(route_location.get(Integer.parseInt(src_loc_dtls[1])));
			//Log.e("src_loc_distance ",src_loc_dtls[0]+" mi");
			//Log.e("src_loc_name indx",Integer.parseInt(src_loc_dtls[1])+"");
			//Log.e("src_loc_name value",route_location.get(Integer.parseInt(src_loc_dtls[1])));
			

			dst_dst_dsply.setText(dst_loc_dtls[0] + " mi");
			dst_Loc.setText(route_location.get(Integer.parseInt(dst_loc_dtls[1])));
			//Log.e("dst_loc_distance ",dst_loc_dtls[0]+" mi");
			//Log.e("dst_loc_name indx",Integer.parseInt(dst_loc_dtls[1])+"");
			//Log.e("dst_loc_name value",route_location.get(Integer.parseInt(dst_loc_dtls[1])));


			src_dst_dsply.setVisibility(View.VISIBLE);
			dst_dst_dsply.setVisibility(View.VISIBLE);
			dst_Loc.setVisibility(View.VISIBLE);
			src_Loc.setVisibility(View.VISIBLE);
			roobus_list = src_loc_dtls[1] + "|" + dst_loc_dtls[1] + "|"
					+ src_geoPoint + "|" + dst_geoPoint + "|" + roobus_list;
			// //Log.e("roo list",roobus_list);

			webview.loadUrl("javascript:calcRoute(\"" + roobus_list + "\")");
			 progressDialog.dismiss(); 

		}

		private String GetDistance(String src_geoPoint, String dest) {

			String url_str = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="
					+ src_geoPoint
					+ "&destinations="
					+ dest
					+ "&mode=walking&sensor=true";
			 //Log.e("string url",url_str);
			double temp_dst = Integer.MAX_VALUE;
			int tmp_route_no = 0;

			NumberFormat formatter = new DecimalFormat("#0.00");
			StringBuilder response = new StringBuilder();

			try {
				URL url = new URL(url_str);
				HttpURLConnection httpconn = (HttpURLConnection) url
						.openConnection();

				if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {

					BufferedReader input = new BufferedReader(
							new InputStreamReader(httpconn.getInputStream()),
							8192);
					String strLine = null;

					while ((strLine = input.readLine()) != null) {
						response.append(strLine);
					}
					input.close();
				}

				String jsonOutput = response.toString();
				JSONObject jsonObject = new JSONObject(jsonOutput);
				JSONArray results_array = jsonObject.getJSONArray("rows");
				JSONObject Jobject = results_array.getJSONObject(0);
				JSONArray elem = Jobject.getJSONArray("elements");
				for (int x = 0; x < elem.length(); x++) {
					JSONObject elem0 = elem.getJSONObject(x);
					JSONObject dst = elem0.getJSONObject("distance");
					String name = (String) dst.get("text");
					// DecimalFormat twoDForm = new DecimalFormat("#.##");
					double dst_mi = Double.parseDouble(name.replaceAll(
							"[a-zA-Z\\s]", "")) * 0.621371;
					if (temp_dst >= dst_mi) {
						temp_dst = dst_mi;
						tmp_route_no = x + 1;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Log.e("dst and route no",formatter.format(temp_dst) + "|" + tmp_route_no+"");
			return formatter.format(temp_dst) + "|" + tmp_route_no;
		}

	}
}
