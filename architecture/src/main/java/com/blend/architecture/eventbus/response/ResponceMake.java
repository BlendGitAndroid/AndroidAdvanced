package com.blend.architecture.eventbus.response;

import com.blend.architecture.eventbus.Request;
import com.blend.architecture.eventbus.Responce;
import com.blend.architecture.eventbus.bean.RequestBean;
import com.blend.architecture.eventbus.bean.RequestParameter;
import com.blend.architecture.eventbus.core.ObjectCenter;
import com.blend.architecture.eventbus.core.TypeCenter;
import com.google.gson.Gson;

public abstract class ResponceMake {
    //UserManage  的Class
    protected Class<?> reslutClass;
    // getInstance()  参数数组
    protected Object[] mParameters;

    Gson GSON = new Gson();

    protected TypeCenter typeCenter = TypeCenter.getInstance();
//    怎么生成单例对象
// getInstance()  参数数组
//UserManage  的Class

    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();


    protected abstract Object invokeMethod();

    protected abstract void setMethod(RequestBean requestBean);

    public Responce makeResponce(Request request) {

        //取出request中的RequestBean消息
        RequestBean requestBean = GSON.fromJson(request.getData(), RequestBean.class);

        //通过requestBean中设置的目标单例类的名字去加载类
        reslutClass = typeCenter.getClassType(requestBean.getResultClassName());

        //组装参数，将参数进行还原组装
        RequestParameter[] requestParameters = requestBean.getRequestParameter();
        if (requestParameters != null && requestParameters.length > 0) {
            mParameters = new Object[requestParameters.length];
            for (int i = 0; i < requestParameters.length; i++) {
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                mParameters[i] = GSON.fromJson(requestParameter.getParameterValue(), clazz);
            }
        } else {
            mParameters = new Object[0];
        }

        //通过requestBean中设置的方法名获取到要执行的具体方法
        setMethod(requestBean);

        //执行方法，并得到方法结果
        Object resultObject = invokeMethod();

        //将执行结果封装成response返回给B
        ResponceBean responceBean = new ResponceBean(resultObject);
        String data = GSON.toJson(responceBean);
        Responce responce = new Responce(data);
        return responce;
    }
}
