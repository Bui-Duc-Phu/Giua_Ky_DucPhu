package com.example.giua_ky_ducphu.Activitys;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import com.example.giua_ky_ducphu.R;
import com.example.giua_ky_ducphu.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Main extends AppCompatActivity {
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private ActionBarDrawerToggle toggle;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        init_();
    }

    private void init_() {
        navigationDrawer();
    }

    private void navigationDrawer() {
        setSupportActionBar(binding.toolbar);
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.navView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.changePassword) {
                Toast.makeText(getApplicationContext(), "changePassword", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.setLanguage) {
                // Handle setLanguage
            } else if (itemId == R.id.lightMode) {
                // Handle lightMode
            } else if (itemId == R.id.privacyPolicy) {
                // Handle privacyPolicy
            } else if (itemId == R.id.notification) {
                // Handle notification
            } else if (itemId == R.id.nav_logout) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.revokeAccess().addOnCompleteListener(this, task -> {});
                googleSignInClient.signOut().addOnCompleteListener(this, task -> {});
                startActivity(new Intent(this, LoginOrSignUp.class));
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
