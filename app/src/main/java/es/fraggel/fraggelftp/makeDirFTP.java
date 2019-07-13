package es.fraggel.fraggelftp;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.Properties;

public class makeDirFTP extends AsyncTask<String,Void,String[]>
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
        FTPClient ftpClient = new FTPClient();
        String[] files2=null;
        try {

            ftpClient.connect(Propiedades.server, Propiedades.puerto);

            int replyCode = ftpClient.getReplyCode();
            boolean success = ftpClient.login(Propiedades.usuario, Propiedades.pass);
            ftpClient.makeDirectory(Propiedades.urlServletDirs+params[0]);
            JSch jsch = new JSch();
            Session session=jsch.getSession(Propiedades.usuario, Propiedades.server, Propiedades.puertossh);
            session.setPassword(Propiedades.pass);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel=session.openChannel("exec");
            //write the command, which expects password
            String chmodCommand="chmod -R 777 "+Propiedades.urlServletDirs+params[0];
            ((ChannelExec)channel).setCommand(chmodCommand);
            channel.connect();
            channel.disconnect();
            session.disconnect();
        } catch (Exception ex) {
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
