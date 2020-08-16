<?php

use MySqlConnection\SelectQueryCreator;

include "../utils/includes.php";

$result = ['data' => ['status' => 'error', 'msg' => 'invalid', 'code' => 0], 'about' => ['time' => date('yy-m-d h:i')]];

//0 invalid error
//1 server error
//2 BAD parameter
//3 INVALID information
//4 success

define('INVALID_ERROR', 0);
define('SERVER_ERROR', 1);
define('BAD_PARAMETER', 2);
define('INVALID_INFORMATION', 3);
define('SUCCESS', 4);

function check_base_parameter()
{
    if(!array_key_exists('phone_number', $_POST)) {
        return 'enter phone number';
    } else if(!array_key_exists('password', $_POST)) {
        return 'enter password';
    }
    return '';
}

function select_user($phone)
{
    $select = new SelectQueryCreator(DatabaseHelper::get_register_table());
    $select->set_condition(['phoneNumber' => $phone]);
    $select->select_columns(['id', 'firstname', 'lastname', 'phoneNumber', 'email', 'loginDate', 'language', 'admin']);
    return $select->run_query(DatabaseHelper::create_connection());
}

function select_user_by_id($id)
{
    $select = new SelectQueryCreator(DatabaseHelper::get_register_table());
    $select->set_condition(['id' => $id]);
    $select->select_columns(['id', 'firstname', 'lastname', 'phoneNumber', 'email', 'loginDate', 'language', 'admin']);
    return $select->run_query(DatabaseHelper::create_connection());
}

if(array_key_exists('token', $_POST)) {
    if(token::is_valid($_POST['token'])) {
        $db = DatabaseHelper::create_register_table();

        if(array_key_exists('action', $_POST)) {
            switch ($_POST['action']) {

                case 'sign_in':
                    $check_parameter = check_base_parameter();
                    if($check_parameter != '') {
                        $result['data']['msg'] = $check_parameter;
                        $result['data']['code'] = BAD_PARAMETER;
                        break;
                    }

                    $count = $db->select_count(['phoneNumber' => $_POST['phone_number'], 'password' => $_POST['password']]);
                    if($db->get_connection()->get_last_error() != '') {
                        $result['data']['msg'] = $db->get_connection()->get_last_error();
                        $result['data']['code'] = BAD_PARAMETER;
                        break;
                    }

                    if($count == 1) {
                        $result['data']['status'] = 'success';
                        $result['data']['code'] = SUCCESS;
                        $result['data']['user'] = select_user($_POST['phone_number']);
                        if(count($result['data']['user']) > 0)
                            $result['data']['user'] = $result['data']['user'][0];
                    } else {
                        $result['data']['msg'] = 'phone number or password is invalid';
                        $result['data']['code'] = INVALID_INFORMATION;
                    }

                    break;

                case 'sign_up':
                    $check_parameter = check_base_parameter();
                    if($check_parameter != '') {
                        $result['data']['msg'] = $check_parameter;
                        $result['data']['code'] = SERVER_ERROR;
                        break;
                    }

                    $resultId = $db->insert_if_not_exist(['phoneNumber' => $_POST['phone_number'], 'password' => $_POST['password']], ['phoneNumber' => $_POST['phone_number']]);

                    if($db->get_connection()->get_last_error() != '') {
                        $result['data']['msg'] = $db->get_connection()->get_last_error();
                        break;
                    }

                    if($resultId == -1) {
                        $result['data']['msg'] = 'phone number is taken';
                        $result['data']['code'] = INVALID_INFORMATION;
                        break;
                    }

                    $result['data']['status'] = 'success';
                    $result['data']['code'] = SUCCESS;
                    $result['data']['user'] = select_user($_POST['phone_number']);
                    if(count($result['data']['user']) > 0)
                        $result['data']['user'] = $result['data']['user'][0];

                    break;

                case 'update_information':

                    $data = array();

                    if(!array_key_exists('id', $_POST)) {
                        $result['data']['msg'] = 'Enter id as `id` POST parameter';
                        break;
                    }

                    if(array_key_exists('first_name', $_POST)) $data['firstname'] = $_POST['first_name'];
                    if(array_key_exists('last_name', $_POST)) $data['lastname'] = $_POST['last_name'];
                    if(array_key_exists('phone_number', $_POST)) $data['phoneNumber'] = $_POST['phone_number'];
                    if(array_key_exists('language', $_POST)) $data['language'] = $_POST['language'];

                    $update = $db->update_query($data, ['id' => $_POST['id']]);
                    if($db->get_connection()->get_last_error() != '') {
                        $result['data']['msg'] = $db->get_connection()->get_last_error();
                        $result['data']['code'] = SERVER_ERROR;
                        break;
                    }

                    $result['data']['code'] = SUCCESS;
                    $result['data']['status'] = 'success';
                    $result['data']['user'] = select_user($_POST['phone_number']);
                    if(count($result['data']['user']) > 0)
                        $result['data']['user'] = $result['data']['user'][0];

                    break;

                case 'select':
                    if (!array_key_exists('uid', $_POST)) {
                        $result['data']['msg'] = 'enter user id as `uid` POST parameter';
                        break;
                    }

                    $count = $db->select_count(['id' => $_POST['uid']]);
                    if($db->get_connection()->get_last_error() != '') {
                        $result['data']['msg'] = $db->get_connection()->get_last_error();
                        $result['data']['code'] = BAD_PARAMETER;
                        break;
                    }

                    if($count == 1) {
                        $result['data']['status'] = 'success';
                        $result['data']['code'] = SUCCESS;
                        $result['data']['user'] = select_user_by_id($_POST['uid']);
                        if(count($result['data']['user']) > 0)
                            $result['data']['user'] = $result['data']['user'][0];
                    }
                    break;

                default:
                    $result['data']['code'] = BAD_PARAMETER;
                    $result['data']['msg'] = 'invalid action';
                    break;
            }
        } else {
            $result['data']['msg'] = 'enter your request as `action` POST parameter';
            $result['data']['code'] = BAD_PARAMETER;
        }
    } else {
        $result['data']['msg'] = 'token not valid';
        $result['data']['code'] = BAD_PARAMETER;
    }
} else {
    $result['data']['msg'] = 'enter token';
    $result['data']['code'] = BAD_PARAMETER;
}

echo json_encode($result, JSON_UNESCAPED_UNICODE);