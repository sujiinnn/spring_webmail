/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author guym3
 */
@Slf4j
public class AddrBookManager {
    private String mysqlServerIp;
    private String mysqlServerPort;
    private String dbUser;
    private String dbPw;
    private String jdbcDriver;

    public AddrBookManager() {
        log.debug("AddrBookManager(): myServerIp = {}, myServerPort = {}, jdbcDriver = {}", mysqlServerIp, mysqlServerPort, jdbcDriver);
    }

    public AddrBookManager(String mysqlServerIp, String mysqlServerPort, String dbUser, String dbPw,
            String jdbcDriver) {
        this.mysqlServerIp = mysqlServerIp;
        this.mysqlServerPort = mysqlServerPort;
        this.dbUser = dbUser;
        this.dbPw = dbPw;
        this.jdbcDriver = jdbcDriver;
        log.debug("AddrBookManager(): myServerIp = {}, myServerPort = {}, jdbcDriver = {}", mysqlServerIp, mysqlServerPort, jdbcDriver);
    }

    public List<AddrBookRow> getAllRows(String username) {
        List<AddrBookRow> dataList = new ArrayList<>();
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul",
                this.mysqlServerIp, this.mysqlServerPort);
        
        log.debug("JDBC_URL = {}, myServerIp = {}, myServerPort = {}, jdbcDriver = {}", JDBC_URL, mysqlServerIp, mysqlServerPort, jdbcDriver);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            //드라이버 적재
            Class.forName(jdbcDriver);
            //db 연결
            conn = DriverManager.getConnection(JDBC_URL, dbUser, dbPw);
            // prepared statement 생성
            String sql = "SELECT addrname, name, concat(substr(phone, 1, 3), '-', substr(phone, 4, 4), '-', substr(phone, 8, 4)) \"phone\" FROM users u JOIN addrbook a ON a.addrname=u.username WHERE a.username='?'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            //query 실행
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String addrname = rs.getString("addrname");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                dataList.add(new AddrBookRow(username, addrname, name, phone));
            }
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex) {
            log.error("오류가 발생했습니다. (발생 오류: {})", ex.getMessage());
        }
        return dataList;
    }

    public void addRow(String username, String addrname) {
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul",
                mysqlServerIp, mysqlServerPort);
        
        log.debug("JDBC_URL = {}", JDBC_URL);
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(JDBC_URL, this.dbUser, this.dbPw);
            
            String sql = "INSERT into addrbook(username, addrname) VALUES(?,?)";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, username);
            pstmt.setString(2, addrname);
            
            pstmt.executeUpdate();
            
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch(Exception ex){
            log.error("오류가 발생했습니다. (발생 오류: {})", ex.getMessage());
        }
    }
    
    public void delRow(String username, String addrname) {
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul",
                mysqlServerIp, mysqlServerPort);
        
        log.debug("JDBC_URL = {}", JDBC_URL);
        
        Connection conn = null;
        
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(JDBC_URL, this.dbUser, this.dbPw);
            
            String sql = "DELETE FROM addrbook WHERE username=? AND addruser=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, username);
            pstmt.setString(2, addrname);
            
            pstmt.executeUpdate();
            
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch(Exception ex){
            log.error("오류가 발생했습니다. (발생 오류: {})", ex.getMessage());
        }
    }
}
