package com.futech.coffeeshop.obj.posts;

public class PostsApi {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private String msg;
        private String status;
        private PostData[] items;

        public boolean isSuccess() {
            return !status.equals("error");
        }

        public String getMessage() {
            return msg;
        }

        public PostData[] getItems() {
            return items;
        }
    }

}
