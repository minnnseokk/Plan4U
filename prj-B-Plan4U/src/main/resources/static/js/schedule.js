// 일정을 저장할 배열
let scheduleData = {
    dates: [],
    startTimes: [],
    endTimes: [],
    contents: [],
};

function saveSchedule() {
    // URL에서 날짜 정보를 추출
    const urlParams = new URLSearchParams(window.location.search);
    const dates = urlParams.get('dates');
    const datesArray = dates ? dates.split(',') : [];
    const roomId = document.getElementById('roomId').getAttribute('data-room-id');


	   datesArray.forEach(dateStr => {
	       if (!dateStr) return;

	       const timeElements = document.querySelectorAll(`#schedule-${dateStr} input[name="startTime"]`);
	       const endTimeElements = document.querySelectorAll(`#schedule-${dateStr} input[name="endTime"]`);
	       const contentElements = document.querySelectorAll(`#schedule-${dateStr} textarea[name="content"]`);

	       timeElements.forEach((timeElement, index) => {
	           const startTime = timeElement.value;
	           const endTime = endTimeElements[index] ? endTimeElements[index].value : '';
	           const content = contentElements[index] ? contentElements[index].value : '';

	           if (content && content.trim() !== '') {
	               if (endTime && startTime > endTime) {
	                   alert(`종료 시간이 시작 시간보다 이릅니다. 날짜: ${dateStr}, 시작 시간: ${startTime}, 종료 시간: ${endTime}`);
	                   return;
	               }
	               scheduleData.dates.push(dateStr);
	               scheduleData.startTimes.push(startTime);
	               scheduleData.endTimes.push(endTime);
	               scheduleData.contents.push(content); // 잘 넘어오는거 확인함, 날짜도따로 잘 들어옴
	           }
	       });
	   });
	   
		   const formBody = new URLSearchParams();
	
		   formBody.append('roomId', roomId);
		   
		   scheduleData.dates.forEach((date, index) => {
		       formBody.append('dates[]', date);
		       formBody.append('startTimes[]', scheduleData.startTimes[index]);
		       formBody.append('endTimes[]', scheduleData.endTimes[index]);
		       formBody.append('contents[]', scheduleData.contents[index]);
		   });
		   
		   /*console.log("Sending data:");
		       formBody.forEach((value, key) => {
		           console.log(`${key}: ${value}`);
		       });*/
			   
	 fetch('/api/schedule/save', {
	       method: 'POST',
	       headers: {
	           'Content-Type': 'application/x-www-form-urlencoded',
	       },
	       body: formBody.toString()
	   })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(data => {
        alert('일정이 저장되었습니다.');
        document.getElementById('scheduleForm').reset();
		window.location.href = `/app/calendar/${roomId}`; 
    })
    .catch(error => {
        console.error('Error:', error);
        alert('일정 저장 중 오류가 발생했습니다.');
    });
}

function sortDates(dates) {
    return dates.sort((a, b) => new Date(a) - new Date(b));
}

