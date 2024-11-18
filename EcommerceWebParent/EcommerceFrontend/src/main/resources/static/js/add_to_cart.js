function addToCart() {
    quantity = $("#quantity" + productId).val()
    url = contextPath + "cart/add/" + productId + "/" + quantity;

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
    }).then(text => {
        showModalDialog("Shopping Cart", text)
    }).catch(error => {
        showErrorModal(error)
    });
}

$(document).ready(function () {
    $("#buttonAddToCart").on("click", function (e) {
        addToCart()
    })
})