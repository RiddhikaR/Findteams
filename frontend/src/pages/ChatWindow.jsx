import React, { useEffect, useState, useRef } from "react";
import "../css/chatWindow.css";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

function ChatWindow({ group, onBack }) {
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const token = sessionStorage.getItem("token");
  const messagesEndRef = useRef(null);
  const stompClientRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  // Scroll when messages change
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Fetch initial messages when group changes
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

  // WebSocket connection
  useEffect(() => {
  if (!group) return;

  const socket = new SockJS("http://localhost:8080/ws");
  const stompClient = Stomp.over(socket);
  stompClientRef.current = stompClient;

  stompClient.connect({ Authorization: `Bearer ${token}` }, () => {
    console.log("Connected to WebSocket");

    // Subscribe once
    stompClient.subscribe(`/topic/group/${group.id}`, (msg) => {
      const message = JSON.parse(msg.body);
      setMessages((prev) => {
        // prevent duplicate by checking id
        if (prev.find(m => m.id === message.id)) return prev;
        return [...prev, message];
      });
    });
  });

  return () => {
    if (stompClient.connected) {
      stompClient.disconnect();
    }
  };
}, [group.id, token]);

  const sendMessage = () => {
  if (!text.trim() || !group) return;

  const myId = sessionStorage.getItem("userId");

  const messageObj = {
    groupId: group.id,
    senderId:sessionStorage.getItem("userId"),
    senderName: sessionStorage.getItem("studentName"),
    content: text,
  };

  // Send via WebSocket only
  if (stompClientRef.current && stompClientRef.current.connected) {
    stompClientRef.current.send("/app/chat/sendMessage", {}, JSON.stringify(messageObj));
  }

  setText("");
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
  const myId = String(sessionStorage.getItem("userId"));
  const isMine = String(m.senderId) === myId;

  return (
    <div
      key={m.id}
      className={`chat-bubble-container ${isMine ? "mine" : "other"}`}
    >
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
