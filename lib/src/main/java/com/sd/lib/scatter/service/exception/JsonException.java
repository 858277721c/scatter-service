package com.sd.lib.scatter.service.exception;

public class JsonException extends Exception
{
    public JsonException()
    {
    }

    public JsonException(String message)
    {
        super(message);
    }

    public JsonException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public JsonException(Throwable cause)
    {
        super(cause);
    }
}
