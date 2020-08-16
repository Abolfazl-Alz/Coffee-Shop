<?php

use MySqlConnection\SelectQueryCreator;

include '../utils/includes.php';


$result = ['data' => ['status' => 'error', 'msg' => 'invalid'], 'about' => ['time' => date('yy-m-d h:i')]];

if (array_key_exists('token', $_POST)) {
    if (token::is_valid($_POST['token'])) {
        $token = $_POST['token'];
        $db = DatabaseHelper::create_items_table();
        $tableControl = new ItemsTableControl();
        if (token::is_valid($token)) {
            if (array_key_exists('action', $_POST)) {
                switch ($_POST['action']) {
                    case 'select':
                        if (array_key_exists('collection', $_POST)) {
                            $collectionName = $_POST['collection'];
                            try {
                                $result['data']['items'] = $tableControl->select_by_collection($collectionName);
                                $result['data']['status'] = 'success';
                            } catch (Exception $ex) {
                                $result['data']['msg'] = $ex->getMessage();
                            }
                        } else {
                            $result['data']['msg'] = 'at first set collection name by `collection` key as parameter';
                        }
                        break;
                    case 'insert':
                        $cols = ['title', 'information', 'image', 'type', 'category', 'sizes', 'price'];
                        $status = true;
                        foreach ($cols as $col) {
                            if (!array_key_exists($col, $_POST)) {
                                $status = false;
                                $result['data']['msg'] = "Enter $col as POST parameter";
                                break;
                            }
                        }
                        if (!$status)
                            break;

                        $imageName = $_POST['title'];
                        $pictureName = str_replace(' ', '_', mb_strtolower($imageName)) . ".png";
                        if (upload::write_image_base64($_POST['image'], $pictureName)) {
                            $values = ['title' => $_POST['title'],
                                'information' => $_POST['information'],
                                'type' => $_POST['type'],
                                'image' => 'img/' . $pictureName,
                                'category' => $_POST['category'],
                                'sizes' => $_POST['sizes'],
                                'price' => $_POST['price']];

                            if ($db->insert_query($values) > 0) {
                                $result['data']['status'] = 'success';
                                $result['data']['msg'] = $pictureName;
                            } else {
                                unlink('../img/' . $pictureName);
                                $result['data']['msg'] = 'Problems with adding information to the database - ' . $db->get_connection()->get_last_error() . " - image name: " . $pictureName;
                            }
                        } else {
                            $result['data']['msg'] = 'Inability to save photos';
                        }

                        break;
                    case 'update':
                        if (!array_key_exists('id', $_POST)) {
                            $result['data']['msg'] = 'at first enter item id to update';
                            break;
                        }
                        $imageResult = true;
                        if (array_key_exists('title', $_POST)) $imageName = $_POST['title'];
                        else $imageName = 'y-m-d_H-i-s';
                        $pictureName = str_replace('', '_', strtolower($imageName)) . ".png";
                        if (array_key_exists('image', $_GET) && $_GET['image'] != '') {
                            $imageResult = upload::write_image_base64($_GET['image'], $pictureName);
                            $_GET['image'] = $pictureName;
                        } else {
                            unset($_GET['image']);
                        }
                        try {
                            $update = $db->update_query($_GET, ['id' => $_POST['id']]);
                        } catch (mysqli_sql_exception $ex) {
                            $result['data']['msg'] = $ex->getMessage();
                            break;
                        }
                        if ($db->get_connection()->get_connect_error()) {
                            $result['data']['msg'] = $db->get_connection()->get_connect_error();
                        } else if ($db->get_connection()->get_last_error()) {
                            $result['data']['msg'] = $db->get_connection()->get_last_error();
                        } else if (!$update) {
                            $result['data']['msg'] = 'failed to update';
                        } else {
                            $result['data']['status'] = 'success';
                        }

                        break;
                    default:
                        $result['data']['msg'] = 'invalid request, enter valid request as `action` POST parameter';
                        break;
                }
            } else {
                $result['data']['msg'] = 'Enter your request as `action`';
            }
        } else {
            $result['data']['msg'] = 'invalid token';
        }
    } else {
        $result['data']['msg'] = 'invalid token, enter valid token';
    }
} else {
    $result['data']['msg'] = 'Enter token to continue';
}

echo json_encode($result, JSON_UNESCAPED_UNICODE, 300);