package com.example.giua_ky_ducphu.Activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.giua_ky_ducphu.Models.Users;
import com.example.giua_ky_ducphu.R;
import com.example.giua_ky_ducphu.databinding.ActivityLoginOrSignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class LoginOrSignUp extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private GoogleSignInOptions gso;
    private ActivityLoginOrSignUpBinding binding;

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
        binding.signBtn.setOnClickListener(view -> startActivity(new Intent(LoginOrSignUp.this, SignUp.class)));

        binding.loginBtn.setOnClickListener(view -> startActivity(new Intent(LoginOrSignUp.this, Login.class)));

        binding.googleBtn.setOnClickListener(view -> signInGoogle());

        Glide.with(this).asGif().load(R.drawable.logo).into(binding.imageView);
    }

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResults(task);
        }
    }

    private void handleResults(Task<GoogleSignInAccount> completedTask) {
        if (completedTask.isSuccessful()) {
            GoogleSignInAccount account = completedTask.getResult();
            if (account != null) {
                String email = account.getEmail();
                if (email != null && !email.isEmpty()) {
                    checkMail(email, new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> innerTask) {
                            if (innerTask.isSuccessful()) {
                                boolean isEmailExists = innerTask.getResult();
                                if (isEmailExists) {
                                    Toast.makeText(getApplicationContext(), "Email này đã được đăng ký", Toast.LENGTH_SHORT).show();
                                    googleSignInClient.revokeAccess().addOnCompleteListener(LoginOrSignUp.this, task -> {});
                                    googleSignInClient.signOut().addOnCompleteListener(LoginOrSignUp.this, task -> {});
                                } else {
                                    updateUI(account);
                                }
                            } else {
                                Toast.makeText(LoginOrSignUp.this, innerTask.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Không thể truy cập thông tin email của tài khoản Google này.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, completedTask.getException().toString(), Toast.LENGTH_SHORT).show();
        }
    }



    private void updateUI(GoogleSignInAccount account) {
        progressDialog = ProgressDialog.show(LoginOrSignUp.this, "App", "Loading...", true);
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")
                    .child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("userID", userId);
                        hashMap.put("userName", account.getDisplayName());
                        hashMap.put("email", account.getEmail());
                        hashMap.put("password", "");
                        hashMap.put("typeAccount", "2");
                        databaseReference.setValue(hashMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginOrSignUp.this, Main.class));
                                finish();
                            } else {
                                Toast.makeText(LoginOrSignUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        });
                    } else {
                        startActivity(new Intent(LoginOrSignUp.this, Main.class));
                        finish();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginOrSignUp.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void checkMail(String email, OnCompleteListener<Boolean> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://coffe-app-19ec3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEmailExists = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null && user.getEmail().equals(email) && user.getTypeAccount().equals("1")) {
                        isEmailExists = true;
                        break;
                    }
                }
                Task<Boolean> task = Tasks.forResult(isEmailExists);
                callback.onComplete(task);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Lỗi khi truy xuất dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                callback.onComplete(Tasks.forException(error.toException())); // Pass exception as Task<Boolean>
            }
        });
    }


}

