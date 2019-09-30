<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en" class="mdl-js">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="icon" href="<c:url value = '/static/img/favicon.ico'/>">

        <title>Store</title>

        <link rel="stylesheet" href="<c:url value = '/static/css/bootstrap.min.css'/>">
        <link rel="stylesheet" href="<c:url value = '/static/css/cart-confirmed.css'/>">
    </head>

    <c:set var="user" value="${user}" scope="session" />
    <c:set var="cart" value="${cart}" scope="request" />
    <c:set var="itemsCount" value="${itemsCount}" scope="request" />
    <c:set var="cartSum" value="${cartSum}" scope="request" />
    <c:set var="creationDate" value="${creationDate}"/>" scope="request" />


    <body class="bg-light">
        <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
            <a class="navbar-brand" href="#">Our store</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarCollapse">
                <ul class="navbar-nav mr-auto">
                </ul>
                <c:if test = "${user != null}">
                <form class="form mt-2 mt-md-0">
                    <span class="navbar-text hello-user-text">
                        Hello, <b><c:out value="${user.firstName}" /> <c:out value="${user.login}" /></b>
                    </span>
                    <a class="btn btn-sm btn-outline-secondary" href="<c:url value = '/logout'/>" type="button">Logout</a>
                </form>
                </c:if>
                <c:if test = "${user == null}">
                <form class="form mt-2 mt-md-0">
                    <a class="btn btn-sm btn-outline-secondary" href="<c:url value = '/register'/>" type="submit">Register</a>
                    <a class="btn btn-sm btn-outline-secondary" href="<c:url value = '/'/>" type="submit">Login</a>
                </form>
                </c:if>
                <form class="form-inline mt-2 mt-md-0 d-none">
                    <input class="form-control mr-sm-2" type="text" placeholder="Search" aria-label="Search">
                    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
                </form>
            </div>
        </nav>

        <main role="main" class="container-fluid bg-light">
            <h2 class="text-center">Thanks a lot for your order!</h2>
            <br>
            <hr>
            <br>
            <h3 class="text-center">Order summary:</h3>
            <table class="table table-borderless table-sm w-auto">
                <tbody>
                    <tr>
                        <td class="stat-heading">Cart code:</td>
                        <td class="stat-value"><c:out value="${cart.id}"/></td>
                    </tr>
                    <tr>
                        <td class="stat-heading">Creation date:</td>
                        <td class="stat-value"><fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${creationDate}" /></td>
                    </tr>
                    <tr>
                        <td class="stat-heading">Items count:</td>
                        <td class="stat-value"><c:out value="${itemsCount}"/> pcs.</td>
                    </tr>
                    <tr>
                        <td class="stat-heading">Total sum:</td>
                        <td class="stat-value"><fmt:formatNumber value="${cartSum / 100}" currencySymbol="$" type="currency" /></td>
                    </tr>
                </tbody>
            </table>
            <hr>
            <a class="btn btn-light btn-outline-secondary mb-2" href="<c:url value = '/items'/>">Back to items list</a>
            <h4 id="cart-sum" class="float-right">Total cart sum: <fmt:formatNumber value="${cartSum / 100}" currencySymbol="$" type="currency" /></h4>
            <input id="cart-operation-action" type="text" name="action" value="" hidden>
        </main>
    </body>
</html>