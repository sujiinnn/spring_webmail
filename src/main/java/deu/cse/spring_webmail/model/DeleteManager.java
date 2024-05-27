/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author user
 */
@Slf4j
public class DeleteManager {

    private String mysqlServerIp;
    private String mysqlServerPort;
    private String userName;
    private String password;
    private String jdbcDriver;

    public DeleteManager() {
        log.debug("RegistarManager(): mysqlServerIp = {}, jdbcDriver = {}", mysqlServerIp, jdbcDriver);

    }

    public DeleteManager(String mysqlServerIp, String mysqlServerPort, String userName, String password,
            String jdbcDriver) {
        this.mysqlServerIp = mysqlServerIp;
        this.mysqlServerPort = mysqlServerPort;
        this.userName = userName;
        this.password = password;
        this.jdbcDriver = jdbcDriver;
        log.debug("RegistarManager(): mysqlServerIp = {}, jdbcDriver = {}", mysqlServerIp, jdbcDriver);
    }

    public void deleteRow(String[] userList) {
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul",
                mysqlServerIp, mysqlServerPort);

        log.debug("JDBC_URL = {}", JDBC_URL);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        for (String userId : userList) {
            try {
                Class.forName(jdbcDriver);
                conn = DriverManager.getConnection(JDBC_URL, this.userName, this.password);
                String sql = "DELETE FROM authorities WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, userId); 

                pstmt.executeUpdate();

                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                log.error("오류가 발생했습니다. (발생 오류: {})", ex.getMessage());
            }
        }
    }
}
