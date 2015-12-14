package com.univie.mz11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.lang.System;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public class OverlayView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String LOGTAG = "PaintMyRoom";
    private Camera myCamera;
    private SurfaceHolder myCameraSurfaceHolder;
    private SurfaceHolder myOverSurfaceHolder;
    private byte[] myFrame;
    private IntBuffer mFrameDiff;
    private Camera.Size mFrameSize;
    private boolean mRunning;

    private boolean takeMiddle = false;
    public int midColor;

    private native void doNativeProcessing(byte[] frame, int width, int height, IntBuffer diff);

    //Link to load the native class
    static
    {
        System.loadLibrary("mz11-native");
    }

    public OverlayView(Context c, AttributeSet attr)
    {
        super(c, attr);
        myOverSurfaceHolder = getHolder();
        myOverSurfaceHolder.addCallback(this);
    }

    public void setCamera(Camera c)
    {
        myCamera = c;
        if(myCamera == null) return;

        myCamera.setPreviewCallback(new PreviewCallback()
    {
		//If we get a frame
        public void onPreviewFrame(byte[] f, Camera c)
        {

            Camera.Parameters p = myCamera.getParameters();
        Camera.Size fs = p.getPreviewSize();

            //scale the image to the correct ratio
        if(fs.width != mFrameSize.width || fs.height != mFrameSize.height)
        {
            setPreviewSize(fs);
        }

        Canvas canvasOverlay = null;
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setColor(Color.WHITE);
        paint.setAlpha(0xFF);


        if(mRunning) canvasOverlay = myOverSurfaceHolder.lockCanvas(null);

        if(mRunning && canvasOverlay!=null)
        {
            try
            {
                //when colorFromCamera button has been pressed
                if(takeMiddle) {
                    Camera.Parameters parameters = c.getParameters();
                    int width = parameters.getPreviewSize().width;
                    int height = parameters.getPreviewSize().height;

                    YuvImage yuv = new YuvImage(f, parameters.getPreviewFormat(), width, height, null);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                    byte[] bytes = out.toByteArray();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    midColor = bitmap.getPixel(width/2,height/2);
                    Log.w(LOGTAG, Integer.toString(midColor));

                    takeMiddle = false;
                }
			//do the calculations for the edge detection
            System.arraycopy(f, 0, myFrame, 0, myFrame.length);
            doNativeProcessing(myFrame, mFrameSize.width, mFrameSize.height, mFrameDiff);
            mFrameDiff.position(0);

            Bitmap bmp = Bitmap.createBitmap(mFrameSize.width, mFrameSize.height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(mFrameDiff);
            Rect src = new Rect(0, 0, (mFrameSize.width)-1, (mFrameSize.height)-1);
            RectF dst = new RectF(0, 0, canvasOverlay.getWidth()-1, canvasOverlay.getHeight()-1);

                //draw the overlay to the screen

            canvasOverlay.drawBitmap(bmp, src, dst, paint);
            }
            finally
            {
            myOverSurfaceHolder.unlockCanvasAndPost(canvasOverlay);
            }
        }
        }
    });
    }

    public void setRunning(boolean r)
    {
        mRunning = r;
    }

    public void setPreviewSize(Camera.Size s)
    {
        mFrameSize = s;
		myFrame = new byte[s.width*s.height];
    	mFrameDiff = ByteBuffer.allocateDirect((s.width*s.height)<<2).asIntBuffer();
    }

    public void surfaceCreated(SurfaceHolder sh)
    {
    }

    public void surfaceDestroyed(SurfaceHolder sh)
    {
    }

    public void surfaceChanged(SurfaceHolder sh, int format, int w, int h)
    {
    }

    public int getMiddleColor(){
        takeMiddle = true;
        return midColor;
    }


}
