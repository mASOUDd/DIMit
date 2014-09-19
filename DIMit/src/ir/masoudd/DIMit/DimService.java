package ir.masoudd.DIMit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class DimService extends Service {

	private final IBinder myBinder = new LocalBinder();
	// This is the semi transparent layer
	myViewGroup myView;  

	// TODO: add color seekBars too.
	private int alpha = 200;
	private int red = 0;
	private int green = 0;
	private int blue = 0;

	class LocalBinder extends Binder {
		DimService getService() {
			return DimService.this;
		}
	}

	class myViewGroup extends ViewGroup {

		public myViewGroup(Context context) {
			super(context);
		}

		@Override
		public void onDraw(Canvas canvas) {
			canvas.drawARGB(alpha, red, green, blue);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		}
	}

	public DimService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d("debug_service", "onCreate");

		myView = null;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//TODO: I'm not sure if the super constructor needs to be called here
		super.onStartCommand(intent, flags, startId);
		
		Log.d("debug_service", "onStartCommand");
//		startDIM();
		
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.d("debug_service", "onDestroy");
		stopDIM();

	}

	// we have to invalidate the ViewGroup so that its onDraw gets called again.
	private void updateView() {
		if (myView != null) {
			myView.invalidate();
		}
	}

	/* public methods for the client to call from this service */

	public void startDIM() {
		Log.d("debug_service", "startDIM");
		if (myView == null) {
			myView = new myViewGroup(this);
			WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
							| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
					PixelFormat.TRANSLUCENT);

			WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
			wm.addView(myView, params);
		}
	}

	public void stopDIM() {
		Log.d("debug_service", "stopDIM");
		if (myView != null) {
			((WindowManager) getSystemService(WINDOW_SERVICE))
					.removeView(myView);
			myView = null;
		}
	}

	public void setAlpha(int alpha) {
		Log.d("debug_service", "setAlpha");

		// Nobody wants to change the alpha linearly.

		// this.alpha = (int)((-0.01) * (alpha * alpha) + (3.5 * alpha));
		this.alpha = (int) (Math.log10(alpha) * 124);
		updateView();
	}

}
