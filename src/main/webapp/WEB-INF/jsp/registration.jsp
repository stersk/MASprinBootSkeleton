<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en"  class="mdl-js">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="icon" href="<c:url value = '/static/img/favicon.ico'/>">

        <link href="<c:url value = '/static/css/bootstrap.min.css'/>" rel="stylesheet">

        <script type="text/javascript" src=<c:url value = "/static/js/jquery-3.4.1.min.js"/>></script>
        <script type="text/javascript" src=<c:url value = "/static/js/bootstrap.min.js"/>></script>

        <script type="text/javascript" src=<c:url value = "/static/js/register.js"/>></script>

        <title>Registration</title>
    </head>
    <body class="bg-light">
        <div class="container">
            <div class="py-5 text-center">
                <img class="d-block mx-auto mb-4" src="<c:url value = '/static/img/login.svg'/>" alt="" width="72" height="72">
                <h2>Registration form</h2>
                <p class="lead">Hello, input all data about you, please!</p>
            </div>
            <div class="row">
                <div class="col-xl-3 col-md-2"></div>
                <div class="col-xl-6 col-md-8">
                    <form class="needs-validation" action="<c:url value = '/register'/>" method="post" novalidate>
                        <input type="text" name="action" value="register" hidden>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="firstName">First name</label>
                                <input type="text" name="fname" class="form-control" id="firstName" placeholder="" value="" required>
                                <div class="invalid-feedback">
                                    Valid first name is required.
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="lastName">Last name</label>
                                <input type="text" name="lname" class="form-control" id="lastName" placeholder="" value="" required>
                                <div class="invalid-feedback">
                                    Valid last name is required.
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="username">Username</label>
                            <input type="text" autocomplete="username" name="login" class="form-control" id="username" placeholder="" oninput="checkPasswordLoginAvailability(this);" required>
                            <div class="invalid-feedback" style="width: 100%;">
                                Your username is required.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="password">Password</label>
                            <input type="password" autocomplete="new-password" name="password" class="form-control" id="password" placeholder="Password" required>
                            <div class="invalid-feedback" style="width: 100%;">
                                Password is required.
                            </div>
                        </div>
                        <div class="mb-3">
                            <input type="password" class="form-control" id="passwordConfirm" placeholder="Password confirmation" oninput="checkPasswordConfirmation(this);" required>
                            <div class="invalid-feedback" style="width: 100%;">
                                Password confirmation is required.
                            </div>
                        </div>
                        <hr class="mb-4">
                        <button class="btn btn-dark btn-lg btn-block" type="submit">Continue to register</button>
                        <br>
                        <p>
                            <a href="<c:url value = '/'/>">Back to the login form</a>
                        </p>
                    </form>
                </div>
                <div class="col-xl-3 col-md-2"></div>
            </div>
            <footer class="my-5 pt-5 text-muted text-center text-small">
                <p class="mb-1"> &copy; 2019 Sters</p>
            </footer>
        </div>
    </body>
</html>
