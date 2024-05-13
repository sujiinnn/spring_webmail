<%-- 
    Document   : addrbar_menu.jsp
    Created on : 2024. 5. 11., 오후 2:53:57
    Author     : guym3
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록 메뉴</title>
    </head>
    <body>
        <br> <br>
        
        <span style="color: indigo"> <strong>사용자: <%= session.getAttribute("userid") %> </strong> </span> <br>
        
        <p> <a href="add_addr"> 주소 추가 </a> <p>
        <p> <a href="main_menu"> 이전 메뉴로 </a> <p>
    </body>
</html>
