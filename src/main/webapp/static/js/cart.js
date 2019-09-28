
var notifyOffset = 71;

$( document ).ready(function() {
    $('.btn-remove-item').click(removeFromCart);
    $('.btn-edit-item').click(openEditDialog);
    $('#item-amount').change(onAmountChange);
    $('#amount-edit-commit-btn').click(onItemAmountCommit);
    $('#confirm-btn').click(setCartConfirmOperation);
    $('#discard-btn').click(setCartDiscardOperation);
});

function removeFromCart(event) {
    var orderId = $(event.target).closest('tr').attr('data-order-id');
    $.ajax({
        type: 'post',
        url: 'order/removeFromOpenCart',
        dataType : "json",
        data: {orderId: orderId},
        success: function(data, textStatus) {
            $.notify({
                // options
                message: 'Item &quot;' + $('.item-name', $(event.target).closest('tr')).first().text() + '&quot; removed from you cart'
            },{
                // settings
                type: 'success',
                offset: {
                    x: 20,
                    y: notifyOffset
                }
            });

            $(event.target).closest('tr').remove();
            refillRowNumbersAndConfirmationVisibility();
            updateTotalCartSumElement(data.cartSum);
        },
        error: function (data, textStatus) {
            $.notify({
                // options
                message: 'Item &quot;' + $('.item-name', $(event.target).closest('tr')).first().text() + '&quot; not removed from you cart'
            },{
                // settings
                type: 'danger',
                offset: {
                    x: 20,
                    y: notifyOffset
                }
            });
        }
    });
}

function refillRowNumbersAndConfirmationVisibility() {
    var rows = $('tr');

    // Element with index 0 is a header
    for (var i = 0; i < rows.length; i++){
        $('th.concrete-row-number', rows.get(i)).text(i);
    }

    if (rows.length == 1) { // Element with index 0 is a header
        $('#confirm-btn').addClass('d-none');
        $('#discard-btn').addClass('d-none');
    } else {
        $('#confirm-btn').removeClass('d-none');
        $('#discard-btn').removeClass('d-none');
    }
}

function openEditDialog(event) {
    var rowElement = $(event.target).closest('tr');

    $('#changeAmountModal').attr('data-order-id', rowElement.attr('data-order-id'));

    $('#changeAmountModalLabel').text("Change amount of " + $('.item-name',rowElement).first().text().trim());
    $('#item-price').val(parseLocalizedNumber($('.item-price',rowElement).first().text()).toFixed(2));
    $('#item-amount').val($('.item-amount',rowElement).first().text().trim());

    $('#item-sum').val(($('#item-price').val() * $('#item-amount').val()).toFixed(2));

    $('#changeAmountModal').modal('show');
}

function onAmountChange(event) {
    $('#item-sum').val(($('#item-price').val() * $(event.target).val()).toFixed(2));
}

function onItemAmountCommit(event) {
    var rowElement = $('tr[data-order-id=' + $(event.target).closest('.modal').first().attr('data-order-id') + ']').first();
    var amountElement = $('.item-amount', rowElement).first();

    if (amountElement.text().trim() == $('#item-amount').val()) {
        $('#changeAmountModal').modal('hide');
        return;
    }

    $.ajax({
        type: 'post',
        url: 'cart',
        dataType : "json",
        data: {orderId: rowElement.attr('data-order-id'),
               amount: $('#item-amount').val(),
               action: 'updateItemAmountInOrder'},
        success: function(data, textStatus) {
            var alertText = '';

            if ($('#item-amount').val() == 0) {
                alertText = 'Item &quot;' + $('.item-name', rowElement).first().text() + '&quot; removed from your cart';

                rowElement.remove();

            } else {
                alertText = 'Item &quot;' + $('.item-name', rowElement).first().text() + '&quot; amount updated';

                var price = parseLocalizedNumber($('.item-price', rowElement).first().text());
                var newAmount = $('#item-amount').val();
                var newSum = (newAmount * price).toFixed(2);

                amountElement.text(newAmount);
                $('.item-sum', rowElement).first().text(newSum);
            }

            $.notify({
                // options
                message: alertText
            },{
                // settings
                type: 'success',
                offset: {
                    x: 20,
                    y: notifyOffset
                }
            });

            $(event.target).closest('tr').remove();

            refillRowNumbersAndConfirmationVisibility();
            updateTotalCartSumElement(data.cartSum);
            $('#changeAmountModal').modal('hide');
        },
        error: function (data, textStatus) {
            $.notify({
                // options
                message: 'Item &quot;' + $('.item-name', rowElement).first().text() + '&quot; amount not changed'
            },{
                // settings
                type: 'success',
                offset: {
                    x: 20,
                    y: notifyOffset
                }
            });
        }
    });
}

function updateTotalCartSumElement(value) {
    var totalSum = getLocalCurrencyRepresentation(value, true);

    $('#cart-sum').text('Total cart sum: ' + totalSum);
}

function setCartConfirmOperation(event) {
    $('#cart-operation-action').val('confirm');
}

function setCartDiscardOperation(event) {
    $('#cart-operation-action').val('discard');
}