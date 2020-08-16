package com.futech.coffeeshop.ui.review;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.futech.coffeeshop.CoffeeApplication;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.local_database.ItemLocalDatabase;
import com.futech.coffeeshop.utils.local_database.ReviewLocalDatabase;

import java.io.File;
import java.util.List;

public class WriteReview extends AppCompatActivity implements View.OnClickListener {

    ImageButton mCloseBtn;
    Button mPostBtn;
    EditText mMessageEdit;
    RatingBar mRatingBar;
    ProgressBar mProgressBar;
    TextView mTitleText;
    ImageView mImageView;

    public static final String TAG = "WriteReview";

    private int mItemId = 0;
    private int mUid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        mCloseBtn = findViewById(R.id.close_btn);
        mPostBtn = findViewById(R.id.post_btn);
        mMessageEdit = findViewById(R.id.text);
        mRatingBar = findViewById(R.id.rating);
        mProgressBar = findViewById(R.id.progress_circular);
        mTitleText = findViewById(R.id.title_text);
        mImageView = findViewById(R.id.item_image);

        final Bundle extras = getIntent().getExtras();
        if (extras == null || extras.isEmpty() || extras.getInt("id", 0) == 0) {
            finish();
            return;
        }

        mItemId = extras.getInt("id", 0);

        ItemLocalDatabase db = new ItemLocalDatabase(this);
        List<ItemData> select = db.select("id=?", new String[]{String.valueOf(mItemId)});
        if (select.size() == 1) {
            ItemData itemData = select.get(0);
            mTitleText.setText(itemData.getTitle());

            final String filename = itemData.getTitle() + ".png";
            final File sd = CoffeeApplication.getAppContext().getFilesDir();
            final File dest = new File(sd, filename);

            if (dest.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(dest.getAbsolutePath());
                mImageView.setImageBitmap(myBitmap);
            }
        }

        mUid = RegisterControl.getRegisterData(this).getId();

        mCloseBtn.setOnClickListener(this);
        mPostBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mCloseBtn) finish();
        else if (v == mPostBtn) {
            Log.d(TAG, "onClick: Start Writing");
            ReviewLocalDatabase db = new ReviewLocalDatabase(this);
            String text = mMessageEdit.getText().toString().trim();
            float rate = mRatingBar.getRating();
            setProgressBarStatus(true);
            db.addReview(mUid, mItemId, text, rate, new ReviewLocalDatabase.ActionListener() {
                @Override
                public void onResponse(boolean isSuccess) {
                    setProgressBarStatus(false);
                    if (isSuccess) {
                        Toast.makeText(WriteReview.this, "Thank's for your review", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onError(String error) {
                    setProgressBarStatus(false);
                    Toast.makeText(WriteReview.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setProgressBarStatus(boolean status) {
        mCloseBtn.setEnabled(!status);
        mPostBtn.setEnabled(!status);
        mMessageEdit.setEnabled(!status);
        mRatingBar.setEnabled(!status);
        mProgressBar.setVisibility(status ? View.GONE : View.VISIBLE);
    }
}
