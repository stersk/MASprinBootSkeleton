
var notifyOffset = 71;

$( document ).ready(function() {
    $('.btn-add-to-cart').click(addToCart);
});

function addToCart(event) {
    $.ajax({
        type: 'post',
        url: 'order/addItemToCart',
        dataType : "json",
        data: {itemId: $(event.target).attr('data-id')},
        success: function(data, textStatus) {
            $.notify({
                // options
                message: 'Item &quot;' + data.name + '&quot; added to you cart'
            },{
                // settings
                type: 'success',
                offset: {
                    x: 20,
                    y: notifyOffset
                }
            });
        },
        error: function (data, textStatus) {
            $.notify({
                // options
                message: 'Item &quot;' + data.name + '&quot; not added to you cart'
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