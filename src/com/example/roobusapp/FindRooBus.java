package com.example.roobusapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FindRooBus extends Activity implements OnClickListener,
		AdapterView.OnItemClickListener {

	protected static final int RESULT_SPEECH = 1;
	private ImageView btnSpeak1, btnSpeak2;
	private EditText tetxtTo, tetxtFrom;
	private String speak_flag1, speak_flag2;
	DBActivity dbActivity;
	ArrayList<String> busList;
	private ListView lstview;
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	public static List<String> bust_routes_list = new ArrayList<String>();
	public static List<Integer> id_list = new ArrayList<Integer>();
	public SimpleAdapter adapter;
	public Intent busIntent;
	String[] long_lat = new String[2];
	String SrcLatLng, DstLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routes);

		lstview = (ListView) findViewById(R.id.listView1);
		btnSpeak2 = (ImageView) findViewById(R.id.imageBtn2);
		btnSpeak1 = (ImageView) findViewById(R.id.imageBtn1);
		tetxtFrom = (EditText) findViewById(R.id.editText1);
		tetxtTo = (EditText) findViewById(R.id.editText2);
		dbActivity = new DBActivity(this);

		
		tetxtTo.setHint("Student Union");
		tetxtFrom.setHint("518 Allyn St");
		//tetxtTo.setText("Student Union");
		new BusDataInfo().execute();

		ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {

			alert_message("Network Error..", " please connect to Internet");
		} else {

			btnSpeak1.setOnClickListener(this);
			btnSpeak2.setOnClickListener(this);

			adapter = new SimpleAdapter(FindRooBus.this, list,
					R.layout.listvalue,
					new String[] { "bus_name", "bus_avail" }, new int[] {
							R.id.bus_name, R.id.bus_availble });

		}

	}

	private void UpdateListView() {

		list.clear();
		ArrayList<HashMap<String, String>> available_list = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> offline_list = new ArrayList<HashMap<String, String>>();

		// Log.e("list values in FindRooBus :", bust_routes_list + "");

		String[] busListArray = getResources().getStringArray(
				R.array.res_bus_list_array);
		// Log.e("busListArray", busListArray + "");

		for (int i = 0; i < busListArray.length; i++) {

			if (bust_routes_list.indexOf(busListArray[i]) != -1) {

				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("bus_name", busListArray[i]);
				temp.put("bus_avail", "Service Available now");
				available_list.add(temp);
				// Log.e("available list values :",available_list+"");
			} else {

				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("bus_name", busListArray[i]);
				temp.put("bus_avail", " ");
				offline_list.add(temp);
				// Log.e("offline list values :",available_list+"");
			}

		}
		list.addAll(available_list);
		list.addAll(offline_list);

	}

	public void alert_message(String title, String message) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertdialog = new AlertDialog.Builder(
				FindRooBus.this);
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
	public void onClick(View v) {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

		try {
			startActivityForResult(intent, RESULT_SPEECH);

			switch (v.getId()) {
			case R.id.imageBtn1:
				tetxtFrom.setText("");
				speak_flag1 = "Activate";
				speak_flag2 = null;

				break;
			case R.id.imageBtn2:
				tetxtTo.setText("");
				speak_flag2 = "Activate";
				speak_flag1 = null;
				break;

			}
		} catch (ActivityNotFoundException a) {
			Toast t = Toast.makeText(getApplicationContext(),
					"Opps! Your device doesn't support Speech to Text",
					Toast.LENGTH_SHORT);
			t.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (speak_flag1 == "Activate")
					tetxtFrom.setText(text.get(0));

				if (speak_flag2 == "Activate") {
					tetxtTo.setText(text.get(0));
				}
			}
			break;
		}

		}
	}

	private class BusDataInfo extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			bust_routes_list.clear();
			id_list.clear();
		}

		@Override
		protected Void doInBackground(Void... urls) {

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

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject JsonObject = jsonArray.getJSONObject(i);
					String bus_name = JsonObject.getString("name");
					bus_name = bus_name.replace("Dave's Supermarket Route","Daves Route");
					bus_name = bus_name.replace("Saturday", "");
					bus_name = bus_name.replace("9pm-Midnight", "");
					bus_name = bus_name.replace("Sunday", "");
					bus_name = bus_name.replace("7pm-Midnight", "");
					bus_name = bus_name.trim();

					bust_routes_list.add(bus_name);
					Log.e("bus name added", bus_name);
					id_list.add(JsonObject.getInt("id"));

				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			UpdateListView();
			lstview.setAdapter(adapter);
			lstview.setOnItemClickListener(FindRooBus.this);

		}

	}

	private class getLatLong extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {

			String stringUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
					+ tetxtFrom.getText().toString().replace(" ", "+")
					+ ",+AKRON,+OH&sensor=true_or_false";

			getLatLongfun(stringUrl);

			SrcLatLng = Double.parseDouble(long_lat[1]) + ","
					+ Double.parseDouble(long_lat[0]);

			stringUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
					+ tetxtTo.getText().toString().replace(" ", "+")
					+ ",+AKRON,+OH&sensor=true_or_false";

			getLatLongfun(stringUrl);

			DstLatLng = Double.parseDouble(long_lat[1]) + ","
					+ Double.parseDouble(long_lat[0]);

			return null;

		}

		@Override
		protected void onPostExecute(String result) {

			busIntent.putExtra("SrcLatLng", SrcLatLng);
			busIntent.putExtra("DstLatLng", DstLatLng);
			// Log.e("Activty","New Activity started");
			startActivity(busIntent);

		}

		private void getLatLongfun(String stringUrl) {

			// Log.e("background", "In the function");
			StringBuilder response = new StringBuilder();
			try {
				URL url = new URL(stringUrl);
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
				JSONArray results_array = jsonObject.getJSONArray("results");
				JSONObject Jobject = results_array.getJSONObject(0);
				JSONObject geo = Jobject.getJSONObject("geometry");
				JSONObject location = geo.getJSONObject("location");
				String latandlong = location.toString().replaceAll("[{}:\"]","");
				latandlong = latandlong.replaceAll("[a-z]", "");
				long_lat = latandlong.split(",");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		TextView txtview = (TextView) arg1.findViewById(R.id.bus_name);

		if (tetxtFrom.getText().toString().isEmpty()
				|| tetxtTo.getText().toString().isEmpty()) {

			alert_message("Error ..", "Enter correct From & To Address");

		} else {
			new getLatLong().execute();
			Log.e("click values :", txtview.getText().toString() + "");
			busIntent = new Intent(this, BusDetails.class);
			busIntent.putExtra("BUS_NAME", txtview.getText().toString());

		}

	}

}
