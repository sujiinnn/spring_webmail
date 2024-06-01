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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author USER
 */
@Slf4j
public class UserManager {
    private HttpSession session;
    private String mysqlServerIp;
    private String mysqlServerPort;
    private String userName;
    private String password;
    private String jdbcDriver;
    private String ID;
    public UserManager() {
        log.debug("RegistarManager(): mysqlServerIp = {}, jdbcDriver = {}", mysqlServerIp, jdbcDriver);

    }

    public UserManager(String mysqlServerIp, String mysqlServerPort, String userName, String password,
            String jdbcDriver, HttpSession session) {
        this.session =session;
        this.mysqlServerIp = mysqlServerIp;
        this.mysqlServerPort = mysqlServerPort;
        this.userName = userName;
        this.password = password;
        this.jdbcDriver = jdbcDriver;
        this.ID = (String) session.getAttribute("userid");
        log.debug("RegistarManager(): mysqlServerIp = {}, jdbcDriver = {}", mysqlServerIp, jdbcDriver);
    }

    public List<UserRow> getAllRows(String username) {
        List<UserRow> dataList = new ArrayList<>();
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul",
                this.mysqlServerIp, this.mysqlServerPort);
        log.debug("JDBC_URL= {}, mysqlServerIp = {}, jdbcDriver = {}", JDBC_URL, mysqlServerIp, jdbcDriver);
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(JDBC_URL, userName, password);
            stmt = conn.createStatement();
            String sql = "SELECT username, name, pwdHash, phone FROM users WHERE username='" + ID + "'";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String rid = rs.getString("username");
                String rpw = rs.getString("pwdHash");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                dataList.add(new UserRow(rid, rpw, name, phone));
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex) {
            log.error("오류가 발생했습니다. (발생 오류: {})", ex.getMessage());
        }

        return dataList;
    }

    public void addRow(String rid, String rpw, String name, String phone) {
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul",
                mysqlServerIp, mysqlServerPort);

        log.debug("JDBC_URL = {}", JDBC_URL);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(JDBC_URL, this.userName, this.password);
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, passwordEncoder().encode(rpw));
            pstmt.setString(2, rid); // username이 rid인 레코드를 식별하기 위해 rid 값을 설정

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void deleteRow(String rid, String rpw, String name, String phone) {
    }
}