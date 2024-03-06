package com.example.giua_ky_ducphu.Activitys;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.giua_ky_ducphu.R;
import com.example.giua_ky_ducphu.databinding.ActivityLoginOrSignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginOrSignUp extends AppCompatActivity {

    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;

    ActivityLoginOrSignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOrSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        init_();
    }


    private void init_() {
        binding.signBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginOrSignUp.this, SignUp.class));
            finish();
        });
        binding.loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginOrSignUp.this, Login.class));
            finish();
        });
        binding.googleBtn.setOnClickListener(v -> signInGoogle());
        Glide.with(this).asGif().load(R.drawable.logo).into(binding.imageView);
    }

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        launcher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleResults(task);
                }
            });

    private void handleResults(Task<GoogleSignInAccount> task) {
        if (task.isSuccessful()) {
            GoogleSignInAccount account = task.getResult();
            if (account != null) {
                updateUI(account);
            }
        } else {
            Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            progressDialog = ProgressDialog.show(LoginOrSignUp.this, "App", "Loading...", true);
            firebaseUser = auth.getCurrentUser();
            if (firebaseUser != null) {
                Intent intent = new Intent(LoginOrSignUp.this, Main.class);
                intent.putExtra("email", account.getEmail());
                intent.putExtra("name", account.getDisplayName());
                startActivity(intent);
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
