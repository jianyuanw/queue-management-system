window.onload = function() {
    document.getElementById("form").addEventListener("submit", validatePassword);
}

function validatePassword(event) {
    if (document.getElementById("password").value !==
        document.getElementById("confirm-password").value) {
        event.preventDefault()
        document.getElementById("password-error").style.display = "block";
        return false;
    }
    document.getElementById("password-error").style.display = "none";
}