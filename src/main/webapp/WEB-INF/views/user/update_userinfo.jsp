<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>회원 관리</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
    </head>
    <body>
        <h1>회원관리</h1>
        <hr />

        <form action="${pageContext.request.contextPath}/update" method="POST">
            <table border="0">
                <tbody>
                    <tr>
                        <td>PASSWORD</td>
                        <td><input type="text" name="rpw" size="20"/></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                <center>
                    <input type="submit" value="수정" /> 
                </center>                           
                </td>
                </tr>
                </tbody>
            </table>
        </form>
            <br>
        <form method="post" action="${pageContext.request.contextPath}/delete">
            <input type="submit" value="탈퇴하기">
        </form>
    </body>
</html>