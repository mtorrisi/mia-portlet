<%-- 
    Document   : view
    Created on : Feb 15, 2016, 10:29:44 AM
    Author     : mario
--%>

<%@page import="it.dfa.unict.AppInfrastructureInfo"%>
<%@page import="it.dfa.unict.AppPreferences"%>
<%@page import="it.dfa.unict.util.Constants"%>
<%@page import="it.dfa.unict.util.Utils"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@include file="../init.jsp"%>

<%
	PortletPreferences preferences = renderRequest.getPreferences();
	String JSONAppInfras = GetterUtil.getString(preferences.getValue(
	Constants.APP_INFRASTRUCTURE_INFO_PREFERENCES, null));

	List<AppInfrastructureInfo> enabledInfas = Utils
	.getEnabledInfrastructureInfo(JSONAppInfras);
	
	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TS_FORMAT);
	String timestamp = dateFormat.format(Calendar.getInstance().getTime());
	
	String jobLabel = user.getScreenName() + "_" +  timestamp;
%>

<aui:layout>
	<aui:column columnWidth="50" first="true">
		<img src="<%=request.getContextPath()%>/images/AppLogo.png"
			height="80%" width="80%" />
	</aui:column>
	<aui:column columnWidth="50" last="true">
		<%=LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "how-to-use") %>
	</aui:column>

	<aui:column columnWidth="100" first="true">
		<c:choose>
			<c:when test="<%=enabledInfas.size() == 0%>">
				<div class="portlet-msg-info">
					<liferay-ui:message key="no-infras-available" />
				</div>
			</c:when>
			<c:otherwise>
				<liferay-ui:error key="error-space" message="error-disk-space" />
				<portlet:actionURL name="submit" var="submitURL" />

				<aui:form action="<%=submitURL%>" enctype="multipart/form-data"
					method="post">

					<aui:fieldset label="application-input">
						<liferay-ui:error key="empty-file" message="empty-file" />
						<liferay-ui:error key="error-limit-exceeded"
							message="error-limit-exceeded" />

						<aui:input type="file" name="fileupload" label="input-file"
							id="file" help="file-help" />

						<aui:input type="text" name="jobLabel" label="job-label" size="60"
							helpMessage="job-label-help" id="jobLabel" value="<%= jobLabel %>" />

						<aui:field-wrapper>
							<aui:button name="submit" value="submit" type="submit" />
							<aui:button name="reset" value="cancel" type="reset" />
						</aui:field-wrapper>
					</aui:fieldset>
				</aui:form>
			</c:otherwise>
		</c:choose>
	</aui:column>
</aui:layout>