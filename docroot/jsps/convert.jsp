<%@page import="it.dfa.unict.AppPreferences"%>
<%@page import="it.dfa.unict.util.Constants"%>
<%@page import="it.dfa.unict.util.Utils"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@include file="../init.jsp"%>

<%
	PortletPreferences preferences = renderRequest.getPreferences();

	SimpleDateFormat dateFormat = new SimpleDateFormat(
			Constants.TS_FORMAT);
	String timestamp = dateFormat.format(Calendar.getInstance()
			.getTime());

	String jobLabel = user.getScreenName() + "_" + timestamp;
%>

<!-- <h1>convert</h1> -->
<portlet:actionURL name="submit" var="submitURL" />
<aui:form action="<%=submitURL%>" enctype="multipart/form-data"
	method="post">
	<aui:column columnWidth="50" first="true">
		<liferay-ui:error key="error-space" message="error-disk-space" />
		<aui:fieldset label="application-input">
			<liferay-ui:error key="empty-file" message="empty-file" />
			<liferay-ui:error key="error-limit-exceeded"
				message="error-limit-exceeded" />

			<aui:input type="text" name="selected-process" label="Process"
				id="selected-process" help="CIAO" />
			<aui:input type="file" name="fileupload" label="input-file" id="file"
				help="file-help">
				<aui:validator name="required" />
				<aui:validator name="acceptFiles">'zip'</aui:validator>
			</aui:input>

			<aui:input type="text" name="jobLabel" label="job-label" size="60"
				helpMessage="job-label-help" id="jobLabel" value="<%=jobLabel%>" />

			<aui:field-wrapper>
				<aui:button name="submit" value="submit" type="submit" />
				<aui:button id="reset-btn" name="reset" value="cancel" type="reset"
					onClick="hideDiv('convertId')" />
			</aui:field-wrapper>
		</aui:fieldset>
	</aui:column>
</aui:form>
