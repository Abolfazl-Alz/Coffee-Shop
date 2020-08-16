<?php

use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;
use MySqlConnection\TableControl;

class CartTableControl
{

    /**
     * @var TableControl
     */
    private $tableControl;

    /**
     * CartTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_cart_table();
    }

    /**
     * add cart
     * @param $userId
     * @param $itemId
     * @param $size
     * @param $count
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     * @throws Exception
     */
    public function add_cart($userId, $itemId, $size, $count)
    {
        $data = array();
        $data['userId'] = $userId;
        $data['itemId'] = $itemId;
        $data['size'] = $size;
        $data['count'] = $count;

        $dbItems = DatabaseHelper::create_items_table();
        if ($dbItems->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($dbItems->get_connection()->get_connect_error());
        }
        $items = $dbItems->select_with_condition(['id' => $itemId]);
        if ($dbItems->get_connection()->get_last_error()) {
            throw new DatabaseException($dbItems->get_connection()->get_last_error());
        }
        if (count($items) == 0) {
            throw new Exception('item not exist in server, enter valid id for item');
        }


        $data['cartPrice'] = $items[0]['price'];

        $discount = (new DiscountTableControl())->select_by_item($itemId, $userId);
        if ($discount != null && count($discount) == 1) {
            $price = $items[0]['price'] * $discount[0]['value'] / 100;
            $data['cartPrice'] = $price;
        }


        $insert = $this->tableControl->insert_query($data);
        $con_error = $this->tableControl->get_connection()->get_last_error();
        if ($con_error != '') {
            throw new Exception($con_error);
        } else if ($insert <= 0) {
            throw new Exception("can't to add this item");
        }

        return $this->select_cart([DatabaseHelper::get_cart_table() . '.id' => $insert]);

    }

    /**
     * delete carts by condition
     * @param int $cartId
     * @throws ConnectionException
     * @throws DatabaseException
     * @throws Exception
     */
    public function delete_cart($cartId)
    {
        $delete = $this->tableControl->delete_query(['id' => $cartId]);

        if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_connect_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        } elseif (!$delete) {
            throw new Exception('deleting is not successfully');
        }
    }

    /**
     * delete all incomplete user carts from database
     * @param int $userId
     * @throws ConnectionException
     * @throws DatabaseException
     * @throws Exception
     */
    public function delete_all_user_cart($userId)
    {
        $delete = $this->tableControl->delete_query(['id' => $userId]);

        if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_connect_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        } elseif (!$delete) {
            throw new Exception('deleting is not successfully');
        }
    }

    /**
     * Update cart information by Id
     * @param int $cartId
     * @param int $itemId
     * @param int $uid
     * @param int $count
     * @param string $size
     * @return bool
     * @throws DatabaseException
     * @throws QueryException
     * @throws ConnectionException
     */
    public function update_cart_information($cartId, $itemId, $uid, $count, $size)
    {
        $data = array();
        $data['itemId'] = $itemId;
        $data['userId'] = $uid;
        $data['count'] = $count;
        $data['size'] = $size;
        return $this->update_cart($data, ['id' => $cartId]);
    }

    /**
     * Update cart status by id
     * @param int $status
     * @param $id
     * @throws DatabaseException
     */
    public function update_cart_status($status, $id)
    {
        $update = $this->tableControl->update_query(['status' => $status], ['id' => $id]);
        $con_error = $this->tableControl->get_connection()->get_last_error();
        if ($update <= 0 || $con_error != '') {
            throw new DatabaseException($con_error);
        }
    }

    /**
     * select all cart by condition
     * @param array|string $condition
     * @return array
     * @throws DatabaseException
     * @throws ConnectionException
     */
    public function select_cart($condition)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_cart_table());
        $selectQuery->set_condition($condition);

        return $this->select_query($selectQuery);
    }

    /**
     * select incomplete carts
     * @param $userId
     * @return array
     * @throws DatabaseException|ConnectionException
     */
    public function select_incomplete($userId)
    {
        return $this->select_cart(['userId' => $userId, 'orderId' => -1]);
    }

    /**
     * update carts status by order id
     * @param int $status
     * @param int $orderId
     * @throws DatabaseException
     * @throws QueryException
     * @throws ConnectionException
     */
    public function update_cart_status_by_order($status, $orderId)
    {
        $this->update_cart(['status' => $status], ['orderId' => $orderId]);
    }

    /**
     * update cart
     * @param $data
     * @param $condition
     * @return bool
     * @throws DatabaseException
     * @throws QueryException
     * @throws ConnectionException
     */
    public function update_cart($data, $condition)
    {
        $update = $this->tableControl->update_query($data, $condition);
        $con_error = $this->tableControl->get_connection()->get_last_error();
        if ($con_error != '') {
            throw new DatabaseException($con_error);
        } else if ($update <= 0) {
            throw new QueryException();
        }
        $this->select_cart('');
        return $update > 0;
    }

    /**
     * @param SelectQueryCreator $selectQuery
     * @param $useCount
     * @return array
     * @throws DatabaseException
     * @throws ConnectionException
     */
    public function select_query($selectQuery, $useCount = false)
    {
        $columns = array();
        array_push($columns, DatabaseHelper::get_cart_table() . ".`id` as cartId");
        array_push($columns, 'itemId');
        array_push($columns, 'userId');
        array_push($columns, 'count');
        array_push($columns, '`size` as sizes');
        array_push($columns, 'orderId');
        array_push($columns, 'status');
        array_push($columns, 'cartPrice');
        $itemTbl = new ItemsTableControl();

        if ($useCount) array_push($columns, 'COUNT(*) as `allCount`');
        $selectQuery->select_columns($columns);
        $result = $this->tableControl->get_connection()->run_select_query($selectQuery);

        for ($i = 0; $i < count($result); $i++) {
            $item = $itemTbl->select_condition(['id' => $result[$i]['itemId']]);
            if (count($item) == 1) {
                $result[$i] = array_merge($item[0], $result[$i]);
                if ($result[$i]['status'] != 0) continue;
                $discount = (new DiscountTableControl())->select_by_item($item[0]['id'], $result[$i]['userId']);
                if ($discount != null && count($discount) > 0) {
                    unset($discount[0]['items']);
                    $price = $item[0]['price'] * $discount['value'] / 100;
                    $result[$i]['cartPrice'] = $price;
                    $this->tableControl->update_query(['cartPrice' => $price], ['id' => $result[$i]['cartId']]);
                }


            }
        }

        if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        }

        return $result;
    }

    /**
     * @return TableControl
     */
    public function getTableControl()
    {
        return $this->tableControl;
    }
}
