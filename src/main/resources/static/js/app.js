document.addEventListener("DOMContentLoaded", () => {

    const chatWindow = document.getElementById("chat-window");
    const input = document.getElementById("messageInput");
    const sendBtn = document.getElementById("sendBtn");
    const personalitySelect = document.getElementById("personality");
    const clearBtn = document.getElementById("clearBtn");
    const themeToggle = document.getElementById("themeToggle");

    // Läs sparat val från localStorage (så det håller sig vid sidladdning)
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "light") {
        document.body.classList.add("light");
        themeToggle.textContent = "🌙";
    }

    // Växla tema när man klickar
    themeToggle.addEventListener("click", () => {
        const isLight = document.body.classList.toggle("light");

        if (isLight) {
            themeToggle.textContent = "🌙";
            localStorage.setItem("theme", "light");
        } else {
            themeToggle.textContent = "🔆";
            localStorage.setItem("theme", "dark");
        }
    });

    // Session
    let sessionId = localStorage.getItem("sessionId");
    if (!sessionId) {
        sessionId = crypto.randomUUID();
        localStorage.setItem("sessionId", sessionId);
    }

    function appendMessage(text, sender, personality = null) {

        const wrapper = document.createElement("div");
        wrapper.classList.add("message-wrapper", sender);

        if (personality && sender === "ai") {
            const label = document.createElement("div");
            label.classList.add("message-label");
            label.textContent = personality;

            wrapper.appendChild(label);
        }

        const div = document.createElement("div");
        div.classList.add("message", sender);

        div.innerHTML = DOMPurify.sanitize(marked.parse(text));

        wrapper.appendChild(div);

        chatWindow.appendChild(wrapper);

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
        const selectedPersonality = personalitySelect.value;

        appendMessage(message, "user");
        input.value = "";

        try {
            const response = await fetch("/api/v1/chat", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    personality: selectedPersonality,
                    message: message,
                    sessionId: sessionId
                })
            });

            if (!response.ok) {
                throw new Error("API error: " + response.status);
            }

            const data = await response.json();

            const personalityNameMap = {
                assistant: "Assistant",
                coder: "Coder",
                "ron-burgundy": "Ron Burgundy"
            };

            appendMessage(
                data.reply ?? "No response from server",
                "ai",
                personalityNameMap[selectedPersonality] ?? "Assistant"
            );

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