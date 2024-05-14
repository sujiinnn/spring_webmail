<%--
    Document   : addr_add.jsp
    Created on : 2024. 5. 11., 오후 2:56:41
    Author     : guym3
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib tagdir="/WEB-INF/tags/addrbook" prefix="mytags"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@environment.getProperty('spring.datasource.username')" var="db_username" />
<spring:eval expression="@environment.getProperty('spring.datasource.password')" var="db_password" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소 추가</title>
        <link rel="stylesheet" type="text/css" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="/WEB-INF/views/header.jspf"%> 
        <script>
            function tbValue(){
                var table = document.getElementById("addrbook");
                var data = [];
                for (var i = 1; i < table.rows.length; i++){
                    var row = table.rows[i];
                    var rowData = [];
                    
                    rowData.push(row.cells[0].innerHTML);   
                    
                    data.push(rowData);
                }
                return data;
            }
            
            function validateForm() {
                var username = document.forms["AddAddr"]["username"].value;
                var addrname = document.forms["AddAddr"]["addrname"].value;
                var tableValue = tbValue();
                if (username === addrname) {
                    alert("본인은 주소록에 추가할 수 없습니다!");
                    return false; // 폼 제출을 막음
                }
                else if(!addrname){
                    alert("입력이 비어있습니다!")
                    return false;
                }
                else if(tableValue.flat().includes(addrname)) {
                    alert("이미 존재하는 주소입니다!")
                    return false;
                }
                // 폼 제출을 허용
                return true;
            }
        </script>
        <div id="main">
            <p style="color: indigo">주소록에 추가할 사용자 ID를 입력해 주시기 바랍니다. <p>
            <p style="color:grey"> ※ 존재하지 않는 사용자나 중복된 사용자는 추가 불가!! <p>
            <form name="AddAddr" action="add_addr.do" method="POST" onsubmit="return validateForm()">
                <input type ="hidden" name="username" value="<%=(String)session.getAttribute("userid")%>">
                <table style="border: 1px; text-align: left;">
                    <tr>
                        <td>사용자 ID</td>
                        <td> <input type="text" name="addrname" value="" size="20" />  </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="추가" name="add_addr" />
                            <input type="reset" value="초기화" name="reset" />
                        </td>
                    </tr>
                </table>
            </form>
            <p style="color: indigo"> 현재 <%= session.getAttribute("userid") %>님의 주소록 <p>
                <c:catch var="errorReason">
                    <mytags:addrbook user="${db_username}" password="${db_password}" schema="mail" table="addrbook" username='<%=(String)session.getAttribute("userid")%>'/>
                </c:catch>
                ${empty errorReason ? "<noerror/>" : errorReason}
            <p> <a href="addrbook"> 이전 메뉴로 </a> <p>
        </div>
        <%@include file="/WEB-INF/views/footer.jspf"%>
    </body>
</html>
