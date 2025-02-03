const today = new Date();
let currentMonth = today.getMonth();
let currentYear = today.getFullYear();

const monthNames = ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"];
const selectedDates = new Set();
let rangeSelectMode = false;
let firstSelectedDate = null;
// 여기는 날짜 선택을 위한 코드
function toggleRangeSelectMode() {
	rangeSelectMode = !rangeSelectMode;
	const rangeButton = document.getElementById('rangeSelectToggle');
	if (rangeSelectMode) {
		rangeButton.style.backgroundColor = 'gray';  // 범위 선택 모드 활성화 시 버튼 색상 변경
	} else {
		rangeButton.style.backgroundColor = '';  // 비활성화 시 원래 색상으로 복원
		firstSelectedDate = null; // 범위 선택 초기화
	}
}

function selectDateRange(start, end) {
	const startDate = new Date(start);
	const endDate = new Date(end);

	const addDay = 24 * 60 * 60 * 1000;
	const direction = startDate < endDate ? 1 : -1;

	let currentDate = startDate;

	while ((direction === 1 && currentDate <= endDate) || (direction === -1 && currentDate >= endDate)) {
		const dateStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}-${String(currentDate.getDate()).padStart(2, '0')}`;
		selectedDates.add(dateStr);
		currentDate = new Date(currentDate.getTime() + direction * addDay);
	}

	renderCalendar();  // 범위 선택 반영을 위해 캘린더 다시 렌더링
}

function deselectDateRange(start, end) {
	const startDate = new Date(start);
	const endDate = new Date(end);

	const addDay = 24 * 60 * 60 * 1000;
	const direction = startDate < endDate ? 1 : -1;

	let currentDate = startDate;

	while ((direction === 1 && currentDate <= endDate) || (direction === -1 && currentDate >= endDate)) {
		const dateStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}-${String(currentDate.getDate()).padStart(2, '0')}`;
		selectedDates.delete(dateStr);
		currentDate = new Date(currentDate.getTime() + direction * addDay);
	}

	renderCalendar();  // 범위 선택 반영을 위해 캘린더 다시 렌더링
}

