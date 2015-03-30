package com.example.roobusapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RemainderAlaram extends Activity implements OnItemSelectedListener {

	private HashMap<String, String> loc_details = new HashMap<String, String>();
	private HashMap<Integer, String> loc_id_lat_lng = new HashMap<Integer, String>();

	private Spinner bus_spinner, route_spinner;
	private DBActivity dbActivity1;
	private String selected_route;
	private EditText input_min;
	private Button Rmind_Btn;
	private TextView Rmind_txt;
	private List<Integer> bus_stop_location = new ArrayList<Integer>();
	private List<Double> time_needed = new ArrayList<Double>();
	static BusProperties[] busprop;
	private int selected_location_position;
	private List<String> bust_list_names = new ArrayList<String>();
	private List<Integer> rotue_list = new ArrayList<Integer>();
	private int selected_route_index;
	double temp_time_remaing = Integer.MAX_VALUE;
	double alaram_end_time;
	private Handler handler;
	private Runnable statusChecker;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		//handler.removeCallbacks(null);
		Intent MainClassIntent = new Intent(RemainderAlaram.this,
				MainActivity.class);
		startActivity(MainClassIntent);
		finish();

	}
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent MainClassIntent = new Intent(RemainderAlaram.this,
				MainActivity.class);
		startActivity(MainClassIntent);
		finish();
		
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		setContentView(R.layout.alaramdetails);

		bus_spinner = (Spinner) findViewById(R.id.spinner1);
		route_spinner = (Spinner) findViewById(R.id.spinner2);
		input_min = (EditText) findViewById(R.id.predictText1);
		Rmind_txt = (TextView) findViewById(R.id.textView5);
		Rmind_Btn = (Button) findViewById(R.id.buttonRemind);
		dbActivity1 = new DBActivity(this);
		// new AccessBusData().execute();

		new AccessIdData().execute();

		Rmind_Btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(input_min.getWindowToken(), 0);

				if (input_min.getText().toString().isEmpty()) {
					alert_message("Input Error",
							"please Enter Input value for alaram to ring");
				} else {

					if (Integer.parseInt(input_min.getText().toString()) >= 10) {

						alert_message("Input Error",
								"please Enter Input value of lessthan 10 mins");

					} else {

						CheckRooBusStatus();
					}
				}

			}

			private void CheckRooBusStatus() {
				// TODO Auto-generated method stub

				final double alaram_time = Double.parseDouble(input_min
						.getText().toString());

				Rmind_Btn.setClickable(false);
				handler = new Handler();
				statusChecker = new Runnable() {
					@Override
					public void run() {

						new AccessBusInfo().execute();
						// Log.e("before", alaram_end_time + "");

						double temp_alaram_end_time = alaram_end_time
								- (alaram_end_time % 1);
						// Log.e("after", temp_alaram_end_time + "");
						if (alaram_time >= temp_alaram_end_time) {

							Log.e("true time called", "__");

							MessageSpoken MSObject = new MessageSpoken(
									getBaseContext());
							MSObject.MessageFromAlaram(selected_route,
									alaram_end_time);
							// MSObject.onDestroy();

							Rmind_Btn.setClickable(true);
							handler.removeCallbacks(this);
							handler.removeCallbacks(null);
							// onDestroy();
						}
						if (alaram_time < temp_alaram_end_time)
							handler.postDelayed(this, 8000);
					}
				};

				statusChecker.run();

			}

		});
	}

	public void alert_message(String title, String message) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertdialog = new AlertDialog.Builder(
				RemainderAlaram.this);
		alertdialog.setTitle(title);
		alertdialog.setMessage(message);
		alertdialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});

		alertdialog.show();

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.spinner1:
			TextView textpos = (TextView) arg1;
			selected_route = textpos.getText().toString();
			selected_route_index = rotue_list.get(arg2);
			route_spinner.setAdapter(null);
			new AccessRouteData().execute();

			break;
		case R.id.spinner2:
			selected_location_position = arg2;
			new AccessBusInfo().execute();
			break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private class AccessRouteData extends AsyncTask<String, String, String> {

		String selectQuery = null;
		SQLiteDatabase db2 = dbActivity1.getWritableDatabase();
		private List<String> route_list = new ArrayList<String>();

		@Override
		protected void onPreExecute() {

			loc_details.clear();
			route_list.clear();
			bus_stop_location.clear();
			time_needed.clear();
			loc_id_lat_lng.clear();

		}

		@Override
		protected String doInBackground(String... urls) {
			try {

				selectQuery = "SELECT LOCATION_NAME,LAT,LONG,ID,TIME FROM "
						+ selected_route.replace(" ", "_")
						+ " ORDER BY ROUTE_NO ASC";

				// //Log.e("selQuery", selectQuery);

				Cursor cursor = db2.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					do {

						String location_name = cursor.getString(cursor
								.getColumnIndex("LOCATION_NAME"));
						double lat = cursor.getDouble(cursor
								.getColumnIndex("LAT"));
						double lng = cursor.getDouble(cursor
								.getColumnIndex("LONG"));
						String lat_lng = lat + "," + lng;
						int bus_loc_id = cursor.getInt(cursor
								.getColumnIndex("ID"));
						double time_remaining = cursor.getDouble(cursor
								.getColumnIndex("TIME"));
						route_list.add(location_name);
						loc_details.put(location_name, lat_lng);
						bus_stop_location.add(bus_loc_id);
						time_needed.add(time_remaining);
						loc_id_lat_lng.put(bus_loc_id, lat_lng);

					} while (cursor.moveToNext());

				}
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

			ArrayAdapter<String> route_adapter = new ArrayAdapter<String>(
					RemainderAlaram.this,
					android.R.layout.simple_spinner_dropdown_item, route_list);
			route_spinner.setAdapter(route_adapter);
			route_spinner.setOnItemSelectedListener(RemainderAlaram.this);

		}

	}

	private class AccessIdData extends AsyncTask<String, String, String> {

		// selected_route data retrival
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... urls) {

			StringBuilder response = new StringBuilder();

			try {

				URL url = new URL(
						"http://akron.doublemap.com/map/v2/routes?private=show");
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

				JSONArray jsonArray = new JSONArray(jsonOutput);
				// int arr_length =jsonArray.length();
				// System.out.println("array length :"+arr_length);

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject JsonObject = jsonArray.getJSONObject(i);
					String bus_name = JsonObject.getString("name");
					bus_name = bus_name.replace("Dave's Supermarket Route",
							"DAVES_ROUTE");
					bus_name = bus_name.replace("Saturday", "");
					bus_name = bus_name.replace("9pm-Midnight", "");
					bus_name = bus_name.replace("Sunday", "");
					bus_name = bus_name.replace("7pm-Midnight", "");
					bus_name = bus_name.trim();
					// //Log.e("bus_name",bus_name);
					// bus_name = bus_name.replace(" ", "");
					bust_list_names.add(bus_name);
					rotue_list.add(JsonObject.getInt("id"));
				}
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

			if (bust_list_names.size() == 0) {

				alert_message(
						"Notification",
						"There are no Buses available now \n   (OR)  \n The service is temporarily down");

			} else {

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						RemainderAlaram.this,
						android.R.layout.simple_spinner_dropdown_item,
						bust_list_names);
				bus_spinner.setAdapter(adapter);
				bus_spinner.setOnItemSelectedListener(RemainderAlaram.this);
			}

		}

	}

	private class AccessBusInfo extends AsyncTask<String, String, String> {

		// String bus_time;

		// selected_route data retrival
		@Override
		protected void onPreExecute() {

			Log.e("AccessBusInfo", "called");
			// selected_route_index =;

		}

		@Override
		protected String doInBackground(String... urls) {

			temp_time_remaing = Integer.MAX_VALUE;

			StringBuilder response = new StringBuilder();
			try {
				URL url = new URL("http://akron.doublemap.com/map/v2/buses");
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
				JSONArray jArray = new JSONArray(jsonOutput);

				int number_of_valid_obj = 0;
				for (int b = 0; b < jArray.length(); b++) {
					JSONObject jsonObj = jArray.getJSONObject(b);
					if (selected_route_index == jsonObj.getInt("route")) {
						number_of_valid_obj++;
					}
				}

				busprop = new BusProperties[number_of_valid_obj];
				// Log.e("busprop","busprop object created ");

				int f = 0;
				for (int i = 0; i < jArray.length(); i++) {

					JSONObject jsonObj = jArray.getJSONObject(i);
					if (selected_route_index == jsonObj.getInt("route")) {
						// Log.e("------------------","-------------------------");
						busprop[f] = new BusProperties();
						busprop[f].setBus_id(jsonObj.getInt("id"));
						busprop[f].setLat(jsonObj.getDouble("lat"));
						busprop[f].setLng(jsonObj.getDouble("lon"));
						busprop[f].setRoute_no(jsonObj.getInt("route"));
						busprop[f].setLastStop(jsonObj.getInt("lastStop"));
						int recent_bus_stop_idx = bus_stop_location
								.indexOf(jsonObj.getInt("lastStop"));
						double time_taken = Integer.MAX_VALUE;

						if (recent_bus_stop_idx != -1) {

							time_taken = 0;
							int go_to_bus_stop_idx = recent_bus_stop_idx + 1;
							time_taken = cal_moving_distance(
									jsonObj.getDouble("lat") + ","
											+ jsonObj.getDouble("lon"),
									loc_id_lat_lng.get(bus_stop_location
											.get(go_to_bus_stop_idx
													% bus_stop_location.size())));

							if (recent_bus_stop_idx == selected_location_position && (jsonObj.getInt("heading") == 0 || time_taken <= 1)) 
							{
								 
									busprop[f].setTime_remaining(0);
									temp_time_remaing = 0;
								

							} else {

								while (go_to_bus_stop_idx
										% bus_stop_location.size() != selected_location_position) {
									time_taken += time_needed
											.get((go_to_bus_stop_idx + 1)
													% bus_stop_location.size()) + 0.2;

									go_to_bus_stop_idx++;

								}

								busprop[f].setTime_remaining(time_taken);
								if (busprop[f].getTime_remaining() <= temp_time_remaing) {

									temp_time_remaing = Double
											.parseDouble(new DecimalFormat(
													"#.##").format(busprop[f]
													.getTime_remaining()));
								}

							}
						}
						f++;
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private double cal_moving_distance(String src_lat, String dest_lat) {
			// TODO Auto-generated method stub

			// Log.e("function called ", "cal_moving_distance");
			double bus_time = 0;
			StringBuilder response = new StringBuilder();

			try {
				URL url = new URL(
						"http://maps.googleapis.com/maps/api/directions/json?origin="
								+ src_lat + "&destination=" + dest_lat
								+ "+&mode=driving&sensor=true");
				// Log.e("url", url + "");
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
				JSONObject object = new JSONObject(jsonOutput);
				JSONArray array = object.getJSONArray("routes");
				JSONObject routes = array.getJSONObject(0);
				String summary = routes.getString("summary");
				JSONArray legs = routes.getJSONArray("legs");
				JSONObject steps = legs.getJSONObject(0);
				JSONObject duration = steps.getJSONObject("duration");
				bus_time = Double.parseDouble(duration.getString("text")
						.replaceAll("([a-zA-Z])", ""));
				// Log.e("bus_time",bus_time+"");

			} catch (Exception e) {
				e.printStackTrace();

			}
			return bus_time;

		}

		@Override
		protected void onProgressUpdate(String... progress) {

		}

		@Override
		protected void onPostExecute(String result) {

			// /Log.e("updating the text", "post execute");

			alaram_end_time = temp_time_remaing;
			Rmind_txt.setText("Min time to catch the bus :" + alaram_end_time
					+ " Mins");

			if (alaram_end_time > 500) {
				alert_message(
						"Broadcast Error",
						selected_route
								+ "might not have started their serrvice  \n  \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t or \n The Bus is having technical errors broadcasting their current gps location");
			}

		}

	}

}
