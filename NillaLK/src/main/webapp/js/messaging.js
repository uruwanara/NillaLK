var channelInfo = {};
var activeChannel = {
	id: null,
	user: {
		name: null,
		ing: null
	},
	messages: [],
	page: 1
};
var curUser = null;
var _eventMsgSending = false;
var _fetching = false;

//fetch user info
getAPI(`api/users/${getUserID()}`, null, function (x) {
	curUser = x;
}, function() {
	console.warn("failed loading current user info");
});

fetchChannels();

//fetch channels
function fetchChannels() {
	getAPI("api/channels", null, function(channels) {
		channelInfo = {}; //clear channels
		let uType = getUserType();
		
		//fetch channelInfo
		channels.forEach(function (channel) {
			let userID = (uType == 'Gardener') ? channel.instructorID : channel.gardenerID;
		    getAPI(`api/users/${userID}`, null, function(userInfo) {
				let data = {
					channelID: channel.channelID,
					id: 'channel-'+channel.channelID,
					name: userInfo.firstName+' '+userInfo.lastName,
					imgUrl: userInfo.profilePicture
				}
				channelInfo[data.id] = data; //add channel
				$('#channel-content').loadTemplate($('#template-channel'), data, {append: true});
			}, function() {
				console.warn('Channel: User info load failed')
			});
		});
	}, function() {
		console.warn('Channel: Channel load failed')
	});
};

function loadMessages(channelID) {
	let intID = channelID.match(/[0-9]+/)[0];
	if(intID != activeChannel.id && !_fetching) {
		_fetching = true;
		$('#message-content').children().remove();
		setActiveChannel(channelID, intID);
		$('#chatbox').css('visibility', 'visible');
		setTimeout(function() {
			getAPI(`api/channels/${intID}/messages`, null, function(messages) {
				// set active channel info
				activeChannel.id = intID;
				activeChannel.user = {
					name: channelInfo[channelID].name.split(" ")[0],
					img: channelInfo[channelID].imgUrl
				};
				activeChannel.messages = [];
				activeChannel.page = 1;
				
				userImg = ((curUser != null) ? curUser.profilePicture : "/img/avatars/gardener.png");
				if(messages != null) {
					messages.forEach(message => addMessage(message, userImg));
				}
				messageTimedFetch();
				_fetching = false;
			}, function() {
				console.warn('Channel: Message load failed')
			});
  		}, 100);
	}
}

function setActiveChannel(channelID, intID) {
	$(`#${channelID}`).addClass("active");
	$('.channel').each(function(index, element) {
		if(element.id != channelID) {
			$(element).removeClass("active");
		}
	})
}

function fetchNewMessages() {
	if(activeChannel != null) {
		getAPI(`api/channels/${activeChannel.id}/messages`, null, function(messages) {
			userImg = ((curUser != null) ? curUser.profilePicture : "/img/avatars/gardener.png");
			if(messages != null) {
				messages.reverse().forEach(message => addMessage(message, userImg, true));
			}
		}, function() {
			console.error("Channel: failed to fetch new messages");
		});
	}
}

function fetchOldMessages() {
	if(activeChannel != null) {
		activeChannel.page++;
		getAPI(`api/channels/${activeChannel.id}/messages`, {page: activeChannel.page}, function(messages) {
			userImg = ((curUser != null) ? curUser.profilePicture : "/img/avatars/gardener.png");
			messages.reverse().forEach(message => addMessage(message, userImg));
		}, function() {
			console.error("Channel: failed to fetch new messages");
		});
	}
}

function addMessage(message, userImg, prepend = false) {
	if(!activeChannel.messages.includes(message.msgID)) {
		activeChannel.messages.push(message.msgID);
		
		if(/^\!img\s/.test(message.text)) {
			let data = {
				id: 'message-'+message.msgID,
				name: (message.userID == getUserID()) ? "You" : activeChannel.user.name,
				time: (typeof(message.timestamp) == 'string' ? moment(new Date(message.timestamp)).calendar() : moment(message.timestamp).calendar()),
				preImgUrl: message.text.substr(5),
				imgUrl: (message.userID == getUserID()) ? userImg : activeChannel.user.img,
				type: (message.userID == getUserID()) ? "message sent" : "message"
			}
			
			if(prepend) {
				$('#message-content').loadTemplate($('#template-message-img'), data, {prepend: true});
			} else {
				$('#message-content').loadTemplate($('#template-message-img'), data, {append: true});
			}
		} else {
			let data = {
				id: 'message-'+message.msgID,
				name: (message.userID == getUserID()) ? "You" : activeChannel.user.name,
				time: (typeof(message.timestamp) == 'string' ? moment(new Date(message.timestamp)).calendar() : moment(message.timestamp).calendar()),
				text: message.text,
				imgUrl: (message.userID == getUserID()) ? userImg : activeChannel.user.img,
				type: (message.userID == getUserID()) ? "message sent" : "message"
			}
			
			if(prepend) {
				$('#message-content').loadTemplate($('#template-message'), data, {prepend: true});
			} else {
				$('#message-content').loadTemplate($('#template-message'), data, {append: true});
			}
		}
	}
}

function sendMessage(text) {
	if(activeChannel.id != null) {
		_eventMsgSending = true;
		postAPI(`api/channels/${activeChannel.id}`, null, text, function(data) {
			console.log("Channel: message sent");
			let message = {
				msgID: data.id,
				text: text,
				userID: getUserID()
			}
			userImg = ((curUser != null) ? curUser.profilePicture : "/img/avatars/gardener.png");
			addMessage(message, userImg, true);
			_eventMsgSending = false;
			$('#chat-input').val("");
			$('#button-send').attr("disabled", true);
		}, function() {
			console.error("Channel: message sent failed");
			_eventMsgSending = false;
		});
	}
}

function messageTimedFetch() {
	setTimeout(function() {
		if(!_eventMsgSending) {
			fetchNewMessages();
		}
		messageTimedFetch();
	}, 3000);
}

$('#chat-input').on('input', function() {
	if($('#chat-input').val().length > 0) {
		$('#button-send').attr("disabled", false);
	} else {
		$('#button-send').attr("disabled", true);
	}
});

$('#button-send').click(function() {
	let text = $('#chat-input').val();
	if(text != null && text.length <= 3000) {
		text = sanitize(text);
		sendMessage(text);
	}
});

$('#button-upload').click(function() {
	$('#f-upload-window').show();
});

$('body').loadTemplate('/templates/file-upload.html', {type: 'Other', scope: 'Public'}, {append: true, success: function() {
	$("#upload-form").submit(function(e) {
	    e.preventDefault();
		
		console.log('uploading...')
		
		uploadAPI(new FormData(e.target), function(data) {
			//on success
			sendMessage("!img "+data.path);
			closeUploadWindow();
		}, function(error) {
			console.error(error);
			//on failure
		});
	});
}});