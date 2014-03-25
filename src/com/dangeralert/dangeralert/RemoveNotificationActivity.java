package com.dangeralert.dangeralert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dangeralert.util.FilesManager;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RemoveNotificationActivity extends Activity implements OnItemClickListener {
	FilesManager mFilesManager;
	private ArrayList<String> list;
	private List<Map<String, String>> listMap;
	private SimpleAdapter mAdapter;
	private int positionView;
	Context context;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_notification);
		context = this;
		createListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_remove_notification, menu);
		return true;
	}
	
	public void createListView(){
		mFilesManager = new FilesManager();
		listMap = new ArrayList<Map<String, String>>();
		list = mFilesManager.ReadsNotification(this);
		String[] from = { "Categ", "Desc" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
		
		for(int i=0; i<list.size(); i++){
			Map<String, String> map = new HashMap<String, String>();
			String s1 = list.get(i).substring(list.get(i).indexOf("***")+3, list.get(i).lastIndexOf("***"));// titulo
			String s2 = list.get(i).substring(list.get(i).lastIndexOf("***")+3, list.get(i).indexOf("&&&"));// descrição
			String s3 = list.get(i).substring(list.get(i).indexOf("&&&")+3, list.get(i).lastIndexOf("&&&"));// categoria
			map.put("Categ", s3);
			map.put("Desc", s1 + " - " + s2);
			listMap.add(map);
		}
		
		mAdapter = new SimpleAdapter(this, listMap, android.R.layout.simple_list_item_2, from, to);
		ListView mListView = (ListView) findViewById(R.id.removeNot_listview);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mAdapter);
	}

	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		positionView = position;
		RemoveNotDialogFragment df = new RemoveNotDialogFragment();
		df.show(getFragmentManager(), "Exiting");
	}
	
	@SuppressLint({ "NewApi", "ValidFragment" })
	public class RemoveNotDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.actRemNot_dialogMsg)
	        	   .setTitle(R.string.actRemNot_dialogTitle)
	               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   list.remove(positionView);
	               		   listMap.remove(positionView);
	               		   mAdapter.notifyDataSetChanged();
	               		   
	               		   mFilesManager.WritesNotification(context, list);
	                   }
	               })
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        return builder.create();
	    }
	}

}
