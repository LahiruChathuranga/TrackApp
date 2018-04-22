package com.example.lahiru.trackapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AddActivity extends AppCompatActivity {
    private EditText mVehi_num;
    private EditText mVehi_model;
    private EditText mVehi_insurance;
    private EditText mVehi_licence;
    private EditText mDeviceId;
    private Button mSubmit;
    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST=1;
    Uri mImageUri;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private String hashEmail=null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        sharedPreferences=getSharedPreferences("myPref",0);
        editor=sharedPreferences.edit();
        mStorage= FirebaseStorage.getInstance().getReference();
        mProgress=new ProgressDialog(this);
        hashEmail=getIntent().getExtras().getString("hashEmail");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Vehicle").child(hashEmail);
        mSelectImage=(ImageButton)findViewById(R.id.imageButton);
        mVehi_num=(EditText)findViewById(R.id.vehi_num);
        mVehi_model=(EditText)findViewById(R.id.vehi_model);
        mVehi_insurance=(EditText)findViewById(R.id.vehi_insurance);
        mVehi_licence=(EditText)findViewById(R.id.vehi_licence);
        mDeviceId=(EditText)findViewById(R.id.device_id);
        mSubmit=(Button)findViewById(R.id.submit_btn);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


    }

    private void startPosting() {
        mProgress.setMessage("Adding Vehicle");
        mProgress.show();
        final String vehi_num=mVehi_num.getText().toString().trim();
        final String vehi_model=mVehi_model.getText().toString();
        final String vehi_insurance=mVehi_insurance.getText().toString().trim();
        final String vehi_licence=mVehi_licence.getText().toString().trim();
        final String deviceId=convertToSha(mDeviceId.getText().toString().trim());
        editor.putString("deviceId",deviceId);

       if(!TextUtils.isEmpty(vehi_num) && !TextUtils.isEmpty(vehi_model) && mImageUri!=null && !TextUtils.isEmpty(vehi_insurance) && !TextUtils.isEmpty(vehi_licence) && !TextUtils.isEmpty(deviceId)){

            StorageReference filepath=mStorage.child("Vehi_image").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    DatabaseReference newVehicle=mDatabase.child(deviceId);
                    newVehicle.child("vehi_num").setValue(vehi_num);
                    newVehicle.child("vehi_model").setValue(vehi_model);
                    newVehicle.child("vehi_insurance").setValue(vehi_insurance);
                    newVehicle.child("vehi_licence").setValue(vehi_licence);
                    newVehicle.child("vehi_image").setValue(downloadUrl.toString());


                    mProgress.dismiss();
                    Intent intent=new Intent(AddActivity.this,HomeActivity.class);
                    intent.putExtra("hashEmail",hashEmail);
                    intent.putExtra("deviceId",deviceId);
                    startActivity(intent);
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK ){

            mImageUri=data.getData();
            mSelectImage.setImageURI(mImageUri);
        }
    }
    private String convertToSha(String email){
        MessageDigest messageDigest=null;
        try {
            messageDigest=MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(email.getBytes(),0,email.length());

        String Email=new BigInteger(1,messageDigest.digest()).toString(16);
        return Email;


    }
}
