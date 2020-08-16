<?php
include_once 'language.php';

class EnglishLanguage
{

    private static $lang = [
        ORDER_MSG_DEFAULT => 'nothing change',
        ORDER_MSG_ACCEPT => 'Your order is being prepared',
        ORDER_MSG_SENT => 'Your order has been sent',
        ORDER_MSG_ARRIVED => 'Your order has arrived',
        ORDER_DELETE_ADMIN => 'Your order has deleted by admin',
        ORDER_MSG_READY => 'Your order is ready',
        ORDER_MSG_DELIVERED => 'Your order delivered',
        ORDER_NOTIFICATION_STATUS => 'Your order status has changed'];

    public static function get_language()
    {
        return self::$lang;
    }
}
