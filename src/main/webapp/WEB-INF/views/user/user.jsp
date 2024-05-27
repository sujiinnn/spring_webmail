<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:eval expression="@environment.getProperty('spring.datasource.driver-class-name')" var="db_driver" />
<spring:eval expression="@environment.getProperty('spring.datasource.username')" var="db_username" />
<spring:eval expression="@environment.getProperty('spring.datasource.password')" var="db_password" />

<spring:eval expression="@ConfigProperties['mysql.server.ip']" var="mysqlServerIp" />
<spring:eval expression="@ConfigProperties['mysql.server.port']" var="mysqlServerPort" />

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원 정보 보기</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
    </head>
    <body>
        <h1>회원 정보</h1>
        <hr />

        <%
        final String JDBC_DRIVER = (String) pageContext.getAttribute("db_driver");
        final String mysqlServerIp = (String) pageContext.getAttribute("mysqlServerIp");
        final String mysqlServerPort = (String) pageContext.getAttribute("mysqlServerPort");
        final String JDBC_URL = String.format("jdbc:mysql://%s:%s/mail?serverTimezone=Asia/Seoul", mysqlServerIp, mysqlServerPort);
        final String USER = (String) pageContext.getAttribute("db_username");
        final String PASSWORD = (String) pageContext.getAttribute("db_password");
        final String ID = (String) session.getAttribute("userid");

        try {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            // 드라이버 적재
            Class.forName(JDBC_DRIVER);
            // db 연결
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            // statement 생성
            stmt = conn.createStatement();
            // SQL 질의 실행
            
            String sql = "SELECT username, name, password, phone FROM users WHERE username='" + ID + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
        %>
<!--        <form action="${pageContext.request.contextPath}/user" method="post">
            아이디: <input type="text" name="username" value="<%= rs.getString("username") %>" readonly><br>
            이름: <input type="text" name="name" value="<%= rs.getString("name") %>" readonly><br>
            비밀번호: <input type="password" name="password" value="<%= rs.getString("password") %>"><br>
            전화번호: <input type="text" name="phone" value="<%= rs.getString("phone") %>"><br>
            <input type="submit" value="정보 수정">
        </form>-->
        <%
        } else {
            out.println("회원정보를 찾을 수 없음");
        }
        rs.close();
        stmt.close();
        conn.close();
        
        if (request.getParameter("name") != null && request.getParameter("phone") != null && request.getParameter("password") != null && request.getParameter("username") != null) {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String username = request.getParameter("username");

            try {
                conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                String updateSql = "UPDATE users SET name = ?, phone = ?, password = ?, enabled = ? WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                pstmt.setString(3, encoder.encode(password));
                
                pstmt.setString(4, "1");
                pstmt.setString(5, username);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    out.println("회원 정보가 성공적으로 수정되었습니다.");
                } else {
                    out.println("회원 정보를 수정하는데 문제가 발생했습니다.");
                }

                pstmt.close();
                conn.close();
            } catch (Exception ex) {
                out.println("오류가 발생했습니다. (발생 오류: " + ex.getMessage() + ")");
            }
        }

        } catch (Exception ex) {
            out.println("오류가 발생했습니다. (발생 오류: " + ex.getMessage() + ")");
        }
        %>
        <br><br>
    </body>
</html>
