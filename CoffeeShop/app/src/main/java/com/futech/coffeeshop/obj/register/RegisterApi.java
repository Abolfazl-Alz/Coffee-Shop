package com.futech.coffeeshop.obj.register;

public class RegisterApi {

    private Data data;
    private About about;

    public About getAbout() {
        return about;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setAbout(About about) {
        this.about = about;
    }

    public static class Data {
        private String msg;
        private String status;
        private RegisterData[] items;
        private int code;
        private RegisterData user;

        public String getMsg() {
            return msg;
        }

        public String getStatus() {
            return status;
        }

        public RegisterData[] getItems() {
            return items;
        }

        public int getCode() {
            return code;
        }

        public RegisterData getUser() {
            return user;
        }

        public void setUser(RegisterData user) {
            this.user = user;
        }
    }

    public static class About {
        private String time;

        public String getTime() {
            return time;
        }
    }
}
