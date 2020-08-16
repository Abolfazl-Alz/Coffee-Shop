<?php


use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;
use MySqlConnection\TableControl;

class NotificationTableControl
{

    private $tableControl;

    /**
     * NotificationTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_notification_table();
    }


    /**
     * @param string $title
     * @param string $text
     * @param int $type
     * @param int $fromId
     * @param int $toId
     * @throws DatabaseException
     * @throws QueryException
     */
    public function add_notification($title, $text, $type, $fromId, $toId)
    {
        $arr = ['title' => $title, 'text' => $text, 'type' => $type, 'fromId' => $fromId, 'toId' => $toId];
        $insert = $this->tableControl->insert_query($arr);
        if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        } elseif ($insert <= 0) {
            throw new QueryException();
        }
    }

    /**
     * @param int $status
     * @param int $uid
     * @param int $toId
     * @throws DatabaseException
     * @throws QueryException
     * @throws ConnectionException
     */
    public function change_order_notification($status, $uid, $toId)
    {
        if ($status == ORDER_MSG_DELIVERED) {
            $regDb = new RegisterTableControl();
            foreach ($regDb->getAdmins() as $admin) {
                $this->add_notification(
                    language::get_language()[ORDER_NOTIFICATION_STATUS],
                    language::get_language()[$status],
                    ORDER_NOTIFICATION_STATUS,
                    $uid,
                    $admin['id']);
            }
        } else {
            $this->add_notification(
                language::get_language()[ORDER_NOTIFICATION_STATUS],
                language::get_language()[$status],
                ORDER_NOTIFICATION_STATUS,
                $uid,
                $toId);
        }

        $orderDb = DatabaseHelper::create_order_table();
        $orderDb->update_query(['status' => $_POST['status']], ['id' => $_POST['order']]);

        $order = new OrderTableControl();
        $order->change_status($_POST['order'], $_POST['status']);
    }

}