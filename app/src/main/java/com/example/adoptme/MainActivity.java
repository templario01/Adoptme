package com.example.adoptme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TextView txtlat,txtlng;
    Spinner spinColor, spinTamano;
    String fecha,hora,idfoto;
    Button btnGuardar;
    private DatabaseReference Perro;



    //variables para el storage del celular
    private ImageView imageView;
    private Button btnChoose;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    //variables para storage de firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    //variables que reciben del mapa de mascota
    //public static final String latitud = "lat";
    //public static final String longitud = "lng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Perro = FirebaseDatabase.getInstance().getReference("Perro");

        txtlat = (TextView) findViewById(R.id.txtLat);
        txtlng = (TextView) findViewById(R.id.txtLng);

        try{
            recibirCoordenadas();
        }catch (Exception e){
            txtlat.setText(" ");
            txtlng.setText(" ");
        }


        spinColor = (Spinner)findViewById(R.id.spinColor);
        List list = new ArrayList();
        list.add("elegir color");
        list.add("negro");
        list.add("blanco");
        list.add("marron");
        list.add("mostaza");
        list.add("plomo");

        spinTamano = (Spinner)findViewById(R.id.spinTamano);
        List list2 = new ArrayList();
        list2.add("elegir tamaño");
        list2.add("pequeño");
        list2.add("mediano");
        list2.add("grande");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinColor.setAdapter(arrayAdapter);

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,list2);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTamano.setAdapter(arrayAdapter2);

        //firebase storage init
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        ImageButton ubicacion = (ImageButton) findViewById(R.id.btnUbicacion);
        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), mapaMascota.class);
                startActivityForResult(intent2, 0);
            }
        });

        btnGuardar=(Button)findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPerro();
                displayNotification();
            }
        });
        btnChoose = (Button)findViewById(R.id.btnChoose);
        imageView = (ImageView)findViewById(R.id.imgView);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }
    //metodo para elegir imagen del dispositivo
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }
    //metodo para seleccionar imagen del dispositivo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data!=null && data.getData()!=null){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    //metodo para cargar imagen a firebase storage
    private void uploadImage(){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo imagen...");
            progressDialog.show();
            StorageReference ref = storageReference.child("imagenes/"+ idfoto);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Imagen e informacion almacenados con exito",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Error al subir la imagen",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Subiendo "+(int)progress+"%");
                        }
                    });
        }
    }
    // metodo para guardar en la base de datos
    public void guardarPerro(){
        try {
            idfoto = UUID.randomUUID().toString();
            String color= spinColor.getSelectedItem().toString();
            String tamano = spinTamano.getSelectedItem().toString();
            String latitud = txtlat.getText().toString();
            String longitud = txtlng.getText().toString();
            String id = Perro.push().getKey();
            Perro perro = new Perro(Double.valueOf(latitud),Double.valueOf(longitud),tamano,color,"1","2",idfoto);
            Perro.child("perros").child(id).setValue(perro);
            uploadImage();
            Toast.makeText(MainActivity.this,"Perro guardado con exito",Toast.LENGTH_SHORT).show();


        } catch(Exception e) {
            //si falla guardar mascota obligamos a que falle la subida de la imagen
            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();

        }
        spinColor.setSelection(0);
        spinTamano.setSelection(0);
    }
    public void recibirCoordenadas(){
        Bundle extras = getIntent().getExtras();
        String latitud = extras.getString("dato1");
        String longitud = extras.getString("dato2");
        txtlat.setText(latitud);
        txtlng.setText(longitud);
    }
    protected void displayNotification(){
        String CHANNEL_ID = "my_channel_01";
        /*
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("mi notificacion")
                .setContentText("reunion a las 9:30");
        Intent resultIntent = new Intent(this,NotificationView.class);
        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId,mBuilder.build());
        */
        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notificacion")
                .setContentText("Gracias por contribuir con la comunidad guardando un nuevo perro");

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
