<%@ include file="/decorators/parts/taglib.jsp" %>
<%@ taglib prefix="vmasTag" tagdir="/WEB-INF/tags" %>
<script language="javascript"> 
	$("#species").change(function(){    
		$("#quote").load("${ctx}/customerpet/patient/searchbreed.htm?type=1&select1="+$("#species").val()); 
	});
</script>
<form:form commandName="patientSearchForm" method="post" >
<input type="submit" value="<spring:message code='label.button.save'/>" id="save" name="_create" class="cssbutton" onmouseover='this.style.cursor="pointer"' onfocus='this.blur();'/>
<smpage:applyDecorator name="editupdateError">
</smpage:applyDecorator>  
<%@ include file="EditPatient.jspf"%>
</form:form>