import React, { useState, useEffect } from "react";
import ChatWindow from "./ChatWindow";
import "../css/chatWindow.css";

export default function GroupsPage() {
  const [groups, setGroups] = useState([]);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [groupName, setGroupName] = useState("");
  const [capacity, setCapacity] = useState(3); // default capacity
  const [purpose, setPurpose] = useState("");
  const token = sessionStorage.getItem("token");

  useEffect(() => {
    if (!token) return;
    fetchGroups();
  }, [token]);

  const fetchGroups = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/groups/myGroups", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setGroups(data);
    } catch (err) {
      console.error(err);
    }
  };

  const createGroup = async () => {
  if (!groupName.trim() || !purpose.trim() || !capacity) {
    alert("Please provide group name, purpose, and capacity");
    return;
  }

  if (capacity < 2 || capacity > 10) {
    alert("Capacity must be between 2 and 10");
    return;
  }

  try {
    const params = new URLSearchParams();
    params.append("name", groupName);
    params.append("capacity", capacity);
    params.append("purpose", purpose);

    const res = await fetch(`http://localhost:8080/api/groups/create?${params}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (res.ok) {
      const newGroup = await res.json();
      setGroups((prev) => [...prev, newGroup]);
      setGroupName("");
      setPurpose("");
      setCapacity(3);
    } else {
      const errorText = await res.text();
      alert("Failed to create group: " + errorText);
    }
  } catch (err) {
    console.error(err);
    alert("Error creating group.");
  }
};


  return (
    <div className="team-chats-container">
      {/* LEFT SIDEBAR */}
      <aside className="team-list">
        <h2>Your Teams</h2>

        {/* Group Creation Form */}
        <div className="create-group-form">
          <input
            type="text"
            placeholder="Group Name"
            value={groupName}
            onChange={(e) => setGroupName(e.target.value)}
          />
          <input
  type="text"
  placeholder="Purpose"
  value={purpose}
  onChange={(e) => setPurpose(e.target.value)}
/>

          <input
            type="number"
            placeholder="Capacity"
            min="2"
            max="10"
            value={capacity}
            onChange={(e) => setCapacity(e.target.value)}
          />
          <button onClick={createGroup}>Create Group</button>
        </div>

        {groups.length === 0 && <p>No groups yet</p>}
        <ul>
          {groups.map((g) => (
            <li
              key={g.id}
              onClick={() => setSelectedGroup(g)}
              className={selectedGroup?.id === g.id ? "active" : ""}
            >
              <div className="team-entry">
                <div className="team-name">{g.name}</div>
                <div className="team-preview">Click to open chat</div>
              </div>
            </li>
          ))}
        </ul>
      </aside>

      {/* RIGHT CHAT PANEL */}
      <section className="chat-area">
        {selectedGroup ? (
          <ChatWindow group={selectedGroup} />
        ) : (
          <div className="chat-placeholder">Select a team to view chat</div>
        )}
      </section>
    </div>
  );
}
