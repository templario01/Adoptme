package com.example.adoptme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class login extends AppCompatActivity {
    private EditText textmail;
    private EditText textpassword;
    private Button btnregistrar, btnIngresa;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        textmail = (EditText)findViewById(R.id.txtMail);
        textpassword = (EditText)findViewById(R.id.txtPassword);
        btnregistrar = (Button) findViewById(R.id.btnRegistrar);

        progressDialog = new ProgressDialog(this);

        btnIngresa = (Button) findViewById(R.id.btnIngresar);
        btnIngresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguearUsuario();
            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario(){
        String email = textmail.getText().toString().trim();
        String password = textpassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"se debe ingresar un email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar una contraseña",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Creando su cuenta, espere un momento...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(login.this, "Se ha registrado con exito", Toast.LENGTH_SHORT).show();
                        }else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(login.this, "El correo ingresado ya existe", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(login.this, "No se pudo registrar al usuario", Toast.LENGTH_SHORT).show();
                            }

                        }
                        progressDialog.dismiss();
                    }
                });
    }
    private  void loguearUsuario() {

        final String email = textmail.getText().toString().trim();
        String password = textpassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "se debe ingresar un email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Validando informacion, espere un momento...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            int pos = email.indexOf("@");
                            String user = email.substring(0,pos);
                            Toast.makeText(login.this, "Bienvenido: "+textmail.getText(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent (getApplication(), mapa.class);
                            intent.putExtra(mapa.user,user);
                            startActivity(intent);
                            //startActivityForResult(intent, 0);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(login.this, "El correo ingresado ya existe", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(login.this, "No se pudo registrar al usuario", Toast.LENGTH_SHORT).show();
                            }

                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
