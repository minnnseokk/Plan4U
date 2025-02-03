let websocket = new WebSocket("ws://plan4u.kafolio.kr/home");
// ip항상 바꿔야 함

// roomId가 클라이언트까지 잘 도착하긴 함, 근데 서버로 보내는게 메세지 처리부분에서 추출해서 사용해야함
// let roomId = [[${room.id}]] ; 
websocket.onopen = () => {
	//roomId:roomId;
};

let chatting_input = document.querySelector("input#chatting_input");
let chatting_area = document.querySelector("#chatting_area");
let chatting_template = document.querySelector("template");

chatting_input.addEventListener("keydown", (e) => {
	if (e.key == "Enter") {
		if (chatting_input.value.trim() !== "") {
			websocket.send(chatting_input.value);
			chatting_input.value = "";
		}
		e.preventDefault();
	}
});

document.querySelector("button[type=button]").addEventListener("click", () => {
	if (chatting_input.value.trim() !== "") {
		websocket.send(chatting_input.value);
		chatting_input.value = "";
	}
});

// ** 빨간줄 오류 아님 - authUser 이름 가져오기 

websocket.onmessage = (e) => {
	let chat = chatting_template.content.cloneNode(true);

	if (e.data.startsWith("/status ")) {
		// 사용자의 상태 업데이트 처리
		let userStatus = e.data.substring(8).split(",");
		let onlineUsers = userStatus
			.filter(status => status.split(":")[1] === "online")
			.map(status => status.split(":")[0]);
		document.querySelector("#user-count").textContent = onlineUsers.length;
		// 사용자 이름을 '/'로 구분하여 표시
		document.querySelector("#user-list").textContent = onlineUsers.join(" :: ");
	}
	else if (e.data.startsWith("/pop ")) {
		// "/announce " 이후의 메시지 추출
		let announceMessage = e.data.substring(5);
		// 공지사항 메시지에 사용자 이름을 추가
		let formattedMessage = "<<" + announceMessage + ">>";
		// 공지사항 영역에 메시지 업데이트
		let announceElement = document.querySelector('#announce');
		// 기존 내용을 지우고 새 메시지를 설정
		announceElement.innerHTML = formattedMessage;
	}
	else if (e.data.startsWith("/horror ")) {
	       // 공포 이벤트 처리
	       let horrorMessage = e.data.substring(8);
	       showHorrorEffect();	   
	}
	else if (e.data.startsWith("/rickroll ")) {
	       showRickRoll();	   
	}
	else if (e.data.startsWith("/birthday ")) {
		   let birthMessage = e.data.substring(10);
	       // 생일 이벤트 처리
	       birthDayEffect(birthMessage);	   
	}
	else if (e.data.startsWith("/snow")) {
        applySnowTheme();
    } 
	else if (e.data.startsWith("/summer")) {
        applySummerTheme();
    } 
	else if (e.data.startsWith("/deletetheme")) {
        removeTheme();
    }
	
	
	
	else if (e.data == "/kick") {
		window.location.href = "https://t4.ftcdn.net/jpg/02/97/51/89/360_F_297518931_vkRgsfWUp5NiXDakG6446oPXWMsqdM4P.jpg";
	} else {
		// 본인과 상대방 채팅 구분
		const [author, chatting] = e.data.split("ⓐ");
		if (author === "noAuthorUser") {
			// 에러 메시지를 HTML에 표시
			alert(chatting); // 간단한 방법으로 alert를 사용
		} else {
			if (author === currentUserName) {
				chat.querySelector(".chatting").textContent = chatting;
				chat.querySelector(".chat").classList.add("myMessage");
			} else {
				chat.querySelector(".author").textContent = author;
				chat.querySelector(".chatting").textContent = "\u00A0" + chatting;
				chat.querySelector(".chat").classList.add("otherMessage");
			}
		}
		chatting_area.appendChild(chat);
		// 이건 전체 화면 위로 올려주기
		//window.scrollTo(0, 0); 
		// 이건 스크롤 화면 맨 아래칸으로 고정
		chatting_area.scrollTop = chatting_area.scrollHeight;
	}
};

