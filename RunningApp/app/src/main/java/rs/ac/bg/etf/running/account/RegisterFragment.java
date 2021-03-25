package rs.ac.bg.etf.running.account;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentRegisterBinding;
import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;
import rs.ac.bg.etf.running.firebase.FirebaseFirestoreInstance;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AccountActivity accountActivity;
    private NavController navController;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFERENCES_PHOTO = "photo-shared-prefences";
    public static final String SHARED_PREFERENCES_DATA = "photo-uri";

    private Uri takenPhoto = null;

    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String[] REQUIRED_PERMISSIONS = { Manifest.permission.CAMERA };

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountActivity = (AccountActivity) requireActivity();

        firebaseAuth = FirebaseAuthInstance.getInstance();
        firebaseFirestore = FirebaseFirestoreInstance.getInstance();

        sharedPreferences = accountActivity.getSharedPreferences(SHARED_PREFERENCES_PHOTO, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        binding.btnRegister.setOnClickListener(v -> {
            register();
        });

        binding.loginLink.setOnClickListener(v -> {
            //vrati se na stranicu za logovanje
            navController.navigate(RegisterFragmentDirections.actionRegisterFragmentPop());
        });

        binding.imageProfile.setOnClickListener(v -> {
            if(!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                        accountActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                );
                if(allPermissionsGranted()) {
                    dispatchTakePictureIntent();
                }
            }
            else {
                dispatchTakePictureIntent();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        ImageView imageView = accountActivity.findViewById(R.id.imageProfile);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.no_profile_pic));
    }

    private void register() {

    }

    private boolean allPermissionsGranted() {
        for(int i = 0; i < REQUIRED_PERMISSIONS.length; i++) {
            if(ContextCompat.checkSelfPermission(accountActivity, REQUIRED_PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private File createImageFile() throws IOException {
        String sdf = new SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH).format(System.currentTimeMillis());
        String imageFileName = "JPEG_"+  sdf + "_";

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                accountActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        );

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(accountActivity.getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile != null) {
                this.takenPhoto = FileProvider.getUriForFile(accountActivity, "rs.ac.bg.etf.running", photoFile);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PREFERENCES_DATA, this.takenPhoto.toString());
                editor.commit();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.takenPhoto);
                accountActivity.startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }
}