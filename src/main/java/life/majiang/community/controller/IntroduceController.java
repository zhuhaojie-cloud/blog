package life.majiang.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;*/
@Controller
public class IntroduceController {
   // private static final String KEY = "abc123"; // KEY为自定义秘钥
    @GetMapping("/introduce")
    public String blogintro(Model model) {
        String section2="blogintro";
        model.addAttribute("section2",section2);
        return "blogintro";
    }
    @GetMapping("/alyuploadshiyan")
    public String alyuploadshiyan() {
        return "alyuploadshiyan";
    }

    @GetMapping("/shiyan1")
    public String shiyan1() {
        return "shiyan1";
    }
    /*@RequestMapping("/qqLogin")
    public void qqLogin(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
    }
*/
    /**
     * 回调方法
     * @param request
     * @param response
     * @throws Exception
     */
   /* @RequestMapping("/connect")
    public void connect(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken   = null,
                    openID        = null;
            long tokenExpireIn = 0L;
            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                out.println("欢迎你，代号为 " + openID + " 的用户!");
                request.getSession().setAttribute("demo_openid", openID);



                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                out.println("<br/>");
                if (userInfoBean.getRet() == 0) {
                    out.println(userInfoBean.getNickname() + "<br/>");
                    out.println(userInfoBean.getGender() + "<br/>");

                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "><br/>");
                } else {
                    out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }



            }
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
    }
*/
   /* @GetMapping("/introduce")
    public String introduce(@RequestParam(name = "qq") String qq,
                            @RequestParam(name = "text") String text, HttpServletRequest request, HttpServletResponse response) {
        User user=(User)request.getSession().getAttribute("user");
        String title=user.getName()+"发来通知:";
        qq=qq+"@qq.com";
        try {
            SendqqMail.send(qq,text,title);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return "introduce";
    }*/
}
