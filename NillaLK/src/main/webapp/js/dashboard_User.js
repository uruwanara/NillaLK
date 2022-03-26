/* TRANSITIONS FROM GARDENER DASHBOARD */
//plant window open and close functions (from user dashboard-> Find a Plant)
function openUserPlant() {
    window.open("user_plant.html", "_self");
}
function closeUserPlant() {
    window.open("dashboard.html", "_self");
}

//Pest window open and close functions (from user dashboard-> Find a Pest)
function openUserPest() {
    window.open("user_pest.html", "_self");
}
function closeUserPest() {
    window.open("dashboard.html", "_self");
}

//Disease window open and close functions (from user dashboard-> Find a Disease)
function openUserDisease() {
    window.open("user_disease.html", "_self");
}
function closeUserDisease() {
    window.open("dashboard.html", "_self");
}

//Product window open and close functions (from user dashboard-> Search a Product)
function openUserProduct() {
    window.open("user_product.html", "_self");
}
function closeUserProduct() {
    window.open("dashboard.html", "_self");
}

//Shop window open and close functions (from user dashboard-> Search a Shop)
function openUserShop() {
    window.open("user_shop.html", "_self");
}
function closeUserShop() {
    window.open("dashboard.html", "_self");
}

//Chat window open and close functions (from user dashboard-> Contact Agricultural Instructor)
function openUserChat() {
    window.open("user_chat.html", "_self");
}
function closeUserChat() {
    window.open("dashboard.html", "_self");
}

//Blog window open and close functions (from user dashboard-> Blog)
function openUserBlog() {
    window.open("user_blog.html", "_self");
}
function closeUserBlog() {
    window.open("dashboard.html", "_self");
}

//Add Plant to System Popup Window in user dashboard Page (from user dashboard-> Add a Plant to Garden)
function openUserAddPlant() {
    document.getElementById("UserAddPlantPopup").style.display = "block";
}
function closeUserAddPlant() {
    document.getElementById("UserAddPlantPopup").style.display = "none";
}

//Enter the Are Popup Window in user dashboard Page (from user dashboard-> Popup to get Area)
function openEnterArea() {
    document.getElementById("EnterAreaPopup").style.display = "block";
}
function closeEnterArea() {
    document.getElementById("EnterAreaPopup").style.display = "none";
}

//Browse Plant Window open and close function (from user dashboard)
function openBrowsePlant() {
	var area = $('#area').val();
	localStorage.setItem("area", JSON.stringify(area));
    window.open("user_browsePlant.html", "_self");
}
function closeBrowsePlant() {
    window.open("dashboard.html", "_self");
}

// Add Watering system window open and close
function openAddWateringSys() {
    window.open("user_addWaterSystem.html", "_self");
}
function closeAddWateringSys() {
    window.open("dashboard.html", "_self");
}

// Add Watering system window open and close
function openManageWateringSys() {
    window.open("user_manageWaterSystem.html", "_self");
}
function closeManageWateringSys() {
    window.open("dashboard.html", "_self");
}

// Monitor Plant window open and close
function openMonitorPlant() {
    window.open("user_monitorPlant.html", "_self");	
}
function closeMonitorPlant() {
    window.open("dashboard.html", "_self");	
}
/* ======================================================================================== */




function openUploadImg() {
    document.getElementById("uploadImage").style.display = "block";
}
function closeUploadImg() {
    document.getElementById("uploadImage").style.display = "none";
}





/* TRANSITIONS FROM BROWSE PLANT PAGE */
//Browse Plant Window open and close function (from user dashboard)
function openAddPlant() {
    window.open("user_addPlant.html", "_self");
}
function closeAddPlant() {
    window.open("user_browsePlant.html", "_self");
}

function openMonitorSystem() {
    window.open("user_toMonitorSystem.html", "_self");
}
function closeMonitorSystem() {
    window.open("user_addPlant.html", "_self");
}

function openaddMonitorSuccess() {
	document.getElementById("addMonitorSuccess").style.display = "block";
}
function closeaddMonitorSuccess() {
	document.getElementById("addMonitorSuccess").style.display = "none";
	window.open("dashboard.html", "_self");
}
/* ======================================================================================== */



/* TRANSITIONS IOT CONFIGURATION PAGE */
//Configure IOT device Popup Window 
function openConfigIOT() {
    document.getElementById("ConfigIOT_msg").style.display = "block";
}
function closeConfigIOT() {
    document.getElementById("ConfigIOT_msg").style.display = "none";
}

//Configure IOT device Popup Window 
function opensuccessConfigIOT() {
    document.getElementById("ConfigIOT_success").style.display = "block";
}
function closesuccessConfigIOT() {
    document.getElementById("ConfigIOT_msg").style.display = "none";
	window.open("dashboard.html", "_self");
}

// Show IOT Device Details form division
function show(param_div_id) {
	document.getElementById('addWaterSys').innerHTML = document.getElementById(param_div_id).innerHTML;
	document.getElementById("ConfigIOT_msg").style.display = "none";
}
/* ======================================================================================== */



/* TRANSITIONS IN UPDATE PASSOWRD OF USER DETAILS */
function openpswChange() {
    document.getElementById("pswChange").style.display = "block";
}
function closepswChange() {
    document.getElementById("pswChange").style.display = "none";
}
/* ======================================================================================== */







