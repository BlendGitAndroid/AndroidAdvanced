package com.blend.architecture.eventbus.bean;

public class RequestBean {
    //    类型 B   UserManager userManager.getInstance()   请求对象的class类型
    //请求单例的全类名
    private String className;
    //    className com.dongn.UserManager
    //类型
    private String resultClassName;
    private String requestObject;
    //返回方法名字
    private String methodName;

    //    参数
    private RequestParameter[] requestParameter;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getResultClassName() {
        return resultClassName;
    }

    public void setResultClassName(String resultClassName) {
        this.resultClassName = resultClassName;
    }

    public String getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(String requestObject) {
        this.requestObject = requestObject;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public RequestParameter[] getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(RequestParameter[] requestParameter) {
        this.requestParameter = requestParameter;
    }
}
