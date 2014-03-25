package com.dangeralert.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.Gravity;
import android.widget.TextView;

@SuppressLint("NewApi")
public class FilesManager {
	public final static String LOG_TAG = "com.dangeralert.dangeralert.LOG_FilesManager";
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public File getNotificationStorageDir(Context context, String dirname) {
	    // Get the directory for the app's private notifications directory. 
	    File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS), dirname);
	    if (!dir.mkdirs()) {
	        //Log.e(LOG_TAG, "Directory not created or already exists");
	    }
	    return dir;
	}
	
	public File getNotificationStorageFile(Context context, File dir, String filename) {
		File file = null;
		try {
			file = new File(dir, filename);
			if (!file.createNewFile()){
				//Log.e(LOG_TAG, "File not created or already exists");
			}
		} catch (IOException e) {
			AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
			TextView myMsg = new TextView(context);
			myMsg.setText("Error creating file notification");
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			popupBuilder.setView(myMsg);
			popupBuilder.show();
			//e.printStackTrace();
		}
		return file;
	}
	
	private File processDir(Context context){
		File dir = null;
		
		if (isExternalStorageWritable()){
			dir = getNotificationStorageDir(context, "UOANot");
		}
		else{
			AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
			TextView myMsg = new TextView(context);
			myMsg.setText("SD Card isn't available");
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			popupBuilder.setView(myMsg);
			popupBuilder.show();
		}
		return dir;
	}
	
	public boolean WritesNotification(Context context, String LatLong, String title, String Desc, String Categ) {
		File dir = processDir(context);
		if (dir == null) return false;
		File file = getNotificationStorageFile(context, dir, "notifications.txt");
		
		try {
			FileWriter fileWriter = new FileWriter(file, true);
			
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(LatLong+"***"+title+"***"+Desc+"&&&"+Categ+"&&&");
			printWriter.flush();
			
		} catch (IOException e) {
			AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
			TextView myMsg = new TextView(context);
			myMsg.setText("Error saving notification");
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			popupBuilder.setView(myMsg);
			popupBuilder.show();
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	// serve apenas para criar um arquivo de notificações para testes no campos da UFBA ondina
	public boolean createPreFile(Context context){
		File dir = processDir(context);
		if (dir == null) return false;
		File file = new File(dir, "notifications.txt");

		if(file.exists()){
			return false;
		}
		else{
			file = getNotificationStorageFile(context, dir, "notifications.txt");
			
			try {
				FileWriter fileWriter = new FileWriter(file, true);
				
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.println("-13.00179, -38.50721***Materiais pelo chão***Materiais espalhados pelo chão. Cuidado onde pisa.&&&Materiais Espalhados&&&");
				printWriter.println("-13.00163, -38.50800***Cuidado com as Motos***Motocicletas passando rápido. Cuidado para não ser atropelado.&&&Trafego de Motociclétas&&&");
				printWriter.println("-13.00275, -38.50688***Cuidado com os alunos***Rua com muitos alunos da UFBA atrevessando no sinal aberto.&&&Travessia Perigosa&&&");
				printWriter.println("-13.00187, -38.50447***Cuidado com o buraco***Buraco grande na pista. Cuidado quando passar.&&&Buraco na pista&&&");
				printWriter.println("-13.00216, -38.50855***Motos passando***Motociclétas passando em alta velocidade. Cuidado, fique atento(a).&&&Trafego de Motociclétas&&&");
				printWriter.println("-13.00225, -38.50913***Coisas espalhadas***Muita coisa jogada pelo chão. Materiais de construção.&&&Materiais Espalhados&&&");
				printWriter.flush();
				
			} catch (IOException e) {
				AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
				TextView myMsg = new TextView(context);
				myMsg.setText("Error saving notification");
				myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
				popupBuilder.setView(myMsg);
				popupBuilder.show();
				//e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	
	public boolean WritesNotification(Context context, ArrayList<String> list){
		File dir = processDir(context);
		if (dir == null) return false;
		File file = getNotificationStorageFile(context, dir, "notifications.txt");
		
		try {
			FileWriter fileWriter = new FileWriter(file, false);
			
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for(int i=0; i<list.size(); i++){
				printWriter.println(list.get(i));
			}
			printWriter.flush();
			
		} catch (IOException e) {
			AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
			TextView myMsg = new TextView(context);
			myMsg.setText("Error saving notification");
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			popupBuilder.setView(myMsg);
			popupBuilder.show();
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ArrayList<String> ReadsNotification(Context context) {
		File dir = processDir(context);
		File file = getNotificationStorageFile(context, dir, "notifications.txt");
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String linha = "";
			while ( ( linha = bufferedReader.readLine() ) != null) {
				//System.out.println(linha);
				list.add(linha);
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
			TextView myMsg = new TextView(context);
			myMsg.setText("Error reading notification");
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			popupBuilder.setView(myMsg);
			popupBuilder.show();
			//e.printStackTrace();
		}
		return list;
	}
}
