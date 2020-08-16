package com.futech.coffeeshop.obj.card;

public class CardApi {


    private Data data;

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private String msg;
        private String status;
        private CardData[] items;

        public String getMessage() {
            return msg;
        }

        public CardData[] getItems() {
            return items;
        }

        public boolean isSuccess() {
            return status.equals("success");
        }
    }

}
