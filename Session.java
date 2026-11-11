package com.example.grocerymate.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lightweight login/session store backed by SharedPreferences.
 * Persists the signed-in user across app launches and keeps the shared
 * {@link DataStore#profile} in sync so every screen shows the real user.
 */
public class Session {

    private static final String PREF = "grocerymate_session";
    private static final String K_LOGGED_IN = "logged_in";
    private static final String K_NAME = "name";
    private static final String K_EMAIL = "email";

    private Session() { }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    /** Save the user and mirror it into the in-memory profile. */
    public static void login(Context ctx, String name, String email) {
        prefs(ctx).edit()
                .putBoolean(K_LOGGED_IN, true)
                .putString(K_NAME, name)
                .putString(K_EMAIL, email)
                .apply();
        applyToProfile(name, email);
    }

    public static boolean isLoggedIn(Context ctx) {
        return prefs(ctx).getBoolean(K_LOGGED_IN, false);
    }

    public static void logout(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }

    public static String name(Context ctx) {
        return prefs(ctx).getString(K_NAME, "");
    }

    public static String email(Context ctx) {
        return prefs(ctx).getString(K_EMAIL, "");
    }

    /** Load a previously saved session into the shared profile (call on startup). */
    public static void restoreProfile(Context ctx) {
        if (isLoggedIn(ctx)) applyToProfile(name(ctx), email(ctx));
    }

    private static void applyToProfile(String name, String email) {
        DataStore d = DataStore.get();
        d.profile.name = name;
        d.profile.email = email;
        d.profile.initials = initialsFor(name);
    }

    /** First letters of the first two words, e.g. "Vivek Kumar" -> "VK". */
    public static String initialsFor(String name) {
        if (name == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (sb.length() == 2) break;
            }
        }
        return sb.toString();
    }
}
