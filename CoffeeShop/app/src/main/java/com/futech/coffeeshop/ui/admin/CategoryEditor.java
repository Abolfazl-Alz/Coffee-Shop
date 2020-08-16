package com.futech.coffeeshop.ui.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.dialog.ProgressDialog;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.utils.ActivityUtils;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.ImageUtils;
import com.futech.coffeeshop.utils.listener.DataChangeListener;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.futech.coffeeshop.ui.admin.ItemEditor.PICK_IMAGE_REQUEST;

public class CategoryEditor extends AppCompatActivity implements View.OnClickListener, DataChangeListener {

    private EditText categoryName, categoryDescription;
    private ImageView imageView;
    private Button changeImage;
    private ProgressDialog progressDialog;

    private Uri imageUri;

    private boolean isImageChanged = false;
    private int editCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_editor);

        categoryName = findViewById(R.id.category_title);
        categoryDescription = findViewById(R.id.category_text);
        imageView = findViewById(R.id.category_icon);
        changeImage = findViewById(R.id.choose_image);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setInformation("");


        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int color = ContextCompat.getColor(this, R.color.home_background);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            getSupportActionBar().setTitle(R.string.category_editor_title);
        }

        changeImage.setOnClickListener(this);

        if (getIntent() == null || getIntent().getExtras() == null) return;
        CategoryData categoryData = (CategoryData) getIntent().getExtras().getSerializable(CategoryData.TABLE_NAME_KEY);
        if (categoryData == null) return;
        categoryName.setText(categoryData.getName());
        categoryDescription.setText(categoryData.getInformation());
        HttpHelper.loadImage(this, categoryData.getImage(), imageView, null, 250, 250, categoryData.getName());
        editCategoryId = categoryData.getId();
    }

    private String getCategoryTitle() {
        return categoryName.getText().toString();
    }

    private String getCategoryInformation() {
        return categoryDescription.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (v == changeImage) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        }
    }

    private boolean goBack() {
        if (!isChanged()) {
            finish();
            return true;
        }
        ActivityUtils.closeFormEditor(this, this::save);
        return false;
    }

    private boolean isChanged() {
        return !getCategoryTitle().equals("") || !getCategoryInformation().equals("") || imageUri != null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                assert imageUri != null;
                imageView.setImageURI(imageUri);
                isImageChanged = true;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem saveMenu = menu.add(R.string.save_item).setOnMenuItemClickListener(item -> {
            CategoryEditor.this.save();
            return false;
        });
        saveMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    private void save() {

        savingProgress(true);

        if (editCategoryId != -1 && !isImageChanged) {
            insertItem("");
            return;
        }
        progressDialog.setInformation(R.string.loading_image_message);

        new Thread(() -> {
            try {
                if (imageUri == null) {
                    runOnUiThread(() -> Toast.makeText(CategoryEditor.this, R.string.select_image_error, Toast.LENGTH_SHORT).show());
                    return;
                }
                InputStream imageStream;
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                ImageUtils.EncodeImage encodeImage = new ImageUtils.EncodeImage(bitmap);
                runOnUiThread(() -> progressDialog.setInformation(R.string.loading_image_information));
                encodeImage.setOnEncodeListener(new ImageUtils.EncodeImage.EncodeListener() {
                    @Override
                    public void onEncode(String encodeString) {
                        insertItem(encodeString);
                    }

                    @Override
                    public void onError(final int error) {
                        runOnUiThread(() -> {
                            savingProgress(false);
                            Toast.makeText(CategoryEditor.this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                encodeImage.execute();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CategoryEditor.this, R.string.bad_image_path, Toast.LENGTH_SHORT).show();
                savingProgress(false);
            }
        }).start();
    }

    private void savingProgress(boolean isProgress) {
        if (isProgress) progressDialog.showDialog();
        else progressDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return goBack();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void insertItem(String imageEncode) {
        progressDialog.setInformation(R.string.uploading_information);

        int msg = 0;
        if (imageEncode.equals("") && editCategoryId == -1 && !isImageChanged) {
            msg = R.string.select_image_error;
        }else if (getCategoryTitle().equals("")) {
            msg = R.string.category_title_error_fill;
        }else if (getCategoryInformation().equals("")) {
            msg = R.string.category_description_error_fill;
        }


        if (msg > 0) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            savingProgress(false);
            return;
        }

        CollectionLocalDatabase db = new CollectionLocalDatabase(this);
        if (editCategoryId == -1) {
            db.insert(getCategoryTitle(), getCategoryInformation(), imageEncode, this);
        }else {
            Map<String, String> updateValues = new HashMap<>();
            updateValues.put("name", getCategoryTitle());
            updateValues.put("information", getCategoryInformation());
            if (isImageChanged) updateValues.put("image_base64", imageEncode);
            db.update(updateValues, editCategoryId, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChange() {
        Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
        finish();
        savingProgress(false);
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        savingProgress(false);
    }
}
