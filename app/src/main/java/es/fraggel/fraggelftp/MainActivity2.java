package es.fraggel.fraggelftp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Uri> imageUris;
    String[] dirs2=null;
    String cual="";
    public String server="fraggel.ddns.net";
    public int puerto=21;
    public int puertossh=2222;
    public String usuario="fraggel";
    public String pass="ak47cold";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/") || type.startsWith("video/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("*/*")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        imageUris=new ArrayList<Uri>();
        imageUris.add(imageUri);
        if (imageUri != null) {
            uploadFile(imageUris);
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            uploadFile(imageUris);
            // Update UI to reflect multiple images being shared
        }
    }

    private void uploadFile(ArrayList<Uri> imageUris) {
                String pathFTP="";
                showAlert();
            }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige un directorio");

// add a list

        String[] directories=null;

        try{

            directories=new getDirsFTP().execute().get();
            dirs2=new String[directories.length];
            dirs2[0]="Nueva carpeta...";
            for(int x=0;x<dirs2.length;x++){
                dirs2[x+1]=directories[x].split("/")[directories[x].split("/").length-1];
            }
        }catch(Exception e){}

        builder.setItems(dirs2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    showAlertText();
                }else{
                    cual=dirs2[which];
                    for(int x=0;x<imageUris.size();x++){
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUris.get(x));
                            String filename="";
                            Cursor cursor = getContentResolver().query(imageUris.get(x), null, null, null, null);
                            try {
                                if (cursor != null && cursor.moveToFirst()) {
                                    filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                            FileOutputStream fos=new FileOutputStream(getFilesDir()+"/"+filename);
                            byte[] b=new byte[1024];

                            while(inputStream.read(b)!=-1){
                                fos.write(b);
                                fos.flush();
                            }
                            fos.close();
                            new sendFileFTP().execute(getFilesDir()+"/"+filename,cual);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void showAlertText(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre de nueva carpeta:");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cual = input.getText().toString();
                for(int x=0;x<imageUris.size();x++){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUris.get(x));
                        String filename="";
                        Cursor cursor = getContentResolver().query(imageUris.get(x), null, null, null, null);
                        try {
                            if (cursor != null && cursor.moveToFirst()) {
                                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                        FileOutputStream fos=new FileOutputStream(getFilesDir()+"/"+filename);
                        byte[] b=new byte[1024];

                        while(inputStream.read(b)!=-1){
                            fos.write(b);
                            fos.flush();
                        }
                        fos.close();
                        new makeDirFTP().execute(cual);
                        SystemClock.sleep(1000);
                        new sendFileFTP().execute(getFilesDir()+"/"+filename,cual);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onClick(View view) {
        try {
            JSch jsch = new JSch();
            Session session=jsch.getSession(usuario, server, puertossh);
            session.setPassword(pass);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel=session.openChannel("exec");
            //write the command, which expects password
            String chmodCommand="export LD_LIBRARY_PATH=/usr/lib/plexmediaserver";
            ((ChannelExec)channel).setCommand(chmodCommand);
            channel.connect();
            channel.disconnect();
            session.disconnect();
        } catch (Exception ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        } finally {

        }
    }
}
