package com.futech.coffeeshop.utils;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.futech.coffeeshop.R;

public class ActivityUtils {
    public static void closeFormEditor(final Activity activity, final Runnable saveMethod) {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(activity);
        alertDlg.setTitle(R.string.close_editor_title);
        alertDlg.setMessage(R.string.close_editor_msg);
        alertDlg.setIcon(android.R.drawable.ic_dialog_alert);
        alertDlg.setNeutralButton(R.string.close_btn, (dialog, which) -> activity.finish());

        alertDlg.setNegativeButton(R.string.keep_btn, (dialog, which) -> dialog.dismiss());

        alertDlg.setPositiveButton(R.string.save_item, (dialog, which) -> {
            try {
                saveMethod.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        alertDlg.show();
    }
}
