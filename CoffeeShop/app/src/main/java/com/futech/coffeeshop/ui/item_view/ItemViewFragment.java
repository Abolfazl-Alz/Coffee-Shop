package com.futech.coffeeshop.ui.item_view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.SizesListAdapter;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.obj.review.ReviewData;
import com.futech.coffeeshop.ui.review.WriteReview;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;
import com.futech.coffeeshop.utils.local_database.ReviewLocalDatabase;
import com.futech.coffeeshop.utils.listener.DataChangeListener;
import com.futech.coffeeshop.utils.listener.SelectListener;
import com.futech.coffeeshop.views.CartListView;
import com.futech.coffeeshop.views.ReviewListView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ItemViewFragment extends Fragment {

    private static final String ITEM_DATA = "itemData";

    private ImageView itemImage;
    private TextView itemName, itemPrice, itemType;
    private Spinner itemCount;
    private Button addCart;
    private ItemData itemData;
    private CartListView itemsInCart;
    private ReviewListView reviewListView;
    private TextView informationText;
    private RecyclerView sizesList;

    private final int REVIEW_RESULT_CODE = 99;
    private SizesListAdapter sizes;

    private static String TAG = "ItemViewFragment";


    public static ItemViewFragment newInstance(ItemData itemData) {
        ItemViewFragment itemViewFragment = new ItemViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEM_DATA, itemData);
        itemViewFragment.setArguments(args);
        return itemViewFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Bundle args = getArguments();
        if (args == null) return;
        this.itemData = (ItemData) args.getSerializable(ITEM_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemType = view.findViewById(R.id.item_type);
        itemImage = view.findViewById(R.id.item_image);
        itemName = view.findViewById(R.id.item_name);
        itemPrice = view.findViewById(R.id.item_price);
        itemCount = view.findViewById(R.id.item_count);
        addCart = view.findViewById(R.id.add_cart);
        itemsInCart = view.findViewById(R.id.items_in_carts);
        reviewListView = view.findViewById(R.id.reviewsList);
        informationText = view.findViewById(R.id.information_text);
        sizesList = view.findViewById(R.id.sizes_list);
        Button seeAllReviews = view.findViewById(R.id.see_all_reviews);
        Button writeReview = view.findViewById(R.id.write_review_btn);

        setItemType(itemData.getType());
        setItemPrice(itemData.getPrice());
        setItemName(itemData.getTitle());
        setItemImage(itemData.getImage(), itemData.getTitle() + "-item");
        setSize(itemData.getSizes());
        setItemInformation(itemData.getInformation());
        reviewListView.setItemId(itemData.getId());

        updateCartView();

        Integer[] array = {1, 2, 3, 4, 5};
        itemCount.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_activated_1, array));

        seeAllReviews.setOnClickListener(v -> showAllReviews());

        writeReview.setOnClickListener(v -> {
            final Intent intent = new Intent(getContext(), WriteReview.class);
            intent.putExtra("id", itemData.getId());
            startActivityForResult(intent, REVIEW_RESULT_CODE);
        });

        addCart.setOnClickListener(v -> addToCart());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REVIEW_RESULT_CODE) {
            reviewListView.refresh();
        }
    }

    private void showAllReviews() {
        if (getContext() == null) return;
        final Context context = getContext();
        ReviewLocalDatabase reviews = new ReviewLocalDatabase(context);
        reviews.selectReview(1, itemData.getId(), new ReviewLocalDatabase.SelectListener() {
            @Override
            public void onSelect(List<ReviewData> reviewData, boolean isOnline) {
                if (isOnline) {
                    Dialog dlg = new Dialog(context);
                    ReviewListView listView = new ReviewListView(context, reviewData);

                    dlg.setContentView(listView);
                    dlg.setCancelable(true);
                    dlg.show();

                    Window window = dlg.getWindow();
                    assert window != null;
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.i(TAG, "onError: " + errorMessage);
            }
        });
    }

    private void setItemType(String type) {
        itemType.setText(type);
    }

    private void setItemImage(String imageUrl, String imageName) {
        HttpHelper.loadImage(getContext(), imageUrl, itemImage, null, 250, 250, imageName);
    }

    private void setItemName(String name) {
        itemName.setText(name);
    }

    private void setItemInformation(String information) {
        informationText.setText(information);
    }

    private void setItemPrice(String price) {
        itemPrice.setText(String.format(getString(R.string.item_price_string), Integer.valueOf(price)));
    }

    private void setSize(String sizeStr) {
        sizes = SizesListAdapter.newInstance(sizesList).addAllSize(sizeStr.split("-"));
    }

    private void addToCart() {

        if (sizes.getSelectedSize().equals("")) {
            Snackbar.make(addCart, R.string.undefine_size, BaseTransientBottomBar.LENGTH_SHORT).show();
            return;
        }

        CartLocalDatabase db = new CartLocalDatabase(getContext());

        int count = itemCount.getSelectedItemPosition() + 1;

        CartData cartData = new CartData(this.itemData, count);
        cartData.setSizes(sizes.getSelectedSize());
        db.insert(cartData, new DataChangeListener() {
            @Override
            public void onChange() {
                Snackbar.make(addCart, R.string.add_cart_result_success, BaseTransientBottomBar.LENGTH_SHORT).show();
                updateCartView();
            }

            @Override
            public void onError(String msg) {
                Snackbar.make(addCart, requireContext().getString(R.string.add_cart_result_failed), BaseTransientBottomBar.LENGTH_SHORT).show();
                Log.i(TAG, "addToCart: onError: " + msg);
            }
        });
    }

    private void updateCartView() {
        final CartLocalDatabase db = new CartLocalDatabase(getContext());
        final List<CartData> select = db.select(CartData.ID_KEY + "=?", new String[]{String.valueOf(itemData.getId())});
        itemsInCart.addItemsToCart(select);
        db.select(new SelectListener<CartData[]>() {
            @Override
            public void onSelect(CartData[] carts, boolean isOnline) {
                List<CartData> select = db.select(ItemData.ID_KEY + "=?", new String[]{String.valueOf(itemData.getId())});
                itemsInCart.removeItemsInCart();
                itemsInCart.addItemsToCart(select);
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

}
