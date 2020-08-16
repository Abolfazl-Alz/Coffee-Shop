<?php

use MySqlConnection\SelectQueryCreator;

include '../utils/includes.php';

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

$db = DatabaseHelper::create_review_table();

/**
 * @param array $result
 * @return array
 */
function is_valid_parameter(array $result)
{
    if (!array_key_exists('uid', $_POST)) $result['data']['msg'] = 'enter user id as uid GET parameter';
    else if (!array_key_exists('msg', $_POST)) $result['data']['msg'] = 'Enter user review message as `msg` GET parameter';
    else if (!array_key_exists('iid', $_POST)) $result['data']['msg'] = 'enter the item id want to have review as `iid` GET Parameter';
    else if (!array_key_exists('rate', $_POST)) $result['data']['msg'] = 'enter user rate for item by `rate` key';
    else return [true, $result];
    return [false, $result];
}

/**
 * @return array
 */
function get_values()
{
    $values = array();
    $values['uid'] = $_POST['uid'];
    $values['text'] = str_replace('\'', '\\\'', $_POST['msg']);
    $values['iid'] = $_POST['iid'];
    $values['rate'] = $_POST['rate'];
    return $values;
}

if ($db->get_connection()->get_connect_error() == '') {
    if ($db->get_connection()->get_connect_error() == '') {
        if (array_key_exists('token', $_POST)) {
            if (token::is_valid($_POST['token'])) {
                if (array_key_exists('action', $_POST)) {
                    switch ($_POST['action']) {

                        case 'select':
                            $page = 1;
                            if (array_key_exists('page', $_POST))
                                $page = $_POST['page'];
                            $count = 10;
                            if (array_key_exists('count', $_POST))
                                $count = $_POST['count'];

                            if (!array_key_exists('iid', $_POST)) {
                                $result['data']['msg'] = 'enter id of item you want to get reviews';
                                break;
                            }

                            if ($page > 1) $page = 1;

                            $select = new SelectQueryCreator(DatabaseHelper::get_review_table());
                            $select->set_condition(['iid' => $_POST['iid']]);
                            $select->set_limit_max($count * ($page - 1), $count * $page);
                            $select->set_order_by('time', SelectQueryCreator::ORDER_BY_OPTION_DESC);
                            $run_query = $select->run_query(DatabaseHelper::create_connection());

                            for ($i = 0; $i < count($run_query); $i++) {
                                $registerDb = DatabaseHelper::create_register_table();
                                $run_query[$i]['registerData'] = $registerDb->select_with_condition(['id' => $run_query[$i]['uid']]);

                                if (count($run_query[$i]['registerData']) > 0)
                                    $run_query[$i]['registerData'] = $run_query[$i]['registerData'][0];
                            }

                            $result['data']['items'] = $run_query;
                            $result['data']['status'] = 'success';
                            $result['data']['msg'] = (string)$select;

                            break;

                        case 'insert':
                            $is_valid_parameter = is_valid_parameter($result);
                            $result = $is_valid_parameter[1];
                            if ($is_valid_parameter[0]) {
                                $result['data']['msg'] = $db->insert_query(get_values());
                                if ($db->get_connection()->get_connect_error() != "") {
                                    $result['data']['msg'] = $db->get_connection()->get_connect_error();
                                } else if ($db->get_connection()->get_last_error() != '') {
                                    $result['data']['msg'] = $db->get_connection()->get_last_error();
                                } else {
                                    $result['data']['status'] = 'success';
                                }
                            }
                            break;

                        case 'delete':
                            if (!array_key_exists('id', $_POST)) $result['data']['msg'] = 'enter the review id to delete';
                            else {
                                $db->delete_query(['id' => $_POST['id']]);
                            }
                            break;

                        case 'update':
                            $is_valid_parameter = is_valid_parameter($result);
                            $result = $is_valid_parameter[1];
                            if (!array_key_exists('id', $_POST)) $result['data']['msg'] = 'enter the review id to delete';
                            else if ($is_valid_parameter[0]) {
                                $db->update_query(get_values(), ['id' => $_POST['id']]);
                            }

                            break;


                        default:
                            $result['data']['msg'] = 'invalid action';
                            break;
                    }
                } else $result['data']['msg'] = 'enter your request as Action parameter';
            } else $result['data']['msg'] = 'invalid token';
        } else $result['data']['msg'] = 'enter token by GET parameter';
    } else $result['data']['msg'] = $db->get_connection()->get_last_error();
} else $result['data']['msg'] = $db->get_connection()->get_connect_error();

echo json_encode($result, JSON_UNESCAPED_UNICODE);