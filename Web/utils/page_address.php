<?php


class pageAddress
{
    private static function is_localhost()
    {
        return $_SERVER['HTTP_HOST'] == 'localhost' or $_SERVER['HTTP_HOST'] == '10.0.2.2';
    }

    public static function get_host()
    {
        return self::is_localhost() ? 'http://localhost/coffee-shop/' : 'http://coffee-shop.abolfazlalz.ir/';
    }
}