document.addEventListener('DOMContentLoaded', () => { // 여기는 날짜와 일정 추가 버튼이 생기는 구간
    const dateContainer = document.getElementById('dateContainer');

	// URL에서 날짜 파라미터를 가져옵니다.
	   const urlParams = new URLSearchParams(window.location.search);
	   const dates = urlParams.get('dates');
	   
	   if (dates) {
	       let dateArray = dates.split(',');
	       dateArray = sortDates(dateArray); // 날짜를 오름차순으로 정렬합니다. 
			
	        dateArray.forEach(dateStr => {
	            const dateDiv = document.createElement('div');
	            dateDiv.className = 'swiper-slide'; // swiper-slide로 수정
	            dateDiv.id = `date-${dateStr}`; // 각 날짜에 고유 ID를 부여
	            dateDiv.innerHTML = `
					<div class="date-header">
	                    <span>${dateStr}</span> <br>
	                </div>
	                <div id="schedule-${dateStr}" class="schedules">
	                   	<div class="schedule-item">
							<label class="start" for="start-time-${dateStr}-${Date.now()}">시작 시간
								<button id="allDayButton" type="button" onclick="selectAllDay('${dateStr}')">하루 종일</button><br>
							</label>
					        <input type="time" id="start-time-${dateStr}-${Date.now()}" name="start-time-${dateStr}" required />
					        <label for="end-time-${dateStr}-${Date.now()}">종료 시간</label>
					        <input type="time" id="end-time-${dateStr}-${Date.now()}" name="end-time-${dateStr}" />
					        <label for="content-${dateStr}-${Date.now()}">내용</label>
					        <textarea id="content-${dateStr}-${Date.now()}" name="content-${dateStr}" rows="4"></textarea>
							<button id="scheduleButton" type="button" onclick="saveEvent('${dateStr}', '${Date.now()}')">추가</button>
						</div>
	                </div>
	            `;
	            dateContainer.appendChild(dateDiv);
	        });
	    } else {
	        alert('날짜 정보가 URL에 포함되어 있지 않습니다.');
	    }
		// 시간 일정 관련
		const showDateSchedule = document.getElementById('showDateSchedule');

		if (dates) {
		    let dateArray = dates.split(',');
		    dateArray = sortDates(dateArray); // 날짜를 오름차순으로 정렬합니다. 

		    dateArray.forEach(dateStr => {
		        const dateSlide = document.createElement('div');
		        dateSlide.className = 'swiper-slide'; 
		        dateSlide.id = `date-schedule-${dateStr}`;
		        dateSlide.innerHTML = `
		            <div class="date-header">
		                <span>${dateStr}</span>
		            </div>
		            <div class="schedule-container">
		                <!-- 0시부터 24시까지의 시간 레이블을 생성 -->
		                ${generateTimeSlots(dateStr)}
		            </div>
		        `;
		        showDateSchedule.appendChild(dateSlide);
		    });
		} else {
		    alert('날짜 정보가 URL에 포함되어 있지 않습니다.');
		}
});
function selectAllDay(dateStr) {
	if(dateStr != null){
		// 해당 날짜에 맞는 모든 시작 시간과 종료 시간을 설정
		const scheduleContainer = document.querySelector(`#schedule-${dateStr}`);
	
		// 모든 시작 시간 필드와 종료 시간 필드를 선택
		const startTimeInputs = scheduleContainer.querySelectorAll(`input[name^="start-time"]`);
		const endTimeInputs = scheduleContainer.querySelectorAll(`input[name^="end-time"]`);
	
	    startTimeInputs.forEach(input => {
	        input.value = '00:00';
	    });
	
	    endTimeInputs.forEach(input => {
	        input.value = '23:59';
	    });
	}
	
}
// Helper function to generate 24 time slots
function generateTimeSlots(dateStr) {
    let timeSlotsHTML = '';
    for (let hour = 0; hour < 24; hour++) {
        timeSlotsHTML += `
            <div class="schedule-row">
                <div class="time-label">${hour.toString().padStart(2, '0')}:00</div>
                <div class="event-container" id="events-${dateStr}-${hour}">
                    <!-- 여기는 이벤트가 추가되는 공간  -->
                </div>
            </div>
        `;
    }
    return timeSlotsHTML;
}
function saveEvent(dateStr, uniqueId) {
    const startTime = document.getElementById(`start-time-${dateStr}-${uniqueId}`).value;
    const endTime = document.getElementById(`end-time-${dateStr}-${uniqueId}`).value;
    const content = document.getElementById(`content-${dateStr}-${uniqueId}`).value;

    if (startTime && content.trim() !== '') {

        if (endTime && startTime > endTime) {
            alert(`종료 시간이 시작 시간보다 이릅니다. 날짜: ${dateStr}, 시작 시간: ${startTime}, 종료 시간: ${endTime}`);
            return;
        }

        // 일정 추가를 위한 함수 호출
		addEventToSchedule(dateStr, startTime, endTime, content);
		
        // 입력 폼 초기화 (원하는 경우)
        document.getElementById(`start-time-${dateStr}-${uniqueId}`).value = '';
        document.getElementById(`end-time-${dateStr}-${uniqueId}`).value = '';
        document.getElementById(`content-${dateStr}-${uniqueId}`).value = '';
    } else {
        alert('시작 시간과 내용을 입력해야 합니다.');
    }
}
function addEventToSchedule(dateStr, startTime, endTime, content) {
	// 데이터 유효성 검사
   if (content.trim() === '') {
       alert('일정 내용이 비어있습니다.');
       return;
   }
	// 데이터 배열에 추가
    scheduleData.dates.push(dateStr);
    scheduleData.startTimes.push(startTime);
    scheduleData.endTimes.push(endTime);
    scheduleData.contents.push(content);
	
	const startHour = parseInt(startTime.split(':')[0], 10); // 시작 시간의 시간을 추출
	const endHour = endTime ? parseInt(endTime.split(':')[0], 10) : startHour; // 종료 시간의 시간을 추출
	
    for (let hour = startHour; hour <= endHour; hour++) {
        const eventContainer = document.getElementById(`events-${dateStr}-${hour}`);

        // 기존 "No Events" 메시지 삭제
        const noEventLabel = eventContainer.querySelector('.no-event');
        if (noEventLabel) {
            eventContainer.removeChild(noEventLabel);
        }

        const eventDiv = document.createElement('div');
        eventDiv.className = 'event';
        eventDiv.innerHTML = `<button class="text-button" onclick="deleteEvent('${dateStr}', '${startTime}', '${endTime}', '${content}', ${hour})">${startTime} ~ ${endTime}<br>${content}</button>`;

        eventContainer.appendChild(eventDiv);
    }
}

function deleteEvent(dateStr, startTime, endTime, content, hour) {
    // 삭제할 일정을 찾기 위한 인덱스
    const index = scheduleData.dates.indexOf(dateStr);
    if (index !== -1) {
        // 데이터 배열에서 해당 항목 삭제
        scheduleData.dates.splice(index, 1);
        scheduleData.startTimes.splice(index, 1);
        scheduleData.endTimes.splice(index, 1);
        scheduleData.contents.splice(index, 1);
    }
	
	// 시간 문자열에서 시간 숫자만 추출
	    const startHour = parseInt(startTime.split(':')[0]);
	    const endHour = parseInt(endTime.split(':')[0]);
		
	// 해당 시간대의 이벤트 삭제
	    for (let hour = startHour; hour <= endHour; hour++) {
	        const eventContainer = document.getElementById(`events-${dateStr}-${hour}`);
	        if (eventContainer) {
	            const events = eventContainer.getElementsByClassName('event');
	            for (let event of events) {
	                if (event.innerText.includes(content)) {
	                    eventContainer.removeChild(event);
	                }
	            }
	            
	            // 체크: 모든 일정 삭제 후 'No Events' 메시지 추가
	            if (!eventContainer.hasChildNodes()) {
	                const noEventLabel = document.createElement('div');
	                noEventLabel.className = 'no-event';
	                noEventLabel.innerText = 'No Events';
	                eventContainer.appendChild(noEventLabel);
	            }
	        }
	    }
	}


// Swiper 초기화
    const swiper = new Swiper('.swiper', {
        // Optional parameters
        direction: 'horizontal',
        loop: false, // 반복 슬라이드 여부
        slidesPerView:auto, // 한 번에 보여줄 슬라이드 수

        // Optional: If we need a scrollbar
        scrollbar: {
            el: '.swiper-scrollbar',
        },
    });