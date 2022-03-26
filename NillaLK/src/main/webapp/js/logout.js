function revokeSuccess() {
	
	window.localStorage.removeItem('user-meta');
	location.href = '/';
	
}

function revokeFailed() {
	console.log("Failed to log out. Maybe you don't need to");
}

function logout() {
	
	postAPINoAuth('auth/revoke', null, null, revokeSuccess, revokeFailed);
	
}