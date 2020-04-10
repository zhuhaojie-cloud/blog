package life.majiang.community.controller;

import life.majiang.community.dto.ContactDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.AttentionMapper;
import life.majiang.community.model.AttentionExample;
import life.majiang.community.model.User;
import life.majiang.community.service.AttentionService;
import life.majiang.community.service.ContactService;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by codedrinker on 2019/5/15.
 */
@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AttentionService attentionService;
    @Autowired
    private AttentionMapper attentionMapper;
    @Autowired
    private ContactService contactService;
    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        } else if ("replies".equals(action)) {
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);

           model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
        }else if ("message".equals(action)) {
            List<ContactDTO> contactDTOList = contactService.list(user.getId());
            model.addAttribute("contactDTOList", contactDTOList);
            //model.addAttribute("unreadMessageCount",contactDTOList.size());
            model.addAttribute("section", "message");
            model.addAttribute("sectionName", "最新回复");
        }
        else if ("attention".equals(action)) {
            size=90;
            PaginationDTO paginationDTO = attentionService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "attention");
            model.addAttribute("sectionName", "我的关注");
        }
        else if ("attentioned".equals(action)) {
            size=90;
            PaginationDTO paginationDTO = attentionService.list1(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "attentioned");
            model.addAttribute("sectionName", "我的粉丝");
        }
        String section2="tongzhi";
        model.addAttribute("section2",section2);
        return "profile";
    }
    @GetMapping("/disattention/{otherUserId}")
    public String profile(@PathVariable(name="otherUserId") long otherUserId,HttpServletRequest request){
        //删除
        User user = (User) request.getSession().getAttribute("user");
        AttentionExample attentionExample=new AttentionExample();
        attentionExample.createCriteria().andAttentionUserEqualTo(user.getId())
                .andAttentionedUserEqualTo(otherUserId);
        attentionMapper.deleteByExample(attentionExample);
        return "redirect:/profile/attention/";
    }
}
