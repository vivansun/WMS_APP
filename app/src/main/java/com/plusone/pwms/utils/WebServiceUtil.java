package com.plusone.pwms.utils;

import android.util.Log;

import com.plusone.pwms.model.WebServicesRequest;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

public class WebServiceUtil {


    public static String call(String httpUrl, String namespace, String methodName, Map<String, Object> params) {

        SoapObject request = new SoapObject(namespace, methodName);

        if (params != null) {
            WebServicesRequest webServicesRequest = new WebServicesRequest();
            if (params.get("userId") != null){
                webServicesRequest.setUserId(Long.parseLong(params.get("userId").toString()));
            }
            if (params.get("whId") != null){
                webServicesRequest.setWhId(Long.parseLong(params.get("whId").toString()));
            }
            if (params.get("pageId") != null && !params.get("pageId").equals("")){
                webServicesRequest.setPageId(params.get("pageId").toString());
            }
            if (params.get("parameters") != null){
                webServicesRequest.setParameters((Map<String, Object>)params.get("parameters"));
            }
            if (methodName != "initMenu"){
                request.addProperty("param", GsonUtil.toJson(webServicesRequest));
            }else {
                request.addProperty("request", GsonUtil.toJson(webServicesRequest));
            }

        }

        String result = null;

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);

        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut

        HttpTransportSE httpTransportSE = new HttpTransportSE(httpUrl,30000);

        //调用
        try {
            httpTransportSE.call(namespace, envelope);
            if (envelope.bodyIn instanceof SoapFault) {
                Log.d("===========",envelope.getResponse().toString());
            }else {
                SoapObject soapObject = (SoapObject) envelope.bodyIn;
                result = soapObject.getProperty(0).toString();
                Log.d("*****************",result);
            }
        } catch (SoapFault e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof java.net.SocketTimeoutException) {
                return "{\"status\":\"E\",\"message\":\"连接服务器超时,请检查网络\"}";
            } else {
                return "{\"status\":\"E\",\"message\":\"系统异常\"}";
            }
        } catch (XmlPullParserException e) {
            //一般情况下是不会产生的，只有在连续访问WebService时，或网络不流畅的情况下亦或者不同的手机类型中抛出该异常.
            e.printStackTrace();
            return "{\"status\":\"E\",\"message\":\"XML Pull解析异常\"}";
        }

        // 获取返回的数据
//        SoapObject object = (SoapObject) envelope.bodyIn;


        // 获取返回的结果
//        String result = object.toString();

        return result;
    }
}
