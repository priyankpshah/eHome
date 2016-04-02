package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Users;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SAVED_LOGIN_PREFS = "saved_login_prefs";
    public static final String EXTRA_LOGOUT = "logout";

    EditText loginEmail;
    EditText loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        SharedPreferences savedLoginPrefs = getSharedPreferences(SAVED_LOGIN_PREFS, MODE_PRIVATE);

        if (getIntent().getBooleanExtra(EXTRA_LOGOUT, false)) {
            // remove saved user login
            savedLoginPrefs.edit()
                    .remove(Users.KEY_EMAIL)
                    .remove(Users.KEY_PASSWORD)
                    .apply();
        } else {
            // check if user is already signed in, and sign them in if they are
            String savedEmail = savedLoginPrefs.getString(Users.KEY_EMAIL, null);
            String savedPassword = savedLoginPrefs.getString(Users.KEY_PASSWORD, null);

            if (savedEmail != null && savedPassword != null)
                login(savedEmail, savedPassword);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                login(email, password);
                break;
            case R.id.registerButton:
                Intent register = new Intent(this, RegisterActivity.class);
                startActivity(register);
                break;
        }
    }

    private void login(String email, String password) {
        User user = ApartmentDatabaseHelper.getInstance(this).getUser(email, password);

        // TODO better error feedback, pass User data to MainActivity
        if (user != null) {
            // save login info
            SharedPreferences savedLoginPrefs = getSharedPreferences(SAVED_LOGIN_PREFS, MODE_PRIVATE);
            savedLoginPrefs.edit()
                    .putString(Users.KEY_EMAIL, email)
                    .putString(Users.KEY_PASSWORD, password)
                    .apply();

            Intent login = new Intent(this, MainActivity.class);
            startActivity(login);
            finish();
        } else {
            Toast.makeText(this, "Login failed.", Toast.LENGTH_LONG).show();
        }
    }

}
