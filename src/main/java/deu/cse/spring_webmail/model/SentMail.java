package deu.cse.spring_webmail.model;


import deu.cse.spring_webmail.entity.Inbox;
import deu.cse.spring_webmail.repository.InboxRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Service
public class SentMail {

    @Getter @Setter private String host;
    @Getter @Setter private String userid;
    @Getter @Setter private String password;
    @Getter @Setter private Store store;
    @Getter @Setter private String excveptionType;
    @Getter @Setter private HttpServletRequest request;

    // 220612 LJM - added to implement REPLY
    @Getter private String sender;
    @Getter private String subject;
    @Getter private String body;

    @Autowired
    InboxRepository inboxRepository;
    @Autowired
    MessageFormatter formatter;

    @Autowired
    public SentMail(InboxRepository inboxRepository, MessageFormatter formatter) {
        this.inboxRepository = inboxRepository;
        this.formatter = formatter;
    }

    // 보낸 메일 리스트 가져오는 함수
    public String getSentMessageList() {

        if (userid == null) {
            log.error("userid is null!");
            return "사용자 ID가 설정되지 않았습니다.";
        } else {
            formatter.setUserid(userid);
        }

        String result = "";
        Message[] messages = null;
        String sender = String.format("%s@localhost", userid);

        try {

            List<Inbox> sentInboxList = inboxRepository.findBySender(sender);
            messages = new Message[sentInboxList.size()];

            for (int i = 0; i < sentInboxList.size(); i++) {
                Inbox testInbox = sentInboxList.get(i);
                Session session = Session.getDefaultInstance(new Properties());
                messages[i] = new MimeMessage(session, testInbox.getMessageBody().getBinaryStream());
            }

            formatter.setSender(sender);
            formatter.setRequest(request);
            result = formatter.getSentMessageTable(messages);

        } catch (Exception ex) {
            log.error("SentMail.getSentMessageList() : exception = {}", ex.getMessage());
            result = "SentMail.getSentMessageList() : exception = " + ex.getMessage();
        }

        return result;

    }



    // 보낸 메일의 내용을 가져오는 함수
    public String getSentMessage(int n) {

        String result = "";
        String sender = String.format("%s@localhost", userid);


        try {
            List<Inbox> sentInboxList = inboxRepository.findBySender(sender);
            if (sentInboxList == null || sentInboxList.isEmpty()) {
                return result;
            }

            Inbox sentInbox = sentInboxList.get(n-1);
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session, sentInbox.getMessageBody().getBinaryStream());

            InternetAddress[] toAddresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
            InternetAddress[] ccAddresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);

            // MessageFormatter 인스턴스 초기화
            if (this.formatter == null) {
                this.formatter = new MessageFormatter();
            }

            formatter.setSender(sender);
            formatter.setRequest(request);
            result = formatter.getSentMessageContent(message);

            MessageParser parser = new MessageParser(message, userid, request);
            parser.parse(true);

        } catch (Exception ex) {
            log.error("SentMail.getSentMessage() : exception = {}", ex.getMessage());
            result = "SentMail.getSentMessage() : exception = " + ex.getMessage();
        } finally {
            return result;
        }
    }
}
