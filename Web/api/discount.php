<?php


include '../utils/includes.php';

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

if (array_key_exists('token', $_POST)) {
    $key = $_POST['token'];
    if (array_key_exists('uid', $_POST)) {
        $uid = $_POST['uid'];
        if (token::is_valid($key)) {
            if (array_key_exists('action', $_POST)) {
                $action = $_POST['action'];
                $tableCtrl = new DiscountTableControl();
                switch ($action) {
                    case 'select':
                        try {
                            $result['data']['status'] = 'success';
                            $result['data']['items'] = $tableCtrl->select_all($uid);
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }
                        break;

                    case 'select_by_id':
                        if (array_key_exists("id", $_GET)) {
                            try {
                                $result['data']['status'] = 'success';
                                $result['data']['items'] = $tableCtrl->select_by_id($_POST['id'], $uid);
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                        } else {
                            $result['data']['msg'] = 'enter id parameter as GET http request';
                        }
                        break;

                    case 'select_by_code':
                        if (array_key_exists("code", $_POST)) {
                            try {
                                $result['data']['status'] = 'success';
                                $result['data']['items'] = $tableCtrl->select_by_code($_POST['code'], $uid);
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                        } else {
                            $result['data']['msg'] = 'enter discount code parameter as GET http request';
                        }
                        break;

                    case 'insert':

                        $columns = ['title', 'code', 'value', 'expiration'];
                        $columnName = '';
                        foreach ($columns as $column) {
                            if (!array_key_exists($column, $_POST)) {
                                $columnName = $column;
                            }
                        }

                        if ($columnName != '') {
                            $result['data']['msg'] = "Enter $columnName for this discount";
                            break;
                        }

                        $items = [-1];

                        $idItemCorrect = true;
                        if (isset($_POST['items'])) {
                            $items = explode('-', $_POST['items']);
                            for ($i = 0; $i < count($items); $i++) {
                                $item = $items[$i];
                                if (!is_numeric($item)) {
                                    $result['data']['msg'] = "invalid item id [id=$item]";
                                    $idItemCorrect = false;
                                    break;
                                }
                            }
                        }
                        if (!$idItemCorrect)
                            break;

                        try {
                            $tableCtrl->insert($_POST['title'], $_POST['code'], $_POST['value'], $_POST['expiration'], $items);
                            $result['data']['status'] = 'success';
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }

                        break;

                    case 'delete':
                        if (array_key_exists('id', $_POST)) {
                            try {
                                $tableCtrl->delete($_POST['id']);
                                $result['data']['status'] = 'success';
                            } catch (Exception $e) {
                                $result['data']['msg'] = $e->getMessage();
                            }
                        } else {
                            $result['data']['msg'] = 'Enter discount id by POST http parameter';
                        }
                        break;

                    default:
                        $result['data']['msg'] = 'invalid request, enter valid request by action parameter as post';
                        break;
                }
            } else $result['data']['msg'] = 'invalid request enter valid request';
        } else $result['data']['msg'] = 'invalid token';
    } else {
        $result['data']['msg'] = 'enter user id';
    }
} else $result['data']['msg'] = 'enter your token as Post parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);