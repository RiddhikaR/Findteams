import React, { useState, useEffect } from "react";
import { FaEnvelope, FaBell, FaUserPlus } from "react-icons/fa";
import "../css/notifications.css";

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const token = sessionStorage.getItem("token");

  useEffect(() => {
    if (!token) return;

    const fetchInvitations = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/invitations/myInvitations", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await res.json();
        setNotifications(data);
      } catch (err) {
        console.error(err);
      }
    };

    fetchInvitations();
  }, [token]);

  const handleAccept = async (id) => {
    await fetch(`http://localhost:8080/api/invitations/${id}/accept`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
    setNotifications((prev) =>
      prev.map((inv) => (inv.id === id ? { ...inv, status: "ACCEPTED" } : inv))
    );
  };

  const handleReject = async (id) => {
    await fetch(`http://localhost:8080/api/invitations/${id}/reject`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
    setNotifications((prev) =>
      prev.map((inv) => (inv.id === id ? { ...inv, status: "REJECTED" } : inv))
    );
  };

  return (
    <div className="notifications-page">
      <h1>My Notifications</h1>
      <div className="notifications-list">
        {notifications.length === 0 && <p>No invitations</p>}
        {notifications.map((inv) => (
          <div className="notification-card" key={inv.id}>
            <div className="notif-icon">
              <FaUserPlus />
            </div>
            <div className="notif-content">
              <p>
  <strong>{inv.senderName}</strong> invited you to join <strong>{inv.groupName}</strong>
</p>
{ <p><em>Purpose:</em> {inv.purpose}</p>}




              <p className="status">Status: {inv.status}</p>
              {inv.status === "PENDING" && (
                <div className="notif-actions">
                  <button onClick={() => handleAccept(inv.id)} className="accept-btn">
                    Accept
                  </button>
                  <button onClick={() => handleReject(inv.id)} className="reject-btn">
                    Reject
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
