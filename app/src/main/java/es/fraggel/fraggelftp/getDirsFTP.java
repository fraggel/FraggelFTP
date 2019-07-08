package es.fraggel.fraggelftp;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.jibble.simpleftp.SimpleFTP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class getDirsFTP extends AsyncTask<String,Void,String[]>
{
    public String server="fraggel.ddns.net";
    public int puerto=21;
    public String usuario="fraggel";
    public String pass="ak47cold";
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
        FTPClient ftpClient = new FTPClient();
        String[] files2=null;
        try {

            ftpClient.connect(server, puerto);

            int replyCode = ftpClient.getReplyCode();
            boolean success = ftpClient.login(usuario, pass);

            // Lists files and directories
            //FTPFile[] files1 = ftpClient.listDirectories("/disks/750GB/Fotos/Sandra/");

            // uses simpler methods
            files2 = ftpClient.listNames("/disks/750GB/Fotos/Sandra/");


        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        } finally {
            // logs out and disconnects from server
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return files2;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        // Show progress update
        super.onProgressUpdate(values);
    }


}
