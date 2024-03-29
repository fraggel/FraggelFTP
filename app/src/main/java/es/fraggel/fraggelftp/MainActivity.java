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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Uri> imageUris;
    String[] dirs2=null;
    String directorio="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                System.exit(0);
            }
        });*/
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
    public void cerrarApp(){
        finish();
        System.exit(0);
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
                    directorio=dirs2[which];
                    ArrayList lista=new ArrayList();
                    lista.add(directorio);
                    for (int y = 0; y < imageUris.size(); y++) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream((Uri) imageUris.get(y));
                            String filename = "";
                            Cursor cursor = getContentResolver().query((Uri) imageUris.get(y), null, null, null, null);
                            try {
                                if (cursor != null && cursor.moveToFirst()) {
                                    filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                            boolean existe = new checkExists().execute(filename, directorio).get();
                            if (!existe) {
                                lista.add(imageUris.get(y));
                            }
                        }catch (Exception e){}
                    }
                    new uploadFile(getApplicationContext(), directorio+":", 25).execute(lista);
                }


            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
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
                directorio = input.getText().toString();
                ArrayList lista = new ArrayList();
                lista.add(directorio);
                boolean createdDir = false;
                try {
                    createdDir = new makeDirFTP().execute(directorio).get();
                } catch (Exception e) {
                }
                if (createdDir) {
                    for (int y = 0; y < imageUris.size(); y++) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream((Uri) imageUris.get(y));
                            String filename = "";
                            Cursor cursor = getContentResolver().query((Uri) imageUris.get(y), null, null, null, null);
                            try {
                                if (cursor != null && cursor.moveToFirst()) {
                                    filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                            boolean existe = new checkExists().execute(filename, directorio).get();
                            if (!existe) {
                                lista.add(imageUris.get(y));
                            }
                        } catch (Exception e) {
                        }
                    }

                    new uploadFile(getApplicationContext(), directorio+":", 25).execute(lista);
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
}
