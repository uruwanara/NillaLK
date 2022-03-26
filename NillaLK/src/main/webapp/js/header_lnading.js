//Show header while scrolling
window.onscroll = function() {scrollFunction()};

function scrollFunction() {
	if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
		document.getElementById("header-home").style.top = "0";
	} else {
		document.getElementById("header-home").style.top = "-100px";
	}
}