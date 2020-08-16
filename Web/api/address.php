<?php


use MySqlConnection\SelectQueryCreator;

include "../utils/includes.php";

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

/**
 * @param array $result
 * @return array
 */
function is_valid_parameter(array $result)
{
    if(!array_key_exists('uid', $_POST)) $result['data']['msg'] = 'enter user id as uid GET parameter';
    else if(!array_key_exists('lat', $_POST)) $result['data']['lat'] = 'Enter latitude as `lat` GET parameter';
    else if(!array_key_exists('lng', $_POST)) $result['data']['lat'] = 'Enter longitude as `lng` GET parameter';
    else if(!array_key_exists('address', $_POST)) $result['data']['msg'] = 'enter address as `address` GET Parameter';
    else if(!array_key_exists('name', $_POST)) $result['data']['msg'] = 'enter name for address by `name` key';
    else return [true, $result];
    return [false, $result];
}

function get_values()
{
    $values = array();
    $values['uid'] = $_POST['uid'];
    $values['lat'] = $_POST['lat'];
    $values['lng'] = $_POST['lng'];
    $values['address'] = $_POST['address'];
    $values['name'] = $_POST['name'];
    return $values;
}

if(array_key_exists('token', $_POST)) {
    $db = DatabaseHelper::create_address_table();
    if($db->get_connection()->get_connect_error() == '') {
        if(token::get_permission($_POST['token'])) {
            if(array_key_exists('action', $_POST)) {
                $action = $_POST['action'];
                switch ($action) {

                    case 'insert':

                        $is_valid_parameter = is_valid_parameter($result);
                        $result = $is_valid_parameter[1];
                        if($is_valid_parameter[0]) {
                            $insert = $db->insert_query(get_values());
                            if($db->get_connection()->get_connect_error() != "") {
                                $result['data']['msg'] = $db->get_connection()->get_connect_error();
                            } else if($db->get_connection()->get_last_error() != '') {
                                $result['data']['msg'] = $db->get_connection()->get_last_error();
                            } else {
                                $result['data']['status'] = 'success';
                                $result['data']['msg'] = $insert;
                            }
                        }

                        break;

                    case 'select':

                        if(array_key_exists('uid', $_POST)) {
                            $select_query = new SelectQueryCreator(DatabaseHelper::get_address_table());
                            $select_query->set_condition(['uid' => $_POST['uid']]);
                            $select = $db->select_query($select_query);
                            if($db->get_connection()->get_last_error()) {
                                $result['data']['msg'] = $db->get_connection()->get_last_error();
                                break;
                            }
                            $result['data']['status'] = 'success';
                            $result['data']['items'] = $select;
                        } else {
                            $result['data']['msg'] = 'uid is not defined, define it by `uid` as a GET parameter key';
                        }

                        break;

                    case 'delete':
                        //Check id key is exist into GET method
                        if(!array_key_exists('id', $_POST)) {
                            $result['data']['msg'] = 'at first enter id then try to delete address';
                            break;
                        }
                        //Run Delete Query
                        $delete = $db->delete_query(['id' => $_POST['id']]);
                        if($db->get_connection()->get_last_error() != '') {
                            $result['data']['msg'] = $db->get_connection()->get_last_error();
                            break;
                        }
                        $result['data']['status'] = 'success';
                        break;

                    case 'update':

                        if(!array_key_exists('id', $_POST)) {
                            $result['data']['msg'] = 'set address id want to change';
                            break;
                        }
                        $is_valid_parameter = is_valid_parameter($result);
                        $result = $is_valid_parameter[1];
                        if($is_valid_parameter[0]) {
                            $update = $db->update_query(get_values(), ['id' => $_POST['id']]);
                            if($db->get_connection()->get_connect_error() != "") {
                                $result['data']['msg'] = $db->get_connection()->get_connect_error();
                            } else if($db->get_connection()->get_last_error() != '') {
                                $result['data']['msg'] = $db->get_connection()->get_last_error();
                            } else {
                                $result['data']['status'] = 'success';
                                $result['data']['msg'] = $update;
                            }
                        }
                        break;

                    default:
                        $result['data']['msg'] = 'invalid action';
                        break;
                }
            } else $result['data']['msg'] = 'enter your request as Action parameter';
        } else $result['data']['msg'] = "You don't have access to this section";
    } else $result['data']['msg'] = $db->get_connection()->get_connect_error();

} else $result['data']['msg'] = 'enter token by GET parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);