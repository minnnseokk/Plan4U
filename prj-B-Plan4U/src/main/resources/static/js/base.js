// 메모 미리보기에 날짜 형식 간단하게 표기
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.memo-date').forEach(function(element) {
        const memoDate = element.textContent.trim();
        const formatDate = memoDate.slice(0,16).replace('T', ' ');
        element.textContent = formatDate;
    });
	
	// 숫자 포맷팅
	    document.querySelectorAll('.calSpanNum').forEach(function(element) {
	        const textContent = element.textContent.trim();
	        const number = parseFloat(textContent.replace(/,/g, '')); // 기존 쉼표 제거 후 숫자로 변환
	        if (!isNaN(number)) {
	            element.textContent = number.toLocaleString(); // 3자리마다 쉼표 추가
	        }
	});
})