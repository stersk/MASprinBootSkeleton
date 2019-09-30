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
        <link rel="stylesheet" href="<c:url value = '/static/css/cart.css'/>">
        <link rel="stylesheet" href="<c:url value = '/static/css/animate.css'/>">

        <script type="text/javascript" src=<c:url value = "/static/js/jquery-3.4.1.min.js"/>></script>
        <script type="text/javascript" src=<c:url value = "/static/js/bootstrap.min.js"/>></script>
        <script type="text/javascript" src=<c:url value = "/static/js/bootstrap-notify.min.js"/>></script>

        <script type="text/javascript" src=<c:url value = "/static/js/cart.js"/>></script>
        <script type="text/javascript" src=<c:url value = "/static/js/utils.js"/>></script>
    </head>

    <c:set var="user" value="${user}" scope="session" />
    <c:set var="items" value="${items}" scope="request" />
    <c:set var="orders" value="${orders}" scope="request" />
    <c:set var="cartSum" value="${cartSum}" scope="request" />
    <c:set var="counter" value="1" scope="page" />

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
            <table class="table table-striped">
                <thead class="thead-dark">
                <tr>
                    <th scope="col" class="row-number col-auto">#</th>
                    <th scope="col">Item</th>
                    <th scope="col">Amount</th>
                    <th scope="col">Price, $</th>
                    <th scope="col">Sum, $</th>
                    <th scope="col" class="row-buttons-panel col-auto"></th>
                </tr>
                </thead>
                <tbody>
                    <c:forEach items="${orders}" var="order">
                        <c:forEach items="${items}" var="item">
                            <c:if test="${order.item == item}">
                                <tr data-order-id="${order.id}">
                                    <th scope="row" class="concrete-row-number">${counter}</th>
                                    <c:set var="counter" value="${counter + 1}" scope="page" />
                                    <td class="item-name">${item.name}</td>
                                    <td class="item-amount">${order.amount}</td>
                                    <td class="item-price"><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${item.price / 100}" /></td>
                                    <td class="item-sum"><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${order.amount * item.price / 100}" /></td>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-outline-secondary btn-edit-item">Edit</button>
                                        <button type="button" class="btn btn-sm btn-outline-secondary btn-remove-item">Delete</button>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </tbody>
            </table>
            <hr>
            <h4 id="cart-sum" class="float-right">Total cart sum: <fmt:formatNumber value="${cartSum / 100}" currencySymbol="$" type="currency" /></h4>
            <form id="cart-action-form" class="form" action="<c:url value = '/cart'/>" method="post">
                <div>
                    <a class="btn btn-light btn-outline-secondary mb-2" href="<c:url value = '/items'/>">Back to items list</a>
                    <c:if test="${orders.size() > 0}">
                    <button id="confirm-btn" type="submit" class="btn btn-dark mb-2">Confirm cart</button>
                    <button id="discard-btn" type="submit" class="btn btn-dark mb-2">Discard cart</button>
                    </c:if>
                </div>
            </form>
        </main>
        <!-- Change item amount dialog -->
        <div class="modal fade" id="changeAmountModal" data-order-id="" tabindex="-1" role="dialog" aria-labelledby="changeAmountModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="changeAmountModalLabel">Change amount</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form class="">
                            <div class="form-row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="item-amount" class="">Amount</label>
                                        <input type="number" class="form-control" id="item-amount" min="0" step="1" value="">
                                    </div>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="item-price" class="">Price</label>
                                        <input type="text" class="form-control" id="item-price" readonly>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="item-sum" class="">Sum</label>
                                        <input type="text" class="form-control" id="item-sum" readonly>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light" data-dismiss="modal">Close</button>
                        <button type="button" id="amount-edit-commit-btn" class="btn btn-dark">Save changes</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>