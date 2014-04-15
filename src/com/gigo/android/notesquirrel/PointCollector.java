package com.gigo.android.notesquirrel;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.graphics.Point;

@SuppressLint("DefaultLocale")
public class PointCollector implements OnTouchListener {
	
	public static final int NUM_POINTS = 4;
	private PointsCollectorListener listener;
	private List<Point> points = new ArrayList<Point>();
	
	public boolean onTouch(View v, MotionEvent event) {

		int x = Math.round(event.getX());
		int y = Math.round(event.getY());
		String message = String.format("Coordinates: (%d %d)", x, y);
		Log.d(MainActivity.DEBUGTAG, message);
		
		points.add(new Point(x, y));
		
		if (points.size() == NUM_POINTS) {
			if (listener != null) {
				listener.pointsCollected(points);
			}
		}
		
		return false;
	}

	public void setListener(PointsCollectorListener listener) {
		this.listener = listener;
	}
	
	public void clear() {
		points.clear();
	}
}
