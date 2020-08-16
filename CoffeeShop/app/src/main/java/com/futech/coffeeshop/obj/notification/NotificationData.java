package com.futech.coffeeshop.obj.notification;

public class NotificationData {

    private static final int ORDER_STATUS_CHANGE = 0;

    public int id;
    public String title;
    public String text;
    public int type;
    public int fromId;
    public int toId;

    public NotificationData(String title, String text, int type, int fromId, int toId) {
        this.title = title;
        this.text = text;
        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
    }

    public static class NotificationStatus {

        private NotificationStatus(){

        }

        public static final int ORDER_DEFAULT_STATUS = 0; //App: Default Status
        public static final int ORDER_ACCEPT_STATUS = 1; //Admin when admin accepting user
        public static final int ORDER_READY_STATUS = 2; //Admin: when order was ready to sent
        public static final int ORDER_SENT_STATUS = 3; //Admin: when order was sent
        public static final int ORDER_ARRIVED_STATUS = 4; // Admin: When order arrived to user address
        public static final int ORDER_DELIVER_STATUS = 5; // Customer: When the customer receives the order
        public static final int ORDER_CANCEL_STATUS = 6; // Customer and Admin
//        public static final int ORDER_DELETE_STATUS = 3;
    }
}
