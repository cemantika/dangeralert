package com.dangeralert.dangeralert;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowMapActivit extends Activity {
	public static final String PRECISELATLNG_MESSAGE = "com.dangeralert.dangeralert.PRECISELATLNG_MESSAGE";
	public static final String TITLE_SHOWMAP_MESSAGE = "com.dangeralert.dangeralert.TITLE_SHOWMAP_MESSAGE";
	public static final String DESC_SHOWMAP_MESSAGE = "com.dangeralert.dangeralert.DESC_SHOWMAP_MESSAGE";
	public static final String CATEG_SHOWMAP_MESSAGE = "com.dangeralert.dangeralert.CATEG_SHOWMAP_MESSAGE";
	private GoogleMap mMap;
	private LatLng preciseLatlng;
	private String latlng;
	private String title;
	private String desc;
	private String posCateg;
	private TextView txMarker;
	
	private final OnMarkerDragListener listener = new OnMarkerDragListener() {
		@Override public void onMarkerDragStart(Marker marker) {}
		@Override public void onMarkerDrag(Marker marker) {}

		@Override
		public void onMarkerDragEnd(Marker marker) {
			preciseLatlng =  marker.getPosition();
			marker.setSnippet(preciseLatlng.toString());
		}
	};
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_map);
		
		txMarker = new TextView(this);
		txMarker.setText(R.string.actShowMap_YouAreHere);

		Intent intent = getIntent();
		latlng = intent.getStringExtra(AddNotificationActivity.LATLNG_MESSAGE);
		title = intent.getStringExtra(AddNotificationActivity.TITLE_ADDNOT_MESSAGE);
		desc = intent.getStringExtra(AddNotificationActivity.DESC_ADDNOT_MESSAGE);
		posCateg = intent.getStringExtra(AddNotificationActivity.CATEG_ADDNOT_MESSAGE);
		
		preciseLatlng = new LatLng(Double.parseDouble(latlng.substring(0, latlng.indexOf(","))), Double.parseDouble(latlng.substring(latlng.indexOf(", ")+2)));
		setUpMapIfNeeded(preciseLatlng);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.savepreciselatlng:
	        	sendMsg();
	    		finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			sendMsg();
    		finish();
    		return true;
		}
		return false;
	}
	
	private void sendMsg(){
		Intent intent = new Intent(this, AddNotificationActivity.class);
    	intent.putExtra(PRECISELATLNG_MESSAGE, String.valueOf(preciseLatlng.latitude)+", "+String.valueOf(preciseLatlng.longitude));
    	intent.putExtra(TITLE_SHOWMAP_MESSAGE, title);
    	intent.putExtra(DESC_SHOWMAP_MESSAGE, desc);
    	intent.putExtra(CATEG_SHOWMAP_MESSAGE, posCateg);
    	startActivity(intent);
	}
	
	@SuppressLint("NewApi")
	private void setUpMapIfNeeded(LatLng latlng) {
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        if (mMap != null){
	        	CameraPosition cameraPosition = new CameraPosition.Builder()
		        	.target(latlng)     	    // Sets the center of the map to Mountain View
		            .zoom(19)                   // Sets the zoom
		            .build();                   // Creates a CameraPosition from the builder
	        	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	        	mMap.setIndoorEnabled(true);
	        	mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	        	addMarker(latlng);
	        	mMap.setOnMarkerDragListener(listener);
	        }
	        else{
	        	// não pode usar o abjeto mapa
	        	NoMapDialogFragment df = new NoMapDialogFragment();
				df.show(getFragmentManager(), "Exiting");
	        }
	    }
	}
	
	public void addMarker(LatLng notLocation){
		mMap.addMarker(new MarkerOptions()
	        .position(notLocation)
	        .title(txMarker.getText().toString())
	        .snippet(notLocation.toString())
	        .draggable(true)
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
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
