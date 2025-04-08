async function sendMessage() {
    const userInquiry = document.getElementById('userInquiry').value;

    try {
        const response = await fetch('/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ inquiry: userInquiry }),
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();

        const responseContainer = document.getElementById('responseContainer');
        responseContainer.innerHTML = '';

        data.forEach((response, index) => {
            const responseDiv = document.createElement('div');
            responseDiv.classList.add('response');
            responseDiv.innerHTML = `<strong>LLM ${index + 1}:</strong> ${response}`;
            responseContainer.appendChild(responseDiv);
        });

    } catch (error) {
        console.error('There was a problem with the fetch operation:', error);
    }
}

document.getElementById('submitButton').addEventListener('click', sendMessage);
