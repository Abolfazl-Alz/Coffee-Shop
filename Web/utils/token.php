<?php

use MySqlConnection\ConditionBuilder;
use MySqlConnection\SelectQueryCreator;

class token
{
    const min_permission = 0;
    const max_permission = 1;

    /**
     * generate random token
     * @return string
     */
    private static function generate_token()
    {
        $str = rand();
        return md5($str);
    }

    public static function is_valid($key)
    {
        $db = DatabaseHelper::create_token_table();
        $condition = new ConditionBuilder();
        $condition->add('key', $key);
        return $db->select_count($condition) == 1;
    }

    public static function get_permission($key)
    {
        $db = DatabaseHelper::create_token_table();
        $select = new SelectQueryCreator(DatabaseHelper::get_token_table());
        $select->set_condition(['key' => $key]);
        $select->select_columns('permission');
        $result = $db->select_query($select);
        if(count($result) == 1 && array_key_exists('permission', $result[0])) {
            return $result[0]['permission'];
        } else {
            return self::min_permission - 1;
        }
    }
}
