package com.mobile.core;

import roboguice.activity.RoboActivity;
import android.view.Menu;

import com.google.inject.Inject;
import com.mobile.core.R;
import com.mobile.core.persistence.DBHelper;

public class MainActivity extends RoboActivity {

	@Inject DBHelper dbHelper;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
