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
<div class="header"><h2>Localization Tables Updater</h2></div>
<div class="lastUpdateTime">
    Videos last update time: ${requestScope.lastUpdateTimeVideo}
    <br/>
    Banners last update time: ${requestScope.lastUpdateTimeBanner}
</div>
<br/>
<form action="" method="post" name="updateForm">
    <c:if test="${requestScope.lockUpdate == 'true'}">
        <input type="hidden" name="runUpdate" value="no">
        <button type="submit" class="submit">Check update status</button>
    </c:if>
    <c:if test="${requestScope.lockUpdate != 'true'}">
        <input type="hidden" name="runUpdate" value="yes">
        <input type="radio" name="creativeType" value="video" id="video" checked><label for="video">Video</label> <input
            type="radio" name="creativeType" value="banner" id="banner"><label for="video">Banner</label>
        <button type="submit" class="submit">Update table</button>
        &nbsp;<input type="checkbox" name="updatePreview" value="yes"> Update Preview
    </c:if>
</form>
<br/>
<div class="lastUpdateTime">
    <c:if test="${requestScope.lockUpdate == 'true'}">Please, wait until the update is finished
    </c:if>
    <c:if test="${requestScope.tableReady == 'true'}">
    The table is updated, please, check. Execution time: ${requestScope.execTime}.

    <c:if test="${requestScope.videoErrors > 0}">&nbsp;<a rel="noopener noreferrer"
        href="https://docs.google.com/spreadsheets/d/1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI/edit?pli=1#gid=1369950641&range=V1"
        class="awemlink" target="_blank">${requestScope.videoErrors} errors found.
        </a>
    </c:if>
</div>
</c:if>
</body>
</html>
