function validateSingleSelection(tableId, errorMessage) {
    const checked = document.querySelectorAll(
        `#${tableId} tbody input[type="checkbox"]:checked`
    );

    if (checked.length !== 1) {
        alert(errorMessage);
        return false;
    }

    return true;
}

function validateMinimalOneSelection(tableId, errorMessage) {
    const checked = document.querySelectorAll(
        `#${tableId} tbody input[type="checkbox"]:checked`
    );

    if (checked.length === 0) {
        alert(errorMessage);
        return false;
    }
    return true;
}