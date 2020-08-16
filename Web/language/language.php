<?php
define('ORDER_MSG_DEFAULT', 0);
define('ORDER_MSG_ACCEPT', 1);
define('ORDER_MSG_READY', 2);
define('ORDER_MSG_SENT', 3);
define('ORDER_MSG_ARRIVED', 4);
define('ORDER_MSG_DELIVERED', 5);
define('ORDER_DELETE_ADMIN', 6);
define('ORDER_NOTIFICATION_STATUS', 7);

class language {
    public static function get_language()
    {
        return PersianLanguage::get_language();
    }
}