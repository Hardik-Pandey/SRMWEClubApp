package in.weclub.srmweclubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class upload_pic extends AppCompatActivity  {

    private Button select,upload;
    private ImageView userpic;
    private Uri filepath;
    private final int Pick_image_request =71;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);
        select = (Button) findViewById(R.id.image_upload);
        upload = (Button)findViewById(R.id.image_loadtodb);
        userpic = (ImageView)findViewById(R.id.userpic);

        //Firebase storage reference and get firebase storage reference

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //initilaizing on click listners for Select and Upload Buttons
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectimage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage();
            }
        });
    }

    //defining selectimage() and upload image() methods
    private void selectimage()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"please select a picture"),Pick_image_request);
    }

    //setting up OnActivityForResult , if all result is ok image would be displayed in the image view

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data )
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == Pick_image_request && resultCode== RESULT_OK && data != null && data.getData() != null)
        {
                filepath= data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                    userpic.setImageBitmap(bitmap);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
        }
    }

    //defining upload image()
    private void uploadimage()
    {
        if(filepath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();

            StorageReference ref = storageReference.child("userpicimages/"+ UUID.randomUUID().toString());
            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(upload_pic.this,"Uploaded",Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(upload_pic.this,"Failed to upload"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Photo Uploaded"+(int)progress+"%");
                        }
                    });
        }
    }

}
