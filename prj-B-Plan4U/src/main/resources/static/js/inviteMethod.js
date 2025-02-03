let timeout = null; // 전역 타임아웃 변수 초기화

function debounce(callback, delay) {
    return function(...args) { // 이게 함수를 끝내는 return의 호출을 늘리는 거
        if (timeout) { 
            clearTimeout(timeout); // 이전 타이머가 존재하면 비우는거
        }
        timeout = setTimeout(() => { // timeout에 callback할 함수를 설정하고(searchUser함수가 callback 함수가 됨) 
            callback.apply(this, args); 
        }, delay); // 지정된 시간 후에 return함수를 콜백하게 됨
    };
}

// 실제로 사용하게 될 지연이 적용된 함수 0.5초 지연
const debouncedSearchUser = debounce(searchUser, 500); 

function searchUser(){
    const name = document.getElementById("name").value; // 입력된 이름 변수에 저장
    
    if(name === ""){
       document.getElementById("dropdownContainer").innerHTML = "";
       return; // 이름이 공백일경우 드랍다운 요소들을 모두 지움
    }
    
    fetch('/api/search',{ // fetch를 통해 apiController의 데이터 불러옴
       method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded', // name을 가져오기 인식하기위한 타입 설정
            },
            body: `name=${encodeURIComponent(name)}`, // 이름 인식 후 요청 본문에 추가
        })
        .then(response => response.text())
        .then(htmlFragment => {
          document.getElementById("dropdownContainer").innerHTML = htmlFragment;
		  container.offsetHeight;
       })
        .catch(error => console.error('Error:', error));
}

function manageInviteList(event){
    event.preventDefault(); // 링크의 기본 동작을 방지합니다.

     // 클릭된 요소에서 이메일을 가져옵니다.
     const email = event.target.getAttribute('data-email'); // 링크나 div가 생성될때마다 데이터를 data-email에 저장해서 실시간으로 넘긴다.
    	
    fetch('/api/invite',{
       method: 'POST',
          headers:{
             'Content-Type':'application/x-www-form-urlencoded',
          },
          body: `email=${encodeURIComponent(email)}`,
       })
       .then(response => response.text())
        .then(htmlFragment => {
          document.getElementById("finalEmails").innerHTML = htmlFragment;
		  // 렌더링 후 스크롤을 맨 아래로 이동
		  requestAnimationFrame(() => {
		  	const inviteScroll = document.getElementById("inviteScroll");
		    inviteScroll.scrollTop = inviteScroll.scrollHeight;
		  });
		  
		  })
        .catch(error => console.error('Error:', error));
}

// 이미지 미리보기
document.addEventListener('DOMContentLoaded', function() {
    const filesInput = document.getElementById('files');
    const imgPreviewDiv = document.getElementById('imgPreviewDiv');

    filesInput.addEventListener('change', function() {
        imgPreviewDiv.innerHTML = ''; // 기존 미리보기 삭제

        const files = filesInput.files;
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const reader = new FileReader();

            reader.onload = function(event) {
                const img = document.createElement('img');
                img.src = event.target.result;
                imgPreviewDiv.appendChild(img);
            };

            reader.readAsDataURL(file);
        }
    });
});
