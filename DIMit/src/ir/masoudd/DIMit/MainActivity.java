package ir.masoudd.DIMit;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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

	private SeekBar alphaSeekBar;
	private volatile int alphaPercent = 0;
	private boolean started = false;

	class mySeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Log.d("masoud",
					String.format("alphaPercent changed to %d", progress));
			alphaPercent = progress;
			if (started) {
				// update the alpha of the DimService with a new intent
				startDimService(null);
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}

	public void startDimService(View view) {
		started = true;

		Intent intent = new Intent(this, DimService.class);
		intent.putExtra("alphaPercent", alphaPercent);
		startService(intent);

	}

	public void stopDimService(View view) {

		started = false;
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
		alphaSeekBar.setOnSeekBarChangeListener(new mySeekBarListener());

	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d("masoud", "onStart");

		alphaPercent = getSharedPreferences("settings", MODE_PRIVATE).getInt(
				"alphaPercent", 20);
		alphaSeekBar.setProgress(alphaPercent);

	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.d("masoud", "onStop");

		Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
		editor.putInt("alphaPercent", alphaSeekBar.getProgress());
		editor.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_menu:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;

		default:
			return false;
		}

	}
}
