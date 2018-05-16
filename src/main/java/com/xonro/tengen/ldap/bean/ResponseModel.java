package com.xonro.tengen.ldap.bean;

/**
 * 请求响应模型
 * @author louie
 * @date 2018-2-1
 */
public class ResponseModel {
    public ResponseModel(boolean result,String message){
        this.result = result;
        this.message = message;
    }

    /**
     * 请求处理结果，true：成功  false：失败
     */
    private boolean result;

    /**
     * 响应信息
     */
    private String message;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
