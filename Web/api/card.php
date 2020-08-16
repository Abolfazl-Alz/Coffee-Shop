<?php

include "../utils/includes.php";

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

if(array_key_exists('token', $_POST)) {
    $key = $_POST['token'];
    if(token::is_valid($key)) {
        if(array_key_exists('uid', $_POST)) {
            $uid = $_POST['uid'];
            $db = new CardTableControl();
            if(array_key_exists('action', $_POST)) {
                $feedCtrl = new FeedTableControl();
                $action = $_POST['action'];
                switch ($action) {
                    case 'select':
                        try {
                            $result['data']['items'] = $db->select();
                            $result['data']['status'] = 'success';
                        } catch (Exception $ex) {
                            $result['data']['msg'] = $ex;
                        }
                        break;
                }
            } else $result['data']['msg'] = 'invalid request enter valid request';
        }
    } else $result['data']['msg'] = 'invalid token';
} else
    $result['data']['msg'] = 'enter your token as Post parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);