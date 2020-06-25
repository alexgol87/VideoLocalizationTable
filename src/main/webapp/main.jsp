<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
    <title>Creatives Tables Updater</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/styles.css">
</head>
<body>
<div class="header"><h2>Video Localization Table Updater</h2></div>
<div class="lastUpdateTime">Last update time: ${requestScope.lastUpdateTime}</div>
<br/>
<form action="" method="post" name="updateForm">
    <c:if test="${requestScope.lockUpdate == 'true'}">
        <input type="hidden" name="runUpdate" value="no">
        <button type="submit" class="submit">Check update status</button>
    </c:if>
    <c:if test="${requestScope.lockUpdate != 'true'}">
        <input type="hidden" name="runUpdate" value="yes">
        <button type="submit" class="submit">Update table</button>
        &nbsp;<input type="checkbox" name="updateVideoPreview" value="yes"> Update Video Preview
    </c:if>
</form>
</br/>
<div class="lastUpdateTime">
    <c:if test="${requestScope.lockUpdate == 'true'}">Please, wait until the update is finished</div></c:if>
    <c:if test="${requestScope.tableReady == 'true'}">
        The table is updated, please, check. Execution time: ${requestScope.execTime}</div><br /><br />
        <div class="error">
        <c:forEach items="${requestScope.videoErrors}" var="error">
            ${error}<br />
        </c:forEach>
        </div>
    </c:if>
</body>
</html>
