package rs.ac.bg.etf.running.account;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.ActivityAccountBinding;
import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private FirebaseAuth firebaseAuth;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuthInstance.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(MainActivity.INTENT_ACTION_LOGGING);
            intent.putExtra(LoginFragment.KEEP_LOGGED_IN, true);
            startActivity(intent);
            finish();
        }

        sharedPreferences = this.getSharedPreferences(RegisterFragment.SHARED_PREFERENCES_PHOTO, MODE_PRIVATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RegisterFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String imageString = sharedPreferences.getString(RegisterFragment.SHARED_PREFERENCES_DATA, null);
                Uri imageUri = Uri.parse(imageString);
                ImageView imageView = findViewById(R.id.imageProfile);
                imageView.setImageURI(imageUri);
            }
        }
    }
}