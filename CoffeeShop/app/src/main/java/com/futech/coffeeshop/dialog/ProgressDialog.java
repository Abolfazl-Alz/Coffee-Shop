package com.futech.coffeeshop.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.futech.coffeeshop.R;

public class ProgressDialog {

    @NonNull
    private final Context context;
    private TextView titleText, informationText;
    private ProgressBar progressBar;
    private AlertDialog alert;

    public ProgressDialog(@NonNull Context context) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_dialog_layout, null, false);
        builder.setView(view);
        titleText = view.findViewById(R.id.title_text);
        informationText = view.findViewById(R.id.information_text);
        progressBar = view.findViewById(R.id.progress_circular);
        builder.setTitle("");
        alert = builder.create();
    }

    public void setTitle(@StringRes int titleId) {
        titleText.setText(titleId);
    }

    public void setTitle(CharSequence title) {
        titleText.setText(title);
    }

    public void setInformation(@StringRes int informationId) {
        informationText.setText(informationId);
    }

    public void setInformation(String information) {
        informationText.setText(information);
    }

    public void setProgressBar(int progress) {
        progressBar.setProgress(progress);
    }

    public void showDialog() {
        alert.show();
    }

    public void dismiss() {
        alert.dismiss();
        alert.cancel();
    }

    @NonNull
    public Context getContext() {
        return context;
    }
}
