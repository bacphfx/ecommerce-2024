$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        window.location = moduleURL;
    });
    $("#imageUpload").change(function () {
        if (!checkFileSize(this)) {
            return;
        }
        showImageThumbnail(this)

    })
})

function checkFileSize(fileInput) {
    fileSize = fileInput.files[0].size

    if (fileSize > 1048576) {
        fileInput.setCustomValidity("You must choose an image less than 1MB!")
        fileInput.reportValidity()
        return false;
    } else {
        fileInput.setCustomValidity("")
        return true;
    }
}

function showImageThumbnail(fileInput) {
    const file = fileInput.files[0];
    const reader = new FileReader()
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result)
    }
    reader.readAsDataURL(file);
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title)
    $("#modalBody").text(message)
    $("#modalDialog").modal();
}

function showWarningModal(message) {
    showModalDialog("Warning", message)
}

function showErrorModal(message) {
    showModalDialog("Error", message)
}