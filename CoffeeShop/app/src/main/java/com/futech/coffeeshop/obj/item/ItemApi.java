package com.futech.coffeeshop.obj.item;

public class ItemApi {
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
        private ItemData[] items;

        public String getMsg() {
            return msg;
        }

        public String getStatus() {
            return status;
        }

        public ItemData[] getItems() {
            return items;
        }

        public boolean isSuccess() {
            return !getStatus().equals("error");
        }
    }

    public static class About {
        private String time;
        public String getTime() {
            return time;
        }
    }
}
