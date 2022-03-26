/* SIDEBAR */
//show and hide sidebar of header
function showSideBar() {
	if(document.getElementById("userSidebar").style.width == "250px") {
		closeNav();
	}
	else {
		openNav();
	}
}
function openNav() {
	document.getElementById("userSidebar").style.width = "250px";
	document.getElementById("content").style.marginLeft = "250px";
}
function closeNav() {
	document.getElementById("userSidebar").style.width = "0";
	document.getElementById("content").style.marginLeft= "0";
}


/* DROPDOWN */
//show and hide dropdown menu of header
function showDropDown() {
	document.getElementById("userDropdown").classList.toggle("show-user");
}
// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
	if (!event.target.matches('.userdropbtn')) {
		var dropdowns = document.getElementsByClassName("user-dropdown-content");
		var i;
		for (i = 0; i < dropdowns.length; i++) {
			var openDropdown = dropdowns[i];
			if (openDropdown.classList.contains('show-user')) {
			openDropdown.classList.remove('show-user');
			}
		}
	}
}