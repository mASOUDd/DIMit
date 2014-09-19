package ir.masoudd.DIMit;

import ir.masoudd.DIMit.DimService.LocalBinder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private DimService myService;
	private ServiceConnection myConn;
	private SeekBar alphaSeekBar;

	class mySeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			myService.setAlpha(progress);

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}

	public void startService(View view) {

		Intent intent = new Intent(this, DimService.class);
		startService(intent);
		myService.startDIM();

	}

	public void stopService(View view) {

		myService.stopDIM();
		Intent intent = new Intent(this, DimService.class);
		stopService(intent);
	}

	public void showToast(String mesg) {
		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("masoud", "onCreate");

		alphaSeekBar = (SeekBar) findViewById(R.id.skbar_alpha);
		myConn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				LocalBinder binder = (LocalBinder) service;
				myService = binder.getService();
				alphaSeekBar
						.setOnSeekBarChangeListener(new mySeekBarListener());
				myService.setAlpha(alphaSeekBar.getProgress());
			}
		};

		SeekBar alphaSeekBar = (SeekBar) findViewById(R.id.skbar_alpha);

	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d("masoud", "onStart");
		// bind to DimService
		Intent intent = new Intent(this, DimService.class);
		Log.d("debug_service", "MainActivity: binding to DimService");
		bindService(intent, myConn, BIND_AUTO_CREATE);

		int progress = getPreferences(MODE_PRIVATE).getInt("alpha", 20);
		alphaSeekBar.setProgress(progress);

	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.d("masoud", "onStop");
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putInt("alpha", alphaSeekBar.getProgress());
		editor.commit();
		
		Log.d("debug_service", "MainActivity: unbinding from DimService");
		unbindService(myConn);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		Log.d("masoud", "onRestoreInstanceState");
		alphaSeekBar.setProgress(savedInstanceState.getInt("alpha"));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("masoud", "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		outState.putInt("alpha", alphaSeekBar.getProgress());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.about_menu:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
			
		default:
			return false;
		}
			
	}
}
