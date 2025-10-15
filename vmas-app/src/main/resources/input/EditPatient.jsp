<%@ include file="/decorators/parts/taglib.jsp" %>
<%@ taglib prefix="vmasTag" tagdir="/WEB-INF/tags" %>
<c:if test="${empty id }">
 <c:set var="show" value="none" />
</c:if>
<c:if test="${not empty id }">
 <c:set var="show" value="block" />
</c:if>
<div id='SearchResultPopup' style='z-index:10;display:none; position: absolute; left: 200px; top: 100px; border: solid black 1px;
	                       padding: 10px; background-color: rgb(225,235,225); text-align: justify; width: 900px;'>
                     
  <smpage:applyDecorator name="selectBarInput">
 	<%@ include file="/WEB-INF/pages/buttonbar/pet_EditNewPop.jsp"%>
</smpage:applyDecorator> 
	<div id="err2" style="display:none;">
		<smpage:applyDecorator name="editupdateError">
		</smpage:applyDecorator>
	</div>
	<form:form commandName="patientSearchForm" method="post" >
		<%@ include file="EditPatient.jspf"%>
	</form:form>
	</div>
<script type="text/javascript">
<!--

if("block"=="${show}") {
	$("#SearchResultPopup").show();
}
//-->
</script>
<!-- end popup -->

