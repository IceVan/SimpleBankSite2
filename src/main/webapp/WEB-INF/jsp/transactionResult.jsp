<%@page session="false"%>
<html>
<head>
<title>${title}</title>
</head>
<body>
    <jsp:include page="menu.jsp" />


    <h1>Transaction details</h1>
     <table>

                <tr>
                  <tr> Address: "${transactionResult.accountAddress}"</tr>
                  <tr> Amount: "${transactionResult.amount}"</tr>
                  <tr> Description: "${transactionResult.description}"</tr>
                </tr>
     </table>

    <form name="submitForm" modelAttribute="transaction" method="POST" action="${pageContext.request.contextPath}/addTransaction">
        <input name="accountAddress" type="hidden" value="${transactionResult.accountAddress}"/>
        <input name="amount" type="hidden" value="${transactionResult.amount}" />
        <input name="description" type="hidden" value="${transactionResult.description}">
        <input type="submit" />
    </form>
</body>
</html>