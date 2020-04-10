package life.majiang.community.controller;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
public class WebUserRejisterController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/register")
    public String login(@RequestParam(name = "userName") String userName,@RequestParam(name = "userPhone") String userPhone,
                           @RequestParam(name = "userPassword") String userPassword, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setName(userName);
        user.setAccountId("0");
        user.setPhone(userPhone);
        user.setPassword(userPassword);
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        user.setAvatarUrl("/images/4.png");
        user.setBio("webUser");
        List<User> users= userService.checkWebUserExist(user);
        if (users.size() == 0) {
            // 号码没有人注册过
            userMapper.insert(user);
            response.addCookie(new Cookie("token", token));
            request.getSession().setAttribute("user",user);
            return "redirect:/";
        } else {
            // 号码有人注册过
            return "redirect:/register";
        }
   }
}
