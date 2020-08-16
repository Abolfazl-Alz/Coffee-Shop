package com.futech.coffeeshop.ui.create_account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.ui.login.LoginFormState;
import com.futech.coffeeshop.ui.login.LoginResult;
import com.futech.coffeeshop.ui.login.LoginViewModel;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.santalu.maskedittext.MaskEditText;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class CreateAccountActivity extends AppCompatActivity {

    private MaskEditText phoneNumberText;
    private EditText passwordText, passwordConfirmText;
    private Button signup;
    private ProgressBar loadingProgress;
    private SlidingUpPanelLayout sliding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final LoginViewModel loginViewModel = new LoginViewModel();

        phoneNumberText = findViewById(R.id.phone_number);
        passwordText = findViewById(R.id.password);
        passwordConfirmText = findViewById(R.id.password_confirm);
        sliding = findViewById(R.id.sliding_layout);

        Button toLogin = findViewById(R.id.toLogin);
        signup = findViewById(R.id.signup);

        loadingProgress = findViewById(R.id.loading);


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {

            @Override
            public void onChanged(LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                signup.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    phoneNumberText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordText.setError(getString(loginFormState.getPasswordError()));
                }
            }

        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.CreateAccountDataChanged(getApplicationContext(), phoneNumberText.getRawText(), passwordText.getText().toString(), passwordConfirmText.getText().toString());
            }
        };
        phoneNumberText.addTextChangedListener(afterTextChangedListener);
        passwordText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmText.addTextChangedListener(afterTextChangedListener);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgress.setVisibility(View.VISIBLE);
                loginViewModel.signup(getBaseContext(), phoneNumberText.getRawText(), passwordText.getText().toString());
                sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });


        passwordConfirmText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.signup(getBaseContext(), phoneNumberText.getRawText(), passwordText.getText().toString());
                }
                return false;
            }
        });


        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgress.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser();
                    setResult(Activity.RESULT_OK);
                    finish();
                    startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                }
            }
        });


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void updateUiWithUser() {
        String welcome = getString(R.string.welcome);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
