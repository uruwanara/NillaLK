function openUpdateProduct() {
    window.open("shop_updateProduct.html", "_self");
}
function closeUpdateProduct() {
    window.open("shop_dashboard.html", "_self");
}

function openAddNewProduct(shopID) {
	window.location.href = "shop_addNewProduct.html?shopID="+shopID;
}
function closeAddNewProduct() {
    window.open("shop_dashboard.html", "_self");
}

function openReqNewItem(shopID) {
//    window.open("shop_reqNewItem.html", "_self");
    window.location.href = "shop_reqNewItem.html?shopID="+shopID;
}
function closeReqNewItem(shopID) {
     window.location.href = "shop_dashboard.html?shopID="+shopID;
}

function openReqPrioritize() {
    window.open("shop_reqPrioritize.html", "_self");
}
function closeReqPrioritize() {
    window.open("shop_dashboard.html", "_self");
}

function openUpdateProduct(shopID) {
    window.location.href = "shop_updateProduct.html?shopID="+shopID;
}
function closeUpdateProduct() {
    window.open("shop_dashboard.html", "_self");
}

function openAddNewShop() {
    window.open("shopAddNewShop.html", "_self");
}


function openShopDesc(shopID){
	window.location.href = "shop_dashboard.html?shopID="+shopID;
}

function openAddProduct(productID){
	localStorage.setItem("productID", productID)
	document.getElementById("addProducttoShop").style.display = "block";
}
function closeAddProduct(){
	document.getElementById("addProducttoShop").style.display = "none";
}

function openUpdateShopProduct(productID){
	localStorage.setItem("productID", productID)
	document.getElementById("updateProducttoShop").style.display = "block";    
}
function closeUpdateShopProduct(){
	document.getElementById("updateProducttoShop").style.display = "none";
}

/*
var requestType;

NewProduct.onclick = getNewProduct;
function getNewProduct(){
    openReqNewItem("NewProduct"); 
}
*/

function openUploadImg() {
    document.getElementById("uploadImage").style.display = "block";
}
function closeUploadImg() {
    document.getElementById("uploadImage").style.display = "none";
}