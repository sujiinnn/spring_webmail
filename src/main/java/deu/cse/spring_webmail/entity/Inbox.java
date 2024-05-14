/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDateTime;

import lombok.*;

/**
 *
 * @author sujin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "inbox")
@IdClass(InboxId.class)
public class Inbox implements Serializable {

    @Id
    @Column(name = "message_name")
    private String messageName;

    @Id
    @Column(name = "repository_name")
    private String repositoryName;


    @Column(name = "message_state")
    private String messageState;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sender")
    private String sender;

    @Column(name = "recipients")
    private String recipients;

    @Column(name = "remote_host")
    private String remoteHost;

    @Column(name = "remote_addr")
    private String remoteAddr;

    @Lob
    @Column(name = "message_body")
    private Blob messageBody;

    @Lob
    @Column(name = "message_attributes")
    private Blob messageAttributes;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;


}

