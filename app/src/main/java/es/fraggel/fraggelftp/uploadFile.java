package es.fraggel.fraggelftp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Notification.FLAG_ONLY_ALERT_ONCE;

public class uploadFile extends AsyncTask<String, Double, Void> {
    private final static String TAG = uploadFile.class.getName();
    private Context mContext;
    private NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final int mId;
    private final String mTitle;
    private final int mNumElementos;
    private final int mElementoActual;

    public uploadFile(Context context, String title, int id,int numElementos,int elementoActual) {
        mContext = context;
        mTitle = title;
        mId = id;
        mNumElementos=numElementos;
        mElementoActual=elementoActual;
    }
    @Override
    protected Void doInBackground(String... strings) {
        String archivo=strings[0];
        String directorio=strings[1];
        String charset = "UTF-8";
        String requestURL = Propiedades.urlServletUpload;

        MultipartUtility multipart = null;
        try {
            multipart = new MultipartUtility(requestURL, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File ff=new File(archivo);
        multipart.addFormField("nombre", ff.getName());
        multipart.addFormField("ruta", directorio);
        try {
            multipart.addFilePart("fichero",ff );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String response = multipart.finish(); // response from server.
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(new Double(mElementoActual*100)/mNumElementos);
        return null;
    }
    private void initNotification() {
        createNotification(mTitle,"Enviando archivo "+mElementoActual+" de "+mNumElementos);
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute");
        super.onPostExecute(result);
        if(mElementoActual==mNumElementos) {
            createNotification(mTitle,"Envío completado");
        }

    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        super.onPreExecute();

        initNotification();

    }

    @Override
    protected void onProgressUpdate(Double... values) {
        Log.d(TAG, "onProgressUpdate with argument = " + values[0]);
        super.onProgressUpdate(values);

        int incr = values[0].intValue();
        //if (incr == 0)
            //setProgressNotification();
        //updateProgressNotification(incr);

    }

    private void updateProgressNotification(int incr) {
        mBuilder.setProgress(100,incr,false);
        Notification noti=mBuilder.build();
        noti.flags=FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(0, noti);
    }

    public void createNotification(String title,String mensaje)
    {
            /**Creates an explicit intent for an Activity in your app**/
            /*Intent resultIntent = new Intent(mContext, ResultActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 , resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/

            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title)
                    .setContentText(mensaje)
                    .setAutoCancel(false)
                    .setProgress(100,0,false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(false);
                //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
        mNotificationManager.cancelAll();
            Notification noti=mBuilder.build();
            noti.flags=FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(0 /* Request Code */, noti);


    }
}
