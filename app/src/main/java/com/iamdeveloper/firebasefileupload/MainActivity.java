package com.iamdeveloper.firebasefileupload;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_SOMETHING = 1;
    private StorageReference storageReference;
    private TextView text;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageReference = FirebaseStorage.getInstance().getReference();

        text = (TextView) findViewById(R.id.txt_url);


        button = (Button)findViewById(R.id.btn_upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContent();
            }
        });
    }


    private void getContent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_SOMETHING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_SOMETHING) {
                Log.d("PICK SOMETHING", data.getData().toString());
                String realPath = GetRealPath.getRealPathFromAPI19(this, data.getData());
                Log.i("realPath", realPath + "");
                if (realPath == null) {
                    realPath = GetRealPath.getRealPathFromAPI11_to18(this, data.getData());
                }
                Log.i("realPath", realPath + "");
                if (realPath == null) {
                    realPath = GetRealPath.getRealPathFromSD_CARD(data.getData());
                }
                Log.i("realPath", realPath + "");

                Uri file = Uri.fromFile(new File(realPath));

                upload(file);
            }
        }
    }


    private void upload(Uri file) {
        StorageReference referent = storageReference.child("herewego/"+file.getLastPathSegment());


        referent.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this,"Download "+taskSnapshot.getDownloadUrl(),Toast.LENGTH_SHORT).show();
                Log.i("onSuccess", "Download : " + taskSnapshot.getDownloadUrl() );
                String url = String.valueOf(taskSnapshot.getDownloadUrl());
                if(!url.isEmpty()){
                    text.setText(url);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("onFailure",e.toString());
                Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

}
