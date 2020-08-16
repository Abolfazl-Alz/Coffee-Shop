package com.futech.coffeeshop.obj.address;

@SuppressWarnings("class")
public class AddressApi {
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
        private AddressData[] items;

        public String getMsg() {
            return msg;
        }

        public String getStatus() {
            return status;
        }

        public AddressData[] getItems() {
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
