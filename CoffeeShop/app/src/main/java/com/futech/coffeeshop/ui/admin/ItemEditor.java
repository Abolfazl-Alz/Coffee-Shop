package com.futech.coffeeshop.ui.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.CategorySelectorAdapter;
import com.futech.coffeeshop.adapter.SizesListAdapter;
import com.futech.coffeeshop.dialog.ProgressDialog;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.ActivityUtils;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.ImageUtils;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;
import com.futech.coffeeshop.utils.local_database.ItemLocalDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ItemEditor extends AppCompatActivity implements ItemLocalDatabase.ResultListener {

    private ImageView image, categoryImage;
    private EditText itemName, itemType, itemDescription, itemPrice;
    private TextView categoryTitle;
    private SlidingUpPanelLayout sliding;
    private RecyclerView categorySelector;

    private SizesListAdapter sizesAdapter;
    private ProgressDialog progressDlg;
    private Uri imageUri;

    private int categoryId = 0;

    static final int PICK_IMAGE_REQUEST = 1;

    private boolean isImageChanged = false;
    private int editItemId = -1;

    private static final String TAG = "ItemEditorLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);

        itemName = findViewById(R.id.item_name);
        itemType = findViewById(R.id.item_type);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        categoryTitle = findViewById(R.id.category_title);
        categoryImage = findViewById(R.id.category_icon);
        image = findViewById(R.id.item_image);
        categorySelector = findViewById(R.id.category_selector);
        RecyclerView sizesList = findViewById(R.id.size_list);
        final EditText sizesText = findViewById(R.id.sizes_text);
        Button browseImage = findViewById(R.id.choose_image);
        Button selectCategory = findViewById(R.id.select_category);
        Button addSize = findViewById(R.id.add_size_btn);
        sliding = findViewById(R.id.sliding_layout);
        progressDlg = new ProgressDialog(this);
        progressDlg.setTitle(R.string.please_wait);


        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int color = ContextCompat.getColor(this, R.color.home_background);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            getSupportActionBar().setTitle(R.string.item_editor_title);
        }

        browseImage.setOnClickListener(v -> chooseImage());

        selectCategory.setOnClickListener(v -> openCategorySelector());

        sizesText.setOnEditorActionListener((v, actionId, event) -> {
            final String size = sizesText.getText().toString();
            if (!size.equals("")) {
                sizesAdapter.addSize(size);
                sizesText.setText("");
                return true;
            }
            return false;
        });


        sizesAdapter = SizesListAdapter.newInstance(sizesList);
        sizesAdapter.setOnClickMode(SizesListAdapter.DELETE_MODE);
        addSize.setOnClickListener(v -> {
            if (sizesText.getText().toString().equals("")) return;
            sizesAdapter.addSize(sizesText.getText().toString());
            sizesText.setText("");
        });

        if (getIntent() == null || getIntent().getExtras() == null || !(getIntent().getExtras().getSerializable(ItemData.TABLE_NAME_KEY) instanceof ItemData))
            return;

        ItemData data = (ItemData) getIntent().getExtras().getSerializable(ItemData.TABLE_NAME_KEY);
        if (data == null) return;
        editItemId = data.getId();
        itemName.setText(data.getTitle());
        HttpHelper.loadImage(this, data.getImage(), image, null, 300, 300, data.getTitle());
        itemType.setText(data.getType());
        itemPrice.setText(data.getPrice());
        itemDescription.setText(data.getInformation());
        categoryId = data.getCategory();
        sizesAdapter.addAllSize(data.getSizeList());
        CollectionLocalDatabase db = new CollectionLocalDatabase(this);
        db.selectCollectionById(categoryId, new CollectionLocalDatabase.SelectListener() {
            @Override
            public void onSelect(CategoryData[] items, boolean isOnline) {
                if (items.length == 0) {
                    categoryId = -1;
                    categoryImage.setImageResource(R.drawable.logo);
                    categoryTitle.setText("");
                }else {
                    CategoryData collection = items[0];
                    categoryTitle.setText(collection.getName());
                    categoryId = collection.getId();
                    HttpHelper.loadImage(ItemEditor.this, collection.getImage(), categoryImage, null, 150, 150, collection.getName());
                }
            }

            @Override
            public void onError(String error) {
                Log.i(TAG, "onError: " + error);
                Toast.makeText(ItemEditor.this, R.string.category_item_editor_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCategorySelector() {
        sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        CategorySelectorAdapter.newInstance(categorySelector, categoryId, collection -> {
            onSelectCollection(collection);
            sliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        });
    }

    private void onSelectCollection(CategoryData collection) {
        HttpHelper.loadImage(this, collection.getImage(), categoryImage, null, 250, 250, collection.getName());
        categoryTitle.setText(collection.getName());
        categoryId = collection.getId();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return goBack();
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean goBack() {
        if (!isChanged()) {
            finish();
            return true;
        }
        ActivityUtils.closeFormEditor(this, this::save);
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                assert imageUri != null;
                image.setImageURI(imageUri);
                isImageChanged = true;
            }
        }

    }

    private void chooseImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem saveMenu = menu.add(R.string.save_item).setOnMenuItemClickListener(item -> {
            ItemEditor.this.save();
            return false;
        });
        saveMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        savingProgress(true);

        if (editItemId != -1 && !isImageChanged) {
            insertItem("");
            return;
        }
        progressDlg.setInformation(R.string.loading_image_message);

        if (imageUri == null) {
            Toast.makeText(ItemEditor.this, R.string.select_image_error, Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                if (imageUri == null) {
                    runOnUiThread(() -> Toast.makeText(ItemEditor.this, R.string.select_image_error, Toast.LENGTH_SHORT).show());
                    return;
                }
                InputStream imageStream;
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                ImageUtils.EncodeImage encodeImage = new ImageUtils.EncodeImage(bitmap);
                runOnUiThread(() -> progressDlg.setInformation(R.string.loading_image_information));
                encodeImage.setOnEncodeListener(new ImageUtils.EncodeImage.EncodeListener() {
                    @Override
                    public void onEncode(String encodeString) {
                        insertItem(encodeString);
                    }

                    @Override
                    public void onError(final int error) {
                        runOnUiThread(() -> {
                            savingProgress(false);
                            Toast.makeText(ItemEditor.this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                encodeImage.execute();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ItemEditor.this, R.string.bad_image_path, Toast.LENGTH_SHORT).show();
                savingProgress(false);
            }
        }).start();
    }

    private void savingProgress(boolean progress) {
        if (progress) {
            progressDlg.dismiss();
        }else {
            progressDlg.showDialog();
        }
    }

    private void insertItem(String encodeStringImage) {
        int msg = 0;
        progressDlg.setInformation(R.string.uploading_information);

        String title = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String type = itemType.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String[] sizes = sizesAdapter.getSizesArray();

        if (encodeStringImage.equals("") && editItemId == -1 && !isImageChanged) {
            msg = R.string.select_image_error;
        }else if (title.equals("")) {
            msg = R.string.name_item_error;
            itemName.requestFocus();
        }else if (description.equals("")) {
            msg = R.string.description_item_error;
            itemDescription.requestFocus();
        }else if (type.equals("")) {
            msg = R.string.type_item_error;
            itemType.requestFocus();
        }else if (price.equals("")) {
            msg = R.string.type_item_error;
            itemType.requestFocus();
        }else if (categoryId < 1) {
            msg = R.string.category_item_error;
            openCategorySelector();
        }else if (sizes.length == 0) {
            msg = R.string.size_item_error;
        }

        if (msg > 0) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            savingProgress(false);
            return;
        }
        ItemLocalDatabase db = new ItemLocalDatabase(this);
        StringBuilder sizeText = new StringBuilder();
        for (String size : sizes) {
            if (!sizeText.toString().equals("")) sizeText.append("-");
            sizeText.append(size);
        }
        if (editItemId != -1) {
            Map<String, String> params = new HashMap<>();
            if (encodeStringImage.equals("")) {
                params.put(ItemData.IMAGE_KEY, encodeStringImage);
            }
            params.put(ItemData.TYPE_KEY, type);
            params.put(ItemData.TITLE_KEY, title);
            params.put(ItemData.INFORMATION_KEY, description);
            params.put(ItemData.CATEGORY_KEY, String.valueOf(categoryId));
            params.put(ItemData.SIZES_KEY, sizeText.toString());
            params.put(ItemData.PRICE_KEY, price);
            db.update(editItemId, params, this);
            return;
        }
        db.insert(title, description, type, categoryId, sizeText.toString(), Float.parseFloat(price), encodeStringImage, this);
    }

    private boolean isChanged() {
        String title = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String type = itemType.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String[] sizes = sizesAdapter.getSizesArray();
        return imageUri != null || !title.equals("") || !description.equals("") || !type.equals("") || !price.equals("") || sizes.length != 0;
    }

    @Override
    public void onResult() {
        Toast.makeText(ItemEditor.this, R.string.item_saved, Toast.LENGTH_SHORT).show();
        finish();
        savingProgress(false);
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(ItemEditor.this, msg, Toast.LENGTH_SHORT).show();
        savingProgress(false);
    }

}
