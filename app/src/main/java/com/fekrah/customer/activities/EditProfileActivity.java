package com.fekrah.customer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.customer.R;
import com.fekrah.customer.helper.Utility;
import com.fekrah.customer.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rafakob.drawme.DrawMeButton;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends BaseActivity {
    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.profile_image_view)
    View profile_image_view;

    @BindView(R.id.profile_image)
    SimpleDraweeView profile_image;

    @BindView(R.id.register)
    DrawMeButton registerBtn;

    Dialog dialog;

    byte[] profilebyte;
    // firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMyProfileReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference licenceImageStorageReference;
    private StorageReference frontImageStorageReference;
    private StorageReference formImageStorageReference;
    private StorageReference backImageStorageReference;
    private StorageReference profileStorageReference;

    private String UserId;
    UCrop.Options options;
    private User user;
    private String REQUEST_FOR_PICTURE;
    private static final String PROFILE_IMAGE_REQUEST = "PROFILE_IMAGE_REQUEST";
    private Bitmap thumbBitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.update_profile);
        Utility.hideSoftKeyboard(this);
        options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        dialog = new Dialog(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mMyProfileReference = mFirebaseDatabase.getReference().child("users");
        frontImageStorageReference = mFirebaseStorage.getReference();
        backImageStorageReference = mFirebaseStorage.getReference();
        profileStorageReference = mFirebaseStorage.getReference();
        licenceImageStorageReference = mFirebaseStorage.getReference();
        formImageStorageReference = mFirebaseStorage.getReference();

        mMyProfileReference.child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    name.setText(user.getName());
                    profile_image.setImageURI(user.getImg());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(name.getText().toString());

                if (name.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.first_name_empty));
                } else if (firstName.matches()) {
                    displaySnackbar(getString(R.string.first_name_no_number));
                }  else if (profilebyte == null) {
                    displaySnackbar(getString(R.string.profile_required));
                } else {
                    if (hasInternet()) {
                        // verifyPhone(phone.getText().toString());
                        createUser();
                    } else {
                        displaySnackbar(getString(R.string.something_went_wrong_net));
                    }
                }
            }


        });

        profile_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_FOR_PICTURE = PROFILE_IMAGE_REQUEST;
                ImagePicker.create(EditProfileActivity.this)
                        .limit(1)
                        .theme(R.style.UCrop)
                        .folderMode(true)
                        .start();
            }
        });
    }

    private void createUser() {
        Utility.showProgressDialog(dialog, true);

        final StorageReference profileRef = profileStorageReference.
                child(FirebaseAuth.getInstance().getUid())
                .child("profile_image")
                .child(System.currentTimeMillis() + ".jpg");

        profileRef.putBytes(profilebyte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri profileUri) {
                        final User driver2 = new User(
                                name.getText().toString(),
                                user.getEmail(),
                                profileUri.toString(),
                                user.getMobile(),
                                user.getDevice_token(),
                                user.getUser_key()
                        );
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(driver2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            //Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            return;
        }
        String destinationFileName = "SAMPLE_CROPPED_IMAGE_NAME" + ".jpg";

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            Image image = ImagePicker.getFirstImageOrNull(data);
            Uri res_url = Uri.fromFile(new File((image.getPath())));
            CropImage(image, res_url);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            //  if (resultUri!=null)
            assert resultUri != null;
            bitmapCompress(resultUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

            if (REQUEST_FOR_PICTURE.equals(PROFILE_IMAGE_REQUEST)){
                profilebyte = byteArrayOutputStream.toByteArray();
                profile_image.setImageURI(resultUri);
                profile_image.setVisibility(View.VISIBLE);
            }

        }

    }

    private void CropImage(Image image, Uri res_url) {
        UCrop.of(res_url, Uri.fromFile(new File(getCacheDir(), image.getName())))
                .withOptions(options)
                .start(EditProfileActivity.this);
    }

    //upload thumb image
    private void uploadThumbImage(byte[] thumbByte, final StorageReference thumbFilePathRef) {

        thumbFilePathRef.putBytes(thumbByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                thumbFilePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri thumbUri) {
                        mMyProfileReference.child("user_thumb_image").setValue(thumbUri.toString());
                        //  myProfileImage.setImageURI(thumbUri);
                    }
                });
            }
        });
    }

    private void bitmapCompress(Uri resultUri) {
        final File thumbFilepathUri = new File(resultUri.getPath());

        try {
            thumbBitmap = new Compressor(this)
                    .setQuality(50)
                    .compressToBitmap(thumbFilepathUri);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
