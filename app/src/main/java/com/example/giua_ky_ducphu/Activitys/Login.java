package com.example.giua_ky_ducphu.Activitys;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.giua_ky_ducphu.Models.Users;
import com.example.giua_ky_ducphu.R;
import com.example.giua_ky_ducphu.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        init_();
    }

    private void init_() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.dangKyTv.setOnClickListener(v -> startActivity(new Intent(Login.this, SignUp.class)));

        binding.dangNhapBtn.setOnClickListener(v -> checked());

        binding.quyenMatKhauTv.setOnClickListener(v -> startActivity(new Intent(Login.this, InputEmailActivity.class)));
    }

    private void checked() {
        String email = binding.emailEdt.getText().toString().trim();
        String password = binding.passwordEdt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email not null", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password not null", Toast.LENGTH_SHORT).show();
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginWithUsername(email, password);
            } else {
                loginWithEmail(email, password);
            }
        }
    }

    private void loginWithUsername(String email, String password) {
        progressDialog = ProgressDialog.show(this, "App", "Loading...", true);
        FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("User")

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Users user = dataSnapshot.getValue(Users.class);
                            if (user != null && user.getUserName().equals(email)) {
                                loginWithEmail(user.getEmail(), password);
                                return;
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Tên đăng nhập không tồn tại!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login with username, connect database faile!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NotConstructor")
    private void loginWithEmail(String email, String password) {
        progressDialog = ProgressDialog.show(this, "App", "Loading...", true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.emailEdt.setText("");
                        binding.passwordEdt.setText("");
                        progressDialog.dismiss();
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "email or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
