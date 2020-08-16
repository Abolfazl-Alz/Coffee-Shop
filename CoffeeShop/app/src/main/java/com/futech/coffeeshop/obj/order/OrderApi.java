package com.futech.coffeeshop.obj.order;

public class OrderApi {
    private Data data;
    private About about;

    public Data getData() {
        return data;
    }

    public About getAbout() {
        return about;
    }

    public static class Data {
        private String msg;
        private String status;
        private OrderData[] items;

        public String getMsg() {
            return msg;
        }

        public String getStatus() {
            return status;
        }

        public OrderData[] getItems() {
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
