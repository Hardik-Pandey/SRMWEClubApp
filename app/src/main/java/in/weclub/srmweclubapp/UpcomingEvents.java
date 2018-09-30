package in.weclub.srmweclubapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UpcomingEvents extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rView;
    private RecyclerView.Adapter rA;
    private RecyclerView.LayoutManager rLM;
    private List<EventInfo> infoList = new ArrayList<>();
    private EventAdapter ea;
   // private boolean en;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private SwipeRefreshLayout srp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rView= (RecyclerView)findViewById(R.id.rec);
        rLM = new LinearLayoutManager(this);
        rView.setLayoutManager(rLM);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ea = new EventAdapter(this,infoList);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        srp = (SwipeRefreshLayout)findViewById(R.id.swipeContainerEvents);

            final DatabaseReference ref = database.getReference("Event");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final String id = ds.getKey();
                        String name = ds.child("Event Name").getValue(String.class);
                        String spk = ds.child("Speaker").getValue(String.class);
                        String sTime = ds.child("Start Time").getValue(String.class);
                        String etime = ds.child("End Time").getValue(String.class);
                        String type = ds.child("Type").getValue(String.class);
                        String url = ds.child("Image").getValue(String.class);
                        infoList.add(new EventInfo(name, spk, sTime, etime, type, url, id));//, en));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
           getInfo();
                ea.notifyDataSetChanged();
                rView.setAdapter(ea);
                refresh();


        //infoList.add(new EventInfo("Java Workshop", "Apan Trikha", "10:00 AM", "Computer Lab 6","EV1001"));
    }

   /*private void setEn(boolean b){
        en = b;
    }*/

    private void refresh(){
        srp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                     @Override
                                     public void onRefresh() {
                                         finish();
                                         overridePendingTransition( 0, 0);
                                         startActivity(getIntent());
                                         overridePendingTransition( 0, 0);
                                     }
                                 });
        //srp.setRefreshing(!(srp.isRefreshing()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
        refresh();
    }

    private void getInfo(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Event");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            DatabaseReference ref1 = database.getReference("Users");
                            final String id = ds.getKey();
                            ref1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    FirebaseUser u = auth.getCurrentUser();
                                    String g = dataSnapshot.child(u.getUid()).child("Registered Events").child(id)
                                            .getValue(String.class);
                                    if (g != null) {
                                        for (EventInfo i : infoList) {
                                            i.setEnrolled(g.equals("Registered"));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            ea.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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
        getMenuInflater().inflate(R.menu.upcoming_events, menu);
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
                    startActivity(new Intent(UpcomingEvents.this, LoginActivity1.class));
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
        switch(id)
        {
            case R.id.partners2:
                Intent i = new Intent(UpcomingEvents.this, FindPartner.class);
                i.putExtra("RefreshOnce", true);
                startActivity(i);
                break;
            case R.id.virtCard2:
                startActivity(new Intent(UpcomingEvents.this, Profile.class));
                break;
            case R.id.edit3:
                startActivity(new Intent(UpcomingEvents.this, EditProfile.class));
                break;
            case R.id.enrolled1:
                startActivity(new Intent(UpcomingEvents.this, EnrolledEvents.class));
                break;

        }
/*        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
