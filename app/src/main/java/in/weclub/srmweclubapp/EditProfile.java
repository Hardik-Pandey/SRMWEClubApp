package in.weclub.srmweclubapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String f,m;
    private EditText fName, moN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fName = (EditText)findViewById(R.id.fNameE);
        moN = (EditText)findViewById(R.id.moNumE);

        user = firebaseAuth.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007ee5")));
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                f = dataSnapshot.child(user.getUid()).child("First Name: ").getValue(String.class);
                m = dataSnapshot.child(user.getUid()).child("Mobile number: ").getValue(String.class);
                String e = user.getEmail();
                TextView email = (TextView)findViewById(R.id.emailE);
                email.setText(e);
                fName.setText(f); moN.setText(m);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref1 = database.getReference("Users").child(user.getUid());
        Button c = (Button)findViewById(R.id.chng);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!f.equals(fName.getText().toString()))
                    ref1.child("First Name: ").setValue(f);
                if(!m.equals(moN.getText().toString()))
                    ref1.child("Mobile number: ").setValue(m);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }
}
