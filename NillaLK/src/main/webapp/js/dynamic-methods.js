/* calls the api and loads returns JSON object
		* uri - resource identifier
		* payload - body in json format if required
		* successCallBack - callback function on successful request
		* failureCallBack - callback function on failed request
*/

function getAPI(uri, parameters, successCallBack, failureCallBack) {
	
	let auth_promise = authenticate();
	auth_promise
		.then(function(nonce) {
			var actualURI = window.location.origin+'/'+uri;
	
			$.ajax({
				method: "GET",
			 	url: actualURI,
				headers: { 'nonce': nonce },
			 	data: parameters,
			}).done(function(data) {
				if(successCallBack != null) {
					successCallBack(data);
				}
			}).fail(function(data) {
				if(failureCallBack != null) {
					var error;
					if(data['responseJSON'] == null) {
						error = data['status'];
					} else {
						error = data['responseJSON'];
					}
					failureCallBack(error);
				}
			});
		})
		.catch(function() {
			failureCallBack({'error': 'not authed'});
		})
};

function postAPI(uri, parameters, payload, successCallBack, failureCallBack) {
	
	let auth_promise = authenticate();
	auth_promise
		.then(function(nonce) {
			let actualURI = window.location.origin+'/'+uri;
			if(parameters != null) {
				let urlAppend = new URLSearchParams(parameters).toString();
				actualURI = actualURI + '?' + urlAppend;
			}
			if(typeof(payload) != "string") {
				payload = JSON.stringify(payload);
			};
			$.ajax({
				method: "POST",
			 	url: actualURI,
				headers: { 'nonce': nonce },
			 	data: payload,
				contentType: "application/json; charset=utf-8"
			}).done(function(data) {
				if(successCallBack != null) {
					successCallBack(data);
				}
			}).fail(function(data) {
				if(failureCallBack != null) {
					var error;
					if(data['responseJSON'] == null) {
						error = data['status'];
					} else {
						error = data['responseJSON'];
					}
					failureCallBack(error);
				}
			});
		})
		.catch(function() {
			failureCallBack({'error': 'not authed'});
		})
};

function putAPI(uri, parameters, payload, successCallBack, failureCallBack) {
	
	let auth_promise = authenticate();
	auth_promise
		.then(function(nonce) {
			let actualURI = window.location.origin+'/'+uri;
			if(parameters != null) {
				let urlAppend = new URLSearchParams(parameters).toString();
				actualURI = actualURI + '?' + urlAppend;
			}
			if(typeof(payload) != "string") {
				payload = JSON.stringify(payload);
			};
			$.ajax({
				method: "PUT",
			 	url: actualURI,
				headers: { 'nonce': nonce },
			 	data: payload,
				contentType: "application/json; charset=utf-8"
			}).done(function(data) {
				if(successCallBack != null) {
					successCallBack(data);
				}
			}).fail(function(data) {
				if(failureCallBack != null) {
					var error;
					if(data['responseJSON'] == null) {
						error = data['status'];
					} else {
						error = data['responseJSON'];
					}
					failureCallBack(error);
				}
			});
		})
		.catch(function() {
			failureCallBack({'error': 'not authed'});
		})
};

function deleteAPI(uri, parameters, successCallBack, failureCallBack) {
	
	let auth_promise = authenticate();
	auth_promise
		.then(function(nonce) {
			var actualURI = window.location.origin+'/'+uri;
	
			$.ajax({
				method: "DELETE",
			 	url: actualURI,
				headers: { 'nonce': nonce },
			 	data: parameters,
			}).done(function(data) {
				if(successCallBack != null) {
					successCallBack(data);
				}
			}).fail(function(data) {
				if(failureCallBack != null) {
					
					var error;
					if(data['responseJSON'] == null) {
						error = data['status'];
					} else {
						error = data['responseJSON'];
					}
					failureCallBack(error);
					
				}
			});
		})
		.catch(function() {
			failureCallBack({'error': 'not authed'});
		})
};

function postAPINoAuth(uri, parameters, payload, successCallBack, failureCallBack) {
		
	let actualURI = window.location.origin+'/'+uri;

	if(parameters != null) {
		let urlAppend = new URLSearchParams(parameters).toString();
		actualURI = actualURI + '?' + urlAppend;
	}
	if(typeof(payload) != "string") {
		payload = JSON.stringify(payload);
	};
	$.ajax({
		method: "POST",
	 	url: actualURI,
	 	data: payload,
		contentType: "application/json; charset=utf-8"
	}).done(function(data) {
		if(successCallBack != null) {
			console.log(data);
		    successCallBack(data);
		}
	}).fail(function(data) {
		if(failureCallBack != null) {
			
			var error;
			if(data['responseJSON'] == null) {
				error = data['status'];
			} else {
				error = data['responseJSON'];
			}
			failureCallBack(error);
		}
	});

};

