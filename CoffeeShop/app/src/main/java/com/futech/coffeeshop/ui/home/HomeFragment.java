package com.futech.coffeeshop.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.home.HomeCollectionAdapter;
import com.futech.coffeeshop.adapter.home.HomeItemAdapter;
import com.futech.coffeeshop.dialog.ProgressDialog;
import com.futech.coffeeshop.obj.card.CardData;
import com.futech.coffeeshop.obj.feed.FeedData;
import com.futech.coffeeshop.obj.home.CollectionData;
import com.futech.coffeeshop.obj.home.CollectionItemData;
import com.futech.coffeeshop.obj.home.HomeData;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.obj.posts.PostData;
import com.futech.coffeeshop.ui.item_view.ItemViewFragment;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.ui.post.PostViewActivity;
import com.futech.coffeeshop.utils.Internet;
import com.futech.coffeeshop.utils.listener.SelectListener;
import com.futech.coffeeshop.utils.local_database.CardLocalDatabase;
import com.futech.coffeeshop.utils.local_database.FeedLocalDatabase;
import com.futech.coffeeshop.utils.local_database.ItemLocalDatabase;
import com.futech.coffeeshop.utils.local_database.PostsLocalDatabase;
import com.futech.coffeeshop.views.CardHomeItem;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements HomeItemAdapter.Listener {

    private SwipeRefreshLayout refreshLayout;
    private SlidingUpPanelLayout sliding;
    private ScrollView scrollView;
    private LinearLayout layout, homeFeeds;

    public static final String TAG = "HomeFragment";

    private ProgressDialog progressDialog;

    private HomeData mHomeData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = view.findViewById(R.id.refresh_layout);
        sliding = view.findViewById(R.id.sliding_layout);
        scrollView = view.findViewById(R.id.scrollView);
        layout = view.findViewById(R.id.loading_views);
        homeFeeds = view.findViewById(R.id.home_feeds);
        mHomeData = new HomeData();

        refreshLayout.setOnRefreshListener(this::loadHome);
        refreshLayout.setRefreshing(true);


        if (getActivity() != null && getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setHomeFragment(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (sliding.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    sliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    return;
                }
                requireActivity().finish();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        progressDialog = new ProgressDialog(view.getContext());

        loadHome();
    }

    public boolean isItemOpen() {
        return sliding.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null && getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setHomeFragment(null);
    }

    private void changeAnimationStatus(boolean start) {
        final ArrayList<View> items = getViewsByTag(layout, "home_load_item");
        for (View item : items) {
            AnimationDrawable animDrawable = (AnimationDrawable) item.getBackground();
            animDrawable.setEnterFadeDuration(10);
            animDrawable.setExitFadeDuration(50);
            if (start) animDrawable.start();
            else animDrawable.stop();
        }
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    private void loadHome() {
        refreshStatusChange(true);
        FeedLocalDatabase dbFeed = new FeedLocalDatabase(requireContext());
        dbFeed.select(new SelectListener<Map<Integer, List<FeedData>>>() {
            @Override
            public void onSelect(Map<Integer, List<FeedData>> list, boolean isOnline) {

                removeItems(RecyclerView.class);

                homeFeeds.removeAllViews();
                for (Map.Entry<Integer, List<FeedData>> data : list.entrySet()) {
                    mHomeData.addFeed(data.getValue());
                    RecyclerView col = new RecyclerView(new ContextThemeWrapper(requireContext(), R.style.Feeds), null, 0);
                    homeFeeds.addView(col);
                    HomeCollectionAdapter adapter = HomeCollectionAdapter.newInstance(col, new ArrayList<>(), HomeFragment.this);
                    for (FeedData feed : data.getValue()) {
                        ItemLocalDatabase db = new ItemLocalDatabase(getContext());
                        if (isOnline) {
                            for (ItemData item : feed.getItems()) {
                                db.insert(item);
                            }
                        }
                        CollectionItemData[] collectionItem = CollectionItemData.getItemsData(feed.getItems());
                        CollectionData collectionData = new CollectionData();
                        collectionData.setItems(collectionItem);
                        collectionData.setTitle(feed.getTitle());
                        collectionData.setDescription(feed.getDescription());
                        collectionData.setColor(feed.getColor());
                        adapter.addItem(collectionData);
                    }
                }
                final boolean isConnected = Internet.isConnected(requireContext());
                if (isConnected && isOnline || !isConnected && !isOnline)
                    refreshStatusChange(false);
                requireActivity().runOnUiThread(() -> loadCards());
            }

            @Override
            public void onError(String msg) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
                refreshStatusChange(false);
                Log.d(TAG, "onError: " + msg);
            }
        });
    }

    private void loadCards() {
        CardLocalDatabase dbCard = new CardLocalDatabase(getContext());
        dbCard.select(new SelectListener<CardData[]>() {
            @Override
            public void onSelect(CardData[] cards, boolean isOnline) {
                removeItems(CardHomeItem.class);
                int i = 0;
                for (CardData card : cards) {
                    mHomeData.addCard(card);
                    if (getContext() == null) continue;
                    CardHomeItem item = new CardHomeItem(getContext());
                    item.setOnClickListener(v -> {
                        if (CardData.Link.POST_KEY.equals(card.getLink().getAction())) {
                            progressDialog.showDialog();
                            progressDialog.setTitle(R.string.please_wait);
                            progressDialog.setInformation("");
                            PostsLocalDatabase db = new PostsLocalDatabase(getContext());
                            try {
                                db.selectById(Integer.parseInt(card.getLink().getValue()), new SelectListener<PostData>() {
                                    @Override
                                    public void onSelect(PostData data, boolean isOnline) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(getContext(), PostViewActivity.class);
                                        intent.putExtra(PostData.TABLE_POST_KEY, data);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(String msg) {
                                        progressDialog.dismiss();
                                        Log.i(TAG, "onError: " + msg);
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (NumberFormatException ex) {
                                progressDialog.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setTitle(R.string.incorrect_information_dialog_error);
                                alert.setMessage(R.string.card_id_error_msg);
                            }
                        }
                    });
                    item.setCardData(card);
                    if (i == -1) homeFeeds.addView(item);
                    else homeFeeds.addView(item, i);
                    if (i + 2 < homeFeeds.getChildCount() && i != -1) {
                        i += 2;
                    }else {
                        i = -1;
                    }
                }
            }

            @Override
            public void onError(String msg) {
                Log.d(TAG, "onError: " + msg);
            }
        });
    }

    private void removeItems(Type viewType) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < homeFeeds.getChildCount(); i++) {
            if (homeFeeds.getChildAt(i).getClass() == viewType) views.add(homeFeeds.getChildAt(i));
        }
        for (View view : views) {
            homeFeeds.removeView(view);
        }
    }

    private void refreshStatusChange(boolean status) {
        refreshLayout.setRefreshing(status);
        changeAnimationStatus(!status);
        layout.setVisibility(status ? View.VISIBLE : View.GONE);
        scrollView.setVisibility(status ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(CollectionItemData data) {
        if (data.getTag() instanceof ItemData) {
            ItemData itemData = (ItemData) data.getTag();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.item_view, ItemViewFragment.newInstance(itemData));
            transaction.commit();
            sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }
}
