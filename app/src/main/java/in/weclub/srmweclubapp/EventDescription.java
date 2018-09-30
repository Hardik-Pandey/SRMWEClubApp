package in.weclub.srmweclubapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDescription extends AppCompatActivity {

    private Button b;
    private boolean registered;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference("Event");
        DatabaseReference dr = database.getReference("Users");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String g = dataSnapshot.child(u.getUid()).child("Registered Events").child(getIntent().
                        getExtras().getString("Event ID"))
                        .getValue(String.class);
                if (g != null)
                    registered = g.equals("Registered");
                else
                    registered = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = getIntent().getExtras().getString("Event ID");
                DataSnapshot ds = dataSnapshot.child(s);
                String name = ds.child("Event Name").getValue(String.class);
                String spk = ds.child("Speaker").getValue(String.class);
                String url = ds.child("Image").getValue(String.class);
                String decp = ds.child("Description").getValue(String.class);

                img = (ImageView) findViewById(R.id.imageView4);
                Glide.with(EventDescription.this).load(url).into(img);
                TextView n = findViewById(R.id.eventName1);
                TextView det = findViewById(R.id.eventDet);
                TextView des = findViewById(R.id.eventDesc);
                det.setText(decp);

                final String i = ds.getKey();

                n.setText(name);
                des.setText(String.format("Speaker: %s", spk));

                b = (Button) findViewById(R.id.button2);

                changeCol();
                final String f = i;
                if (!registered) {
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(EventDescription.this
                                    , R.style.AlertDialogTheme);
                            adb.setMessage(Html.fromHtml("<font color='#000000'>Do you want to register in this event?</font>"))
                                    .setPositiveButton("YES",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference ref1 = database.getReference("Users");
                                                    ref1.child(u.getUid()).child("Registered Events").child(f).setValue("Registered");
                                                    Toast.makeText(EventDescription.this, "Event Registration Successful", Toast.LENGTH_SHORT).show();
                                                    registered = true;
                                                    changeCol();
                                                    redraw();
                                                }
                                            }).setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    }).create();
                            adb.setTitle(Html.fromHtml("<font color='#000000'>Confirm Registration</font>"));
                            adb.show();
                        }
                    });
                } else {
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(EventDescription.this
                                    , R.style.AlertDialogTheme);
                            adb.setMessage(Html.fromHtml("<font color='#000000'>Do you want not to attend this event?</font>"))
                                    .setPositiveButton("YES",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference ref1 = database.getReference("Users");
                                                    ref1.child(u.getUid()).child("Registered Events").child(f).setValue("Un-Registered");
                                                    Toast.makeText(EventDescription.this, "You've been unregistered from the event", Toast.LENGTH_SHORT).show();
                                                    registered = false;
                                                    changeCol();
                                                    redraw();
                                                }
                                            }).setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    }).create();
                            adb.setTitle(Html.fromHtml("<font color='#000000'>Confirm Registration</font>"));
                            adb.show();

                        }
                    });

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        Button b1 = (Button) findViewById(R.id.verifyReg);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registered) {
                    Intent it = new Intent(EventDescription.this, ClaimReward.class);
                    it.putExtra("UID", getIntent().getExtras().getString("Event ID"));
                    it.putExtra("Title", "Verify Registration");
                    startActivity(it);
                } else
                    Toast.makeText(EventDescription.this, "Register yourself first", Toast.LENGTH_SHORT).show();
            }
        });

        //recreate();

    }

    private void changeCol() {
        //b.setBackgroundColor(Color.rgb(0,100,13));
        if (registered) {
            b.setText("Unregister");
            b.setBackgroundTintList(ColorStateList.valueOf((int)(R.color.colorPrimary)));
        }
        else
        {
            b.setText("Register");
            b.setBackgroundTintList(ColorStateList.valueOf((int)(R.color.registered)));
        }
    }

    private void redraw(){
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, UpcomingEvents.class));
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        startActivityForResult(new Intent(this, UpcomingEvents.class), 0);
        return true;

    }
}