/* TRANSITIONS IN BLOG */
//Create article for blogs open and close functions (from blog)
function openCreateArticle() {
    window.open("user_createArticle.html", "_self");
}
function closeCreateArticle() {
    window.open("user_blog.html", "_self");
}
function clearCreateArticle() {
    document.getElementById("articleTitle").value = "";
    document.getElementById("articleDesc").value = "";
}
function openArticle(articleID){
	window.location.href = "user_articleView.html?articleID="+articleID;
}
function openMyArticle(articleID){
	window.location.href = "user_articleUserView.html?articleID="+articleID;
}
function openViewMyArticle() {
    window.open("user_blogUser.html", "_self");
}
function closeViewMyArticle() {
    window.open("user_blog.html", "_self");
}

/* ======================================================================================== */
function openNewPlantAdd() {
	window.open("agri_createPlant.html", "_self");
}

function openProduct(productID){
	window.location.href = "user_productSingle.html?productID="+productID;
}



/* TRANSITIONS IN TEMPLATE BLOCKS (BELOW ONWARDS ALL THE FUNCTIONS ARE RELATED TO TEAMPLATE BLOCKS)*/ 
//Display Description of Selected Plant
function openPlantDesc(plantID) {
	window.location.href = "user_plantDesc.html?plantID="+plantID;
}
function closePlantDesc() {
    window.open("user_plant.html", "_self");
}

//Display Description of Selected Pest
function openPestDesc(pestID) {
	window.location.href = "user_pestDesc.html?pestID="+pestID;
}
//Display Description of Selected Disease
function openDiseaseDesc(diseaseID) {
	window.location.href = "user_diseaseDesc.html?diseaseID="+diseaseID;
}
//Display Description of Selected Shop
function openShopDesc(shopID) {
	window.location.href = "user_shopDesc.html?shopID="+shopID;
}
/* ======================================================================================== */

/* TRANSITIONS IN PLANT DESCRIPTION PAGE */
//Pest window related to plant open and close functions (from plant description)
function openPlantPest(plantID) {
	window.location.href = "user_plantPest.html?plantID="+plantID;
}
function closePlantPest(plantID) {
	window.location.href = "user_plantDesc.html?plantID="+plantID;
}

//Disease window related to plant open and close functions (from plant description)
function openPlantDisease(plantID) {
	window.location.href = "user_plantDisease.html?plantID="+plantID;
}
function closePlantDisease(plantID) {
	window.location.href = "user_plantDesc.html?plantID="+plantID;
}

function closeViewArticle(){
   window.open("user_blogUser.html", "_self");	
}


//---------------- IOT -----------------------

var _fetching = false;
var _setting = false;

function deviceSetup() {
	console.log("wwwww");
}

$(document).ready(function(){
	
	if(window.localStorage.getItem('local-ip') != null) {
		$('#local-ip').val(window.localStorage.getItem('local-ip'));
		iotTimedFetch();
	} else {
		$('#device-1').addClass('setup');
	}
	
	// on open
    $("#setup-btn").click(function(){
        $('#device-1').addClass('setup');
    });

	// on close
	$("#close-btn").click(function(){
		let l_ip = $('#local-ip').val();
		if(/^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/.test(l_ip)) {
			window.localStorage.setItem('local-ip', l_ip);
        	$('#device-1').removeClass('setup');
			$('#reading-status').html("Waiting");
			if(!_fetching) {
				iotTimedFetch();
			}
		}
    });

	$('#state-toggle').change(function() {
		setState();
	})

	// fetch iot status
	function iotTimedFetch() {
		$.ajax({
			method: "GET",
		 	url: "http://"+window.localStorage.getItem('local-ip')+'/status',
			contentType: "application/json; charset=utf-8"
		}).done(function(data) {
			console.log(data);
			
			if(!_setting) {
				if(data.status == "INITIATING") {
					$('#reading-status').html("Starting");
					$('#state-toggle').prop('checked', false);
				} else if(data.status == "IDLE") {
					$('#reading-status').html("Idle");
					$('#state-toggle').prop('checked', false);
				} else if(data.status == "WATERING") {
					$('#reading-status').html("Watering");
					$('#state-toggle').prop('checked', true);
				} else {
					$('#reading-status').html(data.status);
					$('#state-toggle').prop('checked', false);
				}
				
				$('#reading-moisture').html(data.data1);
				$('#reading-humidity').html(data.data2);
				
				$('#state-toggle').prop('disabled', false);
			}

			// fetch again
			setTimeout(function() {
				iotTimedFetch();
			}, 2000);
			
		}).fail(function() {
			
			if(!_setting) {
				$('#reading-moisture').html(0.0);
				$('#reading-humidity').html(0.0);
				$('#reading-status').html("Setup Required");
				$('#state-toggle').prop('checked', false);
				$('#state-toggle').prop('disabled', true);
			}
			
			// retry
			setTimeout(function() {
				iotTimedFetch();
			}, 6000);
		});
	}
	
	function setState() {
		
		_setting = true;
		$('#state-toggle').prop('disabled', true);
		let param = $('#state-toggle').prop('checked') ? "on" : "off";
		$.ajax({
			method: "GET",
		 	url: "http://"+window.localStorage.getItem('local-ip')+'/state?state='+param,
			data: null
		}).done(function() {
			$('#state-toggle').prop('disabled', false);
			_setting = false;
		}).fail(function() {
			$('#state-toggle').prop('disabled', true);
			_setting = false;
		});
	}
 });
