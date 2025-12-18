package util;

import com.world.back.entity.res.Result;

public class ResultHandler
{
  public static <T> Result buildResult(Integer code, String message, T data)
  {
    Result result = new Result();
    result.setCode(code);
    result.setMessage(message);
    result.setData(data);
    return result;
  }
}
