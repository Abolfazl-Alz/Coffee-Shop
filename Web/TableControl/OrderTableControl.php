<?php

use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;
use MySqlConnection\TableControl;


class OrderTableControl
{

    /**
     * @var TableControl
     */
    private $tableControl;

    /**
     * OrderTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_order_table();
    }

    /**
     * @param int $uid user integer id
     * @param int $addressId user integer id
     * @param string $message user integer message (can be null)
     * @throws DatabaseException
     * @throws Exception
     */
    public function add_order($uid, $addressId, $message = '')
    {
        if($message == null) $message = '';
        $id = $this->tableControl->insert_query(['uid' => $uid, 'message' => $message, 'addressId' => $addressId]);
        if($this->tableControl->get_connection()->get_last_error()) {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error(), 0);
        }
        if($id <= 0) {
            throw new Exception('something wrong in adding item');
        }
        $dbCart = DatabaseHelper::create_cart_table();
        $dbCart->update_query(['orderId' => $id, 'userId' => -1], ['userId' => $uid]);
    }

    /**
     * Select incomplete orders
     * @throws DatabaseException
     * @throws ConnectionException
     */
    public function select_incomplete()
    {
        return $this->select_order('status < ' . DELIVERED_STATUS);
    }

    /**
     * @param SelectQueryCreator $selectQuery
     * @return mixed
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_select_creator($selectQuery)
    {
        $selectQuery->set_order_by('time', SelectQueryCreator::ORDER_BY_OPTION_DESC);
        $select = $selectQuery->run_query($this->tableControl->get_connection());

        if($this->tableControl->get_connection()->get_last_error() != '') throw new DatabaseException($this->tableControl->get_connection()->get_last_error());

        $addressDb = DatabaseHelper::create_address_table();
        if(count($select) > 0) {
            for ($i = 0; $i < count($select); $i++) {

                $select_address = $addressDb->select_with_condition(['id' => $select[$i]['addressId']]);

                if(count($select_address) > 0)
                    $select[$i]['address'] = $select_address[0];

                $cartTable = new CartTableControl();
                $select[$i]['carts'] = $cartTable->select_cart(['orderId' => $select[$i]['id']]);
                if(count($select[$i]['carts']) == 0)
                    unset($select[$i]['carts']);

                $select[$i]['statusText'] = language::get_language()[$select[$i]['status']];

                $regTable = new RegisterTableControl();
                $reg = $regTable->getRegister($select[$i]['uid']);

                if(count($reg) > 0)
                    $select[$i]['registerData'] = $reg[0];
            }
        }

        return $select;
    }

    /**
     * select orders by condition
     * @param string|array $condition
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_order($condition)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_order_table());
        $selectQuery->set_condition($condition);

        return $this->select_select_creator($selectQuery);
    }

    /**
     * get number of unread orders
     * @param int $userId
     * @return int
     */
    public function unread_count($userId)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_order_table());
//        $selectQuery->set_condition('status = 0');

        $select = $this->tableControl->select_query($selectQuery);
        $count = count($select);

        $db_view = DatabaseHelper::create_order_view_table();
        for ($i = 0; $i < count($select); $i++) {
            if($db_view->select_count(['uid_view' => $userId, 'orderId' => $select[$i]['id']]) > 0) {
                $count--;
            }
        }

        return $count;

    }

    /**
     * change order status
     * @param $id
     * @param $status
     * @throws DatabaseException
     * @throws QueryException
     * @throws ConnectionException
     */
    public function change_status($id, $status)
    {
        $this->tableControl->update_query(['status' => $status], ['id' => $id]);
        (new CartTableControl())->update_cart_status_by_order($id, $status > 2 ? 1 : 0);
    }
}
