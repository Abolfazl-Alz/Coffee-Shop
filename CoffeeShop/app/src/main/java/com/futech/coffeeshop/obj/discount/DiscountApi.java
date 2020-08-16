package com.futech.coffeeshop.obj.discount;

@SuppressWarnings("ALL")
public class DiscountApi {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private String msg;
        private String status;
        private DiscountData[] items;

        public String getMessage() {
            return msg;
        }

        public boolean isSuccess() {
            return !status.equals("error");
        }

        public DiscountData[] getItems() {
            return items;
        }
    }

}
