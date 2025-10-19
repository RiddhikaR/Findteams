import React, { useEffect, useState, useRef } from "react";
import "../css/chatWindow.css";

function ChatWindow({ group, onBack }) {
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const token = sessionStorage.getItem("token");
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (!group) return;

    const fetchMessages = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/messages/${group.id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setMessages(data);
        }
      } catch (err) {
        console.error(err);
      }
    };

    fetchMessages();
  }, [group, token]);

  const sendMessage = async () => {
    if (!text.trim() || !group) return;
    try {
      const res = await fetch(`http://localhost:8080/api/messages/send`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          groupId: group.id,
          content: text,
        }),
      });

      if (res.ok) {
        setText("");
        const updatedRes = await fetch(`http://localhost:8080/api/messages/${group.id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const updatedMessages = await updatedRes.json();
        setMessages(updatedMessages);
      }
    } catch (err) {
      console.error(err);
    }
  };

  if (!group) return null;

  return (
    <div className="chat-overlay">
      <div className="chat-header">
        <button className="back-btn" onClick={onBack}>✕</button>
        <h2>{group.name}</h2>
      </div>
      <div className="chat-messages">
        {messages.map((m) => {
          const myId = sessionStorage.getItem("userId"); // your actual user ID
          const isMine = m.senderId === myId; 
          return (
            <div key={m.id} className="chat-bubble-container">
              {/* Show sender ID above the bubble */}
              {!isMine && <div className="sender-name">{m.senderName || m.senderId}</div>}

              <div className={`chat-bubble ${isMine ? "mine" : "other"}`}>
                <span>{m.content}</span>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>
      <div className="chat-input">
        <input
          type="text"
          placeholder="Type a message..."
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
        />
        <button onClick={sendMessage}>➤</button>
      </div>
    </div>
  );
}

export default ChatWindow;
