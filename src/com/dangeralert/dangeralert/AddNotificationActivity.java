package com.dangeralert.dangeralert;

import com.dangeralert.util.FilesManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddNotificationActivity extends Activity {
	public static final String LATLNG_MESSAGE = "com.dangeralert.dangeralert.LATLNG_MESSAGE";
	public static final String TITLE_ADDNOT_MESSAGE = "com.dangeralert.dangeralert.TITLE_ADDNOT_MESSAGE";
	public static final String DESC_ADDNOT_MESSAGE = "com.dangeralert.dangeralert.DESC_ADDNOT_MESSAGE";
	public static final String CATEG_ADDNOT_MESSAGE = "com.dangeralert.dangeralert.CATEG_ADDNOT_MESSAGE";
	private String latlngGPSNET = null;
	private String preciseLatlng = null;
	private String title = null;
	private String desc = null;
	private int posCateg; // a posição do item selecionado nas categorias
	private EditText EditText1;
	private EditText EditText2;
	private TextView TextView1;
	private Spinner Spinner1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_notification);
		
		Intent intent = getIntent();
		latlngGPSNET = intent.getStringExtra(MainActivity.LATLNGGPSNET_MESSAGE);
		preciseLatlng = intent.getStringExtra(ShowMapActivit.PRECISELATLNG_MESSAGE);
		title = intent.getStringExtra(ShowMapActivit.TITLE_SHOWMAP_MESSAGE);
		desc = intent.getStringExtra(ShowMapActivit.DESC_SHOWMAP_MESSAGE);
		if (intent.getStringExtra(ShowMapActivit.CATEG_SHOWMAP_MESSAGE) == null)
			posCateg = 0;
		else
			posCateg = Integer.valueOf(intent.getStringExtra(ShowMapActivit.CATEG_SHOWMAP_MESSAGE));
		
		TextView1 = (TextView) findViewById(R.id.AddNot_textView1);
		EditText1 = (EditText) findViewById(R.id.AddNot_editText1);
		EditText2 = (EditText) findViewById(R.id.AddNot_editText2);
		
		Spinner1 = (Spinner) findViewById(R.id.AddNot_spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner1.setAdapter(adapter);
		
		if(latlngGPSNET != null)
			TextView1.setText(latlngGPSNET);
		if(preciseLatlng != null)
			TextView1.setText(preciseLatlng);
		if(title != null && desc != null){
			EditText1.setText(title);
			EditText2.setText(desc);
		}
		if(posCateg != 0){
			Spinner1.setSelection(posCateg);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_notification, menu);
		return true;
	}
	
	public void saveNotification(View view){
		String tx1 = TextView1.getText().toString();
		String tx2 = EditText1.getText().toString();
		String tx3 = EditText2.getText().toString();
		
		// falta acrescentar a lógica para salvar a informação de categorias no arquivo
		String tx4 = Spinner1.getSelectedItem().toString();
		
		AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
		TextView myMsg = new TextView(this);
		
		if(tx2.length()>0 && tx3.length()>0 && !tx4.equals("Select an option")){
			FilesManager mFilesManager = new FilesManager();
			mFilesManager.WritesNotification(this, tx1, tx2, tx3, tx4);
			
			Button Button1 = (Button) findViewById(R.id.AddNot_button1);
			Button1.setText(R.string.actNot_Back);
			
			myMsg.setText("Notification successfully added");
			EditText1.setText(""); EditText2.setText("");
			Spinner1.setSelection(0);
		}
		else{
			myMsg.setText("Fill in all fields");
		}
		myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		popupBuilder.setView(myMsg);
		popupBuilder.show();
	}
	
	public void cancelNotification(View view){
		finish();
	}
	
	public void adjustOnTheMap(View view){
		Intent intent = new Intent(this, ShowMapActivit.class);
    	intent.putExtra(LATLNG_MESSAGE, TextView1.getText().toString());
    	intent.putExtra(TITLE_ADDNOT_MESSAGE, EditText1.getText().toString());
    	intent.putExtra(DESC_ADDNOT_MESSAGE, EditText2.getText().toString());
    	intent.putExtra(CATEG_ADDNOT_MESSAGE, Integer.toString(Spinner1.getSelectedItemPosition()));
		startActivity(intent);
		finish();
	}
}
