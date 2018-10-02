package in.weclub.srmweclubapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledEvents extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private List<String> events = new ArrayList<>();

    private RecyclerView rView;
    private EventAdapter ea;
    private RecyclerView.LayoutManager rLM;
    private List<EventInfo> infoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rView = (RecyclerView)findViewById(R.id.enrEvents);
        rLM = new LinearLayoutManager(this);
        rView.setLayoutManager(rLM);

        ea = new EventAdapter(this,infoList);

        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference dr = ref.getReference("Users").child(user.getUid()).child("Registered Events");

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        events.add(ds.getKey());
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       DatabaseReference d = ref.getReference("Event");
       d.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot ds) {
                   for (String s : events) {
                           String name = ds.child(s).child("Event Name").getValue(String.class);
                           String spk = ds.child(s).child("Speaker").getValue(String.class);
                           String sTime = ds.child(s).child("Start Time").getValue(String.class);
                           String etime = ds.child(s).child("End Time").getValue(String.class);
                           String type = ds.child(s).child("Type").getValue(String.class);
                           String url = ds.child(s).child("Image").getValue(String.class);
                           infoList.add(new EventInfo(name, spk, sTime, etime, type, url, s));//, en));
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
        ea.notifyDataSetChanged();
        rView.setAdapter(ea);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enrolled_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder a = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            a.setTitle(Html.fromHtml("<font color='#000000'>Logout</font>"));
            a.setMessage(Html.fromHtml("<font color='#0000000'>Are you sure you want to Logout?</font>")).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    auth.signOut();
                    startActivity(new Intent(EnrolledEvents.this, LoginActivity1.class));
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.virtCard:
                Intent it = new Intent(EnrolledEvents.this, Profile.class);
                startActivity(it);
                break;
            case R.id.eventsUp2:
                Intent it1 = new Intent(EnrolledEvents.this, UpcomingEvents.class);
                startActivity(it1);
                break;
            case R.id.edit4:
                Intent it2 = new Intent(EnrolledEvents.this, EditProfile.class);
                startActivity(it2);
                break;
            case R.id.partner4:
                startActivity(new Intent(this, FindPartner.class));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
