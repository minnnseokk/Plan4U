// 입력 필드를 선택
var inputField = document.getElementById('explanation');
var inputPriceField = document.getElementById('price');
//// 이 부분들은 input의 기본값에 대한 설정
// DOM이 완전히 로드된 후에 실행
document.addEventListener('DOMContentLoaded', function() {
	inputField.addEventListener('focus', function() {
	    if (inputField.value === '더치페이 내용') {
	        inputField.value = '';
	    }
	});
	// keydown 이벤트 핸들러 추가
    inputField.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault(); // 기본 동작(폼 제출 등)을 막음
        }
    });

    inputPriceField.addEventListener('keydown', function(event) {
    	// keydown 이벤트 핸들러 추가
    	if (event.key === 'Enter') {
    	   event.preventDefault(); // 기본 동작(폼 제출 등)을 막음
        }
    });
});

function validateLength(input) {
    if (input.value.length > 16) {
        input.value = input.value.slice(0, 16);
    }
}
// 여기부터 펑션 알고리즘
function createDiv(state) {
    var inputExplanation = inputField.value.trim();
    var inputPrice = inputPriceField.value.trim();
    inputPrice = inputPrice;
    
    if (inputExplanation === '' || inputPrice === '') {
        alert('설명과 금액을 모두 입력해주세요.');
        return; }
    var priceNumber = parseFloat(inputPrice);
    if (isNaN(priceNumber)) {
        alert('유효한 금액을 입력해주세요.');
        return; }

    if (state === '+') {
        priceNumber = Math.abs(priceNumber);
    } else if (state === '-') {
        priceNumber = -Math.abs(priceNumber);
    }

    var listDiv = document.createElement('div');
	listDiv.className = 'listItem';
	// inputExplanation을 굵게 강조하고, priceNumber를 일반 텍스트로 추가
    listDiv.innerHTML = '<strong>' + inputExplanation + '</strong> | ' + priceNumber + '원';
	
    var itemList = document.querySelector('.itemList');
    itemList.appendChild(listDiv);

    document.getElementById('explanation').value = '더치페이 내용';
    document.getElementById('price').value = '';
    updateSum();
    
    var button = document.getElementById('dynamicButton');
    button.type = 'submit'; // 버튼의 type 속성을 submit으로 변경
    button.classList.remove('hidden'); // 버튼을 표시
}

function clearInput() {
    document.getElementById('explanation').value = '';
    document.getElementById('price').value = '';
}

function updateSum() {
    var itemList = document.querySelector('.itemList');
    var itemListDivs = itemList.querySelectorAll('.listItem');
    var sum = 0;

	itemListDivs.forEach(function(div) {
	        var textContent = div.textContent;
	        // 텍스트를 공백으로 스플릿하고, 배열의 마지막 요소가 금액이라고 가정
	        var parts = textContent.split('|');

	        // 배열의 마지막 부분을 금액으로 간주
	        var lastPart = parts[parts.length - 1];
	        var number = parseFloat(lastPart);

	        if (!isNaN(number)) {
	            sum += number;
	        }
	    });
    var totalSum = sum;

    var numPeople = parseInt(document.querySelector('.numPeople').value);
    if (!isNaN(numPeople) && numPeople > 0) {
        sum = sum / numPeople;
    }

    sum = Math.round(sum);

    var calculatorResult = document.querySelector('.calculatorResult');
    calculatorResult.textContent = '총합: ' + totalSum + '원 (인당 ' + sum + '원)';

    document.getElementById('totalSum').value = totalSum;
    document.getElementById('resultPrice').value = sum;
    document.getElementById('numPeople').value = numPeople;
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.numPeople').addEventListener('input', updateSum);
});

// 아래는 일반 계산기 스크립트
let currentInput = '';
let operator = '';
let previousInput = '';

document.addEventListener('keydown', function(event) {
    const activeElement = document.activeElement;
    if (activeElement.closest('.calculatorContainer')) {
        return;  
    }

    const key = event.key;
    if (isValidKey(key)) {
        handleKeyInput(key);
    }
});

function isValidKey(key) {
    // 숫자와 연산자만 유효한 키로 인정
   if (event.keyCode >= 112 && event.keyCode <= 123) {
        key.preventDefault();
    }
    return /[0-9+\-*/=]/.test(key) || key === 'Enter' || key === 'Backspace' || key === 'Escape';
}

function handleKeyInput(key) {
    if (key === 'Enter' || key === '=') {
        calculate();
    } else if (key === 'Backspace') {
        handleBackspace();
    } else if (key === 'Escape') {
        clearDisplay();
    } else if (/[0-9]/.test(key)) {
        appendNumber(key);
    } else if (/[+\-*/]/.test(key)) {
        appendOperator(key);
    }
}

function handleBackspace() {
    if (currentInput.length > 0) {
        // 자릿수를 하나 지움
        currentInput = currentInput.slice(0, -1);
        
        // 만약 모든 숫자가 지워졌다면 0으로 설정
        if (currentInput === '') {
			clearDisplay();
        }
        
    }	else if (operator) {
	       // currentInput이 비어 있고 operator가 있는 경우 operator를 지움
	       operator = '';
	   } else if (previousInput.length > 0) {
	       // 이전 입력(previousInput)으로 돌아가기 위해 previousInput의 자릿수를 하나 지움
	       previousInput = previousInput.slice(0, -1);
	   }
        updateDisplay(createDisplayString());

}


function clearDisplay() {
    currentInput = '';
    operator = '';
    previousInput = '';
    updateDisplay('');
}

function appendNumber(number) {
	if (currentInput.length < 10) {  // Optional: Limiting input length
		
        currentInput += number; // key가 숫자로 입력되었으면 해당 값 적용함
        updateDisplay(createDisplayString());
    }
}
function appendOperator(op) {
    if (currentInput !== '') {
        if (previousInput !== '' && operator !== '') {
            calculate();
        }
        operator = op;  // 연산자를 업데이트
        previousInput = currentInput;
        currentInput = '';
    } else if (operator === '') {  
        // operator가 비어있을 때만 새로운 operator를 추가할 수 있도록 처리
        operator = op;
    }
    updateDisplay(createDisplayString());
}

function createDisplayString() {
    if (currentInput === '') {
        return previousInput + ' ' + operator;
    } else {
        return previousInput + ' ' + operator + ' ' + currentInput;
    }
}

function updateDisplay(value) {
    document.getElementById('display').textContent = value;
}

function calculate() {
    if (previousInput !== '' && currentInput !== '' && operator !== '') {
        let result;
        const num1 = parseFloat(previousInput);
        const num2 = parseFloat(currentInput);
        switch (operator) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                result = num1 / num2;
                break;
            default:
                return;
        }
        updateDisplay(result);
        currentInput = result.toString();
        operator = '';
        previousInput = '';
    }
}
function updateNumberInput(value) {
    document.getElementById('number').value = value;
}

function updateRangeInput(value) {
    document.getElementById('range').value = value;
}
window.onload = function() {
        var scrollableDiv = document.getElementById('scrollableDiv');
        scrollableDiv.scrollTop = scrollableDiv.scrollHeight;
    };
