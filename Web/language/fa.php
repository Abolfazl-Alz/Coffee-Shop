<?php
include_once 'language.php';

class PersianLanguage
{

    private static $lang = [
        ORDER_MSG_DEFAULT => 'چیزی تغییر نکرده است',
        ORDER_MSG_ACCEPT => 'سفارش شما در حال آماده سازی می باشد',
        ORDER_MSG_SENT => 'سفارش شما فرستاده شد',
        ORDER_MSG_ARRIVED => 'سفارش شما به مقصد رسید',
        ORDER_DELETE_ADMIN => 'سفارش شما توسط ادمین حذف شد',
        ORDER_MSG_READY => 'سفارش شما آماده است',
        ORDER_MSG_DELIVERED => 'سفارش مشتری تحویل داده شد',
        ORDER_NOTIFICATION_STATUS => 'وضعیت سفارش شما تغییر پیدا کرد'];

    public static function get_language()
    {
        return self::$lang;
    }
}
