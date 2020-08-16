<?php

include "../utils/includes.php";

$result = ['data' => ['status' => 'error', 'msg' => ''], 'about' => ['time' => date('yy-m-d h:i')]];

if(array_key_exists('token', $_POST)) {
    $key = $_POST['token'];
    if(token::is_valid($key)) {
        if(array_key_exists('uid', $_POST)) {
            $uid = $_POST['uid'];
            if(array_key_exists('action', $_POST)) {
                $feedCtrl = new FeedTableControl();
                $action = $_POST['action'];
                switch ($action) {
                    case 'select_order':
                        try {
                            $result['data']['items'] = $feedCtrl->select_best_seller();
                            $result['data']['status'] = 'success';
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }
                        break;

                    case 'select_new_items':
                        try {
                            $result['data']['items'] = $feedCtrl->select_new_items();
                            $result['data']['status'] = 'success';
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }

                        break;

                    case 'select_last_order':
                        if(!array_key_exists('uid', $_POST)) {
                            $result['data']['msg'] = 'enter user id as uid';
                            break;
                        }
                        try {
                            $result['data']['items'] = $feedCtrl->select_last_order($uid);
                            $result['data']['status'] = 'success';
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }

                        break;

                    case 'select':
                        try {
                            $result['data']['items'] = $feedCtrl->select($feedCtrl->get_select_query(), $uid);
                            $result['data']['status'] = 'success';
                        } catch (Exception $e) {
                            $result['data']['msg'] = $e->getMessage();
                        }
                        break;

                    default:
                        $result['data']['msg'] = 'Enter valid request as action parameter';
                        break;
                }
            } else $result['data']['msg'] = 'invalid request enter valid request';
        }
        else {
            $result['data']['msg'] = 'Enter user id as uid POST parameter';
        }
    } else $result['data']['msg'] = 'invalid token';
} else
    $result['data']['msg'] = 'enter your token as Post parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);