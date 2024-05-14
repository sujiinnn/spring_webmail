<%-- 
    Document   : show_sent_messaage
    Created on : 2024. 5. 13., 오전 2:07:54
    Author     : sujin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>메일 보기 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="../header.jspf"%>

        <div id="sidebar">
            <jsp:include page="sidebar_sent_menu.jsp" />
        </div>

        <div id="msgBody">
            ${msg}
        </div>

        <%@include file="../footer.jspf"%>
    </body>
</html>
