package com.futech.coffeeshop.obj.cart;

public class CartApi {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private String msg;
        private String status;
        private CartApiData[] items;

        public String getMsg() {
            return msg;
        }

        public String getStatus() {
            return status;
        }

        public CartApiData[] getItems() {
            return items;
        }
    }

    public static class CartApiData extends CartData {
        private int itemId;
        private String userId;

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }
}
