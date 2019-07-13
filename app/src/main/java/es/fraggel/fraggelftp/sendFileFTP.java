package es.fraggel.fraggelftp;

import android.net.Uri;
import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.apache.commons.net.ftp.FTPClient;
import org.jibble.simpleftp.SimpleFTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class sendFileFTP extends AsyncTask<String,Void,String>
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
    protected void onPostExecute(String lista) {
        //Populate Ui
        super.onPostExecute(lista);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            SimpleFTP ftp = new SimpleFTP();

            // Connect to an FTP server on port 21.
            ftp.connect(server, puerto, usuario, pass);

            // Set binary mode.
            ftp.bin();

            // Change to a new working directory on the FTP server.
            ftp.cwd(Propiedades.rutaFotos+params[1]);

            // Upload some files.
            ftp.stor(new File(params[0]));
            //ftp.stor(new File(params[1]));

            // You can also upload from an InputStream, e.g.
            //ftp.stor(new FileInputStream(new File("test.png")), "test.png");

            // Quit from the FTP server.
            ftp.disconnect();
            JSch jsch = new JSch();
            Session session=jsch.getSession(usuario, server, puertossh);
            session.setPassword(pass);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel=session.openChannel("exec");
            //write the command, which expects password
            String chmodCommand="chmod -R 777 "+Propiedades.urlServletDirs+params[1];
            ((ChannelExec)channel).setCommand(chmodCommand);
            channel.connect();
            channel.disconnect();
            session.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // Show progress update
        super.onProgressUpdate(values);
    }


}
