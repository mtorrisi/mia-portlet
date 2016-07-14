<%-- 
    Document   : edit.jsp
    Created on : Feb 15, 2016, 10:08:35 AM
    Author     : mario
--%>

<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="it.dfa.unict.AppPreferences"%>
<%@page import="it.dfa.unict.util.Constants"%>
<%@page import="it.dfa.unict.util.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@include file="../../init.jsp"%>

<%
	PortletPreferences preferences = null;
	
	String portletResource = ParamUtil.getString(request, "portletResource");
		
	if (Validator.isNotNull(portletResource)) {
    	preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
	}

	String JSONAppPrefs = GetterUtil.getString(preferences
		.getValue(Constants.APP_PREFERENCES, null));
	AppPreferences appPreferences = Utils.getAppPreferences(JSONAppPrefs);
%>

<liferay-portlet:actionURL portletConfiguration="true"
	var="savePreferencesUrl">
	<portlet:param name="<%=Constants.CMD%>"
		value="<%=Constants.SAVE_PREFS%>" />
</liferay-portlet:actionURL>

<liferay-portlet:renderURL portletConfiguration="true"
	var="pilotScriptUrl">
	<liferay-portlet:param name="render-page"
		value="<%=Constants.VIEW_PILOT_PAGE%>" />
	<liferay-portlet:param name="pilotScript"
		value="<%=appPreferences.getPilotScript()%>" />
</liferay-portlet:renderURL>
<%-- <liferay-portlet:renderURL portletConfiguration="true" --%>
<%-- 	varImpl="iteratorURL"> --%>
<%-- 	<liferay-portlet:param name="render-page" --%>
<%-- 		value="<%=Constants.CONFIGURATION_PAGE%>" /> --%>
<%-- </liferay-portlet:renderURL> --%>

<liferay-ui:success key="<%=Constants.CONFIG_SAVED_SUCCESS%>"
	message="<%=Constants.CONFIG_SAVED_SUCCESS%>" />
<liferay-ui:success key="pilot-update-success"
	message="pilot-update-success" />

<aui:form action="${savePreferencesUrl}" name="aForm" method="post">
	<aui:fieldset label="generic-pref-lbl">

		<aui:field-wrapper>
			<liferay-ui:icon-menu>
				<liferay-ui:icon image="export" message="Save"
					url="<%=savePreferencesUrl%>" />
			</liferay-ui:icon-menu>
			<liferay-ui:icon-menu>
				<liferay-ui:icon image="edit" message="Edit pilot script"
					url="<%=pilotScriptUrl%>" />
			</liferay-ui:icon-menu>
			<aui:button name="save" value="save" type="submit" />
			<%-- 			<aui:button id="pilot" name="piltot_btn" value="" --%>
			<%-- 					onClick="" /> --%>
		</aui:field-wrapper>
		<aui:layout>
			<aui:column columnWidth="50" first="true">
				<aui:input type="text" name="pref_fgHost" size="60" id="fgHost"
					label="Futuregateway host" value="<%=appPreferences.getFgHost()%>" />

				<aui:input type="text" name="pref_fgPort" id="fgPort"
					label="Futuregateway port" size="60"
					value="<%=appPreferences.getFgPort()%>" />

				<aui:input type="text" name="pref_applicationId" id="applicationId"
					label="Application identifier" size="60"
					value="<%=appPreferences.getApplicationId()%>">
					<aui:validator name="digits" errorMessage="app-id" />
				</aui:input>

				<%-- 				<aui:input type="text" name="pref_applicationDesc" --%>
				<%-- 					id="applicationDesc" label="Application label" size="60" --%>
				<%-- 					value="<%=appPreferences.getApplicationDescription()%>"> --%>
				<%-- 										<aui:validator name="required" /> --%>
				<%-- 				</aui:input> --%>



			</aui:column>

		</aui:layout>
	</aui:fieldset>
</aui:form>
<br />

<script type="text/javascript">
	var productionEnvId = "<portlet:namespace/>productionEnv";
	var devEnvPrefsId = "<portlet:namespace/>devEnvPrefs";

	AUI().ready(function(A) {
		if (productionEnvId)
			showDevEnvPanel();
	});

	function showDevEnvPanel() {
		if (document.getElementById(productionEnvId).value === "true") {
			document.getElementById(devEnvPrefsId).style.display = 'none';
		} else {
			document.getElementById(devEnvPrefsId).style.display = 'block';
		}
	}
</script>