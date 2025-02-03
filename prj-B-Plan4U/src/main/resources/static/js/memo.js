function showMemoWrite() {
	// 메모리스트 크기작아짐
	document.querySelector('.functionMemo').classList.add('shrinkedFunctionMemo');
	
	document.getElementById('memoPopupBox').classList.remove('hidden');
	// 메모작성버튼 누르면 메모보던거 hidden
	document.getElementById('viewmemoarea').classList.add('hidden');
	
    document.getElementById('memowritearea').classList.remove('hidden');
}

function showMemoView() {
    document.getElementById('memowritearea').classList.add('hidden');
	document.getElementById('memoPopupBox').classList.add('hidden');

    // 메모리스트 크기커짐
    document.querySelector('.functionMemo').classList.remove('shrinkedFunctionMemo');
}

document.querySelectorAll('.memodiv').forEach(div => {
    div.addEventListener('click', function() {
        // 메모리스트 크기작아짐
        document.querySelector('.functionMemo').classList.add('shrinkedFunctionMemo');
		
		document.getElementById('memoPopupBox').classList.remove('hidden');
        
        // 메모작성칸 hidden
        document.getElementById('memowritearea').classList.add('hidden');
		
		document.getElementById('viewmemoarea').classList.remove('hidden');

        // 데이터 추출
        const memoId = this.getAttribute('data-memo-id');
        const memoBlock = this.querySelector('.memo-block');
        const memoInfo = this.querySelector('.memoinfo');

        // 자식 요소를 올바르게 선택
        const memoContent = memoBlock.children[1].innerText.trim();
        const memoUser = memoInfo.children[1].innerText.trim();
        const memoDate = memoInfo.children[2].innerText.trim();
        
        const formatDate = memoDate.slice(0,16).replace('T', ' ');


        // 뷰 업데이트
        document.getElementById('viewmemoarea').classList.remove('hidden');
        document.getElementById('viewmemocontent').innerHTML = `
			<div class="memotitle">
            <p><strong>작성일:</strong> ${formatDate}</p>
            <p><strong>작성자:</strong> ${memoUser}</p>
			</div>
            <p class="memotext">${memoContent}</p>  
        `;
    });
});

// 메모 보기 화면의 닫기 버튼 클릭 시 처리
document.getElementById('closeviewmemo').addEventListener('click', function() {
    document.getElementById('viewmemoarea').classList.add('hidden');
	document.getElementById('memoPopupBox').classList.add('hidden');

    // 메모리스트 크기커짐
    document.querySelector('.functionMemo').classList.remove('shrinkedFunctionMemo');
});

// 메모 미리보기에 날짜 형식 간단하게 표기
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.memo-date').forEach(function(element) {
        const memoDate = element.textContent.trim();
        const formatDate = memoDate.slice(0,16).replace('T', ' ');
        element.textContent = formatDate;
    })
})