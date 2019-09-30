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
        <link rel="stylesheet" href="<c:url value = '/static/css/items.css'/>">
        <link rel="stylesheet" href="<c:url value = '/static/css/animate.css'/>">

        <script type="text/javascript" src=<c:url value = "/static/js/jquery-3.4.1.min.js"/>></script>
        <script type="text/javascript" src=<c:url value = "/static/js/bootstrap.min.js"/>></script>
        <script type="text/javascript" src=<c:url value = "/static/js/bootstrap-notify.min.js"/>></script>

        <script type="text/javascript" src=<c:url value = "/static/js/items.js"/>></script>
    </head>

    <c:set var="user" value="${user}" scope="session" />
    <c:set var="items" value="${items}" scope="request" />
    <c:set var="discardCompleted" value="${param.discardCompleted}" scope="request" />

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
                    <a class="btn btn-sm btn-outline-secondary" href="<c:url value = '/cart'/>" type="button">Cart</a>
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
            <div class="row">
                <c:forEach items="${items}" var="item">
                    <div class="col-xl-2 col-md-4 col-sm-6 col-12">
                        <div class="card mb-4 shadow-sm">
                            <img src="<c:url value = "/static/img/nophoto.jpg"/>" class="card-img-top border-bottom" width="auto" height="225"  alt="${item.name}"/>
                            <div class="card-body">
                                <h5 class="card-text">${item.name}</h5>
                                <p class="card-text"><b>Price: </b><fmt:formatNumber value="${item.price / 100}" currencySymbol="$" type="currency" /><span  class="float-right"><b>code: </b>${item.itemCode}</span></p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-outline-secondary">View</button>
                                        <c:if test = "${user != null}">
                                        <button type="button" class="btn btn-sm btn-outline-secondary btn-add-to-cart" data-id="${item.id}">Add to cart</button>
                                        </c:if>
                                    </div>
                                    <small class="text-muted d-none">9 mins</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </main>
    </body>

    <c:if test="${not empty discardCompleted}">
        <c:if test="${discardCompleted}">
            <script type="text/javascript">
                $(document).ready(function() {
                    $.notify({
                        // options
                        message: 'You cart cleared successfully'
                    },{
                        // settings
                        type: 'success',
                        offset: {
                            x: 20,
                            y: notifyOffset
                        }
                    });
                });
            </script>
<%--            <c:remove var="discardCompleted" scope="session" />--%>
        </c:if>
    </c:if>
</html>