package in.weclub.srmweclubapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ClaimReward extends AppCompatActivity {

    private String t;
    private String id;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_claim_reward);

            ActionBar actionBar = getSupportActionBar();

            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#007ee5")));
            }

            ImageView qrCode = (ImageView)findViewById(R.id.imageView8);
            id = getIntent().getStringExtra("UID");
            t = getIntent().getStringExtra("Title");
            setTitle(t);
            MultiFormatWriter mfw = new MultiFormatWriter();
            try
            {
                BitMatrix bm = mfw.encode(id , BarcodeFormat.QR_CODE, 250, 250);
                BarcodeEncoder be = new BarcodeEncoder();
                Bitmap i = be.createBitmap(bm);
                qrCode.setImageBitmap(i);
            }
            catch (WriterException e)
            {
                e.printStackTrace();
            }
        }

    public boolean onOptionsItemSelected(MenuItem item){
            Intent it;
            if(t.equals("Claim Offer"))
                it = new Intent(this, FindPartner.class);
            else{
                it = new Intent(this, EventDescription.class);
                it.putExtra("Event ID",id);
            }
        startActivityForResult(it, 0);
        return true;

    }
}
