<?php

include '../utils/includes.php';

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

if (array_key_exists('token', $_POST)) {
    $key = $_POST['token'];
    if (token::is_valid($key)) {
        if (array_key_exists('action', $_POST)) {
            $action = $_POST['action'];
            $tableCtrl = new PostTableControl();
            switch ($action) {
                case 'select':
                    $page = -1;
                    $count = -1;
                    if (array_key_exists('page', $_GET)) {
                        $page = $_GET['page'];
                        $count = 15;
                    }
                    if (array_key_exists('count', $_GET)) {
                        $count = $_GET['count'];
                    }
                    try {
                        if ($page != -1) {
                            $result['data']['items'] = $tableCtrl->select($page, $count);
                        } else {
                            $result['data']['items'] = $tableCtrl->select_all();
                        }
                        $result['data']['status'] = 'success';
                    } catch (Exception $e) {
                        $result['data']['msg'] = $e->getMessage();
                    }
                    break;

                case 'select_by_id':
                    if (array_key_exists("id", $_GET)) {
                        try {
                            $result['data']['items'] = $tableCtrl->select_by_id($_GET['id']);
                            $result['data']['status'] = 'success';
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }
                    } else {
                        $result['data']['msg'] = 'enter id parameter as GET http request';
                    }
                    break;
                default:
                    $result['data']['msg'] = 'invalid request, enter valid request by action parameter as post';
                    break;
            }
        } else $result['data']['msg'] = 'invalid request enter valid request';
    } else $result['data']['msg'] = 'invalid token';
} else $result['data']['msg'] = 'enter your token as Post parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);