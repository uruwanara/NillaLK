/* FUNCTIONS RELATED TO LOGIN PAGE */
//Login Window open and close functions (From Landing Page)
function openLogin() {
    window.open("../login.html", "_self");
}
function cancelLogin() {
    window.open("index.html", "_self");
}

//Popup Window in Login Page
function openResetPW() {
    document.getElementById("ResetPWPopup").style.display = "block";
}
function closeResetPW() {
    document.getElementById("ResetPWPopup").style.display = "none";
}
/* ================================================================== */


/* FUNCTIONS RELATED TO REGISTRATION PAGE */
//Choose User Type popup window
function openChooseUserType() {
    document.getElementById("ChooseUserType").style.display = "block";
}
function closeChooseUserType() {
    document.getElementById("ChooseUserType").style.display = "none";
}

//Get User type in pop up Window Open and Close
var userType;
var fullname;

Gardener.onclick = getGardener;
ShopOwner.onclick = getShopOwner;

function getGardener() {
    document.getElementById("ChooseUserType").style.display = "block";
    userType = this.getAttribute('name');
	openRegistration(userType);
}
function getShopOwner() {
    document.getElementById("ChooseUserType").style.display = "block";
    userType = this.getAttribute('name');
	openRegistration(userType);
}

//Registration Window Open and Close
function openRegistration(userType) {
    window.location.href = "../registration.html?userType="+userType;
}
function cancelRegistration() {
    window.open("index.html", "_self");
}

// Clear filled Data
function clearRegUser() {
    document.getElementById("fname").value = "";
    document.getElementById("lname").value = "";
    document.getElementById("email").value = "";
    document.getElementById("psw").value = "";
    document.getElementById("repsw").value = "";
}


function openRegSuccessful() {
    document.getElementById("Reg_Successful").style.display = "block";
}
function closeRegSuccessful() {
    document.getElementById("Reg_Successful").style.display = "none";
}
function openRegUnsuccessful() {
    document.getElementById("Reg_Unsuccessful").style.display = "block";
}
function closeRegUnsuccessful() {
    document.getElementById("Reg_Unsuccessful").style.display = "none";
}
function show(param_div_id) {
    document.getElementById('RegUser').innerHTML = document.getElementById(param_div_id).innerHTML;
}

function darkMode() {
   var element = document.body;
   element.classList.toggle("dark-mode");
}

