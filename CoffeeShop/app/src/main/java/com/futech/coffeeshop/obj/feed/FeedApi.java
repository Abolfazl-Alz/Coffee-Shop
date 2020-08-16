package com.futech.coffeeshop.obj.feed;

import java.util.List;
import java.util.Map;

public class FeedApi {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        private String msg;
        private String status;
        private Map<Integer, List<FeedData>> items;

        public String getMessage() {
            return msg;
        }

        public boolean isSuccess() {
            return !status.equals("error");
        }

        public Map<Integer, List<FeedData>> getItems() {
            return items;
        }
    }

}
