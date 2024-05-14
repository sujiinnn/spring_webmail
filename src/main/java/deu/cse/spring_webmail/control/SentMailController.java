package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.Pop3Agent;
import deu.cse.spring_webmail.model.SentMail;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

@Controller
@PropertySource("classpath:/system.properties")
@PropertySource("classpath:/config.properties")
@Slf4j
public class SentMailController {

    @Autowired
    private ServletContext ctx;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Value("${file.download_folder}")
    private String DOWNLOAD_FOLDER;

    @Autowired private Pop3Agent pop3Agent;
    @Autowired private SentMail sentMail;

    // 보낸 메일함 목록
    @GetMapping("/sent_mail")
    public String sentMail(Model model){
        System.out.println("sentMail()");
//        Pop3Agent pop3 = new Pop3Agent();
//        pop3.setHost((String) session.getAttribute("host"));
//        pop3.setUserid((String) session.getAttribute("userid"));
//        pop3.setPassword((String) session.getAttribute("password"));

        sentMail.setHost((String) session.getAttribute("host"));
        sentMail.setUserid((String) session.getAttribute("userid"));
        sentMail.setPassword((String) session.getAttribute("password"));


        String sentmessageList = sentMail.getSentMessageList();
        model.addAttribute("sentmessageList", sentmessageList);

        return "sent_mail/sent_mail";

    }

    // 보낸 메일 선택 시 메일 상세 내용 확인
    @GetMapping("/show_sent_message")
    public String showSentMessage(@RequestParam Integer msgid, Model model) throws Exception {
        log.debug("download_folder = {}", DOWNLOAD_FOLDER);
        System.out.println("showSentMessage()");

        sentMail.setHost((String) session.getAttribute("host"));
        sentMail.setUserid((String) session.getAttribute("userid"));
        sentMail.setPassword((String) session.getAttribute("password"));
        sentMail.setRequest(request);

        String msg = sentMail.getSentMessage(msgid);
        session.setAttribute("sender", sentMail.getSender());  // 220612 LJM - added
        session.setAttribute("subject", sentMail.getSubject());
        session.setAttribute("body", sentMail.getBody());
        model.addAttribute("msg", msg);

        return "sent_mail/show_sent_message";
    }
}
