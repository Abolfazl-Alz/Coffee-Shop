<?php

use MySqlConnection\Connection;
use MySqlConnection\Server;
use MySqlConnection\TableControl;

include 'MySqlConnection/MySqlConnection.php';


class DatabaseHelper
{
    private static function is_localhost()
    {
        return $_SERVER['HTTP_HOST'] == 'localhost' or $_SERVER['HTTP_HOST'] == '10.0.2.2';
    }

    public static function get_server()
    {
        return new Server(self::get_host(), self::get_username(), self::get_password());
    }

    public static function create_connection()
    {
        return new Connection(self::get_server(), self::get_db_name());
    }

    /**
     * @param $tblName
     * @return TableControl
     */
    public static function create_table_ctrl($tblName)
    {
        return new TableControl(self::create_connection(), $tblName);
    }

    public static function create_collection_table()
    {
        return self::create_table_ctrl(self::get_collection_table());
    }

    public static function create_items_table()
    {
        return self::create_table_ctrl(self::get_items_table());
    }

    public static function create_token_table()
    {
        return self::create_table_ctrl(self::get_token_table());
    }

    public static function create_review_table()
    {
        return self::create_table_ctrl(self::get_review_table());
    }

    public static function create_address_table()
    {
        return self::create_table_ctrl(self::get_address_table());
    }

    /**
     * @return TableControl
     */
    public static function create_cart_table()
    {
        return self::create_table_ctrl(self::get_cart_table());
    }

    public static function create_order_table()
    {
        return self::create_table_ctrl(self::get_order_table());
    }

    public static function create_order_view_table()
    {
        return self::create_table_ctrl(self::get_order_view_table());
    }

    public static function create_register_table()
    {
        return self::create_table_ctrl(self::get_register_table());
    }

    public static function create_notification_table()
    {
        return self::create_table_ctrl(self::get_notification_table());
    }

    public static function create_feed_table()
    {
        return self::create_table_ctrl(self::get_feed_table());
    }

    public static function create_card_table()
    {
        return self::create_table_ctrl(self::get_card_table());
    }

    public static function create_post_table()
    {
        return self::create_table_ctrl(self::get_post_table());
    }

    public static function create_post_link_table()
    {
        return self::create_table_ctrl(self::get_post_link_table());
    }

    public static function create_discount_table() {
        return self::create_table_ctrl(self::get_discount_table());
    }

    public static function create_discount_items_table() {
        return self::create_table_ctrl(self::get_discount_items_table());
    }

    public static function create_discount_user_table() {
        return self::create_table_ctrl(self::get_discount_user_table());
    }

    private static function get_host()
    {
        return 'localhost';
    }

    private static function get_username()
    {
        return self::is_localhost() ? 'root' : 'abolfa18_coffeeshop';
    }

    private static function get_password()
    {
        return self::is_localhost() ? '' : 'a-FXuz-@R4Et';
    }

    private static function get_db_name()
    {
        return self::is_localhost() ? 'coffee-shop' : 'abolfa18_coffee-shop';
    }

    public static function get_collection_table()
    {
        return 'category';
    }

    public static function get_items_table()
    {
        return 'items';
    }

    public static function get_cart_table()
    {
        return 'cart';
    }

    public static function get_token_table()
    {
        return 'token';
    }

    public static function get_admin_table()
    {
        return 'admin';
    }

    public static function get_review_table()
    {
        return 'review';
    }

    public static function get_address_table()
    {
        return 'address';
    }

    public static function get_order_table()
    {
        return 'order_table';
    }

    public static function get_order_view_table()
    {
        return 'order_view';
    }

    public static function get_register_table()
    {
        return 'register';
    }

    public static function get_notification_table()
    {
        return 'notification';
    }

    public static function get_feed_table()
    {
        return 'feed';
    }

    public static function get_card_table()
    {
        return 'card';
    }

    public static function get_post_table()
    {
        return 'posts';
    }

    public static function get_post_link_table()
    {
        return 'post_link';
    }

    public static function get_discount_table() {
        return 'discount';
    }

    public static function get_discount_items_table() {
        return 'discount_items';
    }

    private static function get_discount_user_table()
    {
        return 'discount_user';
    }
}