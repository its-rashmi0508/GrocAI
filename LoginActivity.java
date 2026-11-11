package com.example.grocerymate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymate.data.Session;
import com.example.grocerymate.databinding.ActivityLoginBinding;

/**
 * Simple name/email/password login. Whoever signs in becomes the active user —
 * their name (and initials) flow into the Home greeting and Profile screen.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Already signed in? Skip straight to the app.
        if (Session.isLoggedIn(this)) {
            Session.restoreProfile(this);
            goToApp();
            return;
        }

        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String name = text(b.etName);
        String email = text(b.etEmail);
        String password = text(b.etPassword);

        b.tilName.setError(null);
        b.tilEmail.setError(null);
        b.tilPassword.setError(null);

        if (TextUtils.isEmpty(name)) {
            b.tilName.setError(getString(R.string.err_name));
            b.etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            b.tilEmail.setError(getString(R.string.err_email));
            b.etEmail.requestFocus();
            return;
        }
        if (password.length() < 4) {
            b.tilPassword.setError(getString(R.string.err_password));
            b.etPassword.requestFocus();
            return;
        }

        // Demo auth: any valid input signs the user in.
        Session.login(this, name.trim(), email.trim());
        goToApp();
    }

    private void goToApp() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private String text(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString();
    }
}
