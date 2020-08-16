<?php


include_once '../utils/includes.php';

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

if(array_key_exists('token', $_POST)) {
    if(token::is_valid($_POST['token'])) {
        $table = new CartTableControl();
        if($table->getTableControl()->get_connection()->get_connect_error() == '') {
            if(array_key_exists('user_id', $_POST)) {

                $userId = $_POST['user_id'];

                $resultAction = false;
                if(array_key_exists('action', $_POST))
                    switch ($_POST['action']) {

                        case 'select':

                            try {
                                $result['data']['items'] = $table->select_incomplete($userId);
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }

                            break;

                        case 'insert':
                            if(!array_key_exists('item_id', $_POST)) {
                                $result['data']['msg'] = 'undefined item id, at first set it then try again';
                                break;
                            } else if(!array_key_exists('count', $_POST)) {
                                $result['data']['msg'] = 'undefined count, at first set it then try again';
                                break;
                            } else if(!array_key_exists('size', $_POST)) {
                                $result['data']['msg'] = 'undefined size, at first set it then try again';
                                break;
                            }

                            try {
                                $result['data']['status'] = 'success';
                                $result['data']['items'] = $table->add_cart($userId, $_POST['item_id'], $_POST['size'], $_POST['count']);
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }

                            break;

                        case 'delete':
                            if(!array_key_exists('id', $_POST)) {
                                $result['data']['msg'] = 'at first enter `id` then request delete item';
                                break;
                            }
                            try {
                                $table->delete_cart($_POST['id']);
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                            break;

                        case 'delete_all':
                            try {
                                $table->delete_all_user_cart($userId);
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                            break;

                        case 'update':
                            if(!array_key_exists('id', $_POST)) {
                                $result['data']['msg'] = 'undefined cart id, set cart id as `id` GET parameter';
                                break;
                            }
                            if(!check_writable($result)) break;
                            try {
                                if($table->update_cart_information($_POST['id'], $_POST['item_id'], $userId, $_POST['count'], $_POST['size']))
                                    $result['data']['status'] = 'success';
                                else
                                    $result['data']['msg'] = $table->getTableControl()->get_connection()->get_last_error();
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                            break;

                        case 'update_status':
                            if(array_key_exists('id', $_POST)) {
                                if(!array_key_exists('status', $_POST)) {
                                    $result['data']['msg'] = 'undefined status, at first set it then try again';
                                    break;
                                }
                                try {
                                    $table->update_cart_status($_POST['status'], $_POST['id']);
                                    $result['data']['status'] = 'success';
                                } catch (Exception $e) {
                                    $result['data']['msg'] = $e->getMessage();
                                }
                            } else {
                                $result['data']['msg'] = 'undefined cart id, set cart id as `id` POST parameter';
                            }
                            break;

                        default:
                            $result['data']['msg'] = 'invalid action';
                            break;

                    } else {
                    $result['data']['msg'] = 'undefined action';
                }

            } else {
                $result['data']['msg'] = 'userId is undefined';
            }
        } else {
            $result['data']['msg'] = $table->getTableControl()->get_connection()->get_connect_error();
        }
    }
} else {
    $result['data']['msg'] = 'enter Token as `token` key parameter';
}

echo json_encode($result, JSON_UNESCAPED_UNICODE);


/**
 * @param array $result
 * @return boolean
 */
function check_writable($result)
{
    if(!array_key_exists('item_id', $_POST)) {
        $result['data']['msg'] = 'undefined item id, at first set it then try again';
    } else if(!array_key_exists('count', $_POST)) {
        $result['data']['msg'] = 'undefined count, at first set it then try again';
    } else if(!array_key_exists('size', $_POST)) {
        $result['data']['msg'] = 'undefined size, at first set it then try again';
    } else {
        return true;
    }
    return false;
}