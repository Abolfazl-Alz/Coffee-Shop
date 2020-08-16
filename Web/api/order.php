<?php


use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;
use MySqlConnection\TableControl;

include_once "../utils/includes.php";

define('DEFAULT_STATUS', 0);
define('ACCEPT_STATUS', 1);
define('READY_STATUS', 2);
define('SENT_STATUS', 3);
define('ARRIVED_STATUS', 4);
define('DELIVERED_STATUS', 5);

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

/**
 * @param TableControl $db
 * @param array $result
 * @param integer $status
 * @return bool
 */
function change_status($db, $result, $status)
{
    if(!array_key_exists('id', $_POST)) {
        $result['data']['msg'] = 'please enter id for deleting order';
        return false;
    }
    $update = $db->update_query(['status' => $status], ['id' => $_POST['id']]);
    if($db->get_connection()->get_last_error()) {
        $result['data']['msg'] = $db->get_connection()->get_last_error();
        return false;
    } else if($update instanceof mysqli_result or !$update) {
        $result['data']['msg'] = $db->get_connection()->get_last_error();
        return false;
    }
    $result['data']['status'] = 'success';
    return true;
}

if(array_key_exists('token', $_POST)) {
    if(token::is_valid($_POST['token'])) {
        $db = DatabaseHelper::create_order_table();
        $orderTable = new OrderTableControl();
        if($db->get_connection()->get_connect_error() == '') {

            if(array_key_exists('user_id', $_POST)) {
                $userId = $_POST['user_id'];
                $resultAction = false;
                if(array_key_exists('action', $_POST)) {
                    switch ($_POST['action']) {
                        case 'add':
                            $msg = '';
                            if(!array_key_exists('address', $_POST)) {
                                $result['data']['msg'] = 'enter user address for this order as `address`';
                                break;
                            }
                            $address = $_POST['address'];
                            if(!array_key_exists('msg', $_POST)) {
                                $msg = $_POST['msg'];
                            }

                            try {
                                $orderTable->add_order($userId, $address, $msg);
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                                break;
                            }

                            $result['data']['status'] = 'success';

                            break;

                        case 'select_unread_count':
                            echo $orderTable->unread_count($userId);
                            exit();

                        case 'select_incomplete':

                            try {
                                $result['data']['items'] = $orderTable->select_incomplete();
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }

                            break;

                        case 'select':
                            $pagination = 1;
                            $count = 10;

                            if (array_key_exists('pagination', $_POST)) {
                                $pagination = $_POST['pagination'];
                            }
                            if (array_key_exists('count', $_POST)) {
                                $count = $_POST['count'];
                            }

                            $selectQuery = new SelectQueryCreator(DatabaseHelper::get_order_table());
                            $selectQuery->set_limit_max(($count * $pagination) - $count + 1, $count * $pagination);

                            try {
                                $result['data']['items'] = $orderTable->select_select_creator($selectQuery);
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }

                            break;

                        case 'read':

                            if(!array_key_exists('id', $_POST)) {
                                $result['data']['msg'] = 'enter order id by `id` as GET parameter';
                                break;
                            }

                            $db_view = DatabaseHelper::create_order_view_table();
                            if($db_view->get_connection()->get_connect_error() != '') {
                                $result['data']['msg'] = $db_view->get_connection()->get_connect_error();
                                break;
                            }
                            $arr = ['uid_view' => $userId, 'orderId' => $_POST['id']];
                            $insert = $db_view->insert_if_not_exist($arr, $arr);
                            if($db_view->get_connection()->get_last_error() != '') {
                                $result['data']['msg'] = $db_view->get_connection()->get_last_error();
                                break;
                            }

                            $result['data']['status'] = 'success';

                            break;

                        case 'change_status':
                            if(array_key_exists('status', $_POST)) {
                                change_status($db, $result, $_POST['status']);
                                break;
                            }
                            $result['data']['msg'] = 'enter status';
                            break;

                        case 'select_history':
                            try {
                                $result['data']['items'] = $orderTable->select_order(['uid' => $userId]);
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                            break;

                        default:
                            $result['data']['msg'] = 'invalid request, enter valid request by action POST parameter';
                            break;
                    }
                } else $result['data']['msg'] = 'undefined action';
            } else $result['data']['msg'] = 'userId is undefined';
        } else $result['data']['msg'] = $db->get_connection()->get_connect_error();
    }
} else $result['data']['msg'] = 'enter Token as `token` key parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);
