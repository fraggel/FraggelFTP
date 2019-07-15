package es.fraggel.fraggelftp;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.jibble.simpleftp.SimpleFTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class getDirsFTP extends AsyncTask<String,Void,String[]>
{
    @Override
    protected void onPreExecute() {
        // Show progress dialog
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String[] lista) {
        //Populate Ui
        super.onPostExecute(lista);
    }

    @Override
    protected String[] doInBackground(String... params) {
        String[] files2=null;
        try {
            URL openUrl = new URL(Propiedades.urlServletDirs);
            HttpURLConnection connection = (HttpURLConnection) openUrl.openConnection();
            connection.setDoInput(true);
            //  Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder getOutput = new StringBuilder();
            line = br.readLine();
            br.close();
            files2=line.split(",");
            Collections.sort(Arrays.asList(files2),String.CASE_INSENSITIVE_ORDER);
        } catch (Exception ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
        return files2;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        // Show progress update
        super.onProgressUpdate(values);
    }


}
