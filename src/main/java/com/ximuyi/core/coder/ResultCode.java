package com.ximuyi.core.coder;

import java.util.Objects;

public class ResultCode {

    public static final ResultCode SUCCESS = new ResultCode((short)0);
    public static final ResultCode UNKNOWN = new ResultCode((short)-1);
    public static final ResultCode INVALID = new ResultCode((short)-2);
    public static final ResultCode ENCODE_EXCEPTION = new ResultCode((short)-3);
    public static final ResultCode LOGIN_ALREADY = new ResultCode((short)-4);
    public static final ResultCode SEVER_EXCEPTION = new ResultCode((short)-5);
    public static final ResultCode RETRY_LOGIN = new ResultCode((short)-6);
    public static final ResultCode AKKA_DEATH = new ResultCode((short)-7);

    private final short resultCode;
    private String message;

    private ResultCode(short resultCode) {
       this(resultCode, "error code:" + resultCode);
    }

    private ResultCode(short resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public short code() {
        return resultCode;
    }

    public boolean isSuccess(){
        return resultCode == SUCCESS.resultCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultCode that = (ResultCode) o;
        return resultCode == that.resultCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultCode);
    }

    @Override
    public String toString() {
        return String.valueOf(resultCode);
    }

    /**
     * 其他业务自生成自己的结果码
     * @param resultCode
     * @param message
     * @return
     */
    public static ResultCode customize(short resultCode, String message){
        if (resultCode <= 0){
            throw new UnsupportedOperationException("the value must be greater than 0, current is " + resultCode);
        }
        return new ResultCode(resultCode, message);
    }
}
