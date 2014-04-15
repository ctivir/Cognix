<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<div>
    <jsp.directive.taglib prefix="sc" tagdir="/WEB-INF/tags" />
    <jsp:directive.attribute name="tabNumber" required="true" />
    <c:url var="root" value="/"/>
    
    <div class="tabs ui-tabs ui-widget ui-corner-all visible-lg hidden-xs hidden-sm hidden-md" >
        <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all" role="tablist">

            <li class="ui-state-default ui-corner-top 
                <c:if test="${tabNumber == 1}"> ui-tabs-active ui-state-active</c:if>
                    ">
                    <a href="${root}documents" class="ui-tabs-anchor">Documentos</a>
            </li>
            <security:authorize access="isAuthenticated()">
                <li class="ui-state-default ui-corner-top 
                    <c:if test="${tabNumber == 2}"> ui-tabs-active ui-state-active</c:if>
                        " >
                        <a href="${root}users" class="ui-tabs-anchor">Usuários</a>
                </li>
            </security:authorize>
                <security:authorize access="hasRole('PERM_MANAGE_DOC')">
                <li class="ui-state-default ui-corner-top 
                    <c:if test="${tabNumber == 3}"> ui-tabs-active ui-state-active</c:if>
                        " >
                        <a href="${root}panel" class="ui-tabs-anchor">Informações</a>
                </li>
            </security:authorize>
        </ul>
    </div>
</div>