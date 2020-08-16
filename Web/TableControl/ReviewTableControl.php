<?php


use MySqlConnection\SelectQueryCreator;

class ReviewTableControl
{
    private $tableControl;
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_review_table();
    }

    /**
     * @param int $count
     * @param int $itemId
     * @return array
     */
    public function read_limit($count, $itemId) {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_review_table());
        $selectQuery->set_limit($count);
        return $this->select_query($selectQuery, $itemId);
    }

    /**
     * @param SelectQueryCreator $selectQuery
     * @param int $itemId Item Id
     * @return array
     */
    public function select_query($selectQuery, $itemId) {
        $selectQuery->set_order_by('time');
        $selectQuery->set_condition(['iid' => $itemId]);
        return $this->tableControl->select_query($selectQuery);
    }

}