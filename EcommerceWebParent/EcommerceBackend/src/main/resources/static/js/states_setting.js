let buttonLoadCountriesForState
let dropDownCountryForState
let dropDownStates
let buttonAddState
let buttonUpdateState
let buttonDeleteState
let labelStateName
let fieldStateName

function showToastMessage(message) {
    $("#toastMessage").text(message)
    $(".toast").toast("show")
}

function loadCountriesForStates() {
    url = contextPath + "countries/list"
    $.get(url, function (response) {
        dropDownCountryForState.empty()
        $.each(response, function (index, country) {
            $("<option>").val(country.id).text(country.name).appendTo(dropDownCountryForState)
        })
    }).done(function () {
        buttonLoadCountriesForState.val("Refresh Country List")
        loadStatesForCountry()
        showToastMessage("All countries have been loaded!")
    }).fail(function () {
        showToastMessage("An error has been occurred!")
    })
}

function loadStatesForCountry(){
    countryId = $("#dropDownCountriesForStates").val()
    url = contextPath + "states/list_by_country/" + countryId;
    $.get(url, function (response) {
        dropDownStates.empty()
        $.each(response, function (index, state) {
            $("<option>").val(state.id).text(state.name).appendTo(dropDownStates)
        })
    }).done(function () {
        buttonLoadCountriesForState.val("Refresh Country List")
        showToastMessage("All states have been loaded!")
    }).fail(function () {
        showToastMessage("An error has been occurred!")
    })
}

function changeFormStateToSelectedState() {
    buttonAddState.prop("value", "New")
    buttonUpdateState.prop("disabled", false)
    buttonDeleteState.prop("disabled", false)

    selectedStateName = $("#dropDownStates option:selected").text()
    fieldStateName.val(selectedStateName)
    labelStateName.text("Selected State:")
}

function changeFormStateToNew() {
    buttonAddState.val("Add")
    labelStateName.text("State Name:")
    fieldStateName.val("").focus()
    buttonUpdateState.prop("disabled", "disabled")
    buttonDeleteState.prop("disabled", "disabled")
}

function selectNewlyAddedState(statedId, stateName) {
    $("<option>").val(stateId).text(stateName).appendTo(dropDownStates)
    $("#dropDownStates option[value='" + statedId + "']").prop("selected", true)
    fieldStateName.val("").focus()
}

function validateFormState(){
    formState = document.getElementById("formState");
    if (!formState.checkValidity()){
        formState.reportValidity()
        return false
    }
    return true;
}

function addState() {
    if (!validateFormState()) return;
    url = contextPath + "states/save"
    stateName = fieldStateName.val()
    selectedCountry = $("#dropDownCountriesForStates option:selected")
    countryId = selectedCountry.val()
    countryName = selectedCountry.text()

    jsonData = {name: stateName, country: {id: countryId, name: countryName}}
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
    }).then(text => {
        stateId = parseInt(text)
        selectNewlyAddedState(stateId, stateName)
        showToastMessage("The new state has been added")
    }).catch(error => {
        console.log(error)
        showToastMessage("An error has been occurred!")
    });
}

function updateState() {
    url = contextPath + "states/save"
    stateId = dropDownStates.val()
    stateName = fieldStateName.val()

    selectedCountry = $("#dropDownCountriesForStates option:selected")
    countryId = selectedCountry.val()
    countryName = selectedCountry.text()

    jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}}
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
    }).then(stateId => {
        $("#dropDownStates option:selected").val(stateId)
        $("#dropDownStates option:selected").text(stateName)
        showToastMessage("The selected state has been updated")
        changeFormStateToNew()
    }).catch(error => {
        showToastMessage("An error has been occurred!")
    });
}

function deleteState() {
    stateId = dropDownStates.val()

    url = contextPath + "states/delete/" + stateId
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
        $("#dropDownStates option[value='" + stateId + "']").remove()
        showToastMessage("The selected state has been deleted")
        changeFormStateToNew()
    }).catch(error => {
        showToastMessage("An error has been occurred!")
    });
}

$(document).ready(function () {
    buttonLoadCountriesForState = $("#buttonLoadCountriesForStates")
    dropDownCountryForState = $("#dropDownCountriesForStates")
    dropDownStates=$("#dropDownStates")
    buttonAddState = $("#buttonAddState")
    buttonUpdateState = $("#buttonUpdateState")
    buttonDeleteState = $("#buttonDeleteState")
    labelStateName = $("#labelStateName")
    fieldStateName = $("#fieldStateName")

    buttonLoadCountriesForState.click(function () {
        loadCountriesForStates()
    })

    dropDownCountryForState.on("change", function () {
        loadStatesForCountry()
    })

    dropDownStates.on("change", function (){
        changeFormStateToSelectedState()
    })

    buttonAddState.click(function () {
        if (buttonAddState.val() == "Add") {
            addState()
        } else {
            changeFormStateToNew()
        }
    })

    buttonUpdateState.click(function (){
        updateState()
    })

    buttonDeleteState.click(function (){
        deleteState()
    })
})