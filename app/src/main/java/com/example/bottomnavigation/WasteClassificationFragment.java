package com.example.bottomnavigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.recordcarbonemissions.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.protobuf.StringValue;



import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WasteClassificationFragment extends Fragment {
    private View vClassificationUnit;
    private TextView processResult;

    private ImageView wImageView;
    //private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        vClassificationUnit = inflater.inflate(R.layout.fragment_waste_classification, container, false);

        processResult = vClassificationUnit.findViewById(R.id.processResult);
        wImageView = vClassificationUnit.findViewById(R.id.wasteImageView);
        wImageView.setMaxHeight(225);
        wImageView.setMaxWidth(135);
        //take photo button
        Button takePhoto = vClassificationUnit.findViewById(R.id.takePictureBtn);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //pick photo button
        Button pickPhoto = vClassificationUnit.findViewById(R.id.pickPictureBtn);

        pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                cameraIntent.setType("image/*");
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, 1000);
                }

            }
        });




        return vClassificationUnit;
    }

    public void labelImage(FirebaseVisionImage processImage){
        //Or, to set the minimum confidence required:
        FirebaseVisionOnDeviceImageLabelerOptions options =
                new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.7f)
                        .build();
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler(options);

        labeler.processImage(processImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        // Task completed successfully
                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();
                            PopUpClass popUpClass = new PopUpClass();
                            popUpClass.showPopupWindow(vClassificationUnit,text);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        processResult.setText(R.string.FailDetection);
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try{
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                wImageView.setImageBitmap(imageBitmap);
                autoMlLabelImage(FirebaseVisionImage.fromBitmap(imageBitmap));
            }catch(Exception e){}
        }
        else if(resultCode == RESULT_OK && requestCode == 1000){
            try{
                Uri returnUri = data.getData();
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                wImageView.setImageBitmap(bitmapImage);
                autoMlLabelImage(FirebaseVisionImage.fromBitmap(bitmapImage));
            }catch(Exception e){}
        }
    }

    public void autoMlLabelImage(FirebaseVisionImage processImage){
        FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("subfolder/manifest.json")
                .build();

        FirebaseVisionImageLabeler labeler;
        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                    new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.55f)  // Evaluate your model in the Firebase console
                            // to determine an appropriate value.
                            .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);

            labeler.processImage(processImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            // Task completed successfully
                            // ...
                            for (FirebaseVisionImageLabel label: labels) {
                                String text = label.getText();
                                String entityId = label.getEntityId();
                                float confidence = label.getConfidence();
                                PopUpClass popUpClass = new PopUpClass();
                                popUpClass.showPopupWindow(vClassificationUnit,text);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    });
        } catch (FirebaseMLException e) {
            // ...
        }


    }
}
