let buttonLoad
let dropDownCountry
let buttonAddCountry
let buttonUpdateCountry
let buttonDeleteCountry
let labelCountryName
let fieldCountryName
let labelCountryCode
let fieldCountryCode

function loadCountries() {
    url = contextPath + "countries/list"
    $.get(url, function (response) {
        dropDownCountry.empty()
        $.each(response, function (index, country) {
            optionValue = country.id + "-" + country.code
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry)
        })
    }).done(function () {
        buttonLoad.val("Refresh Country List")
        showToastMessage("All countries have been loaded!")
    }).fail(function () {
        showToastMessage("An error has been occurred!")
    })
}

function showToastMessage(message) {
    $("#toastMessage").text(message)
    $(".toast").toast("show")
}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New")
    buttonUpdateCountry.prop("disabled", false)
    buttonDeleteCountry.prop("disabled", false)

    selectedCountryName = $("#dropDownCountries option:selected").text()
    fieldCountryName.val(selectedCountryName)
    labelCountryName.text("Selected Country:")

    optionValue = dropDownCountry.val().split("-")[1]
    fieldCountryCode.val(optionValue)
}

function changeFormStateToNewCountry() {
    buttonAddCountry.val("Add")
    labelCountryName.text("Country Name:")
    fieldCountryName.val("").focus()
    fieldCountryCode.val("")
    buttonUpdateCountry.prop("disabled", "disabled")
    buttonDeleteCountry.prop("disabled", "disabled")
}

function selectNewlyAddedCountry(countryId, countryName, countryCode) {
    optionValue = countryId + "-" + countryCode
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry)
    $("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true)
    fieldCountryName.val("").focus()
    fieldCountryCode.val("")
}

function validateFormCountry(){
    formCountry = document.getElementById("formCountry");
    if (!formCountry.checkValidity()){
        formCountry.reportValidity()
        return false
    }
    return true;
}

function addCountry() {
    if (!validateFormCountry()) return;
    url = contextPath + "countries/save"
    countryName = fieldCountryName.val()
    countryCode = fieldCountryCode.val()
    jsonData = {name: countryName, code: countryCode}
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfValue
        },
        body: JSON.stringify(jsonData)
    }).then(response => {
        if (!response.ok) {
            throw new Error("HTTP error, status = " + response.status);
        }
        return response.text();
    }).then(countryId => {
        selectNewlyAddedCountry(countryId, countryName, countryCode)
        showToastMessage("The new country has been added")
    }).catch(error => {
        showToastMessage("An error has been occurred!")
    });
}

function updateCountry() {
    url = contextPath + "countries/save"
    countryName = fieldCountryName.val()
    countryCode = fieldCountryCode.val()
    countryId = dropDownCountry.val().split("-")[0]
    jsonData = {id: countryId, name: countryName, code: countryCode}
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfValue
        },
        body: JSON.stringify(jsonData)
    }).then(response => {
        if (!response.ok) {
            throw new Error("HTTP error, status = " + response.status);
        }
        return response.text();
    }).then(countryId => {
        $("#dropDownCountries option:selected").val(countryId + "-" + countryCode)
        $("#dropDownCountries option:selected").text(countryName)
        showToastMessage("The selected country has been updated")
        changeFormStateToNewCountry()
    }).catch(error => {
        showToastMessage("An error has been occurred!")
    });
}

function deleteCountry() {
    optionValue = dropDownCountry.val()
    countryId = optionValue.split("-")[0]
    url = contextPath + "countries/delete/" + countryId
    fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfValue
        },
    }).then(response => {
        if (!response.ok) {
            throw new Error("HTTP error, status = " + response.status);
        }
        return response.text();
    }).then(() => {
        $("#dropDownCountries option[value='" + optionValue + "']").remove()
        showToastMessage("The selected country has been deleted")
        changeFormStateToNewCountry()
    }).catch(error => {
        showToastMessage("An error has been occurred!")
    });
}

$(document).ready(function () {
    buttonLoad = $("#buttonLoadCountries")
    dropDownCountry = $("#dropDownCountries")
    buttonAddCountry = $("#buttonAddCountry")
    buttonUpdateCountry = $("#buttonUpdateCountry")
    buttonDeleteCountry = $("#buttonDeleteCountry")
    labelCountryName = $("#labelCountryName")
    fieldCountryName = $("#fieldCountryName")
    labelCountryCode = $("#labelCountryCode")
    fieldCountryCode = $("#fieldCountryCode")

    buttonLoad.click(function () {
        loadCountries()
    })

    dropDownCountry.on("change", function () {
        changeFormStateToSelectedCountry()
    })

    buttonAddCountry.click(function () {
        if (buttonAddCountry.val() == "Add") {
            addCountry()
        } else {
            changeFormStateToNewCountry()
        }
    })

    buttonUpdateCountry.click(function (){
        updateCountry()
    })

    buttonDeleteCountry.click(function (){
        deleteCountry()
    })
})