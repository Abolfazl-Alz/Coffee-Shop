package com.futech.coffeeshop.obj.review;

public class ReviewApi {
    private Data data;
    private About about;

    public Data getData() {
        return data;
    }

    public About getAbout() {
        return about;
    }

    public class Data {
        private String msg;
        private String status;
        private ReviewData[] items;

        public String getMsg() {
            return msg;
        }

        public String getStatus() {
            return status;
        }

        public ReviewData[] getItems() {
            return items;
        }
    }

    public static class About {
        private String time;
        public String getTime() {
            return time;
        }
    }
}