function uploadAPI(payload, successCallBack, failureCallBack) {
	
	let auth_promise = authenticate();
	auth_promise
		.then(function(nonce) {
			let actualURI = window.location.origin+'/upload';
			$.ajax({
				method: "POST",
				enctype: 'multipart/form-data',
			 	url: actualURI,
				headers: { 'nonce': nonce },
			 	data: payload,
				cache: false,
		        contentType: false,
		        processData: false
			}).done(function(data) {
				if(successCallBack != null) {
					successCallBack(data);
				}
			}).fail(function(data) {
				if(failureCallBack != null) {
					var error;
					if(data['responseJSON'] == null) {
						error = data['status'];
					} else {
						error = data['responseJSON'];
					}
					failureCallBack(error);
				}
			});
		})
		.catch(function() {
			failureCallBack({'error': 'not authed'});
		})
};

function authenticate() {
	return new Promise(function(resolve, reject) {
		if(storageAvailable('localStorage')) {
			var usermeta = JSON.parse(window.localStorage.getItem('user-meta'));
			if(usermeta != null) {
				if(new Date(usermeta['jws-expire']) > new Date()) {
					// if jws not expired, return nonce
					resolve(usermeta['nonce']);
				} else if(new Date(usermeta['rjws-expire']) > new Date()) {
					// else if refresh token, update user-meta and return new nonce
					let nonce = usermeta['nonce'];
					$.ajax({
						method: "POST",
					 	url: window.location.origin+'/auth/refresh',
						headers: { 'nonce': nonce },
						contentType: "application/json; charset=utf-8"
					}).done(function(data) {
						window.localStorage.setItem('user-meta', JSON.stringify(data));
						resolve(data['nonce']);
					}).fail(function() {
						reject(null);
					});
				} else {
					// sign in required
					reject(null);
				}
			} else {
				// sign in required
				reject(null);
			}
		} else {
			// localstorage not allowed :( maybe session storage? TODO
			console.log('LocalStorage is not supported in this browser');
			reject(null);
		}
	});
}

function storageAvailable(type) {
    var storage;
    try {
        storage = window[type];
        var x = '__storage_test__';
        storage.setItem(x, x);
        storage.removeItem(x);
        return true;
    }
    catch(e) {
        return e instanceof DOMException && (
            // everything except Firefox
            e.code === 22 ||
            // Firefox
            e.code === 1014 ||
            // test name field too, because code might not be present
            // everything except Firefox
            e.name === 'QuotaExceededError' ||
            // Firefox
            e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
            // acknowledge QuotaExceededError only if there's something already stored
            (storage && storage.length !== 0);
    }
};

function getUserID() {
	if(storageAvailable('localStorage')) {
		var usermeta = JSON.parse(window.localStorage.getItem('user-meta'));
		if(usermeta != null) {
			return usermeta['userID'];
		} else {
			return null;
		}
	} else {
		return null
	}
}

function getUserType() {
	if(storageAvailable('localStorage')) {
		var usermeta = JSON.parse(window.localStorage.getItem('user-meta'));
		if(usermeta != null) {
			return usermeta['userType'];
		} else {
			return null;
		}
	} else {
		return null
	}
}

function GetURLParameter(sParam) {
	
	var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
		}
	}
}

function sanitize(string) {
	const map = {
		'&': '&amp;',
		'<': '&lt;',
		'>': '&gt;',
		'"': '&quot;',
		"'": '&#x27;',
		"/": '&#x2F;',
		"`": '&grave;',
	};
	const reg = /[&<>"'/]/ig;
	return string.replace(reg, (match)=>(map[match]));
}

// If not logged in, show login page. Else if you are not allowed show 403
function amIAllowedHere(userType) {
	if(authenticate == null) {
		location.href = "/login?referer="+location.href;
	} else {
		var usermeta = JSON.parse(window.localStorage.getItem('user-meta'));
		if(usermeta['userType'] != userType) {
			location.href = "/error/403.html";
		}
	}
}