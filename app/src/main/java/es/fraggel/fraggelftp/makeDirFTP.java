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
        FTPClient ftpClient = new FTPClient();
        String[] files2=null;
        try {

            ftpClient.connect(server, puerto);

            int replyCode = ftpClient.getReplyCode();
            boolean success = ftpClient.login(usuario, pass);
            ftpClient.makeDirectory("/disks/750GB/Fotos/Sandra/"+params[0]);
            JSch jsch = new JSch();
            Session session=jsch.getSession(usuario, server, puertossh);
            session.setPassword(pass);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel=session.openChannel("exec");
            //write the command, which expects password
            String chmodCommand="chmod -R 777 /disks/750GB/Fotos/Sandra/"+params[0];
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
