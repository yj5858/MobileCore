package com.mobile.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.mobile.sample.R;


public class NextActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		setContentView(R.layout.activity_next);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
