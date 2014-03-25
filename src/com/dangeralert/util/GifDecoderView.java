package com.dangeralert.util;

import java.io.InputStream;

import com.dangeralert.dangeralert.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

@SuppressLint("ViewConstructor")
public class GifDecoderView extends ImageView {
	
	private InputStream stream = null;
	private boolean mIsPlayingGif = false;
	private GifDecoder mGifDecoder;
	private Bitmap mTmpBitmap;
	final Handler mHandler = new Handler(Looper.getMainLooper());
	
	final Runnable mUpdateResults = new Runnable() {
	      public void run() {
	         if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
	            GifDecoderView.this.setImageBitmap(mTmpBitmap);
	         }
	      }
	};
	
	public GifDecoderView(Context context) {
		super(context);
		stream = context.getResources().openRawResource(R.drawable.radar_on);
		playGif(stream);
	}
	
	private void playGif(InputStream stream) {
	      mGifDecoder = new GifDecoder();
	      mGifDecoder.read(stream);
	      mIsPlayingGif = true;
	      
	      new Thread(new Runnable() {
	          public void run() {
	             final int n = mGifDecoder.getFrameCount();
	             final int ntimes = mGifDecoder.getLoopCount();
	             int repetitionCounter = 0;
	             do {
	               for (int i = 0; i < n; i++) {
	                  mTmpBitmap = mGifDecoder.getFrame(i);
	                  final int t = mGifDecoder.getDelay(i);
	                  mHandler.post(mUpdateResults);
	                  try {
	                     Thread.sleep(t);
	                  } catch (InterruptedException e) {
	                     e.printStackTrace();
	                  }
	               }
	               if(ntimes != 0) {
	                  repetitionCounter ++;
	               }
	            } while (mIsPlayingGif && (repetitionCounter <= ntimes));
	         }
	      }).start();
	}
	
}
