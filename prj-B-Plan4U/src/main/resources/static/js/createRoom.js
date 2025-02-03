document.addEventListener('DOMContentLoaded', () => {
    const inputField = document.getElementById('inputField');
    const updateButton = document.getElementById('updateButton');
    const outputArea = document.getElementById('outputArea');

    function loadSavedData() {
        const savedData = localStorage.getItem('savedData');
        if (savedData) {
            outputArea.value = savedData;
        }
    }

    function saveData(data) {
        localStorage.setItem('savedData', data);
    }

    loadSavedData();

    updateButton.addEventListener('click', () => {
        const inputValue = inputField.value.trim();
        if (inputValue) {
            const currentContent = outputArea.value;
            outputArea.value = inputValue + (currentContent ? '\n' + currentContent : '');
            saveData(outputArea.value);
            inputField.value = '';
        }
    });
});