/* TRANSITIONS FROM Agri Instrucotr DASHBOARD */
//plant window open and close functions (from user dashboard-> Find a Plant)
function openUserPlant() {
    window.open("agri_plant.html", "_self");
}
function closeUserPlant() {
    window.open("dashboard.html", "_self");
}

//Pest window open and close functions (from user dashboard-> Find a Pest)
function openUserPest() {
    window.open("agri_pest.html", "_self");
}
function closeUserPest() {
    window.open("dashboard.html", "_self");
}

//Disease window open and close functions (from user dashboard-> Find a Disease)
function openUserDisease() {
    window.open("agri_disease.html", "_self");
}
function closeUserDisease() {
    window.open("dashboard.html", "_self");
}

//Agri Officor Dashboard Icons
function openChat(){
    window.open("agri_chat.html", "_self");
}
function openPlant(){
    window.open("agri_plant.html", "_self");
}
function openPest(){
    window.open("agri_pest.html", "_self");
}
function openDisease(){
    window.open("agri_disease.html", "_self");
}
function openblog(){
    window.open("agri_blog.html", "_self");
}


//Chat window open and close functions (from user dashboard-> Contact Agricultural Instructor)
function openUserChat() {
    window.open("agri_chat.html", "_self");
}
function closeUserChat() {
    window.open("dashboard.html", "_self");
}

//Blog window open and close functions (from user dashboard-> Blog)
function openUserBlog() {
    window.open("agri_blog.html", "_self");
}
function closeUserBlog() {
    window.open("dashboard.html", "_self");
}



//Browse Plant Window open and close function (from user dashboard)
function openBrowsePlant() {
    var area = $('#area').val();
    localStorage.setItem("area", JSON.stringify(area));
    window.open("agri_browsePlant.html", "_self");
}
function closeBrowsePlant() {
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
    window.open("agri_addPlant.html", "_self");
}
function closeAddPlant() {
    window.open("agri_browsePlant.html", "_self");
}

function openMonitorSystem() {
    window.open("agri_toMonitorSystem.html", "_self");
}
function closeMonitorSystem() {
    window.open("agri_addPlant.html", "_self");
}

function openaddMonitorSuccess() {
    document.getElementById("addMonitorSuccess").style.display = "block";
}
function closeaddMonitorSuccess() {
    document.getElementById("addMonitorSuccess").style.display = "none";
    window.open("dashboard.html", "_self");
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
    window.open("agri_createArticle.html", "_self");
}
function closeCreateArticle() {
    window.open("agri_blog.html", "_self");
}
function clearCreateArticle() {
    document.getElementById("articleTitle").value = "";
    document.getElementById("articleDesc").value = "";
}
function openArticle(articleID){
    window.location.href = "agri_articleView.html?articleID="+articleID;
}
function openMyArticle(articleID){
    window.location.href = "agri_articleUserView.html?articleID="+articleID;
}
function openViewMyArticle() {
    window.open("agri_blogUser.html", "_self");
}
function closeViewMyArticle() {
    window.open("agri_blog.html", "_self");
}

/* ======================================================================================== */
function openNewPlantAdd() {
    window.open("agri_createPlant.html", "_self");
}

function openProduct(productID){
    window.location.href = "agri_productSingle.html?productID="+productID;
}


/* TRANSITIONS IN TEMPLATE BLOCKS (BELOW ONWARDS ALL THE FUNCTIONS ARE RELATED TO TEAMPLATE BLOCKS)*/ 
//Display Description of Selected Plant
function openPlantDesc(plantID) {
    window.location.href = "agri_plantDesc.html?plantID="+plantID;
}
function closePlantDesc() {
    window.open("agri_plant.html", "_self");
}

//Display Description of Selected Pest
function openPestDesc(pestID) {
    window.location.href = "agri_pestDesc.html?pestID="+pestID;
}
//Display Description of Selected Disease
function openDiseaseDesc(diseaseID) {
    window.location.href = "agri_diseaseDesc.html?diseaseID="+diseaseID;
}
//Display Description of Selected Shop
function openShopDesc(shopID) {
    window.location.href = "agri_shopDesc.html?shopID="+shopID;
}
/* ======================================================================================== */

/* TRANSITIONS IN PLANT DESCRIPTION PAGE */
//Pest window related to plant open and close functions (from plant description)
function openPlantPest(plantID) {
    window.location.href = "agri_plantPest.html?plantID="+plantID;
}
function closePlantPest(plantID) {
    window.location.href = "agri_plantDesc.html?plantID="+plantID;
}

//Disease window related to plant open and close functions (from plant description)
function openPlantDisease(plantID) {
    window.location.href = "agri_plantDisease.html?plantID="+plantID;
}
function closePlantDisease(plantID) {
    window.location.href = "agri_plantDesc.html?plantID="+plantID;
}

function closeViewArticle(){
   window.open("agri_blogUser.html", "_self");  
}


