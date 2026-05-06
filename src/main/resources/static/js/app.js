const clearBtn = document.getElementById("clearBtn");

clearBtn.addEventListener("click", () => {
    chatWindow.innerHTML = "";
    localStorage.removeItem("sessionId");

    sessionId = crypto.randomUUID();
    localStorage.setItem("sessionId", sessionId);
});