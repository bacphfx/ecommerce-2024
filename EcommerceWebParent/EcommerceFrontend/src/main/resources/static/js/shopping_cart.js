let quantityInput
let subtotalValue
let decimalSeparator = digitPointType == "POINT" ? "." : ","
let thousandSeparator = thousandPointType == "POINT" ? "." : ","
$(document).ready(function () {
    $(".linkMinus").on("click", function (e) {
        e.preventDefault()
        decreaseQuantity($(this))
    })

    $(".linkPlus").on("click", function (e) {
        e.preventDefault()
        increaseQuantity($(this))
    })

    $(".link-remove-product").on("click", function (e){
        e.preventDefault()
        removeProduct($(this))
    })
})

function decreaseQuantity(link){
    productId = link.attr("pid")
    quantityInput = $("#quantity" + productId)
    newQuantity = parseInt(quantityInput.val()) - 1
    if (newQuantity > 0) {
        updateQuantity(productId, newQuantity)
    } else {
        showWarningModal("Minimum quantity is 1")
    }
}

function increaseQuantity(link){
    productId = link.attr("pid")
    quantityInput = $("#quantity" + productId)
    newQuantity = parseInt(quantityInput.val()) + 1

    updateQuantity(productId, newQuantity)
}

function updateQuantity(productId, quantity){
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfValue
        }
    }).then(response => {
        if (!response.ok) {
            return response.text().then(error => {
                throw new Error(error)
            })
        }
        return response.text();
    }).then(newSubtotal => {
        quantityInput.val(newQuantity)
        updateSubtotal(newSubtotal, productId)
    }).catch(error => {
        showErrorModal(error)
    });
}

function updateSubtotal(newSubtotal, productId){
    formatedSubtotal = formatNumber(newSubtotal)
    subtotalValue = $("#subtotal" + productId)
    subtotalValue.text(formatedSubtotal)
    updateEstimateTotal()
}

function updateEstimateTotal(){
    total = 0.0;
    productCount = 0;
    $(".subtotal").each(function (index, element){
        productCount ++;
        total += parseFloat(clearCurrencyFormat(element.innerHTML))
    })
    if (productCount < 1){
        showEmptyCart()
    } else {
        $("#estimatedTotal").text(formatNumber(total))
    }
}

function clearCurrencyFormat(numberString){
    result = numberString.replaceAll(thousandSeparator, "")
    return result.replaceAll(decimalSeparator, ".")
}

function showEmptyCart(){
    $("#sectionTotal").hide()
    $("#sectionEmptyCartMessage").removeClass("d-none")
}

function removeProduct(link){
    url = link.attr("href")
    fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfValue
        }
    }).then(response => {
        if (!response.ok) {
            return response.text().then(error => {
                throw new Error(error)
            })
        }
        return response.text();
    }).then(text => {
        rowNumber = link.attr("rowNumber")
        removeProductInHtml(rowNumber)
        updateEstimateTotal()
        updateCountNumber()
        showModalDialog("Shopping Cart", text)
    }).catch(error => {
        showErrorModal(error)
    });
}

function removeProductInHtml(rowNumber){
    $("#row" + rowNumber).remove()
}

function updateCountNumber(){
    $(".divCount").each(function (index, element){
        element.innerHTML = "" + (index + 1);
    })
}

function formatNumber(amount){
    return $.number(amount, digitNumber, decimalSeparator, thousandSeparator);
}