package com.example.dangerous.dangerousor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private TextView mNickname;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private View mProgressView;
    private View mRegisterFormView;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmailView = findViewById(R.id.register_email);

        mPasswordView = findViewById(R.id.register_password);
        mPasswordView2 = findViewById(R.id.register_password2);
        mNickname = findViewById(R.id.nick_name);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    attemptRegister();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mEmailRegisterButton = findViewById(R.id.register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
        token = InstanceID.getInstance(this).getId();
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNickname.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordView2.setError(null);

        // Store values at the time of the login attempt.
        String nickname = mNickname.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPasswordView2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password2) && !isPasswordValid(password2)) {
            mPasswordView2.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView2;
            cancel = true;
        }

        if (!TextUtils.isEmpty(nickname) && !isNicknameValid(nickname)) {
            mNickname.setError(getString(R.string.error_invalid_nickname));
            focusView = mNickname;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password, password2) && !TextUtils.isEmpty(password2)) {
            mPasswordView2.setError(getString(R.string.error_different_password));
            focusView = mPasswordView2;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(nickname)) {
            mNickname.setError(getString(R.string.error_field_required));
            focusView = mNickname;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            try {
                nickname = URLEncoder.encode(nickname, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            showProgress(true);
            mAuthTask = new UserRegisterTask(nickname, email, password, token);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        int count = 0;
        Pattern p = Pattern.compile("@");
        Matcher m = p.matcher(email);
        while (m.find()) {
            count++;
        }
        if(count!=1)
            return false;
        int count2 = 0;
        Pattern p2 = Pattern.compile("\\.");
        Matcher m2 = p2.matcher(email.split("@")[1]);
        while (m2.find()) {
            count2++;
        }
        if(count2==0)
            return false;
        int count3 = 0;
        Matcher m3 = p2.matcher(email.split("@")[0]);
        while (m3.find()) {
            count3++;
        }
        if(count3>0)
            return false;
        String regex="[a-zA-Z0-9]+";
        Matcher x = Pattern.compile(regex).matcher(email.replace("@", "").replace(".", ""));
        return x.matches() ;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isNicknameValid(String nickname) {
        //TODO: Replace this with your own logic
        return nickname.length() < 12;
    }

    private boolean isPasswordValid(String password, String password2) {
        //TODO: Replace this with your own logic
        return password.equals(password2);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String nickname;
        private final String mEmail;
        private final String mPassword;
        private final String token;
        private CheckRegister checkRegister;

        class CheckRegister {
            private String content;
            private boolean success;


            public String getContent() {
                String temp = content;
                try {
                    temp = URLDecoder.decode(temp, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return temp;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public boolean isSuccess() {
                return success;
            }

            public void setSuccess(boolean success) {
                this.success = success;
            }
        }

        UserRegisterTask(String nickname, String email, String password, String token) {
            this.nickname = nickname;
            this.mEmail = email;
            this.mPassword = password;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            StringBuilder response=null;
            try {
                // Simulate network access.
//                Thread.sleep(2000);
                URL url = new URL("http://" + Const.IP + "/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(String.format("nickname=%s&username=%s&password=%s&token=%s", this.nickname, this.mEmail, this.mPassword, this.token));
                InputStream in = connection.getInputStream();
                BufferedReader reader;
                reader = new BufferedReader(new InputStreamReader(in));
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response==null){
                return false;
            }
            Gson gson = new Gson();
            checkRegister = gson.fromJson(response.toString(), CheckRegister.class);
            if(checkRegister==null)
                return false;
            //                Toast.makeText(LoginActivity.this, checkLogin.getContent(), Toast.LENGTH_LONG).show();
            return checkRegister.isSuccess();
//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }

            // TODO: register the new account here.
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("account", checkRegister.getContent());
                editor.putString("email", mEmail);
                editor.putString("password", mPassword);
                editor.apply();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
//                if(checkLogin.getContent().equals("Wrong Password!"))
//                    mPasswordView.requestFocus();
//                else
//                    mEmailView.requestFocus();
//                Looper.prepare();
//                Toast.makeText(LoginActivity.this, checkLogin.getContent(), Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
