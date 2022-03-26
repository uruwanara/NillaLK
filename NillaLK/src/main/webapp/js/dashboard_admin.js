// open and close shop registration request window
function openR_shopRegistration() {
	var requestType = "RegShop";
	window.location.href = "request_registration.html?type="+requestType;
//    window.open("request_registration.html", "_self");
}
function closeR_shopRegistration() {
    window.open("request.html", "_self");
}

// open and close shop registration request window
function openR_shopPrioritize() {
	var requestType = "Prioritize";
	window.location.href = "request_registration.html?type="+requestType;   
}
function closeR_shopPrioritize() {
    window.open("request.html", "_self");
}

// open and close shop registration request window
function openR_reviseContent() {
	var requestType = "ChangeCont";
	window.location.href = "request_registration.html?type="+requestType;
}
function closeR_reviseContent() {
    window.open("request.html", "_self");
}

// open and close shop registration request window
function openR_addNewItem() {
	var requestType = "NewProduct";
	window.location.href = "request_newItem.html?type="+requestType;
//    window.open("request_newItem.html", "_self");
}
function closeR_addNewItem() {
    window.open("request.html", "_self");
}
function closeD() {
    window.open("dashboard.html", "_self");	
}
function openRequest(requestID) {
	window.location.href = "request_view.html?requestID="+requestID;
}


function openpswChange() {
    document.getElementById("pswChange").style.display = "block";
}
function closepswChange() {
    document.getElementById("pswChange").style.display = "none";
}

function openCreateAdd() {
	window.open("admin_createAdd.html", "_self");	
}


/* TRANSITIONS IN BLOG */
//Create article for blogs open and close functions (from blog)
function openCreateArticle() {
    window.open("admin_articleCreate.html", "_self");
}
function closeCreateArticle() {
    window.open("admin_blog.html", "_self");
}
function clearCreateArticle() {
    document.getElementById("articleTitle").value = "";
    document.getElementById("articleDesc").value = "";
}
function openArticle(articleID){
	window.location.href = "admin_articleView.html?articleID="+articleID;
}
function closeViewArticle() {
    window.open("admin_blog.html", "_self");
}
function openChangeState(articleID) {
	document.getElementById("changeArticleState").style.display = "block";
}
function closeChangeState(articleID) {
	document.getElementById("changeArticleState").style.display = "none";
	window.location.href = "admin_articleView.html?articleID="+articleID;
}
function openViewMyArticle() {
	window.open("admin_blogUser.html", "_self");
}
function closeViewMyArticle() {
	window.open("admin_blog.html", "_self");
}
function openMyArticle(articleID){
	window.location.href = "admin_articleUserView.html?articleID="+articleID;
}


/* TRANSITIONS IN TEMPLATE BLOCKS (BELOW ONWARDS ALL THE FUNCTIONS ARE RELATED TO TEAMPLATE BLOCKS)*/ 
//Display Description of Selected Plant
function openPlantDesc(plantID) {
	window.location.href = "admin_plantView.html?plantID="+plantID;
}
function closePlantDesc() {
    window.open("admin_plant.html", "_self");
}
function openAddPlant() {
    window.open("admin_plantCreate.html", "_self");
}
function closeAddPlant() {
    window.open("admin_plant.html", "_self");
}
function openEditPlant(plantID) {
    window.open("admin_plantEdit.html", "_self");
}
function closeEditPlant(plantID) {
	window.location.href = "admin_plantView.html?plantID="+plantID;
}

/* TRANSITIONS IN PLANT DESCRIPTION PAGE */
//Pest window related to plant open and close functions (from plant description)
function openPlantPest(plantID) {
	window.location.href = "admin_plantViewPest.html?plantID="+plantID;
}
function closePlantPest(plantID) {
	window.location.href = "admin_plantDesc.html?plantID="+plantID;
}

//Disease window related to plant open and close functions (from plant description)
function openPlantDisease(plantID) {
	window.location.href = "admin_plantViewDisease.html?plantID="+plantID;
}
function closePlantDisease(plantID) {
	window.location.href = "user_plantDesc.html?plantID="+plantID;
}

//Fertilizer window related to Plant
function openPlantFertilizer(plantID) {
	window.location.href = "admin_plantViewFertilizer.html?plantID="+plantID;
}
function closePlantFertilizer(plantID) {
	window.location.href = "admin_plantDesc.html?plantID="+plantID;
}





function openAdvertiseCreate() {
	window.open("admin_advertiseCreate.html", "_self");
}
function closeAdvertiseCreate() {
	window.open("admin_advertise.html", "_self");
}
function showAdvertisement(addID) {
	window.location.href = "admin_advertiseView.html?addID="+addID;	
}
function closeAdvertiseView() {
	window.open("admin_advertise.html", "_self");
}



function openPestDesc(pestID) {
	window.location.href = "admin_pestView.html?pestID="+pestID;
}
function closePestDesc() {
	window.open("admin_pest.html", "_self");
}


function openDiseaseDesc(diseaseID) {
	window.location.href = "admin_diseaseView.html?diseaseID="+diseaseID;
}
function closeDiseaseDesc() {
	window.open("admin_disease.html", "_self");
}


function openViewUser(userID){
	window.location.href = "admin_userSingleView.html?userID="+userID;
}
function closeViewSingleUser(){
	window.open("admin_userView.html", "_self");
}


function openUploadImg() {
    document.getElementById("uploadImage").style.display = "block";
}
function closeUploadImg() {
    document.getElementById("uploadImage").style.display = "none";
}