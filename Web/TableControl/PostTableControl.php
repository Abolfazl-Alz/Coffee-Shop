<?php


use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;

class PostTableControl
{
    private $tableControl;
    private $linkTableControl;

    /**
     * PostTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_post_table();
        $this->linkTableControl = DatabaseHelper::create_post_link_table();
    }


    /**
     * @param int $pagination
     * @param int $count
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select($pagination = 1, $count = 12)
    {
        if ($pagination < 1) {
            throw new BadMethodCallException("pagination must have higher than 0 value");
        } elseif ($count < 1) {
            throw new BadMethodCallException("count must have higher than 0 value");
        }
        $pagination--;
        $i = $pagination * $count;
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_post_table());
        $selectQuery->set_limit_max($i, $count);
        return $this->select_query($selectQuery);
    }

    /**
     * @param SelectQueryCreator $selectQuery
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_query($selectQuery)
    {
        $selectQuery->set_order_by('createdTime');
        $select = $this->tableControl->select_query($selectQuery);
        if ($this->linkTableControl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->linkTableControl->get_connection()->get_connect_error());
        }

        if ($this->tableControl->get_connection()->get_last_error() != '')
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        if ($this->tableControl->get_connection()->get_connect_error() != '')
            throw new ConnectionException($this->tableControl->get_connection()->get_connect_error());

        $registerDb = new RegisterTableControl();

        for ($i = 0; $i < count($select); $i++) {
            if ($select[$i]['imageUrl'] != null && $select[$i]['imageUrl'] != '')
                $select[$i]['imageUrl'] = pageAddress::get_host() . 'img/' . $select[$i]['imageUrl'];
            else
                unset($select[$i]['imageUrl']);
            $select[$i]['writer'] = $registerDb->getRegister($select[$i]['writerId']);
            if (count($select[$i]['writer']) > 0) {
                $select[$i]['writer'] = $select[$i]['writer'][0];
            } else {
                unset($select[$i]['writer']);
            }
            unset($select[$i]['writerId']);

            $select[$i]['discounts'] = array();

            $links = $this->linkTableControl->select_with_condition(['postId' => $select[$i]['id']]);
            foreach ($links as $link) {
                if ($link['type'] == 0) {
                    $discountTable = new DiscountTableControl();
                    $linkValue = $discountTable->select_by_id($link['address'], $select[$i]['id']);
                    if (count($linkValue) > 0) {
                        array_push($select[$i]['discounts'], $linkValue[0]);
                    }
                }
            }
        }


        return $select;
    }

    /**
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_all()
    {
        return $this->select_query(new SelectQueryCreator(DatabaseHelper::get_post_table()));
    }

    /**
     * @param int $id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_by_id($id)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_post_table());
        $selectQuery->set_condition(['id' => $id]);
        return $this->select_query($selectQuery);
    }
}