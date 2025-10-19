import React, { useState, useEffect, useRef } from "react";
import { FaHome, FaUsers, FaComments, FaBell, FaCog, FaSignOutAlt, FaSearch, FaTrophy } from "react-icons/fa";
import ChatWindow from "./ChatWindow";
import SearchStudents from "./SearchStudents";
import "../css/dashboard.css";

function Dashboard() {
  const [myGroups, setMyGroups] = useState([]);
  const [myInvitations, setMyInvitations] = useState([]);
  const [unreadMessages, setUnreadMessages] = useState(0);
  const [myProfile, setMyProfile] = useState(null);
  const [showChat, setShowChat] = useState(false);
  const [sidebarCollapsed] = useState(false);
  const token = sessionStorage.getItem("token");
  const searchRef = useRef(null);

  useEffect(() => {
    if (!token) return;

    // fetch profile
    fetch("http://localhost:8080/api/getProfile", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => r.json())
      .then((d) => setMyProfile(d))
      .catch((e) => console.error(e));

    // my groups
    fetch("http://localhost:8080/api/groups/myGroups", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => r.json())
      .then((d) => setMyGroups(d))
      .catch((e) => console.error(e));

    // invitations
    fetch("http://localhost:8080/api/invitations/myInvitations", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => r.json())
      .then((d) => setMyInvitations(d))
      .catch((e) => console.error(e));

    // unread messages (replace endpoint if needed)
    fetch("http://localhost:8080/api/messages/unreadCount", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => {
        if (!r.ok) return { count: 0 };
        return r.json();
      })
      .then((d) => {
        // expecting { count: number } - adjust if your API returns differently
        setUnreadMessages(d?.count ?? 0);
      })
      .catch(() => setUnreadMessages(0));
  }, [token]);

  const handleLogout = () => {
    sessionStorage.clear();
    window.location.href = "/";
  };

  const scrollToSearch = () => {
    const el = document.getElementById("search-section") || searchRef.current;
    el?.scrollIntoView({ behavior: "smooth", block: "start" });
  };

  return (
    <div className={`dashboard-container ${sidebarCollapsed ? "collapsed" : ""}`}>
      {/* Sidebar */}
      <div class="dashboard-topbar"></div>

      <aside className="dashboard-sidebar">
        <div className="brand">
          <div className="logo">HT</div>
          <div className="title">HackTeam</div>
        </div>

        <div className="sidebar-section">
          <h4>Navigation</h4>
          <ul>
            <li onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}>
              <FaHome /> <span className="sidebar-item-label">Dashboard</span>
            </li>
            <li onClick={scrollToSearch}>
              <FaSearch /> <span className="sidebar-item-label">Find Teammates</span>
            </li>
            
          </ul>
        </div>

        <div className="sidebar-section">
          <h4>Communication</h4>
          <ul>
            <li onClick={() => window.location.href = "/NotificationsPage"}>
              <FaBell /> <span className="sidebar-item-label">Notifications</span>
              <span style={{ marginLeft: 8 }} className="badge">{myInvitations.filter(i => i.status === "PENDING").length}</span>
            </li>
            <li onClick={() =>window.location.href = "/GroupsPage"}>
              <FaComments /> <span className="sidebar-item-label">Messages</span>
              <span style={{ marginLeft: 8 }} className="badge">{unreadMessages}</span>
            </li>
          </ul>
       

        
          <h4>Account</h4>
          <ul>
            <li onClick={() => window.location.href = "/Profile"}>
              <FaUsers /> <span className="sidebar-item-label">Profile</span>
            </li>
            <li onClick={() => window.location.href = "/settings"}>
              <FaCog /> <span className="sidebar-item-label">Settings</span>
            </li>
            <li onClick={handleLogout}>
              <FaSignOutAlt /> <span className="sidebar-item-label">Logout</span>
            </li>
          </ul>
        </div>
      </aside>

      {/* Main */}
      <main className="dashboard-main">
        <div className="welcome-banner">
          <div className="welcome-left">
            <h1>
  Welcome back, {myProfile?.name ? myProfile.name : "Student"}! 👋
</h1>

            <p>Ready to find your next hackathon teammates?</p>
          </div>

          <div className="banner-actions">
            <button className="find-btn" onClick={scrollToSearch}>
              <FaSearch style={{ marginRight: 8 }} /> Find Teammates
            </button>
          </div>
        </div>

        <div className="cards-row">
          <div className="card">
            <h3>Active Teams</h3>
            <div className="num">{myGroups.length}</div>
            <div className="sub">+1 from last month</div>
          </div>
          <div className="card">
            <h3>New Notifications</h3>
            <div className="num">{myGroups.length}</div>
            <div className="sub">Accept or Decline</div>
          </div>

          

          <div className="card">
            <h3>Unread Messages</h3>
            <div className="num">{unreadMessages}</div>
            <div className="sub">From {unreadMessages > 0 ? "your conversations" : "no new messages"}</div>
          </div>
        </div>

        
        {/* SearchStudents embedded and target for scroll */}
        <div id="search-section" ref={searchRef}>
          <SearchStudents />
        </div>
      </main>

      {/* Chat overlay */}
      {showChat && <ChatWindow group={null} onBack={() => setShowChat(false)} />}
    </div>
  );
}

export default Dashboard;
