<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
    <title>Awem Video Localization Table Updater</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles.css">
</head>
<body>
<img src="${pageContext.request.contextPath}/resources/awem_logo.png" width="100">
<div class="header"><h2>Video Localization Table Updater</h2></div>
<div class="lastUpdateTime">Last update time: ${requestScope.lastUpdateTime}</div>
<br/>
    <form action="" method="post" name="updateForm">
        <input type="hidden" name="runUpdate" value="yes">
        <button type="submit" class="submit" <c:if test="${requestScope.lockUpdateButton == 'true'}">disabled>Please, wait 5 minutes</c:if><c:if test="${requestScope.lockUpdateButton != 'true'}">>Update table</c:if></button>
    </form>
</br/>
<div class="lastUpdateTime">${executionTime}</div>
</body>
</html>
