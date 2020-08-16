<?php


namespace coffee_shop\Exception;


use Exception;

class ConnectionException extends Exception
{
    /**
     * DatabaseException constructor.
     * @param string $message
     * @param int $code
     */
    public function __construct($message, $code = 0)
    {
        parent::__construct($message, $code);
    }
}