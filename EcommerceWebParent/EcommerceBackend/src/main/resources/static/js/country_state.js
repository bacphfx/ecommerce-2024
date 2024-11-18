let dropDownCountry
let dataListState

$(document).ready(function () {
    dropDownCountry = $("#country")
    dataListState = $("#listStates")

    dropDownCountry.on("change", function () {
        loadStatesForCountry()
    })
})

function loadStatesForCountry() {
    selectedCountry = $("#country option:selected")
    countryId = selectedCountry.val()
    url = contextPath + "states/list_by_country/" + countryId

    $.get(url, function (response) {
        $("#state").val("")
        dataListState.empty()
        $.each(response, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dataListState)
        })
    }).fail(function () {
        showErrorModal("Error loading states/provinces for the selected country")
    })
}