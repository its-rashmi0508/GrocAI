package com.example.grocerymate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.grocerymate.databinding.ActivityMainBinding;
import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.data.Session;
import com.example.grocerymate.ui.AddItemDialog;
import com.example.grocerymate.ui.BudgetFragment;
import com.example.grocerymate.ui.HomeFragment;
import com.example.grocerymate.ui.ListFragment;
import com.example.grocerymate.ui.ProfileFragment;
import com.example.grocerymate.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    private final ActivityResultLauncher<String> notifPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Guard: if the process was restored without a session, go back to login.
        if (!Session.isLoggedIn(this)) {
            startActivity(new android.content.Intent(this, LoginActivity.class)
                    .setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        DataStore.init(this);
        Session.restoreProfile(this);

        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // The 3rd menu slot only reserves space for the centre "+" button:
        // hide it completely so only ONE Home icon is visible.
        View spacer = b.bottomNav.findViewById(R.id.nav_placeholder);
        if (spacer != null) {
            spacer.setVisibility(View.INVISIBLE);
            spacer.setClickable(false);
        }

        NotificationHelper.createChannel(this);
        askNotificationPermission();

        b.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return show(new HomeFragment());
            if (id == R.id.nav_list) return show(new ListFragment());
            if (id == R.id.nav_budget) return show(new BudgetFragment());
            if (id == R.id.nav_profile) return show(new ProfileFragment());
            return false;
        });

        b.fab.setOnClickListener(v -> new AddItemDialog(() -> openList())
                .show(getSupportFragmentManager(), "add"));

        if (savedInstanceState == null) {
            b.bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean show(@NonNull Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, f)
                .commit();
        return true;
    }

    /** Called after adding an item so the user immediately sees it. */
    public void openList() {
        b.bottomNav.setSelectedItemId(R.id.nav_list);
    }

    public void openBudget() {
        b.bottomNav.setSelectedItemId(R.id.nav_budget);
    }

    public void openHome() {
        b.bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
