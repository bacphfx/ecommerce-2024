dropdownBrands = $("#brand")
dropdownCategories = $("#category")

function getCategories() {
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories"
    $.get(url, function (response) {
        $.each(response, function (i, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories)
        })
    })
}

function getCategoriesForNewForm() {
    categoryIdField = $("#categoryId")
    let editMode = false
    if (categoryIdField.length) {
        editMode = true
    }
    if (!editMode) {
        getCategories()
    }

}

$(document).ready(function () {
    dropdownBrands.change(function () {
        dropdownCategories.empty();
        getCategories()
    })
    getCategoriesForNewForm()
});
