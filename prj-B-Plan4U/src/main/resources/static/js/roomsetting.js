function deluser(event) {
    // 사용자의 이메일과 방 ID를 가져옵니다.
    const email = event.target.getAttribute('data-email');
    const roomId = event.target.getAttribute('data-roomId'); // 수정: 'roomId' 변수 추가
    
    if (!email || !roomId) {
        alert('필요한 정보가 누락되었습니다.');
        return;
    }
    
    // 삭제 확인 대화상자를 표시합니다.
    const confirmDelete = confirm('정말로 이 사용자를 삭제하시겠습니까?');
    
    if (!confirmDelete) {
        // 사용자가 취소를 클릭하면 아무 작업도 하지 않습니다.
        return;
    }

    // URL 인코딩된 데이터 생성
    const params = new URLSearchParams();
    params.append('email', email);
    params.append('id', roomId);

    fetch(`/api/delguest/${roomId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: params.toString() // URL 인코딩된 데이터를 문자열로 변환하여 전송
    })
    .then(response => {
        if (response.ok) {
            location.reload(); // 페이지를 새로고침하여 변경 사항을 반영
        } else {
            alert('삭제 실패');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('삭제 중 오류가 발생했습니다.');
    });
}

/*-----------------------------------------------------------------------------------*/

function handleDelete(event) {
    // 버튼의 'data-room-id' 속성에서 방 ID를 가져옵니다.
    const roomId = event.target.getAttribute('data-roomid');
    
    if (!roomId) {
        alert('방 ID가 누락되었습니다.');
        return;
    }
    
    // 삭제 확인 대화상자를 표시합니다.
    const confirmDelete = confirm('정말로 이 방을 삭제하시겠습니까?');
    
    if (!confirmDelete) {
        // 사용자가 취소를 클릭하면 아무 작업도 하지 않습니다.
        return;
    }
    
    // AJAX 요청을 통해 방 삭제 요청을 서버로 전송합니다.
    fetch(`/api/delroom/${roomId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest' // AJAX 요청을 나타내는 헤더
        }
    })
    .then(response => {
        if (response.ok) {
            // 요청이 성공하면 페이지를 새로고침하거나 다른 페이지로 리다이렉트합니다.
            alert('방이 성공적으로 삭제되었습니다.');
            window.location.href = '/app/mainpage'; // 원하는 페이지로 리다이렉트
        } else {
            alert('삭제 실패');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('삭제 중 오류가 발생했습니다.');
    });
}



