/* TRANSITIONS IN HOME PAGE */
//Home Plant Window open and close functions (From Landing Page)
function openHomePlant() {
    window.open("home/home_plant.html", "_self");
}
function closeHomePlant() {
    window.open("index.html", "_self");
}

//Home Shop Window open and close functions (From Landing Page)
function openHomeShop() {
    window.open("home/home_shop.html", "_self");
}
function closeHomeShop() {
    window.open("index.html", "_self");
}

//Home Blog Window open and close functions (From Landing Page)
function openHomeBlog() {
    window.open("home/home_blog.html", "_self");
}
function closeHomeBlog() {
    window.open("index.html", "_self");
}

//Home Agri Window open and close functions (From Landing Page)
function openHomeAgri() {
    window.open("home/home_agri.html", "_self");
}
function closeHomeAgri() {
    window.open("index.html", "_self");
}

//Home About us Window open and close functions (From Landing Page)
function openHomeAbout() {
    window.open("home/home_about.html", "_self");
}
function closeHomeAbout() {
    window.open("index.html", "_self");
}
/* ================================================================== */


/* FUNCTIONS RELATED TO LOGIN PAGE */
//Login Window open and close functions (From Landing Page)
function openLogin() {
    window.open("login.html", "_self");
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
Instructor.onclick = getInstructor;

function getGardener() {
    document.getElementById("ChooseUserType").style.display = "block";
	openRegistration("Gardener");
}
function getShopOwner() {
    document.getElementById("ChooseUserType").style.display = "block";
	openRegistration("ShopOwner");
}

function getInstructor() {
    document.getElementById("ChooseUserType").style.display = "block";
	openRegistration("AgriInstructor");
}

//Registration Window Open and Close
function openRegistration(userType) {
    window.location.href = "registration.html?userType="+userType;
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

/*function openRegUser() {
	var form = document.getElementById("form");
	if (form.style.display === "none") {
		form.style.display = "block";
	} else {
		form.style.display = "none";
	}
}
/*/

/*
function openRegUser(userType) {
    if(userType=='Gardener'){
        window.open("reg_Gardener.html", "_self");
    } 
    else if(userType=='ShopOwner'){
        window.open("reg_ShopOwner.html", "_self");
    }
	else if(userType=='Instructor'){
        window.open("reg_ShopOwner.html", "_self");
    }
	else {
		document.getElementById("Reg_Unsuccessful").style.display = "block";
	}
}

//Popup Window in Registration (common user details) and Get user type
function closeRegUser() {
    document.getElementById("UserRegForm").style.display = "none";
}


//Register as Gardener form open and close functions
function openRegGardener() {
    window.open("reg_Gardener.html", "_self");
}
function cancelRegGardener() {
    window.open("registration.html", "_self");
}
function closeRegGardener() {
    window.open("index.html", "_self");
}

//Register as Shop Owner form open and close functions
function openRegShopOwner() {
    window.open("reg_ShopOwner.html", "_self");
}
function cancelRegShopOwner() {
    window.open("registration.html", "_self");
}
function closeRegShopOwner() {
    window.open("index.html", "_self");
}
*/
function darkMode() {
   var element = document.body;
   element.classList.toggle("dark-mode");
}

