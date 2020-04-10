package life.majiang.community.service;

import life.majiang.community.dto.ContactDTO;
import life.majiang.community.mapper.ContactMapper;
import life.majiang.community.mapper.MessageMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    public Long checkContact(Long otheruserId, Long id) {
        ContactExample example = new ContactExample();
        example.createCriteria()
                .andUser2EqualTo(otheruserId)
                .andUser1EqualTo(id);
        List<Contact> contact= contactMapper.selectByExample(example);
        if(contact.size()==0){
            ContactExample example1 = new ContactExample();
            example1.createCriteria()
                    .andUser2EqualTo(id)
                    .andUser1EqualTo(otheruserId);
            contact= contactMapper.selectByExample(example1);
        }
        if (contact.size()==0){
            //添加
            Contact contact1=new Contact();
            contact1.setUser1(id);
            contact1.setUser2(otheruserId);
            contact1.setGmt(System.currentTimeMillis());
            contactMapper.insert(contact1);
            ContactExample example1 = new ContactExample();
            example1.createCriteria()
                    .andUser1EqualTo(id)
                    .andUser2EqualTo(otheruserId);
            contact= contactMapper.selectByExample(example1);
        }else{
            //如果有更新时间
            contact.get(0).setGmt(System.currentTimeMillis());
            ContactExample contactExample= new ContactExample();
            contactExample.createCriteria()
                    .andIdEqualTo(contact.get(0).getId());
            contactMapper.updateByExampleSelective(contact.get(0), contactExample);
        }
        return contact.get(0).getId();
    }
    public List<ContactDTO> list(Long id) {
        //首先根据userId查询是否有纪录
        //再用纪录id去message里查询哪些flagread
        //user1
        ContactExample example1 = new ContactExample();
        example1.createCriteria()
                .andUser1EqualTo(id);
        List<Contact> contacts1= contactMapper.selectByExample(example1);
        //user2
        ContactExample example2 = new ContactExample();
        example2.createCriteria()
                .andUser2EqualTo(id);
        List<Contact> contacts2= contactMapper.selectByExample(example2);
        //整合
        contacts1.addAll(contacts2);
        List<ContactDTO> contactDTOList = new ArrayList<>();
        for (Contact c : contacts1) {
            User fromuser = new User();//发消息的人
            //User touser = new User();
            if (c.getUser1().longValue() == id.longValue()) {
                fromuser = userMapper.selectByPrimaryKey(c.getUser2());
            } else {
                fromuser = userMapper.selectByPrimaryKey(c.getUser1());
            }
            //获取其它联系人的未读条数
            int flagreadnumber = 0;
            List<Message> messages1 = new ArrayList<>();
            MessageExample messageExample1 = new MessageExample();
            messageExample1.createCriteria().andContactIdEqualTo(c.getId());
            messages1 = messageMapper.selectByExample(messageExample1);
            if(messages1.size()!=0){
                for (Message message : messages1) {
                    if (message.getTouser().longValue() == id.longValue() && message.getReadflag() == 0) {
                        flagreadnumber++;
                    }
                }
                ContactDTO contactDTO = new ContactDTO();
                contactDTO.setFlagreadnumber(flagreadnumber);
                contactDTO.setId(c.getId());
                contactDTO.setGmt(c.getGmt());
                contactDTO.setUser1(fromuser);
                contactDTOList.add(contactDTO);
            }
        }
        return contactDTOList;
    }
    //未读数
    public int unreadMessageCount(Long id) {
        int i = 0;
        List<ContactDTO> contactDTOS=new ArrayList<>();
        contactDTOS=list(id);
        for(ContactDTO contactDTO:contactDTOS){
            if (contactDTO.getFlagreadnumber()!=0){
                i++;
            }
        }
        return i;
    }
    //更新界面显示用户已读
    public void readMeassge(HttpServletRequest request,Long id) {
        unreadMessageCount(id);
        HttpSession session = request.getSession();
        session.setAttribute("unreadMessageCount",  unreadMessageCount(id));
    }
}
