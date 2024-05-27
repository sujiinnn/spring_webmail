<%-- 
    Document   : find_id
    Created on : 2024. 5. 23., 오전 10:38:24
    Author     : 911-16
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>아이디찾기</title>
         <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
    </head>
    <body>
        <h1>아이디찾기</h1>
        <hr />
        
        <form action="${pageContext.request.contextPath}/find" method="POST">
            <table border="0">
                <tbody>                    
                    <tr>
                        <td>NAME</td>
                        <td><input type="text" name="name" size="20"/></td>
                    </tr>
                    <tr>
                        <td>PHONE</td>
                        <td><input type="text" name="phone" size="20"/></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <center>
                                <input type="submit" value="찾기" /> <input type="reset"
                                                                          value="초기화"/>
                            </center>                           
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>