<?php

class upload
{
    public static function write_image_base64($encoded, $imageName)
    {

        $mainName = $imageName;
        $i = 0;
        while (file_exists($imageName)) $imageName = $mainName . "-" . $i++;

        $decoded_string = base64_decode($encoded);

        $path = '../img/' . $imageName;

        $file = fopen($path, 'w');

        $is_written = fwrite($file, $decoded_string);
        fclose($file);

        return $is_written > 0;
    }
}