function isRangeSelected(start, end) {
	const startDate = new Date(start);
	const endDate = new Date(end);

	const addDay = 24 * 60 * 60 * 1000;
	const direction = startDate < endDate ? 1 : -1;

	let currentDate = startDate;

	while ((direction === 1 && currentDate <= endDate) || (direction === -1 && currentDate >= endDate)) {
		const dateStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}-${String(currentDate.getDate()).padStart(2, '0')}`;
		if (!selectedDates.has(dateStr)) {
			return false;
		}
		currentDate = new Date(currentDate.getTime() + direction * addDay);
	}

	return true;
}
//// 여기까진 범위 선택을 위한 기능들

//// 여기서부턴 캘린더 랜더링 기능
function renderCalendar() {
	const daysGrid = document.getElementById('daysGrid');
	const monthYear = document.getElementById('monthYear');

	// 월/년도 업데이트
	monthYear.textContent = `${currentYear}년 ${monthNames[currentMonth]}`;

	// 이전 내용 지우기
	daysGrid.innerHTML = '';

	// 해당 월의 첫 번째 날짜와 마지막 날짜 구하기
	const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
	const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0);
	const firstDayIndex = firstDayOfMonth.getDay();
	const daysInMonth = lastDayOfMonth.getDate();

	// 이전 달의 마지막 날짜 구하기
	const prevMonthLastDay = new Date(currentYear, currentMonth, 0).getDate();

	// 지난달 날짜 채우기
	for (let i = 0; i < firstDayIndex; i++) {
		const prevDayCell = document.createElement('div');
		prevDayCell.className = 'day prev-month';
		prevDayCell.textContent = prevMonthLastDay - (firstDayIndex - 1) + i;

		daysGrid.appendChild(prevDayCell);
	}

	// 날짜 채우기
	for (let day = 1; day <= daysInMonth; day++) {
		const dayCell = document.createElement('div');
		dayCell.className = 'day current-month';
		dayCell.textContent = day;

		const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

		if (selectedDates.has(dateStr)) {
			dayCell.classList.add('selected');
		}

		// 스케줄 데이터가 있을 경우 일정 개수와 버튼 추가
		if (schedulesData) {
			const schedulesForDay = schedulesData.filter(schedule => schedule.scheduleDate === dateStr);
			if (schedulesForDay.length > 0) {
				// 일정 개수 표시
				const scheduleCountDiv = document.createElement('div');
				scheduleCountDiv.className = 'schedule-count';
				scheduleCountDiv.textContent = `일정: ${schedulesForDay.length}개`;

				// "일정 보기" 버튼 추가
				const viewScheduleButton = document.createElement('button');
				viewScheduleButton.className = 'view-schedule-button';
				viewScheduleButton.textContent = '일정 보기';
				viewScheduleButton.onclick = (event) => {
					event.stopPropagation(); // 클릭 이벤트 전파 방지
					showScheduleDetails(dateStr, schedulesForDay);
					window.scrollTo({
						top: window.innerHeight, // 화면 높이만큼 아래로 스크롤
						behavior: 'smooth' // 스크롤이 부드럽게 진행되도록 설정
					});
					// 일정 보기 버튼 클릭 시 선택 상태 변경 방지
					document.querySelectorAll('.day').forEach(cell => cell.classList.remove('selected'));
				};

				dayCell.appendChild(scheduleCountDiv);
				dayCell.appendChild(viewScheduleButton);
			}
		}

		dayCell.onclick = () => {
			if (rangeSelectMode) {
				if (firstSelectedDate) {
					// 선택된 범위에 날짜가 이미 있으면 삭제
					if (isRangeSelected(firstSelectedDate, dateStr)) {
						deselectDateRange(firstSelectedDate, dateStr);
					} else {
						selectDateRange(firstSelectedDate, dateStr);
					}
					firstSelectedDate = null;  // 범위 선택 완료 후 초기화
				} else {
					firstSelectedDate = dateStr;  // 첫 번째 날짜 선택
					selectedDates.add(dateStr);
					dayCell.classList.add('selected');
				}
			} else {
				if (selectedDates.has(dateStr)) {
					selectedDates.delete(dateStr);
					dayCell.classList.remove('selected');
				} else {
					selectedDates.add(dateStr);
					dayCell.classList.add('selected');
				}
			}
		};

		daysGrid.appendChild(dayCell);
	}
	// 다음 달 날짜 채우기 
	const totalCells = daysGrid.children.length;
	for (let i = totalCells; i < 42; i++) {
		const nextDayCell = document.createElement('div');
		nextDayCell.className = 'day next-month';
		nextDayCell.textContent = i - totalCells + 1;

		daysGrid.appendChild(nextDayCell);
	}
}


//// 여기서부턴 스케쥴 일정 열람과 관련된 기능
function showScheduleDetails(dateStr, schedules) {
	const scheduleDetails = document.getElementById('scheduleDetails');
	scheduleDetails.innerHTML = ''; // 기존 내용 지우기

	// 선택된 날짜와 월/년도 정보 표시
	const [year, month, day] = dateStr.split('-');
	const dateHeading = document.createElement('h4');

	schedules.forEach(schedule => {
		const scheduleItem = document.createElement('div');
		scheduleItem.className = 'schedule-item';
		
		// 시간 값을 설정
		const startTime = schedule.scheduleTime.substring(0, 5);
		const endTime = schedule.endScheduleTime.substring(0, 5);

		// "00:00 ~ 23:59"일 경우 "하루종일"로 설정, 아니면 기존 시간 형식으로 설정
		const timeDisplay = (startTime === '00:00' && endTime === '23:59') ? '하루종일' : `${startTime} - ${endTime}`;
		
		scheduleItem.innerHTML = `
         <div class="viewyear view">${year}년 ${parseInt(month)}월 ${parseInt(day)}일</div>
            <div class="viewtime view"><strong class="time">시간: </strong>${timeDisplay}</div>
            <div class="viewcontent view">${schedule.content}</div>
         <button class="delete-button">일정 삭제</button>
        `;
		// 삭제 버튼 클릭 이벤트 핸들러 등록
		// 삭제 버튼 클릭 이벤트 핸들러 등록
		const deleteButton = scheduleItem.querySelector('.delete-button');
		deleteButton.addEventListener('click', () => {
			const userConfirmed = confirm('해당 일정을 삭제하시겠습니까?');
			if (userConfirmed) {
				deleteSchedule(schedule.content,schedule.scheduleTime,schedule.endScheduleTime,dateStr)
					.then(() => {
						scheduleItem.remove(); // 삭제 완료 후, 해당 일정 항목 삭제
						dateHeading.textContent = '';
						return fetchSchedules(currentYear, currentMonth); // 스케줄 다시 불러오기
					})
					.then(() => {
						renderCalendar(); // 캘린더 다시 렌더링
					})
					.catch(error => {
						console.error('삭제 중 오류 발생:', error);
					});
			}
		});

		scheduleDetails.appendChild(scheduleItem);
	});
}

// 스케쥴 삭제 요청을 보내기 위한 부분
// 삭제 요청을 보내는 함수
async function deleteSchedule(content,startTime,endTime,date) {
	try {
		const fetchContent = new URLSearchParams();
		fetchContent.append('content', content);
		fetchContent.append('startTime', startTime);
		fetchContent.append('endTime', endTime);
		fetchContent.append('date',formatDateForServer(date))
		return fetch(`/api/schedule/delete/${roomid}`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			body: fetchContent.toString()
		})
			.then(response => {
				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}
			})
			.then(data => {
			})
			.catch(error => {
				console.error('Error fetching schedules:', error);
			});

	} catch (error) {
		console.error('삭제 요청 중 오류 발생:', error);
		throw error;
	}

}
function formatDateForServer(dateStr) {
    const [year, month, day] = dateStr.split('-');
    const shortYear = year.slice(2); // 2024 -> 24
    return `${shortYear}/${month}/${day}`;
}
let schedulesData = []; // 스케줄 데이터를 저장할 변수
// 현재 년도 날짜 서버에 보내기
function fetchSchedules(year, month) {
	const formBody = new URLSearchParams();
	formBody.append('year', year);
	formBody.append('month', month + 1); // month는 0부터 시작하므로 1을 더해줍니다.

	return fetch(`/api/schedule/fetch/${roomid}`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
		body: formBody.toString()
	})
		.then(response => {
			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}
			return response.text(); // 먼저 텍스트로 받아서 확인
		})
		.then(text => {
			
			try {
				schedulesData = JSON.parse(text); // JSON으로 파싱
				renderCalendar(); // 스케줄 데이터를 캘린더에 반영하기 위해 렌더링 호출
			} catch (error) {
				throw new Error('Failed to parse JSON: ' + error.message);
			}
		})
		.then(data => {
			
		})
		.catch(error => {
			console.error('Error fetching schedules:', error);
		});
}
function prevMonth() {
	currentMonth--;
	if (currentMonth < 0) {
		currentMonth = 11;
		currentYear--;
	}
	fetchSchedules(currentYear, currentMonth);
	renderCalendar();
}

function nextMonth() {
	currentMonth++;
	if (currentMonth > 11) {
		currentMonth = 0;
		currentYear++;
	}
	fetchSchedules(currentYear, currentMonth);
	renderCalendar();
}

function submitSelectedDates() {
	if (selectedDates.size === 0) {
		alert('날짜를 선택해 주세요.');
		return;
	}

	const datesArray = Array.from(selectedDates);
	window.location.href = `/app/schedule/${roomid}?dates=${encodeURIComponent(datesArray.join(','))}`;
}

// 처음 캘린더 렌더링
document.addEventListener('DOMContentLoaded', () => {
	fetchSchedules(currentYear, currentMonth);
	renderCalendar();
})
