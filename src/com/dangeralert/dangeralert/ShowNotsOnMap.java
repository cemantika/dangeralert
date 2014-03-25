package com.dangeralert.dangeralert;

import java.util.ArrayList;

import com.dangeralert.util.DistanceAlertManager;
import com.dangeralert.util.FilesManager;
import com.dangeralert.util.LocationUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowNotsOnMap extends Activity {
	private GoogleMap mMap;
	private LatLng mLatlng;
	private LocationManager locationManager;
	private Location currentLocation = null;
	private LocationUtil mLocationUtil;
	private Marker mMarker;
	private ArrayList<Marker> markersNot = new ArrayList<Marker>();
	Context context = this;
	private boolean stopMapRefresh = false;
	private TextView txMarker;
	//private float velocityAlert; //Velocidade em kilometros/hora
	private float velocityAlertMeters; //Velocidade em metros/segundos
	private float distanceAlert; 
	private ArrayList<Marker> markersArray = new ArrayList<Marker>();
	
	// escutador para localização via GPS (pega as coordenadas geograficas do dispositivo)
	private final LocationListener listenerLoc = new LocationListener() {
	    @Override
	    public void onLocationChanged(Location location) {
	    	if (!stopMapRefresh){
		    	if(mLocationUtil.isBetterLocation(location, currentLocation)){ // se o Location novo é melhor que o atual...
		    		currentLocation = location;
		    		mMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
		    		//velocityAlert = location.getSpeed()*(float)3.6; //Velocidade em kilometros/hora
		    		velocityAlertMeters = location.getSpeed(); //Velocidade em metros/segundos
		    		
		    		distanceAlert = new DistanceAlertManager().getAlertDistanceMt(velocityAlertMeters);
			    	if(!markersArray.isEmpty()){// verifica se a lista está vazia. Se está, não faz nada. Se não...
			    		// verifica se as marcas na lista estão dentro do raio. Retira as marcas que estão fora.
			    		for(int i=0; i<markersArray.size(); i++){
			    			LatLng latlng = markersArray.get(i).getPosition();
			    			Location locationMarker = new Location("");
			    			locationMarker.setLatitude(latlng.latitude);
			    			locationMarker.setLongitude(latlng.longitude);
			    			float distanceMarker = location.distanceTo(locationMarker);
			    			Long diferenceTime = System.currentTimeMillis() - Long.parseLong(markersArray.get(i).getSnippet());
			    			// se a marca está fora do raio e tem mais de 10 segundos que ela foi adicionada a lista de marcas...
			    			if((distanceMarker > distanceAlert) && (diferenceTime > 10000)){
			    				markersArray.remove(i);
			    			}
			    		}
			    	}
		    		
		    		plotMarkersAlert(location);
		    	}
		    	else{
		    		mMarker.setPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
		    		//velocityAlert = currentLocation.getSpeed()*(float)3.6; //Velocidade em kilometros/hora
		    		velocityAlertMeters = currentLocation.getSpeed(); //Velocidade em metros/segundos
		    		
		    		distanceAlert = new DistanceAlertManager().getAlertDistanceMt(velocityAlertMeters);
			    	if(!markersArray.isEmpty()){// verifica se a lista está vazia. Se está, não faz nada. Se não...
			    		// verifica se as marcas na lista estão dentro do raio. Retira as marcas que estão fora.
			    		for(int i=0; i<markersArray.size(); i++){
			    			LatLng latlng = markersArray.get(i).getPosition();
			    			Location locationMarker = new Location("");
			    			locationMarker.setLatitude(latlng.latitude);
			    			locationMarker.setLongitude(latlng.longitude);
			    			float distanceMarker = currentLocation.distanceTo(locationMarker);
			    			Long diferenceTime = System.currentTimeMillis() - Long.parseLong(markersArray.get(i).getSnippet());
			    			// se a marca está fora do raio e tem mais de 10 segundos que ela foi adicionada a lista de marcas...
			    			if((distanceMarker > distanceAlert) && (diferenceTime > 10000)){
			    				markersArray.remove(i);
			    			}
			    		}
			    	}
		    		
		    		plotMarkersAlert(currentLocation);
		    	}
		    }
	    }
		@Override public void onProviderDisabled(String arg0) {}
		@Override public void onProviderEnabled(String arg0) {}
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	private final OnMarkerClickListener listenerMark = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			// gerar o infowindow
			return false;
		}
	};
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_nots_on_map);
		
		txMarker = new TextView(this);
		txMarker.setText(R.string.actShowMap_YouAreHere);

		Intent intent = getIntent();
		String latlng = intent.getStringExtra(MainActivity.LATLNGGPSNET_MESSAGE);
		mLatlng = new LatLng(Double.parseDouble(latlng.substring(0, latlng.indexOf(","))), Double.parseDouble(latlng.substring(latlng.indexOf(", ")+2)));
		
		setUpMapIfNeeded(mLatlng);
		mLocationUtil = new LocationUtil();
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // serviço de localização
		final boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listenerLoc); // instacia o evento de escuta da localização por GPS
		if(netEnabled)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listenerLoc); // instacia o evento de escuta da localização por Internet
	
		//playAlert();
	}
	
	private void playAlert(){
		// alerta padrão do sistema
		//Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		//Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		//r.play();
		
		MediaPlayer mp = new MediaPlayer();
		try{
		    AssetFileDescriptor descriptor = getAssets().openFd("alarm.mp3");
		    mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
		    descriptor.close();
		    mp.prepare();
		    mp.start();
		} catch(Exception e){
		    //e.printStackTrace();
		}
		
		try {
			  Thread.sleep(2000L);
		}catch (Exception e) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_nots_on_map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.stopMapRefresh:
	        	stopMapRefresh = true;
	            return true;
	        case R.id.ContinueMapRefresh:
	        	stopMapRefresh = false;
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    locationManager.removeUpdates(listenerLoc);
	}
	
	@SuppressLint("NewApi")
	private void setUpMapIfNeeded(LatLng latlng) {
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.not_on_map)).getMap();
	        if (mMap != null){
	        	CameraPosition cameraPosition = new CameraPosition.Builder()
		        	.target(latlng)             // Sets the center of the map to Mountain View
		            .zoom(19)                   // Sets the zoom
		            .build();                   // Creates a CameraPosition from the builder
	        	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	        	mMap.setIndoorEnabled(true);
	        	mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	        	addMarker(latlng);
	        	
	        	Location location = new Location("");
	        	location.setLatitude(latlng.latitude); location.setLongitude(latlng.longitude);
	        	plotMarkersAlert(location);
	        	
	        	mMap.setOnMarkerClickListener(listenerMark);
	        }
	        else{
	        	// não pode usar o abjeto mapa
	        	NoMapDialogFragment df = new NoMapDialogFragment();
				df.show(getFragmentManager(), "Exiting");
	        }
	    }
	}
	
	public void addMarker(LatLng notLocation){
		mMarker = mMap.addMarker(new MarkerOptions()
			        .position(notLocation)
			        //.title(notLocation.toString())
			        .title(txMarker.getText().toString())
			        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
					);
	}
	
	public void plotMarkersAlert(Location locationUser){
		FilesManager mFilesManager = new FilesManager();
		ArrayList<String> list = mFilesManager.ReadsNotification(context);
		for(int i=0; i<markersNot.size(); i++){
			markersNot.get(i).remove();
		}

		for(int i=0; i<list.size(); i++){
			String lat2 = list.get(i).substring(0, list.get(i).indexOf(","));
			String lng2 = list.get(i).substring(list.get(i).indexOf(", ")+2, list.get(i).indexOf("***"));
			String title = list.get(i).substring(list.get(i).indexOf("***")+3, list.get(i).lastIndexOf("***"));
			//String desc = list.get(i).substring(list.get(i).lastIndexOf("***")+3, list.get(i).indexOf("&&&"));
			String categ = list.get(i).substring(list.get(i).indexOf("&&&")+3, list.get(i).lastIndexOf("&&&"));
			Location locationNot = new Location("");
			locationNot.setLatitude(Double.parseDouble(lat2));
			locationNot.setLongitude(Double.parseDouble(lng2));
			float distance = locationUser.distanceTo(locationNot);
			if(distance <= distanceAlert){
				Marker mAlert = createMarkerAlert(new LatLng(locationNot.getLatitude(), locationNot.getLongitude()), categ, title);// cria a marca de alerta
				mAlert.showInfoWindow();
				markersNot.add(mAlert);
				if(markersArray.isEmpty()){// verifica se a lista está vazia. Se está, coloca a marca atual e soa o alerta. Se não...
					markersArray.add(mAlert);
					playAlert();
				}
				else{// verifica se a marca atual está na lista. Se está, não faz nada. Se não, coloca a marca e soa o alerta.
					boolean jata = false;
					for(int j=0; j<markersArray.size(); j++){
						if((mAlert.getPosition().latitude == markersArray.get(j).getPosition().latitude) && (mAlert.getPosition().longitude == markersArray.get(j).getPosition().longitude)){ // essa igualdade tem que ser melhorada
							jata = true;
							break;
						}
					}
					if (!jata){
						markersArray.add(mAlert);
						playAlert();
					}
				}
			}
		}
	}
	
	public Marker createMarkerAlert(LatLng locationNot, String categ, String title){
		return mMap.addMarker(new MarkerOptions()
		        .position(locationNot)
		        .title(categ)
		        //.snippet(title)
		        .snippet(Long.toString(System.currentTimeMillis()))
				);
	}
	
	@SuppressLint({ "NewApi", "ValidFragment" })
	public class NoMapDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.show_map_dialog_no_map_msg)
	        	   .setTitle(R.string.show_map_dialog_no_map_title)
	               .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}

}
