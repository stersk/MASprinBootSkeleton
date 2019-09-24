(function() {
    'use strict';

    window.addEventListener('load', function() {
        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        var forms = document.getElementsByClassName('needs-validation');

        // Loop over them and prevent submission
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();

var timerId;

function checkPasswordConfirmation(inputElement) {
    var password = $('#password').val();
    var validationMessage = "";

    if (inputElement.value == "") {
        validationMessage = "Password confirmation is required.";
    } else if (inputElement.value != password) {
        validationMessage = "Password an confirmation password are different. Check your input.";
    }

    inputElement.setCustomValidity(validationMessage);
    $(inputElement).parent().find('.invalid-feedback').first().text(validationMessage);
}

function checkPasswordLoginAvailability(inputElement){
    if (timerId != null) {
        clearTimeout(timerId);
        timerId = null;
    }

    timerId = setTimeout(function () {
        $.ajax({
            type: 'get',
            url: '../user',
            dataType : "json",
            data: {login: inputElement.value,
                   action: 'userExist'},
            success: function (data, textStatus) {
                var validationMessage = "";

                if (data.userExist) {
                    validationMessage = "This username '" + inputElement.value + "' used by another user. Input another username, please";
                } else if (inputElement.value == "") {
                    validationMessage = "Your username is required.";
                }

                inputElement.setCustomValidity(validationMessage);
                $(inputElement).parent().find('.invalid-feedback').first().text(validationMessage);

                inputElement.checkValidity();
                inputElement.reportValidity();
            }
        });
    }, 1000);
}
