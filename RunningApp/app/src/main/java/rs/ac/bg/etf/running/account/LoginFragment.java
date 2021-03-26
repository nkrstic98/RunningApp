package rs.ac.bg.etf.running.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentLoginBinding;
import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AccountActivity accountActivity;
    private NavController navController;

    private FirebaseAuth firebaseAuth;

    public static String KEEP_LOGGED_IN = "login-flag";
    private boolean stayLoggedIn = true;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountActivity = (AccountActivity) requireActivity();
        firebaseAuth = FirebaseAuthInstance.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.btnLogin.setOnClickListener(v -> {
            login();
        });

        binding.keepLoggedIn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setChecked(isChecked);
            stayLoggedIn = isChecked;
        });

        binding.registerLink.setOnClickListener(v -> {
            //navigate to Register fragment
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void login() {
        String email = binding.emailText.getText().toString();
        String password  =binding.passwordText.getText().toString();

        if (email.equals("")) {
            binding.emailLabel.getEditText().requestFocus();
            Toast.makeText(accountActivity, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            binding.passwordLabel.getEditText().requestFocus();
            Toast.makeText(accountActivity, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(accountActivity, task -> {
                   if(task.isSuccessful()) {
                       Intent intent = new Intent(accountActivity, MainActivity.class);
                       intent.putExtra(KEEP_LOGGED_IN, this.stayLoggedIn);
                       intent.setAction(MainActivity.INTENT_ACTION_LOGGING);
                       startActivity(intent);
                       accountActivity.finish();
                   }
                });
    }
}