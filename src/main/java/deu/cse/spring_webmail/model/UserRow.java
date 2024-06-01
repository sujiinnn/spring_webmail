/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author USER
 */
@AllArgsConstructor
@Builder
public class UserRow {
    @Getter
    private String rid;
    @Getter
    private String rpw;
    @Getter
    private String name;
    @Getter
    private String phone;
}
