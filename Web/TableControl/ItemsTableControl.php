<?php


use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;


class ItemsTableControl
{

    private $tableControl;

    /**
     * ItemsTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_items_table();
    }

    /**
     * @param $collection
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_by_collection($collection)
    {
        return $this->select_condition(['category' => $collection]);
    }

    /**
     * @param string|array $condition
     * @return array
     * @throws DatabaseException
     * @throws ConnectionException
     */
    public function select_condition($condition)
    {
        $select = new SelectQueryCreator(DatabaseHelper::get_items_table());
        $select->set_condition($condition);
        return $this->select_query($select);
    }

    /**
     * Select Items by SelectQueryCreator class
     * @param string $selectQuery
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     * @see SelectQueryCreator
     * @api Test Information
     * @since test
     * @author Abolfazl Alizadeh
     */
    public function select_query($selectQuery)
    {
        $connection = DatabaseHelper::create_connection();
        if ($connection->get_connect_error() != '') {
            throw new ConnectionException($connection->get_connect_error());
        }
        $selectResult = $connection->run_select_query($selectQuery);
        if ($connection->get_last_error() != '') {
            throw new DatabaseException($connection->get_last_error());
        }

        $discountDb = DatabaseHelper::create_discount_table();
        $discountItemDb = DatabaseHelper::create_discount_items_table();

        for ($i = 0; $i < count($selectResult); $i++) {
            $selectResult[$i]['image'] = pageAddress::get_host() . $selectResult[$i]['image'];
            $selectResult[$i]['information'] = str_replace('\'', '"', $selectResult[$i]['information']);
            $selectResult[$i]['collection'] = (new CollectionTableControl())->select_by_id($selectResult[$i]['category']);
            if (count($selectResult[$i]['collection']) > 0) $selectResult[$i]['collection'] = $selectResult[$i]['collection'][0];
            $cartTable = new CartTableControl();
            $selectResult[$i]['salesNumber'] = $cartTable->getTableControl()->select_count(['itemId' => $selectResult[$i]['id']]);
            $result_discount = $discountItemDb->select_with_condition(['itemId' => $selectResult[$i]['id']]);
            if (count($result_discount) == 1) {
                $selectResult[$i]['discount'] = $discountDb->select_with_condition(['id' => $result_discount[0]['discountId']]);
                if (count($selectResult[$i]['discount']) == 1)
                    $selectResult[$i]['discount'] = $selectResult[$i]['discount'][0];
                else
                    unset($selectResult[$i]['discount']);
            }
            $review = new ReviewTableControl();
            $selectResult[$i]['reviews'] = $review->read_limit(3, $selectResult[$i]['id']);
        }

        return $selectResult;
    }
}