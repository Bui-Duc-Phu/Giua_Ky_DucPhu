package com.example.giua_ky_ducphu.Activitys;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.giua_ky_ducphu.Models.Users;
import com.example.giua_ky_ducphu.R;
import com.example.giua_ky_ducphu.databinding.ActivityForgotPasswordBinding;
import com.example.giua_ky_ducphu.databinding.DialogCustomForgotPasswordTrueBinding;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    String receiver;
    String uri;

    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        receiver = getIntent().getStringExtra("receiver");

        init_();
    }

    private void init_() {
        binding.guiBtn.setOnClickListener(v -> upDatePasswrod());
    }

    private void upDatePasswrod() {
        String newPassword = binding.passwordEdt.getText().toString();
        String retypePassword = binding.retypePassword.getText().toString();
        progressDialog = ProgressDialog.show(this, "App", "Loading...", true);
        if (TextUtils.isEmpty(newPassword)) {
            binding.passwordEdt.setError("Chưa nhập mật khẩu");
        } else if (TextUtils.isEmpty(retypePassword)) {
            binding.passwordEdt.setError("Chưa nhập mật khẩu");
        } else {
            FirebaseDatabase.getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Users user = snapshot1.getValue(Users.class);
                                if (user != null && user.getEmail().equals(receiver)) {
                                    uri = user.getUserID();
                                    updatePassword(receiver, user.getPassword(), newPassword);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePassword(String email, String password, String newPassword) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, signInTask -> {
                    if (signInTask.isSuccessful()) {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            currentUser.reauthenticate(EmailAuthProvider.getCredential(email, password))
                                    .addOnCompleteListener(reauthTask -> {
                                        if (reauthTask.isSuccessful()) {
                                            currentUser.updatePassword(newPassword)
                                                    .addOnCompleteListener(updatePasswordTask -> {
                                                        if (updatePasswordTask.isSuccessful()) {
                                                            FirebaseDatabase.getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                    .getReference("Users")
                                                                    .child(currentUser.getUid())
                                                                    .child("password")
                                                                    .setValue(newPassword)
                                                                    .addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            progressDialog.dismiss();
                                                                            dialog_(1);
                                                                            new Handler().postDelayed(() -> {
                                                                                dialog_(0);
                                                                                startActivity(new Intent(ForgotPasswordActivity.this, Main.class));
                                                                                finish();
                                                                            }, 2000);
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(),
                                                                                    "Failed to set password on realtime: " + task.getException().getMessage(),
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Failed to update password: " + updatePasswordTask.getException().getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),
                                                    "Failed to reauthenticate: " + reauthTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Current user is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void dialog_(int a) {
        if (!isFinishing()) {
            Dialog dialog = new Dialog(this);
            DialogCustomForgotPasswordTrueBinding dialogView = DialogCustomForgotPasswordTrueBinding.inflate(getLayoutInflater());
            dialog.setContentView(dialogView.getRoot());
            if (a == 1) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
        }
    }
}

