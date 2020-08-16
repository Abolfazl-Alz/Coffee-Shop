<?php


use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;
use MySqlConnection\TableControl;

class RegisterTableControl
{
    /**
     * @var TableControl
     */
    private $tableControl;

    /**
     * RegisterTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_register_table();
    }

    /**
     * @param $id
     * @return array
     * @throws DatabaseException
     * @throws ConnectionException
     */
    public function getRegister($id)
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_register_table());
        $selectQuery->select_columns(['id', 'firstname', 'lastname', 'phoneNumber', 'email', 'language', 'admin']);
        $selectQuery->set_condition(['id' => $id]);
        $result = $this->tableControl->select_query($selectQuery);
        if ($this->tableControl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_last_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        }

        return $result;
    }

    /**
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function getAdmins()
    {
        $selectQuery = new SelectQueryCreator(DatabaseHelper::get_register_table());
        $selectQuery->select_columns(['id', 'firstname', 'lastname', 'phoneNumber', 'email', 'language', 'admin']);
        $selectQuery->set_condition(['admin' => '1']);
        $result = $this->tableControl->select_query($selectQuery);
        if ($this->tableControl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_last_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        }

        return $result;
    }


}