package life.majiang.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("userlogin")
    public String login(Model model){
        String section2="login";
        model.addAttribute("section2",section2);
        return  "login";
    }

    @GetMapping("userregister")
    public String register(Model model){
        String section2="login";
        model.addAttribute("section2",section2);
        return  "register";
    }

    @GetMapping("hello2")
    public String hello2(){
        return  "introduce";
    }
}
