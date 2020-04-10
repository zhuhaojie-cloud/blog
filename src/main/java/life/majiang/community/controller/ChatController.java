package life.majiang.community.controller;

import life.majiang.community.dto.CollectDTO;
import life.majiang.community.dto.ContactDTO;
import life.majiang.community.mapper.ContactMapper;
import life.majiang.community.mapper.MessageMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import life.majiang.community.service.ContactService;
import life.majiang.community.websocket.WebSocketOneToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private WebSocketOneToOne webSocketOneToOne;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private ContactService contactService;
    /**
     * 登录界面直接聊天
     * **/
    @RequestMapping("/chat/{otheruserId}")
    public String chat(@PathVariable(name = "otheruserId") Long otheruserId, HttpSession session, Model model,HttpServletRequest request){
        //查询用户聊天历史纪录,只获取12条
        User user= (User)request.getSession().getAttribute("user");
        User otherUser = userMapper.selectByPrimaryKey(otheruserId);
        //查询user1和user2contanct表中有无，交替查询
        //有则不管
        //无则新建contact
        Long contactId= contactService.checkContact(otheruserId,user.getId());
        /**/
        //把对话用户发的消息改成已读更新成已读
        List<Message> messages2=new ArrayList<>();
        MessageExample messageExample2=new MessageExample();
        messageExample2.createCriteria().andContactIdEqualTo(contactId);
        messages2=messageMapper.selectByExample(messageExample2);
        for (Message message:messages2){
            if(message.getTouser().longValue()==user.getId().longValue()&&message.getReadflag()==0){
                message.setReadflag(1);
                MessageExample messageExample= new MessageExample();
                messageExample.createCriteria()
                        .andIdEqualTo(message.getId());
                messageMapper.updateByExampleSelective(message, messageExample);
            }
        }
        //查询contact表中所有user1和user2,看是否有user，整合，再按时间排序
        //user1
        ContactExample example1 = new ContactExample();
        example1.createCriteria()
                .andUser1EqualTo(user.getId());
        List<Contact> contacts1= contactMapper.selectByExample(example1);
        //user2
        ContactExample example2 = new ContactExample();
        example2.createCriteria()
                .andUser2EqualTo(user.getId());
        List<Contact> contacts2= contactMapper.selectByExample(example2);
        //整合
        contacts1.addAll(contacts2);
        //按时间排顺序
        Collections.sort(contacts1, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                Long gmt1 = contact1.getGmt();
                Long gmt2 = contact2.getGmt();
                //可以按Question对象的其他属性排序，只要属性支持compareTo方法
                return gmt2.compareTo(gmt1);
            }
        });
        //找到不是user的touser
        List<ContactDTO> contactDTOList = new ArrayList<>();
        for (Contact c : contacts1) {
            User touser =new User();
            if(c.getUser1().longValue()==user.getId().longValue()){
                touser = userMapper.selectByPrimaryKey(c.getUser2());
            }else{
                touser = userMapper.selectByPrimaryKey(c.getUser1());
            }
            //获取其它联系人的未读条数
            int flagreadnumber = 0;
            if(c.getId().longValue()!=contactId.longValue()) {
                List<Message> messages1 = new ArrayList<>();
                MessageExample messageExample1 = new MessageExample();
                messageExample1.createCriteria().andContactIdEqualTo(c.getId());
                messages1 = messageMapper.selectByExample(messageExample1);
                for (Message message : messages1) {
                    if (message.getFromuser().longValue() != user.getId().longValue() && message.getReadflag() == 0) {
                        flagreadnumber++;
                    }
                }
            }
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setFlagreadnumber(flagreadnumber);
            contactDTO.setId(c.getId());
            contactDTO.setGmt(c.getGmt());
            contactDTO.setUser1(user);
            contactDTO.setUser2(touser);
           // contactDTO.setMessages(messages);
            contactDTOList.add(contactDTO);
        }

        //得到当前对话的message
        List<Message> messages=new ArrayList<>();
        MessageExample messageExample=new MessageExample();
        messageExample.createCriteria().andContactIdEqualTo(contactId);
        messages=messageMapper.selectByExample(messageExample);
        contactService.readMeassge(request,user.getId());
        model.addAttribute("messages",messages);
        model.addAttribute("contactDTOList",contactDTOList);
        model.addAttribute("otheruserId",otheruserId);
        model.addAttribute("otherUser",otherUser);
        return "chat";
    }
    /**
     * 发送消息 消息内容,发送人,接收人,会话标识
     * **/
    @RequestMapping("message")
    public void message(String msg,String from,String to,String socketId){
        //写入message到数据库
        System.out.println(from+"aaa"+to);
        webSocketOneToOne.send(msg, from, to, socketId);
    }
}