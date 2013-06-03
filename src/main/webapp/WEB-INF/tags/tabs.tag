<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div xmlns:jsp="http://java.sun.com/JSP/Page"
     xmlns:c="http://java.sun.com/jsp/jstl/core"
     xmlns:joda="http://www.joda.org/joda/time/tags"
     xmlns:spring="http://www.springframework.org/tags"
     xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
     version="2.0">
    <jsp.directive.taglib prefix="sc" tagdir="/WEB-INF/tags" />
    <jsp:directive.attribute name="tabNumber" required="true" />
    <c:url var="root" value="/"/>
    
    <div class="tabs ui-tabs ui-widget ui-corner-all">
        <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all" role="tablist">

            <li class="ui-state-default ui-corner-top 
                <c:if test="${tabNumber == 1}"> ui-tabs-active ui-state-active</c:if>
                ">
                <a href="${root}documents" class="ui-tabs-anchor">Documentos</a>
            </li>
            
            <li class="ui-state-default ui-corner-top 
                <c:if test="${tabNumber == 2}"> ui-tabs-active ui-state-active</c:if>
                " >
                <a href="${root}users" class="ui-tabs-anchor">Usuários</a>
            </li>
        </ul>
    </div>
</div>