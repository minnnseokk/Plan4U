document.getElementById('imageInput').addEventListener('change', function(event) {
	const imageInput = event.target;
	const imageInputButton = document.getElementById('imageInputButton');

	if (imageInput.files.length > 0) {
		const imageNames = Array.from(imageInput.files).map(file => file.name).join(', ');
		imageInputButton.textContent = imageNames;
	} else {
		imageInputButton.textContent = '이미지 선택';
	}
})


/*------------------------------------------------------------------------------------*/

document.addEventListener('DOMContentLoaded', function() {
    const imageUploadForm = document.getElementById('imageUploadForm');
    const imageInput = document.getElementById('imageInput');
    const previewContainer = document.getElementById('imagePreview');
    const roomId = imageUploadForm.getAttribute('action').split('/').pop(); // action URL에서 roomId 추출

    // 파일 선택 시 미리보기 표시
    imageInput.addEventListener('change', function() {
        previewContainer.innerHTML = ''; // 기존 미리보기 제거
        const files = imageInput.files;
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const reader = new FileReader();

            reader.onload = function(event) {
                const img = document.createElement('img');
                img.src = event.target.result;
                // img.style.width = '100px'; // 원하는 크기로 조절
                // img.style.height = '100px';
                img.style.width = '100%';
                previewContainer.appendChild(img);
            };

            reader.readAsDataURL(file);
        }
    });

    // 폼 제출 시 이미지 업로드
    imageUploadForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(event.currentTarget);

        fetch(`/api/gallery/${roomId}`, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.redirected) {
                // 리다이렉션이 발생한 경우
                window.location.href = response.url;
            } else {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
        })
        .catch(error => {
            console.error('업로드 중 오류 발생:', error);
            alert('파일 업로드에 실패했습니다. 다시 시도해 주세요.');
        });
    });
});








