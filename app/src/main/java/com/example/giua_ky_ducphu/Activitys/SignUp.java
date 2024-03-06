package com.example.giua_ky_ducphu.Activitys;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giua_ky_ducphu.Interfaces.OnUserNameCheckListener;
import com.example.giua_ky_ducphu.Models.Users;
import com.example.giua_ky_ducphu.databinding.ActivitySignUpBinding;
import com.example.giua_ky_ducphu.databinding.DialogCustomBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private DialogCustomBinding dialogBinding;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private OnUserNameCheckListener onUserNameCheckListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init_();
    }

    private void init_() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.dangNhapTv.setOnClickListener(v -> startActivity(new Intent(SignUp.this, Login.class)));

        checked();
    }

    private void checked() {
        auth = FirebaseAuth.getInstance();

        binding.dangKyBtn.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(SignUp.this, "App", "Loading...", true);

            String userName = binding.nameEdt.getText().toString().trim();
            String email = binding.emailEdt.getText().toString().trim();
            String password = binding.passwordEdt.getText().toString().trim();

            checkUserName(userName);
            setOnUserNameCheckListener(isExists -> {
                if (isExists) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(userName)) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên đăng nhập", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(email)) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(password)) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Password not null", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "không đúng định dạng mail", Toast.LENGTH_SHORT).show();
                    } else {
                        Register(userName, email, password);
                    }
                }
            });

        });
    }
    public void setOnUserNameCheckListener(OnUserNameCheckListener listener) {
        this.onUserNameCheckListener = listener;
    }


    private void Register(String userName, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                String userid = user.getUid();
                databaseReference = FirebaseDatabase
                        .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("Users")
                        .child(userid);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userID", userid);
                hashMap.put("userName", userName);
                hashMap.put("email", email);
                hashMap.put("password", password);
                hashMap.put("typeAccount", "1");

                databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        progressDialog.dismiss();
                        dialog_(1);
                        new Handler().postDelayed(() -> {
                            dialog_(0);
                            startActivity(new Intent(SignUp.this, Main.class));
                            finish();
                        }, 2000);
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "signUp false,Email này đã được liên kết bằng google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dialog_(int a) {
        if (!isFinishing()) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(DialogCustomBinding.inflate(getLayoutInflater()).getRoot());
            if (a == 1) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
        }
    }

    private void checkUserName(String userName) {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean isUserNameExists = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null && user.getUserName().equals(userName)) {
                        isUserNameExists = true;
                        break;
                    }
                }
                if (onUserNameCheckListener != null) {
                    onUserNameCheckListener.onUserNameChecked(isUserNameExists);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "checkUsername connect to firebase false : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (onUserNameCheckListener != null) {
                    onUserNameCheckListener.onUserNameChecked(true); // Handling onCancelled event by indicating true
                }
            }
        });
    }

}
