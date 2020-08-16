<?php

use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;

class CardTableControl {

    private $tableCtrl;
    public function __construct()
    {
        $this->tableCtrl = DatabaseHelper::create_card_table();
    }

    /**
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select() {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_card_table());
        $selectQuery->set_order_by('position', SelectQueryCreator::ORDER_BY_OPTION_ASC);
        $select = $this->tableCtrl->select_query($selectQuery);
        if ($this->tableCtrl->get_connection()->get_last_error() != '')
            throw new DatabaseException($this->tableCtrl->get_connection()->get_last_error());
        else if ($this->tableCtrl->get_connection()->get_connect_error() != '')
            throw new ConnectionException($this->tableCtrl->get_connection()->get_connect_error());

        for ($i = 0; $i < count($select); $i++) {
            $select[$i]['image'] = pageAddress::get_host() . "img/card/" . $select[$i]['image'];
        }
        return $select;
    }

}
