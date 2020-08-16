<?php
include '../utils/includes.php';

$result = ['data' => ['status' => 'error', 'msg' => 'invalid'], 'about' => ['time' => date('yy-m-d h:i')]];

$db = DatabaseHelper::create_collection_table();

if (array_key_exists('token', $_POST)) {
    $key = $_POST['token'];
    if (token::is_valid($key)) {
        if (array_key_exists('action', $_POST)) {
            $action = $_POST['action'];
            $tableCtrl = new CollectionTableControl();
            switch ($action) {
                case 'select':
                    if (array_key_exists('id', $_POST)) {
                        $select = $db->select_with_condition(['id' => $_POST['id']]);
                    } else {
                        $select = $db->select_with_condition('');
                    }
                    if ($db->get_connection()->get_connect_error() != '') {
                        $result['data']['msg'] = $db->get_connection()->get_connect_error();
                    } else if ($db->get_connection()->get_last_error() != '') {
                        $result['data']['msg'] = $db->get_connection()->get_last_error();
                    } else {
                        $result['data']['status'] = 'success';
                        foreach ($select as $item) {
                            $item['image'] = pageAddress::get_host() . $item['image'];
                        }

                        for ($i = 0; $i < count($select); $i++) {
                            $select[$i]['image'] = pageAddress::get_host() . $select[$i]['image'];
                        }

                        $result['data']['items'] = $select;
                    }
                    break;

                case 'insert':
                    $array = ['title', 'information', 'image'];
                    $incomplete_fields = "";
                    foreach ($array as $item) {
                        if (!array_key_exists($item, $_POST)) {
                            if ($incomplete_fields == '')
                                $incomplete_fields .= $item;
                            else
                                $incomplete_fields .= ', ' . $item;
                        }
                    }

                    if ($incomplete_fields != '') {
                        $result['data']['msg'] = 'fill fields: ' . $incomplete_fields;
                        break;
                    }

                    try {
                        $insert = $tableCtrl->insert_base64($_POST['title'], $_POST['information'], $_POST['image']);
                        if ($insert == false) {
                            $result['data']['msg'] = "There is a problem when adding a category";
                        } else {
                            $result['data']['status'] = 'success';
                        }
                    } catch (Exception $e) {
                        $result['data']['msg'] = $e->getMessage();
                    }

                    break;

                case 'update':
                    if (!array_key_exists('id', $_POST)) {
                        $result['data']['msg'] = 'Enter category id by id key';
                        break;
                    }
                    $id = $_POST['id'];
                    try {
                        $tableCtrl->update_base64($_GET, $id);
                        $result['data']['status'] = 'success';
                    } catch (Exception $e) {
                        $result['data']['msg'] = $e->getMessage();
                    }
                    break;
            }
        } else $result['data']['msg'] = 'invalid request enter valid request';
    } else $result['data']['msg'] = 'invalid token';
} else $result['data']['msg'] = 'enter your token as Post parameter';

echo json_encode($result, JSON_UNESCAPED_UNICODE);