$(document).ready(function () {
    $(".linkMinus").on("click", function (e) {
        e.preventDefault()
        productId = $(this).attr("pid")
        quantityInput = $("#quantity" + productId)
        newQuantity = parseInt(quantityInput.val()) - 1
        if (newQuantity > 0) {
            quantityInput.val(newQuantity)
        } else {
            showWarningModal("Minimum quantity is 1")
        }
    })

    $(".linkPlus").on("click", function (e) {
        e.preventDefault()
        productId = $(this).attr("pid")
        quantityInput = $("#quantity" + productId)
        newQuantity = parseInt(quantityInput.val()) + 1
        if (newQuantity <= product_quantity) {
            quantityInput.val(newQuantity)
        } else {
            showWarningModal("Maximum quantity is " + product_quantity)
        }
    })
})