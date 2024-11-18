let fieldProductCost
let fieldSubtotal
let fieldShippingCost
let fieldTax
let fieldTotal

$(document).ready(function () {
    fieldProductCost = $("#productCost")
    fieldSubtotal = $("#subtotal")
    fieldShippingCost = $("#shippingCost")
    fieldTax = $("#tax")
    fieldTotal = $("#total")

    formatOrderAmounts()
    formatProductAmounts()

    $("#productList").on("change", ".quantity-input", function () {
        updateSubtotalWhenQuantityChanged($(this))
        updateOrderAmounts()
    })

    $("#productList").on("change", ".price-input", function () {
        updateSubtotalWhenPriceChanged($(this))
        updateOrderAmounts()
    })

    $("#productList").on("change", ".cost-input", function () {
        updateOrderAmounts()
    })

    $("#productList").on("change", ".ship-input", function () {
        updateOrderAmounts()
    })
})

function updateOrderAmounts(){
    totalCost = 0.0
    $(".cost-input").each(function (){
        costInputField = $(this)
        rowNumber = costInputField.attr("rowNumber")
        quantityValue = $("#quantity" + rowNumber).val()
        productCost = parseFloat(costInputField.val().replace(",", ""))
        totalCost += productCost * quantityValue
    })
    setAndFormatNumberForField("productCost", totalCost)

    orderSubtotal = 0.0
    $(".subtotal-output").each(function (){
        subtotalOutputField = $(this)
        orderSubtotal += parseFloat(subtotalOutputField.val().replace(",", ""))
    })
    setAndFormatNumberForField("subtotal", orderSubtotal)

    orderShippingCost = 0.0
    $(".ship-input").each(function (){
        shipInputField = $(this)
        orderShippingCost += parseFloat(shipInputField.val().replace(",", ""))
    })
    setAndFormatNumberForField("shippingCost", orderShippingCost)

    tax = parseFloat(fieldTax.val().replace(",", ""))
    orderTotal = orderSubtotal + orderShippingCost + tax
    setAndFormatNumberForField("total", orderTotal)

}

function formatOrderAmounts() {
    formatNumberForField(fieldProductCost)
    formatNumberForField(fieldSubtotal)
    formatNumberForField(fieldShippingCost)
    formatNumberForField(fieldTax)
    formatNumberForField(fieldTotal)
}

function formatProductAmounts() {
    $(".cost-input").each(function (e) {
        formatNumberForField($(this))
    })
    $(".price-input").each(function (e) {
        formatNumberForField($(this))
    })
    $(".ship-input").each(function (e) {
        formatNumberForField($(this))
    })
    $(".subtotal-output").each(function (e) {
        formatNumberForField($(this))
    })
}

function updateSubtotalWhenQuantityChanged(input) {
    quantityValue = input.val()
    rowNumber = input.attr("rowNumber")
    priceField = $("#price" + rowNumber)
    priceValue = parseFloat(priceField.val().replace(",", ""))
    newSubtotal = quantityValue * priceValue

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal)
}

function updateSubtotalWhenPriceChanged(input){
    priceValue = input.val().replace(",", "")
    rowNumber = input.attr("rowNumber")
    quantityField = $("#quantity" + rowNumber)
    quantityValue = quantityField.val()
    newSubtotal = quantityValue * priceValue

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal)
}

function setAndFormatNumberForField(fieldId, fieldValue){
    formattedValue = $.number(fieldValue, 2, '.', ',')
    $("#" + fieldId).val(formattedValue)
}

function formatNumberForField(fieldRef) {
    fieldRef.val($.number(fieldRef.val(), 2, '.', ','))
}