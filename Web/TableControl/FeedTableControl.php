<?php

use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\ConditionBuilder;
use MySqlConnection\SelectQueryCreator;

define('TOP_TYPE', 0);
define('TOP_CATEGORY', 1);
define('TOP_ITEMS', 2);
define('LAST_ORDER', 3);
define('LAST_CREATED', 4);
define('SELECTED_ITEMS', 5);

class FeedTableControl
{

    private $tableControl;

    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_feed_table();
    }

    public function get_select_query()
    {
        return new SelectQueryCreator(DatabaseHelper::get_feed_table());
    }

    /**
     * @param SelectQueryCreator $selectQuery
     * @param int $uid
     * @return array
     * @throws DatabaseException
     * @throws ConnectionException
     */
    public function select($selectQuery, $uid)
    {
        $result = array();
        $selects = $this->tableControl->select_query($selectQuery);
        if ($this->tableControl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_connect_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        }
        foreach ($selects as $select) {
            $position = $select['position'];
            if (!array_key_exists($position, $result))
                $result[$position] = array();

            switch ($select['action']) {
                case TOP_ITEMS:
                    $select['items'] = $this->select_best_seller(5);
                    break;
                case LAST_CREATED:
                    $select['items'] = $this->select_new_items(5);
                    break;
                case LAST_ORDER:
                    $select['items'] = $this->select_last_order($uid, 5);
                    break;

            }

            array_push($result[$position], $select);
        }

        return $result;
    }

    /**
     * @param int $count
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_new_items($count = -1)
    {
        $select = new SelectQueryCreator(DatabaseHelper::get_items_table());
        $select->set_order_by('dateCreate');
        if ($count > -1)
            $select->set_limit($count);
        $itemDb = new ItemsTableControl();
        return $itemDb->select_query($select);
    }

    /**
     * @param int $count
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_best_seller($count = -1)
    {
        $db = new CartTableControl();
        $select = new SelectQueryCreator(DatabaseHelper::get_cart_table());
        $select->set_condition(['status' => 1]);
        if ($count > -1)
            $select->set_limit($count);
        $select->set_group('itemId');
        $select->set_order_by('`allCount`');
        $condition = new ConditionBuilder();
        $cartSelectResult = $db->select_query($select, true);
        foreach ($cartSelectResult as $item) {
            $condition->addWithOperator('id', $item['itemId'], ConditionBuilder::$OR);
        }

        $itemDb = new ItemsTableControl();
        return $itemDb->select_condition($condition);
    }

    /**
     * Select user last order
     * @param int $uid
     * @param int $count
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_last_order($uid, $count = -1)
    {
        $orderDb = new OrderTableControl();
        $select = $orderDb->select_order(['uid' => $uid]);
        if (count($select) > 0) {
            $condition = new ConditionBuilder();
            if (array_key_exists('carts', $select[0])) {
                foreach ($select[0]['carts'] as $item) {
                    $condition->addWithOperator('id', $item['itemId'], ConditionBuilder::$OR);
                }
            }

            $selectQuery = new SelectQueryCreator(DatabaseHelper::get_items_table());
            if ($count > -1)
                $selectQuery->set_limit($count);
            $selectQuery->set_condition($condition);

            $itemDb = new ItemsTableControl();
            return $itemDb->select_query($selectQuery);
        }
        return array();
    }

}
