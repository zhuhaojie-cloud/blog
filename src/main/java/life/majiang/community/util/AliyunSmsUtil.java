package life.majiang.community.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

public class AliyunSmsUtil {
    /*public static void main(String []args){
        send("18184065263","250131");
    }*/
        public static void send(String phone,String key) {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "你的。。。", "你的。。。");
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();  //组装请求对象
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");//request.setSysDomain("dysmsapi.aliyuncs.com");   报错
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", "zhuhaojie博客");
            request.putQueryParameter("TemplateCode", "模板id");
            request.putQueryParameter("TemplateParam", "{code:"+key+"}");
            try {
                CommonResponse response = client.getCommonResponse(request);
                System.out.println(response.getData());
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
}
