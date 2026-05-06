document.addEventListener("DOMContentLoaded", () => {

    const chatWindow = document.getElementById("chat-window");
    const input = document.getElementById("messageInput");
    const sendBtn = document.getElementById("sendBtn");
    const personalitySelect = document.getElementById("personality");
    const clearBtn = document.getElementById("clearBtn");

    // Session
    let sessionId = localStorage.getItem("sessionId");
    if (!sessionId) {
        sessionId = crypto.randomUUID();
        localStorage.setItem("sessionId", sessionId);
    }

    function appendMessage(text, sender) {
        const div = document.createElement("div");
        div.classList.add("message", sender);
        div.innerHTML = DOMPurify.sanitize(marked.parse(text));
        chatWindow.appendChild(div);

        div.querySelectorAll("pre code").forEach((block) => {
            hljs.highlightElement(block);

            const lang = block.className.match(/language-(\w+)/)?.[1];
            if (lang) {
                const label = document.createElement("span");
                label.className = "code-lang-label";
                label.textContent = lang;
                block.parentElement.prepend(label);
            }
        });

        chatWindow.scrollTop = chatWindow.scrollHeight;
    }

    async function sendMessage() {
        const message = input.value.trim();
        if (!message) return;

        appendMessage(message, "user");
        input.value = "";

        try {
            const response = await fetch("/api/v1/chat", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    personality: personalitySelect.value,
                    message: message,
                    sessionId: sessionId
                })
            });

            if (!response.ok) {
                throw new Error("API error: " + response.status);
            }

            const data = await response.json();

            appendMessage(data.response ?? "No response from server", "ai");

        } catch (error) {
            console.error(error);
            appendMessage("Something went wrong. Try again.", "ai");
        }
    }

    // Events
    sendBtn.addEventListener("click", sendMessage);

    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") sendMessage();
    });

    clearBtn.addEventListener("click", () => {
        chatWindow.innerHTML = "";
        localStorage.removeItem("sessionId");

        sessionId = crypto.randomUUID();
        localStorage.setItem("sessionId", sessionId);
    });

});