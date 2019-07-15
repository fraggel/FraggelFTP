package es.fraggel.fraggelftp;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class makeDirFTP extends AsyncTask<String,Void,Boolean>
{

    @Override
    protected void onPreExecute() {
        // Show progress dialog
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean lista) {
        //Populate Ui
        super.onPostExecute(lista);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean copiado=false;

        try {
            URL openUrl = new URL(Propiedades.urlServletMkdir+"?ruta="+params[0]);
            HttpURLConnection connection = (HttpURLConnection) openUrl.openConnection();
            connection.setDoInput(true);
            //  Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder getOutput = new StringBuilder();
            line = br.readLine();
            Boolean bl=new Boolean(line);
            copiado=bl.booleanValue();
            br.close();
        } catch (Exception ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
        return new Boolean(copiado);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        // Show progress update
        super.onProgressUpdate(values);
    }


}
