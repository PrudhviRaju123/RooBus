package com.example.roobusapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button RemainderButton, FindRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Reading the Layout values */
		RemainderButton = (Button) findViewById(R.id.ReminderAalarm);
		FindRoute = (Button) findViewById(R.id.FindRoutes);

		RemainderButton.setOnClickListener(this);
		FindRoute.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.ReminderAalarm:
                  			
			Intent RemainderIntent = new Intent(MainActivity.this , RemainderAlaram.class);
			startActivity(RemainderIntent);
			break;

		case R.id.FindRoutes:			
			Intent FindRoutesIntent = new Intent (MainActivity.this ,FindRooBus.class);
			startActivity(FindRoutesIntent);
			
			break;

		}

	}

}
