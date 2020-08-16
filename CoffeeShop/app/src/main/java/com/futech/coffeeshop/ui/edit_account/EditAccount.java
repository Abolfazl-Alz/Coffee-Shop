package com.futech.coffeeshop.ui.edit_account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.register.RegisterData;
import com.futech.coffeeshop.ui.address.AddressView;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;
import com.futech.coffeeshop.utils.local_database.ItemLocalDatabase;
import com.santalu.maskedittext.MaskEditText;

import java.util.HashMap;
import java.util.Map;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class EditAccount extends Fragment {

    private EditText firstNameText;
    private EditText lastNameText;
    private MaskEditText phoneNumber;
    private Button submit;
    private Button logout;
    private ProgressBar loading;
    private RegisterControl registerControl;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_account, container, false);

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            final MainActivity activity = (MainActivity) getActivity();
            activity.setTitle(R.string.edit_account_menu);
            activity.setToolbarColor(view.getContext().getResources().getColor(R.color.darkBackground));
            activity.getToolbar().setTitleTextColor(view.getContext().getResources().getColor(R.color.textDark));
        }

        registerControl = new RegisterControl(view.getContext());

        phoneNumber = view.findViewById(R.id.phoneEdit);
        firstNameText = view.findViewById(R.id.first_name_edit);
        lastNameText = view.findViewById(R.id.last_name_edit);
        submit = view.findViewById(R.id.submit);
        logout = view.findViewById(R.id.logout);
        loading = view.findViewById(R.id.loading);
        Button manageAddress = view.findViewById(R.id.go_manage_address);

        final RegisterData registerData = RegisterControl.getRegisterData(view.getContext());
        phoneNumber.setText(registerData.getPhoneNumber());
        firstNameText.setText(registerData.getFirstname());
        lastNameText.setText(registerData.getLastname());

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String rawText = phoneNumber.getRawText();
                if (rawText == null) {
                    submit.setEnabled(false);
                    return;
                }
                submit.setEnabled(rawText.length() == view.getContext().getResources().getInteger(R.integer.phone_number_length));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                updateInformation(v.getContext());
            }
        });

        phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_DONE) {
                    updateInformation(v.getContext());
                }
                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerControl.logout();
                new CartLocalDatabase(getContext()).deleteAll();
                new ItemLocalDatabase(getContext()).deleteAll();
                new CollectionLocalDatabase(getContext()).deleteAll();
                if (getActivity() != null) getActivity().finish();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        manageAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), AddressView.class));
            }
        });

        return view;
    }

    private void updateInformation(final Context context) {
        setLoadingState(true);

        Map<String, String> values = new HashMap<>();
        values.put("first_name", firstNameText.getText().toString().trim());
        values.put("last_name", lastNameText.getText().toString().trim());
        values.put("phone_number", phoneNumber.getRawText());
        values.put("id", String.valueOf(RegisterControl.getCurrentUserUid(context)));

        RegisterControl ctrl = new RegisterControl(context);
        ctrl.updateInformation(values, new RegisterControl.RegisterRequestResultListener() {
            @Override
            public void success() {
                startActivity(new Intent(getContext(), MainActivity.class));
                if (getActivity() != null) getActivity().finish();
                setLoadingState(false);
                Toast.makeText(context, R.string.information_update, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String error, int errorCode) {
                setLoadingState(false);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().hide();
        }
    }

    private void setLoadingState(boolean state) {
        loading.setVisibility(state ? View.VISIBLE : View.GONE);
        firstNameText.setEnabled(!state);
        phoneNumber.setEnabled(!state);
        submit.setEnabled(!state);
        logout.setEnabled(!state);
    }


}
