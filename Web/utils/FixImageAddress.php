<?php


class FixImageAddress
{
    public static function fix_address($image)
    {
        return pageAddress::get_host() . $image;
    }

    public static function fix_images_address($images)
    {
        for ($i = 0; $i < count($images); $i++) {
            $images[$i] = self::fix_address($images[$i]);
        }

        return $images;
    }

    public static function fix_images_Key_address($images, $key) {
        for ($i = 0; $i < count($images); $i++) {
            $images[$i][$key] = self::fix_address($images[$i][$key]);
        }

        return $images;
    }
}