// 공포 효과를 보여주는 함수
function showHorrorEffect() {
	
    let horrorOverlay = document.createElement("div");
    horrorOverlay.id = "horror-overlay";
    horrorOverlay.style.position = "fixed";
    horrorOverlay.style.top = 0;
    horrorOverlay.style.left = 0;
    horrorOverlay.style.width = "100%";
    horrorOverlay.style.height = "100%";
    horrorOverlay.style.backgroundImage = "url('/image/horror.jpg')";
    horrorOverlay.style.backgroundSize = "cover";
    horrorOverlay.style.backgroundPosition = "center";
    horrorOverlay.style.zIndex = 1000;
    horrorOverlay.style.opacity = 1;
    document.body.appendChild(horrorOverlay);

    setTimeout(() => {
        horrorOverlay.style.transition = "opacity 2s ease-out";
        horrorOverlay.style.opacity = 0;
        setTimeout(() => {
            document.body.removeChild(horrorOverlay);
        }, 2000);
    }, 1000); // 5초 후에 시작
}
function showRickRoll() {
	
    let horrorOverlay = document.createElement("div");
    horrorOverlay.id = "horror-overlay";
    horrorOverlay.style.position = "fixed";
    horrorOverlay.style.top = 0;
    horrorOverlay.style.left = 0;
    horrorOverlay.style.width = "100%";
    horrorOverlay.style.height = "100%";
    horrorOverlay.style.backgroundImage = "url('/image/rickroll.gif')";
    horrorOverlay.style.backgroundSize = "150px";
    horrorOverlay.style.backgroundPosition = "center";
    horrorOverlay.style.zIndex = 1000;
    horrorOverlay.style.opacity = 1;
    document.body.appendChild(horrorOverlay);

    setTimeout(() => {
        horrorOverlay.style.transition = "opacity 2s ease-out";
        horrorOverlay.style.opacity = 0;
        setTimeout(() => {
            document.body.removeChild(horrorOverlay);
        }, 2000);
    }, 3000); // 5초 후에 시작
}
function birthDayEffect(message) {
	
    // 생일 축하 메시지 요소 생성
    let messageElement = document.createElement("div");
    messageElement.style.position = "fixed";
    messageElement.style.top = "50%";
    messageElement.style.left = "50%";
    messageElement.style.transform = "translate(-50%, -50%)";
    messageElement.style.color = "black";
    messageElement.style.fontSize = "3rem";
    messageElement.style.fontWeight = "bold";
    messageElement.style.zIndex = 1001;
    messageElement.style.textShadow = "2px 2px 4px rgba(0, 0, 0, 0.5)";
    messageElement.textContent = message;

    document.body.appendChild(messageElement);

    let horrorOverlay = document.createElement("div");
    horrorOverlay.id = "horror-overlay";
    horrorOverlay.style.position = "fixed";
    horrorOverlay.style.top = 0;
    horrorOverlay.style.left = 0;
    horrorOverlay.style.width = "100%";
    horrorOverlay.style.height = "100%";
    horrorOverlay.style.backgroundImage = "url('/image/birthday.png')";
    horrorOverlay.style.backgroundSize = "cover";
    horrorOverlay.style.backgroundPosition = "center";
    horrorOverlay.style.zIndex = 1000;
    horrorOverlay.style.opacity = 1;
    horrorOverlay.style.animation = "zoomInOut 5s ease-in-out";

    document.body.appendChild(horrorOverlay);

    setTimeout(() => {
        horrorOverlay.style.transition = "opacity 2s ease-out";
        horrorOverlay.style.opacity = 0;
        messageElement.style.transition = "opacity 2s ease-out";
        messageElement.style.opacity = 0;
        setTimeout(() => {
            document.body.removeChild(horrorOverlay);
            document.body.removeChild(messageElement);
        }, 2000);
    }, 4000); // 3초 후에 시작

    // 간단한 애니메이션 추가 (줌 인/줌 아웃 효과)
    const style = document.createElement('style');
    style.innerHTML = `
        @keyframes zoomInOut {
            0% {
                transform: scale(1);
            }
            30% {
                transform: scale(1.6);
            }
            100% {
                transform: scale(1);
            }
        }
    `;
    document.head.appendChild(style);
}
function applySnowTheme() {
	// 이미 적용된 테마가 있으면 중복 적용되지 않도록 방지
	let overlay = document.querySelector("#theme");
	if (overlay) {
	       document.body.removeChild(overlay);
    }

    // 새로운 div 요소를 만들어서 화면 전체를 덮도록 설정
    let snowOverlay = document.createElement("div");
    snowOverlay.id = "theme";  // id를 'theme'로 통일
    snowOverlay.style.position = "fixed";
    snowOverlay.style.top = 0;
    snowOverlay.style.left = 0;
    snowOverlay.style.width = "100%";
    snowOverlay.style.height = "100%";
    snowOverlay.style.backgroundImage = "url('/image/snow.gif')";
    snowOverlay.style.backgroundSize = "cover";
    snowOverlay.style.backgroundPosition = "center";
    snowOverlay.style.zIndex = -1;
    snowOverlay.style.opacity = 0.8;

    // body에 추가
    document.body.appendChild(snowOverlay);
}

function applySummerTheme() {
    // 이미 적용된 테마가 있으면 중복 적용되지 않도록 방지
	let overlay = document.querySelector("#theme");
	if (overlay) {
	       document.body.removeChild(overlay);
    }

    // 새로운 div 요소를 만들어서 화면 전체를 덮도록 설정
    overlay = document.createElement("div");
    overlay.id = "theme";  // id를 'theme'로 통일
    overlay.style.position = "fixed";
    overlay.style.top = 0;
    overlay.style.left = 0;
    overlay.style.width = "100%";
    overlay.style.height = "100%";
    overlay.style.backgroundImage = "url('/image/summer2.gif')";
    overlay.style.backgroundSize = "cover";
    overlay.style.backgroundPosition = "center";
    overlay.style.zIndex = -1; 
    overlay.style.opacity = 0.8;

    // body에 추가
    document.body.appendChild(overlay);
}

function removeTheme() {
    // 테마를 제거
    let overlay = document.querySelector("#theme");
    if (overlay) {
        document.body.removeChild(overlay);
    }
}