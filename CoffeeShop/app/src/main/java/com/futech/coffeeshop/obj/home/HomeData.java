package com.futech.coffeeshop.obj.home;

import com.futech.coffeeshop.obj.card.CardData;
import com.futech.coffeeshop.obj.feed.FeedData;

import java.util.ArrayList;
import java.util.List;

public class HomeData {

    private List<FeedData[]> feeds;
    private List<CardData> cards;

    public HomeData() {
        feeds = new ArrayList<>();
        cards = new ArrayList<>();
    }

    public Object getItem(int index) {
        if (index % 2 == 0) {
            if (getItem(index - 1) instanceof CardData) return getCard((index + 1) / 2);
            else return cards.get(index / 2);
        }else {
            if (getItem(index - 1) instanceof FeedData[]) return getFeed((index + 1) / 2);
            else return getFeed(index / 2);
        }
    }

    public void addFeed(FeedData[] feed) {
        feeds.add(feed);
    }

    public void addFeed(List<FeedData> feed) {
        addFeed(feed.toArray(new FeedData[0]));
    }

    public void addCard(CardData card) {
        cards.add(card);
    }

    private CardData getCard(int index) {
        return cards.get(index);
    }

    private FeedData[] getFeed(int index) {
        return feeds.get(index);
    }

    public int size() {
        return feeds.size() + cards.size();
    }
}
