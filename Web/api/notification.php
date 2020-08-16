<?php

use coffee_shop\Exception\DatabaseException;
use MySqlConnection\SelectQueryCreator;

if($_SERVER['REQUEST_METHOD'] == 'GET') {
    $_POST = $_GET;
}

include_once "../utils/includes.php";

$result = ['data' => ['status' => 'error', 'msg' => 'invalid'], 'about' => ['time' => date('yy-m-d h:i')]];

$db = DatabaseHelper::create_notification_table();

if(array_key_exists('token', $_POST)) {
    $key = $_POST['token'];
    if(token::is_valid($key)) {
        $select = $db->select_with_condition('');
        if($db->get_connection()->get_connect_error() != '') {
            $result['data']['msg'] = $db->get_connection()->get_connect_error();
        } else if($db->get_connection()->get_last_error() != '') {
            $result['data']['msg'] = $db->get_connection()->get_last_error();
        } else {
            if(array_key_exists('uid', $_POST)) {
                $notificationTable = new NotificationTableControl();
                if(array_key_exists('action', $_POST)) {
                    switch ($_POST['action']) {
                        case 'select_all':
                            $selectQuery = new SelectQueryCreator(DatabaseHelper::get_notification_table());
                            $selectQuery->set_condition(['toId' => $_POST['uid']]);
                            $result['data']['items'] = $db->select_query($selectQuery);
                            foreach ($result['data']['items'] as $item) {
                                $db->delete_query(['id' => $item['id']]);
                            }
                            break;

//                        case 'add_notification':
//                            break;

                        case 'add_order':
                            if(!array_key_exists('to_id', $_POST)) {
                                $result['data']['msg'] = 'Enter the id of the person you want to send the message to';
                                break;
                            }
                            if(!array_key_exists('status', $_POST)) {
                                $result['data']['msg'] = 'Enter status key for send';
                                break;
                            }
                            if(!array_key_exists('order', $_POST)) {
                                $result['data']['msg'] = 'Enter order id';
                                break;
                            }

                            try {
                                $notificationTable->change_order_notification($_POST['status'], $_POST['uid'], $_POST['to_id']);
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                            $result['data']['status'] = 'success';
                            break;

                        case 'seen':
                            if(array_key_exists('id', $_POST)) {
                                $db->delete_query(['id' => $_POST['id']]);
                            }
                            break;

                        default:
                            $result['data']['msg'] = 'invalid request';
                            break;
                    }
                } else {
                    $result['data']['msg'] = 'enter your request as `action` POST parameter';
                }
            } else {
                $result['data']['msg'] = 'enter user id as uid POST request';
            }
        }
    } else $result['data']['msg'] = 'invalid token';
} else $result['data']['msg'] = 'enter your token as Post parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);
