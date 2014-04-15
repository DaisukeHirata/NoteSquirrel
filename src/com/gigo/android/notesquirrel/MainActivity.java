package com.gigo.android.notesquirrel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	public static final String DEBUGTAG = "JWP";
	public static final String TEXTFILE = "notesquirrel.txt";
	public static final String FILESAVED = "filesaved";
	public static final String RESET_PASSPOINTS = "ResetPasspoints";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addSaveButtonListener();
		addLockButtonListener();
		addClearSaveDataFlag();
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		boolean filesaved = prefs.getBoolean(FILESAVED, false);
		if (filesaved) {
			loadSavedFile();
		}
	}

	private void loadSavedFile() {
		try {
			FileInputStream fis = openFileInput(TEXTFILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new DataInputStream(fis)));

			EditText editText = (EditText) findViewById(R.id.text);
			String line;
			while ((line = reader.readLine()) != null) {
				editText.append(line);
				editText.append("\n");
			}
			fis.close();
		} catch (Exception e) {
			Log.d(DEBUGTAG, "unable to read file");
		}

	}

	private void addLockButtonListener() {
		Button lockBtn = (Button) findViewById(R.id.lock);
		lockBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, ImageActivity.class);
				startActivity(i);
			}
		});

	}

	private void addSaveButtonListener() {
		Button saveBtn = (Button) findViewById(R.id.save);

		saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editText = (EditText) findViewById(R.id.text);
				String text = editText.getText().toString();
				try {

					FileOutputStream fos = openFileOutput(TEXTFILE,
							Context.MODE_PRIVATE);
					fos.write(text.getBytes());
					fos.close();

					setSharedPreferences(true);

				} catch (Exception e) {
					Toast.makeText(MainActivity.this, R.string.toast_cant_save,
							Toast.LENGTH_LONG).show();
					Log.d(DEBUGTAG, "unable to save file");
				}

			}
		});
	}

	private void addClearSaveDataFlag() {
		Button clearBtn = (Button) findViewById(R.id.clear_save_data_flag);

		clearBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSharedPreferences(false);
			}
		});
	}

	private void setSharedPreferences(boolean state) {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(FILESAVED, state);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_passpoints_reset) {
			Intent i = new Intent(this, ImageActivity.class);
			i.putExtra(RESET_PASSPOINTS, true);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_main, container,
					false);
			return rootView;
		}
	}

}
