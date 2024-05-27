/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author sujin
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InboxId implements Serializable {

    @Column(name = "message_name")
    private String messageName;

    @Column(name = "repository_name")
    private String repositoryName;
}