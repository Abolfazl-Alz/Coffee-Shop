package com.futech.coffeeshop.ui.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.RegisterControl;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    void login(Context baseContext, String phoneNumber, String password) {
        final RegisterControl registerControl = new RegisterControl(baseContext);

        registerControl.login(phoneNumber, password, new RegisterControl.RegisterRequestResultListener() {
            @Override
            public void success() {
                loginResult.setValue(new LoginResult(new LoggedInUserView("")));
            }

            @Override
            public void error(String error, int errorCode) {
                if (errorCode > 100)
                    loginResult.setValue(new LoginResult(ErrorTranslator.getErrorMessageResource(ErrorTranslator.getErrorCodeByMessage(error))));
                else {
                    loginResult.setValue(new LoginResult(getCodeMessage(errorCode, R.string.error_register_login)));
                }
            }
        });
    }

    public void signup(Context context, final String phoneNumber, final String password) {
        RegisterControl registerControl = new RegisterControl(context);
        registerControl.createAccount(phoneNumber, password, new RegisterControl.RegisterRequestResultListener() {
            @Override
            public void success() {
                loginResult.setValue(new LoginResult(new LoggedInUserView(phoneNumber)));
            }

            @Override
            public void error(String error, int errorCode) {
                if (errorCode > 100)
                    loginResult.setValue(new LoginResult(ErrorTranslator.getErrorMessageResource(ErrorTranslator.getErrorCodeByMessage(error))));
                else {
                    loginResult.setValue(new LoginResult(getCodeMessage(errorCode, R.string.error_register_signup)));
                }
            }
        });
    }

    private int getCodeMessage(int errorCode, int invalidInformation) {
        int msg = -1;
        switch (errorCode) {
            case 0:
                msg = R.string.error_register_invalid;
                break;
            case 1:
                msg = R.string.error_register_server;
                break;
            case 2:
                msg = R.string.error_register_parameter;
                break;
            case 3:
                msg = invalidInformation;
                break;
        }
        return msg;
    }

    void loginDataChanged(Context context, String phoneNumber, String password) {
        if (isPhoneNumberNotValid(context, phoneNumber)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_phone, null));
        } else if (isPasswordNotValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        }else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void CreateAccountDataChanged(Context context, String phoneNumber, String password, String passwordConfirm) {
        if (isPhoneNumberNotValid(context, phoneNumber)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_phone));
        }else if (!password.equals(passwordConfirm)) {
            loginFormState.setValue(new LoginFormState(null, R.string.password_not_match));
        }else if (isPasswordNotValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        }else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isPhoneNumberNotValid(Context context, String phone) {
        return context.getResources().getInteger(R.integer.phone_number_length) != phone.length();
    }

    private boolean isPasswordNotValid(String password) {
        return password == null || password.trim().length() <= 5;
    }
}
