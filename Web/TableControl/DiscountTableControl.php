<?php


use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;

class DiscountTableControl
{

    private $tableCtrl;
    private $itemTableCtrl;
    private $userTableCtrl;

    /**
     * DiscountTableControl constructor.
     */
    public function __construct()
    {
        $this->tableCtrl = DatabaseHelper::create_discount_table();
        $this->itemTableCtrl = DatabaseHelper::create_discount_items_table();
        $this->userTableCtrl = DatabaseHelper::create_discount_user_table();
    }

    /**
     * @param SelectQueryCreator $selectQuery
     * @param int $uid user id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_query($selectQuery, $uid)
    {
        $select = $this->tableCtrl->select_query($selectQuery);
        for ($i = 0; $i < count($select); $i++) {
            $select_items = $this->itemTableCtrl->select_with_condition(['discountId' => $select[$i]['id']]);
            if ($this->itemTableCtrl->get_connection()->get_connect_error() != '') {
                throw new ConnectionException($this->itemTableCtrl->get_connection()->get_connect_error());
            } elseif ($this->itemTableCtrl->get_connection()->get_last_error() != '') {
                throw new DatabaseException($this->itemTableCtrl->get_connection()->get_last_error());
            }
            $itemTable = DatabaseHelper::create_items_table();
            if ($itemTable->get_connection()->get_connect_error() != '') {
                throw new ConnectionException($itemTable->get_connection()->get_connect_error());
            }
            $canUse = $this->userTableCtrl->select_count(['discountId' => $select[$i]['id'], 'userId' => $uid]) == 0;
            if ($this->userTableCtrl->get_connection()->get_connect_error() != '') {
                throw new ConnectionException($this->userTableCtrl->get_connection()->get_connect_error());
            } else if ($this->userTableCtrl->get_connection()->get_last_error() != '') {
                throw new DatabaseException($this->userTableCtrl->get_connection()->get_last_error());
            }
            $select[$i]['canUse'] = $canUse;
            $select[$i]['items'] = array();
            for ($j = 0; $j < count($select_items); $j++) {
                $item_array_result = $itemTable->select_with_condition(['id' => $select_items[$j]['itemId']]);
                if ($itemTable->get_connection()->get_last_error() != '') {
                    throw new DatabaseException($itemTable->get_connection()->get_last_error());
                }


                if (count($item_array_result) > 0)
                    array_push($select[$i]['items'], $item_array_result[0]);
            }
        }

        return $select;
    }

    /**
     * @param $id
     * @param int $uid User id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_by_id($id, $uid)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_discount_table());
        $selectQuery->set_condition(['id' => $id]);
        return $this->select_query($selectQuery, $uid);
    }

    /**
     * @param $code
     * @param int $uid User id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_by_code($code, $uid)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_discount_table());
        $selectQuery->set_condition(['code' => $code]);
        return $this->select_query($selectQuery, $uid);
    }

    /**
     *
     * @param int $itemId
     * @param int $uid User id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_by_item($itemId, $uid)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_discount_items_table());
        $selectQuery->set_condition(['itemId' => $itemId]);
        $select_query = $this->select_query($selectQuery, $uid);
        if (count($select_query) == 1 && $select_query[0] != null) {
            $select_by_id = $this->select_by_id($select_query[0]['id'], $uid);
            if (count($select_by_id) == 1)
                return $select_by_id[0];
            return array();
        } else
            return array();
    }

    /**
     * @param int $uid User id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_all($uid)
    {
        return $this->select_query(new SelectQueryCreator(DatabaseHelper::get_discount_table()), $uid);
    }

    /**
     * Insert new discount to database
     * @param string $title
     * @param string $code
     * @param int $value
     * @param DateTime $expiration
     * @param int[] $itemsId
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function insert($title, $code, $value, $expiration, $itemsId)
    {
        $data = array();
        $data['title'] = $title;
        $data['code'] = $code;
        $data['value'] = $value;
        $data['expiration'] = $expiration;
        $id = $this->tableCtrl->insert_query($data);

        if ($id > 0) {
            foreach ($itemsId as $itemId) {
                if ($itemId > 0) {
                    $data = array();
                    $data['itemId'] = $itemId;
                    $data['discountId'] = $id;
                    $this->itemTableCtrl->insert_query($data);
                    if ($this->itemTableCtrl->get_connection()->get_connect_error() != '' || $this->itemTableCtrl->get_connection()->get_last_error() != '')
                        $this->tableCtrl->delete_query(['id' => $id]);
                    if ($this->itemTableCtrl->get_connection()->get_connect_error() != '')
                        throw new DatabaseException($this->itemTableCtrl->get_connection()->get_connect_error());
                    if ($this->itemTableCtrl->get_connection()->get_last_error() != '')
                        throw new DatabaseException($this->itemTableCtrl->get_connection()->get_last_error());
                }
            }
        }

        if ($this->tableCtrl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableCtrl->get_connection()->get_last_error());
        } elseif ($this->tableCtrl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->tableCtrl->get_connection()->get_connect_error());
        }
    }

    /**
     * @param int $id
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function delete($id)
    {
        $delete = $this->tableCtrl->delete_query(['id' => $id]);
        if ($this->tableCtrl->get_connection()->get_connect_error() != '') throw new ConnectionException($this->tableCtrl->get_connection()->get_connect_error());
        else if ($this->tableCtrl->get_connection()->get_last_error() != '') throw new DatabaseException($this->tableCtrl->get_connection()->get_last_error());

        if ($delete > 0) {
            $this->itemTableCtrl->delete_query(['discountId' => $id]);
            $this->userTableCtrl->delete_query(['discountId' => $id]);
        } else {
            throw new DatabaseException("no any data exist by entered id");
        }
    }
}