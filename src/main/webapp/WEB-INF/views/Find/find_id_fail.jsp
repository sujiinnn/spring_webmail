<%-- 
    Document   : index
    Created on : 2024. 5. 3., 오전 9:41:42
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>아이디찾기 실패</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
    </head>
    <body>
        <h1>아이디찾기 실패</h1>
        <hr/>
        "${name}"님 입력하신 정보와 일치하는 아이디가 없습니다.
        <br><br>

        <a href="${pageContext.request.contextPath}/find_id">초기 화면</a> &emsp;&emsp;
    </body>
</html>