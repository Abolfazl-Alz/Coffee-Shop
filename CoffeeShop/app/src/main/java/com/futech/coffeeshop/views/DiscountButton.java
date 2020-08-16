package com.futech.coffeeshop.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;

import com.futech.coffeeshop.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DiscountButton extends androidx.appcompat.widget.AppCompatButton {

    private String discountCode;

    private static ContextThemeWrapper getThemeContext(Context context) {
        return new ContextThemeWrapper(context, R.style.Widget_AppCompat_Button_Borderless_Colored);
    }

    public DiscountButton(Context context) {
        super(getThemeContext(context));
        prepareView();
    }

    public void setDiscountCode(String code) {
        this.discountCode = code;
    }

    private void prepareView() {
        setText(R.string.discount_copy_btn);

        setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("coffee-discount", discountCode);
            if (clipboard == null) return;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.copied_msg, Toast.LENGTH_SHORT).show();
        });
    }
}
