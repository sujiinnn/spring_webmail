/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

/**
 *
 * @author 911-16
 */
@AllArgsConstructor
@Builder
public class FindRow {
    @Getter
    private String username;
    @Getter
    private String name;
    @Getter
    private String phone;
}
