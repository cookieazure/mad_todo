package com.android.master.mad.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.master.mad.todo.data.User;
import com.android.master.mad.todo.sync.RetrofitServiceGenerator;
import com.android.master.mad.todo.sync.ITaskAuthOperations;

import java.io.IOException;

import retrofit2.Call;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    // UI references.
    private EditText mailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;
    private Button signInButton;
    private ImageView errorImage;
    private TextView errorHeader;
    private TextView errorItem;

    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, " : onCreate().");
        setContentView(R.layout.activity_login);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up the login form.
        mailView = (EditText) findViewById(R.id.email);
        mailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkLoginButtonState();
                resetAuthError();
            }
        });

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkLoginButtonState();
                resetAuthError();
            }
        });

        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setEnabled(false);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        errorImage = (ImageView) findViewById(R.id.error_image);
        errorHeader = (TextView) findViewById(R.id.error_header);
        errorItem = (TextView) findViewById(R.id.error_item);
    }

    //===========================================
    // LOGIN METHODS
    //===========================================
    private void checkLoginButtonState(){
        if(mailView.getText().toString().isEmpty() ||  passwordView.getText().toString().isEmpty()){
            if(signInButton.isEnabled()){
                signInButton.setEnabled(false);
            }
        } else {
            signInButton.setEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Log.v(LOG_TAG, " : attemptLogin().");
        if (authTask != null) {
            return;
        }

        // Reset errors.
        mailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address and password
        if (TextUtils.isEmpty(email)) {
            mailView.setError(getString(R.string.error_field_required));
            focusView = mailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mailView.setError(getString(R.string.error_invalid_email));
            focusView = mailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            authTask = new UserLoginTask(email, password);
            authTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        Log.v(LOG_TAG, " : isEmailValid().");
        return email.matches("[A-z0-9][A-z0-9._%+-]*@[A-z0-9.-]*\\.[A-z]{2,}");
    }

    private boolean isPasswordValid(String password) {
        Log.v(LOG_TAG, " : isPasswordValid().");
        return password.matches("[0-9]{6}");
    }

    private void showAuthError(){
        Log.v(LOG_TAG, " : showAuthError().");
        errorImage.setImageResource(R.drawable.ic_error_outline);
        errorHeader.setText(getString(R.string.error_connection));
        errorItem.setText(getString(R.string.error_incorrect_credentials));
    }

    private void resetAuthError(){
        Log.v(LOG_TAG, " : resetAuthError().");
        if(errorImage.getDrawable() != null) {
            errorImage.setImageDrawable(null);
            errorHeader.setText(null);
            errorItem.setText(null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        Log.v(LOG_TAG, " : showProgress().");
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //===========================================
    // ASYNC AUTHENTICATION
    //===========================================
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String LOG_TAG = UserLoginTask.class.getSimpleName();
        private final User user;

        UserLoginTask(String email, String password) {
            user = new User(email, password);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.v(LOG_TAG, " : doInBackground().");
            ITaskAuthOperations webServiceAuthenticator = RetrofitServiceGenerator.createService(ITaskAuthOperations.class);
            Call<Boolean> call = webServiceAuthenticator.authenticateUser(user);
            try {
                Boolean authResult = call.execute().body();
                Log.d(LOG_TAG, " : Auth complete (" + authResult +").");
                return authResult;
            } catch (IOException e){
                Log.i(LOG_TAG, " : Auth not successful.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.v(LOG_TAG, " : onPostExecute().");
            authTask = null;
            showProgress(false);

            if (success) {
                setResult(RESULT_OK);
                finish();
            } else {
                showAuthError();
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            Log.v(LOG_TAG, " : onCancelled().");
            authTask = null;
            showProgress(false);
        }
    }
}

