package com.dangeralert.dangeralert;

import java.util.ArrayList;

import com.dangeralert.util.DistanceAlertManager;
import com.dangeralert.util.FilesManager;
import com.dangeralert.util.GifDecoderView;
import com.dangeralert.util.LocationUtil;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity{
	public static final String LATLNGGPSNET_MESSAGE = "com.dangeralert.dangeralert.LATLNGGPSNET_MESSAGE";
	private LocationManager locationManager;
	private TextView mTextView2;
	private TextView mTextView3;
	private boolean GPSOk = false;
	private boolean UOAActivate = false;
	private Location currentLocation = null;
	private LocationUtil mLocationUtil;
	private float velocityAlert; //Velocidade em kilometros/hora
	private float velocityAlertMeters; //Velocidade em metros/segundos
	
	private GifDecoderView mGifDecoderView;
	private int displayWidth;
	private int displayHeight;
	private boolean loadOk = false;
	private Handler mHandler = new Handler();
	Context context = this;
	
	// escutador para localização via GPS (pega as coordenadas geograficas do dispositivo)
	private final LocationListener listener = new LocationListener() {
	    @Override
	    public void onLocationChanged(Location location) {
	    	if(mLocationUtil.isBetterLocation(location, currentLocation)){ // se o Location novo é melhor que o atual...
	    		currentLocation = location;
		    	mTextView2.setText(location.getLatitude() + ", " + location.getLongitude());
		    	velocityAlert = location.getSpeed()*(float)3.6;
		    	velocityAlertMeters = location.getSpeed(); //Velocidade em metros/segundos
		    	mTextView3.setText(String.valueOf(velocityAlert)+"Km/h");
		    	if(UOAActivate) // verifica proximidade de problemas
		    		sendsAlert(location);
	    	}
	    	else{
	    		mTextView2.setText(currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
	    		velocityAlert = currentLocation.getSpeed()*(float)3.6;
	    		velocityAlertMeters = location.getSpeed(); //Velocidade em metros/segundos
	    		mTextView3.setText(String.valueOf(velocityAlert)+"Km/h");
		    	if(UOAActivate) // verifica proximidade de problemas
		    		sendsAlert(currentLocation);
	    	}
	    		
	    }
		@Override public void onProviderDisabled(String arg0) {}
		@Override public void onProviderEnabled(String arg0) {}
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// criação de um arquivo de notificações para testes na UFBA ondina
		FilesManager mFilesManager = new FilesManager();
		mFilesManager.createPreFile(context);
		//////////////////////////////////////////////

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // barra de progresso de tempo indeterminado
		setContentView(R.layout.activity_main);
		setProgressBarIndeterminateVisibility(false); // oculta a barra de progresso
		
		mLocationUtil = new LocationUtil();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // serviço de localização
		mTextView2 = (TextView) findViewById(R.id.Main_textView2);
		mTextView3 = (TextView) findViewById(R.id.Main_textView3);
		
		Button mButton2 = (Button) findViewById(R.id.Main_button2); // botão de desativar
		mButton2.setEnabled(false); // desabilita o botão de desativar
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size); // pega o tamanho do display
		displayWidth = size.x; // largura do display
		displayHeight = size.y; // altura do display
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onStart() {
		super.onStart();
		
		final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!gpsEnabled) {
			NoGPSDialogFragment df = new NoGPSDialogFragment();
			df.show(getFragmentManager(), "Exiting");
			GPSOk = false;
	    }
		else{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener); // instacia o evento de escuta da localização por GPS
			if(netEnabled)
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener); // instacia o evento de escuta da localização por Internet
			GPSOk = true;
		}
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    locationManager.removeUpdates(listener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("static-access")
	public void activate(View view) {
		if(GPSOk){
			if(!mTextView2.getText().equals("Search LatLng (Wait)")){
				setProgressBarIndeterminateVisibility(true);
				
				new Thread(new Runnable() {
		            @SuppressLint("NewApi")
					public void run() {
		            	if (!loadOk)
		            		mGifDecoderView = new GifDecoderView(context); /////
		    			loadOk = true;
		    			
		                mHandler.post(new Runnable() {
		                    public void run() {
		                    	UOAActivate = true;
		                		mTextView2.setTextColor(getResources().getColor(R.color.actMain_tx3_green));
		                		mTextView3.setTextColor(getResources().getColor(R.color.actMain_tx3_green));
		                		
		                		ImageView mImageView = (ImageView) findViewById(R.id.Main_imageView1);
		                		mImageView.setVisibility(mImageView.INVISIBLE);
		                		
		                		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.Main_layout1);
		                    	
		                    	Button mButton1 = (Button) findViewById(R.id.Main_button1);
		                		mButton1.setEnabled(false);
		                		
		                		Button mButton2 = (Button) findViewById(R.id.Main_button2);
		                		mButton2.setEnabled(true);
		                		
		                		mGifDecoderView.setX(displayWidth/2 - 200); /////215
		                		mGifDecoderView.setY(displayHeight/2 - 310); /////315
		            			mRelativeLayout.addView(mGifDecoderView, 0); /////1
		            			
		            			setProgressBarIndeterminateVisibility(false);
		                    }
		                });
		            }
		        }).start();
			}
			else{
				NoLatLngDialogFragment df = new NoLatLngDialogFragment();
				df.show(getFragmentManager(), "Exiting");
			}
		}
		else{
			NoGPSDialogFragment df = new NoGPSDialogFragment();
			df.show(getFragmentManager(), "Exiting");
		}
	}
	
	@SuppressWarnings("static-access")
	public void deactivate(View view) {
		UOAActivate = false;
		mTextView2.setTextColor(getResources().getColor(R.color.actMain_tx2_red));
		mTextView3.setTextColor(getResources().getColor(R.color.actMain_tx2_red));
		
		ImageView mImageView = (ImageView) findViewById(R.id.Main_imageView1);
		
		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.Main_layout1);
		mRelativeLayout.removeView(mGifDecoderView); /////
		mImageView.setVisibility(mImageView.VISIBLE);
		
		Button mButton1 = (Button) findViewById(R.id.Main_button2);
		mButton1.setEnabled(false);
		
		Button mButton2 = (Button) findViewById(R.id.Main_button1);
		mButton2.setEnabled(true);
	}
	
	@SuppressLint("NewApi")
	public void addNotification(View view) {
		if(GPSOk){ // GPSOk
			if(!mTextView2.getText().equals("Search LatLng (Wait)")){
				Intent intent = new Intent(this, AddNotificationActivity.class);
				String message = mTextView2.getText().toString();
		    	intent.putExtra(LATLNGGPSNET_MESSAGE, message);
				startActivity(intent);
			}
			else{
				NoLatLngDialogFragment df = new NoLatLngDialogFragment();
				df.show(getFragmentManager(), "Exiting");
			}
		}
		else{
			NoGPSDialogFragment df = new NoGPSDialogFragment();
			df.show(getFragmentManager(), "Exiting");
		}
	}
	
	public void removeNotification(View view) {
		Intent intent = new Intent(this, RemoveNotificationActivity.class);
		startActivity(intent);
	}
	
	public void sendsAlert(Location location){
		FilesManager mFilesManager = new FilesManager();
		ArrayList<String> list = mFilesManager.ReadsNotification(context);
		
		float distanceAlert = new DistanceAlertManager().getAlertDistanceMt(velocityAlertMeters);
		for(int i=0; i<list.size(); i++){
			String lat2 = list.get(i).substring(0, list.get(i).indexOf(","));
			String lng2 = list.get(i).substring(list.get(i).indexOf(", ")+2, list.get(i).indexOf("***"));
			Location location2 = new Location("");
			location2.setLatitude(Double.parseDouble(lat2));
			location2.setLongitude(Double.parseDouble(lng2));
			float distance = location.distanceTo(location2);
			if(distance <= distanceAlert){
				deactivate(null);
				
				Intent intent = new Intent(this, ShowNotsOnMap.class);
				String message = mTextView2.getText().toString();
		    	intent.putExtra(LATLNGGPSNET_MESSAGE, message);
				startActivity(intent);

				break;
			}
		}
	}
	
	@SuppressLint({ "NewApi", "ValidFragment" })
	public class NoLatLngDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.act_Main_no_latlng)
	        	   .setTitle(R.string.act_Main_dialogTitleNoLatLng)
	               .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	@SuppressLint({ "NewApi", "ValidFragment" })
	public class NoGPSDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.act_Main_gps_dialog)
	        	   .setTitle(R.string.act_Main_dialogTitleNoGps)
	               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	               	       startActivity(settingsIntent);
	                   }
	               })
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}

}
