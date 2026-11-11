package com.example.grocerymate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.grocerymate.LoginActivity;
import com.example.grocerymate.MainActivity;
import com.example.grocerymate.data.DataStore;
import com.example.grocerymate.data.Session;
import com.example.grocerymate.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding b;
    private final DataStore data = DataStore.get();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c,
                             @Nullable Bundle s) {
        b = FragmentProfileBinding.inflate(inflater, c, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        b.tvInitials.setText(data.profile.initials);
        b.tvProfileName.setText(data.profile.name);
        b.tvProfileEmail.setText(data.profile.email);
        b.tvFamily.setText(data.profile.familyMembers + " Members");
        b.switchReminder.setChecked(data.profile.reminderOn);

        b.switchReminder.setOnCheckedChangeListener((btn, checked) -> {
            data.profile.reminderOn = checked;
            data.save();
            Toast.makeText(getContext(), checked ? "Reminders on" : "Reminders off",
                    Toast.LENGTH_SHORT).show();
        });
        b.tvEditProfile.setOnClickListener(x -> editProfile());
        b.rowHelp.setOnClickListener(x -> toast("Help & Support coming soon"));
        b.btnBack.setOnClickListener(x -> ((MainActivity) requireActivity()).openHome());
        b.btnLogout.setOnClickListener(x -> logout());
    }

    /** Edit name and family size; everything updates immediately. */
    private void editProfile() {
        android.content.Context c = requireContext();
        int pad = (int) (20 * getResources().getDisplayMetrics().density);

        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(pad, pad / 2, pad, 0);

        EditText etName = new EditText(c);
        etName.setHint("Full name");
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etName.setText(data.profile.name);
        box.addView(etName);

        EditText etFamily = new EditText(c);
        etFamily.setHint("Family members");
        etFamily.setInputType(InputType.TYPE_CLASS_NUMBER);
        etFamily.setText(String.valueOf(data.profile.familyMembers));
        box.addView(etFamily);

        new AlertDialog.Builder(c)
                .setTitle("Edit profile")
                .setView(box)
                .setPositiveButton("Save", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        toast("Name can't be empty");
                        return;
                    }
                    int family;
                    try {
                        family = Integer.parseInt(etFamily.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        family = data.profile.familyMembers;
                    }
                    family = Math.max(1, Math.min(family, 20));

                    Session.login(c, name, data.profile.email);   // also updates initials
                    data.profile.familyMembers = family;
                    data.save();

                    if (b != null) {
                        b.tvInitials.setText(data.profile.initials);
                        b.tvProfileName.setText(data.profile.name);
                        b.tvFamily.setText(data.profile.familyMembers + " Members");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        Session.logout(requireContext());
        Intent i = new Intent(requireActivity(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        requireActivity().finish();
    }

    private void toast(String m) {
        Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
