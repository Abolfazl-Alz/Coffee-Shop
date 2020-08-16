<?php


use coffee_shop\Exception\ConnectionException;
use coffee_shop\Exception\DatabaseException;

class CollectionTableControl
{
    private $tableControl;

    /**
     * CollectionTableControl constructor.
     */
    public function __construct()
    {
        $this->tableControl = DatabaseHelper::create_collection_table();
    }

    /**
     * Select Collection by id
     * @param int $id
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select_by_id($id)
    {
        return $this->select(['id' => $id]);
    }

    /**
     * Select Collections by condition
     * @param string|array $condition
     * @return array
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function select($condition)
    {
        $select = $this->tableControl->select_with_condition($condition);
        if ($this->tableControl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_connect_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        }
        FixImageAddress::fix_images_Key_address($select, 'image');
        return $select;
    }

    /**
     * Insert new category to database by base64 image format
     * @param string $name category name
     * @param string $information short text about category
     * @param string $image_base64 image as base64 image format
     * @return boolean
     * @throws ConnectionException
     * @throws DatabaseException
     * @author Abolfazl Alizadeh
     * @version 1.0.0.0
     */
    public function insert_base64($name, $information, $image_base64)
    {
        $result = false;
        $pictureName = "category/$name.png";
        $i = 0;
        if (!file_exists('../img/category')) {
            mkdir('../img/category');
        }
        while (file_exists(pageAddress::get_host() . $pictureName)) {
            $pictureName = "category/$name\_" . ++$i . ".png";
        }
        if (upload::write_image_base64($image_base64, $pictureName)) {
            $result = $this->insert($name, $information, "img/$pictureName");
        }
        if (!$result)
            unlink($pictureName);

        return $result;
    }

    /**
     * Insert new category to database
     * @param string $name category name
     * @param string $information short text about category
     * @param string $image_file image path in host
     * @return boolean
     * @throws DatabaseException
     * @throws ConnectionException
     * @author Abolfazl Alizadeh
     * @version 1.0.0.0
     */
    public function insert($name, $information, $image_file)
    {
        $result = $this->tableControl->insert_query(['name' => $name, 'information' => $information, 'image' => $image_file]);
        if ($this->tableControl->get_connection()->get_connect_error() != '') {
            throw new ConnectionException($this->tableControl->get_connection()->get_connect_error());
        } else if ($this->tableControl->get_connection()->get_last_error() != '') {
            throw new DatabaseException($this->tableControl->get_connection()->get_last_error());
        }
        return $result;
    }

    /**
     * Update image by base64 image format
     * @param array $data
     * @param int id
     * @return bool
     * @throws ConnectionException
     * @throws DatabaseException
     */
    public function update_base64($data, $id)
    {
        if (array_key_exists('id', $data)) unset($data['id']);

        $name = date("Y-m-d h-i-s-A");
        if (array_key_exists('name', $data)) $name = $data['name'];
        else {
            $select = $this->select_by_id($id);
            if (count($select) == 1)
                $name = $select[0]['name'];
        }

        if (array_key_exists('image_base64', $data)) {
            $image_base64 = $data['image_base64'];
            unset($data['image_base64']);
            $pictureName = "category/$name.png";
            $i = 0;
            if (!file_exists('../img/category')) {
                mkdir('../img/category');
            } else if (file_exists($pictureName)) {
                unlink($pictureName);
            }
            while (file_exists(pageAddress::get_host() . $pictureName)) {
                $pictureName = "category/$name\_" . ++$i . ".png";
            }
            if (upload::write_image_base64($image_base64, $pictureName))
                $data['image'] = $pictureName;
        }

        return $this->tableControl->update_query($data, ['id' => $id]);
    }

}