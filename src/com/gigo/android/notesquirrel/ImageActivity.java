package com.gigo.android.notesquirrel;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends ActionBarActivity implements
		PointsCollectorListener {

	private static final String PASSWORD_SET = "PASSWORD_SET";
	private static final int POINT_CLOSENESS = 40;
	private PointCollector pointCollector = new PointCollector();
	private Database db = new Database(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		addTouchListener();

		pointCollector.setListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Boolean resetPasspoints = extras.getBoolean(MainActivity.RESET_PASSPOINTS);
			if (resetPasspoints) {
				
			}
		}
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);
		if (!passpointsSet) {
			showSetPasspointsPrompt();
		}

	}

	private void showSetPasspointsPrompt() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}
		});
		builder.setTitle("Create your Passpoint Sequence");
		builder.setMessage("Touch four points on the image to set the passpoint sequence. you must click the same points in future to gain access to your note");
		AlertDialog dlg = builder.create();
		dlg.show();
	}

	private void addTouchListener() {
		ImageView image = (ImageView) findViewById(R.id.touch_image);
		image.setOnTouchListener(pointCollector);
	}

	private void savePasspoints(final List<Point> points) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("storing...");
		final AlertDialog dlg = builder.create();
		dlg.show();

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				db.storePoints(points);
				Log.d(MainActivity.DEBUGTAG, "points saved: " + points.size());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				SharedPreferences prefs = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(PASSWORD_SET, true);
				editor.commit();

				pointCollector.clear();
				dlg.dismiss();

			}
		};

		task.execute();
	}

	private void verifyPasspoints(final List<Point> touchedPoints) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Checking passpoints...");
		final AlertDialog dlg = builder.create();
		dlg.show();

		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				List<Point> savedPoints = db.getPoints();
				Log.d(MainActivity.DEBUGTAG,
						"Loaded points: " + savedPoints.size());

				if (savedPoints.size() != PointCollector.NUM_POINTS
						|| touchedPoints.size() != PointCollector.NUM_POINTS) {
					return false;
				}
				
				for (int i=0; i < PointCollector.NUM_POINTS; i++) {
					Point savedPoint = savedPoints.get(i);
					Point touchedPoint = touchedPoints.get(i);
					
					int xDiff = savedPoint.x - touchedPoint.x;
					int yDiff = savedPoint.y - touchedPoint.y;
					
					int distSquared = xDiff*xDiff + yDiff*yDiff;
					Log.d(MainActivity.DEBUGTAG, "Distance squared: " + distSquared);
					
					if (distSquared > POINT_CLOSENESS*POINT_CLOSENESS) {
						return false;
					} 
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean pass) {
				dlg.dismiss();
				pointCollector.clear();
				if (pass) {
					Intent i = new Intent(ImageActivity.this,
							MainActivity.class);
					startActivity(i);
				} else {
					Toast.makeText(ImageActivity.this, "Access Denied",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		task.execute();
	}

	@Override
	public void pointsCollected(final List<Point> points) {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

		if (!passpointsSet) {
			Log.d(MainActivity.DEBUGTAG, "Saving passpoints...");
			savePasspoints(points);
		} else {
			Log.d(MainActivity.DEBUGTAG, "Verifying passpoints...");
			verifyPasspoints(points);
		}
	}

}
