package com.futech.coffeeshop.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import androidx.annotation.StringRes;

import com.futech.coffeeshop.R;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static class EncodeImage extends AsyncTask<Void, Void, String> {

        private final Bitmap bitmap;
        private EncodeListener listener;

        public EncodeImage(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected String doInBackground(Void... v) {
            if (bitmap == null) {
                listener.onError(R.string.select_image_error);
                return "";
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmap.recycle();
            byte[] array = stream.toByteArray();
            return Base64.encodeToString(array, 0);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("")) return;
            listener.onEncode(result);
        }

        public void setOnEncodeListener(EncodeListener listener) {
            this.listener = listener;
        }

        public interface EncodeListener {
            void onEncode(String encodeString);

            void onError(int stringResource);
        }
    }

    public interface EncodeImageListener {
        void onEncode();

        void onStateChange(@StringRes int msg);

        void onError(@StringRes int msg);
    }
}
