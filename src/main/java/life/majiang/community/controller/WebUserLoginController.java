package life.majiang.community.controller;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
@Controller
public class WebUserLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/login")
    public String login(@RequestParam(name = "userPhone") String userPhone,
                           @RequestParam(name = "userPassword") String userPassword, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setPhone(userPhone);
        user.setPassword(userPassword);
        List<User> users= userService.checkWebUserExist(user);
        if (users.size() == 0) {
            // 登录失败，重新登录
            return "redirect:/userlogin";
        } else {
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());

            UserExample userExample1= new UserExample();
            userExample1.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, userExample1);
            response.addCookie(new Cookie("token", token));
            request.getSession().setAttribute("user", dbUser);
            HttpSession session=request.getSession();
            System.out.println(session.getAttribute("user"));
            return "redirect:/";
        }
   }
    @GetMapping("/smslogin/{userPhone}")
    public String smslogin(@PathVariable(name = "userPhone") String userPhone, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setPhone(userPhone);
       //更新
        UserExample userExample=new UserExample();
        userExample.createCriteria().andPhoneEqualTo(userPhone);
        List<User> users=userMapper.selectByExample(userExample);
         User dbUser = users.get(0);
         User updateUser = new User();
         updateUser=dbUser;
         updateUser.setGmtModified(System.currentTimeMillis());
         UserExample userExample1= new UserExample();
         userExample1.createCriteria()
                 .andIdEqualTo(updateUser.getId());
         userMapper.updateByExampleSelective(updateUser, userExample1);
         response.addCookie(new Cookie("token", token));
         request.getSession().setAttribute("user", updateUser);
         return "redirect:/";
    }
}
