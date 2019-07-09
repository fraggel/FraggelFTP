package es.fraggel.fraggelftp;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.jibble.simpleftp.SimpleFTP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class getDirsFTP extends AsyncTask<String,Void,String[]>
{
    public String server="fraggel.ddns.net";
    public int puerto=21;
    public int puertossh=2222;
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
        String[] files2=null;
        String[] files3=null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try {


            JSch jsch = new JSch();
            Session session = jsch.getSession(usuario, server, puertossh);
            session.setPassword(pass);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd("/disks/750GB/Fotos/Sandra/");
            Vector filelist = channelSftp.ls("/disks/750GB/Fotos/Sandra/");
            files2=new String[filelist.size()];
            for (int i = 0; i < filelist.size(); i++) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) filelist.get(i);
                    files2[i] = entry.getFilename();
            }
            for(int i=0;i<(files2.length-1);i++){
                for(int j=i+1;j<files2.length;j++){
                    if(files2[i].compareToIgnoreCase(files2[j])>0){
                        //Intercambiamos valores
                        String variableauxiliar=files2[i];
                        files2[i]=files2[j];
                        files2[j]=variableauxiliar;

                    }
                }
            }
            files3=new String[files2.length-2];
            for(int x=2;x<files2.length;x++){
                files3[x-2]=files2[x];
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(channel.isConnected()){
                channel.disconnect();
            }
            if(channelSftp.isConnected()) {
                channelSftp.disconnect();
            }


        }
        return files3;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        // Show progress update
        super.onProgressUpdate(values);
    }


}
