    package com.univie.mz11;

    import android.app.Activity;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.PixelFormat;
    import android.hardware.Camera;
    import android.hardware.SensorManager;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.OrientationEventListener;
    import android.view.SurfaceHolder;
    import android.view.SurfaceView;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;

    public class MainActivity extends Activity implements SurfaceHolder.Callback
    {
        private static final String LOGTAG = "MainActivity";

        private Camera mCamera = null;
        private OverlayView mOverSV;
        private OrientationEventListener mOEL;
        private Camera.Size mPreviewSize = null;
        private boolean mPreview = false;
        private int mOrient = 0;


        private View changeColor;
        private boolean selectColor = false;


        // Called when the activity is first created.
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
        	super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.main);

            getWindow().setFormat(PixelFormat.TRANSLUCENT);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			//onClickListener for ColorSelection
            changeColor = findViewById(R.id.currentColorView);
            changeColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(getApplicationContext(),ColorActivity.class);
                    startActivity(in);
                    selectColor = true;
                }
            });

			//onClickListener for colorOverCamerSelection
            View changeColorFromCamera = findViewById(R.id.screenshotView);
            changeColorFromCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeColor.setBackgroundColor(mOverSV.getMiddleColor());
                }
            });
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
        }

        @Override
        public void onStart()
        {
            initCamera();
        super.onStart();
        }

        @Override
        public void onResume()
        {
            if(selectColor) {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                int savedPref = sharedPreferences.getInt("6C", -256);
                Log.w(LOGTAG, Integer.toString(savedPref));
                changeColor.setBackgroundColor(savedPref);
                selectColor = false;
            }
            initCamera();
        super.onResume();
        }

        @Override
        public void onPause()
        {
            stopCamera();
            super.onPause();
        }

        public void surfaceCreated(SurfaceHolder sh)
        {
        }


        //Getting all information from the camera (Orientation, Size etc..); then start the preview
        public void surfaceChanged(SurfaceHolder sh, int format, int w, int h)
        {
    //    	Log.v(LOGTAG, "Surface parameters changed: "+w+"x"+h);
        if(mCamera != null)
        {
			if(mPreview) mCamera.stopPreview();
            Camera.Parameters p = mCamera.getParameters();
            p.setRotation(mOrient);
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            for(Camera.Size s : p.getSupportedPreviewSizes())
            {
				p.setPreviewSize(s.width, s.height);
				mOverSV.setPreviewSize(s);
				mPreviewSize = s;
                break;
            }
            mCamera.setParameters(p);
            try
            {
            	mCamera.setPreviewDisplay(sh);
            }
            catch(Exception e)
            {
    	    	Log.e(LOGTAG, "Camera preview not set");
            }
            mCamera.startPreview();
            mPreview = true;
        }
        }

        public void surfaceDestroyed(SurfaceHolder sh)
        {
        }

        //Initalize the camera
        private void initCamera()
        {
            SurfaceView	mCamSV = (SurfaceView)findViewById(R.id.surface_camera);
            SurfaceHolder mCamSH = mCamSV.getHolder();
			mCamSH.addCallback(this);

        	mOverSV = (OverlayView)findViewById(R.id.surface_overlay);
        	mOverSV.getHolder().setFormat(PixelFormat.TRANSLUCENT);

            if(mCamera == null)
        {
            mCamera = Camera.open();
        }
        	if(mPreviewSize != null && mPreviewSize.width > 0 && mPreviewSize.height > 0)
        {
            mOverSV.setPreviewSize(mPreviewSize);
        }

			mOverSV.setCamera(mCamera);
			mOverSV.setRunning(true);
        	mPreview = false;
            //Find the right orientation with the help of the Sensor
        	mOEL = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL)
        	{
            	@Override
            	public void onOrientationChanged(int orientation)
            	{
            		if(orientation == ORIENTATION_UNKNOWN) return;
            		orientation = (orientation + 45) / 90 * 90;
            		mOrient = orientation % 360;
            	}
        	};
        	if(mOEL.canDetectOrientation()) mOEL.enable();
        	}

        //Stop the camera and release it
        private void stopCamera()
        {
            mOEL.disable();
        	mOverSV.setRunning(false);
        	mCamera.stopPreview();
        	mPreview = false;
        	mCamera.setPreviewCallback(null);
        	mCamera.release();
        	mCamera = null;
        }
    }
