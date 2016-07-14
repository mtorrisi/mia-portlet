<%@taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@taglib prefix="liferay-theme" uri="http://liferay.com/tld/theme"%>
<%@taglib prefix="aui" uri="http://liferay.com/tld/aui"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<aui:layout>
	<aui:column columnWidth="50" first="true">
		<img src="<%=request.getContextPath()%>/images/AppLogo.png"
			height="80%" width="80%" />
	</aui:column>
	<aui:column columnWidth="50" last="true">
		<%=LanguageUtil.get(portletConfig,
							themeDisplay.getLocale(), "how-to-use")%>
	</aui:column>
</aui:layout>
<aui:layout>
	<aui:column columnWidth="100" first="true">
		<aui:select id="process" label="Select process" name="selectionStyle">
			<aui:option selected="true" value="select">Select a process ...</aui:option>
			<aui:option value="convert">DICOM to analyze</aui:option>
			<aui:option value="segmentation">Segmentation</aui:option>
			<aui:option value="extraction">Brain extraction</aui:option>
			<aui:option value="registration">Image registration</aui:option>
		</aui:select>
		<div id="convertId" style="display: none;">
			<%@ include file="convert.jsp"%>
		</div>
		<div id="segmentationId" style="display: none;">
			<%@ include file="segmentation.jsp"%>
		</div>
		<div id="extractionId" style="display: none;">
			<%@ include file="extraction.jsp"%>
		</div>
		<div id="registrationId" style="display: none;">
			<%@ include file="registration.jsp"%>
		</div>
	</aui:column>
</aui:layout>

<script type="text/javascript">
	var process = "<portlet:namespace/>process";
	var selectedProcess = "<portlet:namespace/>selected-process";
</script>