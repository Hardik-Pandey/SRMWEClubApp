package in.weclub.srmweclubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpScroll extends AppCompatActivity {

    private EditText fName;
    private EditText lName;
    private EditText mobNo;
    private EditText email;
    private EditText pass;
    private EditText conpass;
    private ImageView userbutton;
    private Button backbtn;
    private Button reg;
    DatabaseHelper dh;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_scroll);
        dh = new DatabaseHelper(this);
        fName = (EditText)findViewById(R.id.fName);
        //lName = (EditText)findViewById(R.id.lName);
        mobNo = (EditText)findViewById(R.id.moNum);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);
        conpass = (EditText)findViewById(R.id.conpass);
        reg = (Button)findViewById(R.id.reg);
        backbtn= (Button) findViewById(R.id.back_button);
        userbutton  = (ImageView)findViewById(R.id.userbutton);
        userbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
             Intent i = new Intent(SignUpScroll.this,upload_pic.class);
             startActivity(i);
            }
          });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent i = new Intent(SignUpScroll.this,LoginActivity1.class);
             startActivity(i);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass.getText().toString().equals(conpass.getText().toString())) {
                    char[] c = new char[11];
                    fName.getText().toString().getChars(0, 2, c, 0);
                    email.getText().toString().getChars(0, 2, c, 3);
                    StringBuilder b = new StringBuilder(new String(c));
                    int i = Calendar.getInstance().get(Calendar.YEAR) + (int)(Math.random()*10000);
                    b.append(i);
                    final String f1 = b.toString();
                    final String f = fName.getText().toString().trim();
                    //l changed to email from last name
                    final String l = email.getText().toString().trim();
                    final String m = mobNo.getText().toString().trim();

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ref = database.getReference("Users");
                    //final Contact contact = new Contact(m, f, l);
                    /*Intent it = new Intent(SignUpScroll.this, LoginActivity1.class);
                    startActivity(it);*/
                    final ProgressDialog progressDialog = ProgressDialog.show(SignUpScroll.this, "Please wait...", "Processing...", true);
                    (firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        String id = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference d = ref.child(id);
                                        d.child("First Name: ").setValue(f);
                                        d.child("email: ").setValue(l);
                                        d.child("Mobile number: ").setValue(m);
                                        d.child("UID: ").setValue(f1);
                                        Toast.makeText(SignUpScroll.this, "Registration successful", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(SignUpScroll.this, UpcomingEvents.class);
                                        startActivity(i);
                                    }
                                    else
                                    {
                                        Log.e("ERROR", task.getException().toString());
                                        Toast.makeText(SignUpScroll.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                }

                else
                    Toast.makeText(SignUpScroll.this, "Passwords do not match", Toast.LENGTH_SHORT).show();

            }
        });


    }


}
