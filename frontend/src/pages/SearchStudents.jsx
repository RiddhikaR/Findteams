import React, { useState, useEffect } from "react";
import Select from "react-select";
import ChatWindow from "./ChatWindow";
import "../css/searchStudents.css";

function SearchStudents() {
  const [cgpa, setCgpa] = useState("");
  const [batch, setBatch] = useState("");
  const [course, setCourse] = useState("");
  const [hosteller, setHosteller] = useState("");
  const [preferences, setPreferences] = useState([]);
  const [students, setStudents] = useState([]);
  const [myProfile, setMyProfile] = useState(null);
  const [myGroups, setMyGroups] = useState([]);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [message, setMessage] = useState("");
  const [myInvitations, setMyInvitations] = useState([]);
  const token = sessionStorage.getItem("token");

  useEffect(() => {
    if (!token) return;
    const fetchInvitations = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/invitations/myInvitations", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data = await res.json();
        setMyInvitations(data);
      } catch (err) {
        console.error("Error fetching invitations:", err);
      }
    };
    fetchInvitations();
  }, [token]);

  const handleAccept = async (id) => {
    try {
      await fetch(`http://localhost:8080/api/invitations/${id}/accept`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });
      setMyInvitations((prev) =>
        prev.map((inv) => (inv.id === id ? { ...inv, status: "ACCEPTED" } : inv))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleReject = async (id) => {
    try {
      await fetch(`http://localhost:8080/api/invitations/${id}/reject`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });
      setMyInvitations((prev) =>
        prev.map((inv) => (inv.id === id ? { ...inv, status: "REJECTED" } : inv))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const preferenceOptions = [
    { value: "Hackathons", label: "Hackathons" },
    { value: "Research", label: "Research" },
    { value: "Technical Clubs", label: "Technical Clubs" },
    { value: "Non Technical Clubs", label: "Non Technical Clubs" },
    { value: "Projects", label: "Projects" },
    { value: "Internships", label: "Internships" },
  ];

  useEffect(() => {
    if (!token) return;

    const fetchProfile = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/getProfile", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data = await res.json();
        setMyProfile(data);
      } catch (err) {
        console.error("Error fetching profile:", err);
        setMessage("Error fetching profile");
      }
    };

    const fetchGroups = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/groups/myGroups", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data = await res.json();
        setMyGroups(data);
      } catch (err) {
        console.error("Error fetching groups:", err);
        setMessage("Error fetching groups");
      }
    };

    fetchProfile();
    fetchGroups();
  }, [token]);
const handleViewResume = async (studentId) => {
  if (!token) {
    alert("User not authenticated");
    return;
  }
  try {
    const res = await fetch(`http://localhost:8080/api/resume`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) {
      if (res.status === 404) {
        alert("Resume not found for this student.");
        return;
      }
      throw new Error(`HTTP error ${res.status}`);
    }
    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    window.open(url, "_blank");
  } catch (err) {
    console.error(err);
    alert("Error fetching resume: " + err.message);
  }
};

  const handleSearch = async () => {
    if (!token) {
      setMessage("User not authenticated");
      return;
    }

    const queryParams = new URLSearchParams();
    if (cgpa) queryParams.append("cgpa", cgpa);
    if (batch) queryParams.append("batch", batch);
    if (course) queryParams.append("course", course);
    if (hosteller) queryParams.append("hostellerOrDayscholar", hosteller);
    preferences.forEach((p) => queryParams.append("preferences", p.value));

    try {
      const res = await fetch(
        `http://localhost:8080/api/searchStudents?${queryParams.toString()}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      const data = await res.json();
      setStudents(data);
      setMessage("");
    } catch (err) {
      console.error(err);
      setMessage("Error: " + err.message);
    }
  };

  const handleCreateGroup = async () => {
    if (!myProfile) {
      setMessage("Profile not loaded yet");
      return;
    }
    const groupName = prompt("Enter group name:");
    if (!groupName) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/groups/create?name=${groupName}&ownerId=${myProfile.id}&capacity=10`,
        { method: "POST", headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      const newGroup = await res.json();
      setMyGroups((prev) => [...prev, newGroup]);
      setMessage(`Group '${newGroup.name}' created`);
    } catch (err) {
      console.error(err);
      setMessage("Error creating group: " + err.message);
    }
  };

  const handleInvite = async (studentId, groupId) => {
    if (!myProfile) return;
    if (!groupId) {
      alert("Select a group first");
      return;
    }

    try {
      const res = await fetch(
        `http://localhost:8080/api/invitations/send?senderId=${myProfile.id}&receiverId=${studentId}&groupId=${groupId}&purpose=Join`,
        { method: "POST", headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      await res.json();
      alert("Invitation sent!");
    } catch (err) {
      console.error(err);
      alert("Error sending invitation: " + err.message);
    }
  };

  // If a group is selected → show full chat window
  if (selectedGroup) {
    return (
      <ChatWindow group={selectedGroup} onBack={() => setSelectedGroup(null)} />
    );
  }

  return (
    <div className="search-page">
      <div className="search-card">
        <h1 className="search-title">Student Collaboration Portal</h1>

        

        

        {/* Search */}
       
<section className="search-section">
  <h2 style={{ textAlign: "center", marginBottom: "20px", color: "#1f2937" }}>
    Search Students
  </h2>

  {/* Inputs */}
  <div className="search-inputs">
    <input
      placeholder="CGPA"
      value={cgpa}
      onChange={(e) => setCgpa(e.target.value)}
    />
    <input
      placeholder="Batch"
      value={batch}
      onChange={(e) => setBatch(e.target.value)}
    />
    <input
      placeholder="Course"
      value={course}
      onChange={(e) => setCourse(e.target.value)}
    />
    <input
      placeholder="Hosteller/Dayscholar"
      value={hosteller}
      onChange={(e) => setHosteller(e.target.value)}
    />
  </div>

  {/* Preferences multi-select */}
  <div className="search-select">
    <Select
      isMulti
      options={preferenceOptions}
      value={preferences}
      onChange={setPreferences}
      placeholder="Select preferences"
    />
  </div>

  {/* Search Button */}
  <button onClick={handleSearch} className="search-btn">
    Search
  </button>

  {/* Error / Status */}
  {message && <p className="search-error">{message}</p>}
</section>



        {/* Results */}
        {students.length > 0 && (
  <section className="results-cards">
    <div className="card-container">
      {students.map((s) => (
        
        <div className="student-card" key={s.id}>
  <div className="card-header">
    <div className="avatar">{s.name[0]}</div>
    <div className="student-info">
      <div className="name">{s.name}</div>
      <div className="regno">{s.regNo}</div>
    </div>
    <div className="cgpa">⭐ {s.cgpa}</div>
  </div>

  <div className="card-body">
    <p><strong>Course:</strong> {s.course}</p>
    <p><strong>Batch:</strong> 20{s.regNo?.slice(0, 2)}</p>

    <p><strong>Status:</strong> {s.hostellerOrDayscholar}</p>

    <div className="tags">
      {s.preferences.map((pref, i) => (
        <span key={i} className="tag">{pref}</span>
      ))}
    </div>
  </div>

  <div className="card-footer">
    <select id={`group-select-${s.id}`} className="group-select">
      <option value="">Select group</option>
      {myGroups.map((g) => (
        <option key={g.id} value={g.id}>
          {g.name}
        </option>
      ))}
    </select>
    <button
      onClick={() =>
        handleInvite(s.id, document.getElementById(`group-select-${s.id}`)?.value)
      }
      className="invite-btn"
    >
      Send Invite
    </button>
    <button
    onClick={() => handleViewResume(s.id)}
    className="view-resume-btn"
  >
    View Resume
  </button>
  </div>
</div>

      ))}
    </div>
  </section>
)}

      </div>
    </div>
  );
}

export default SearchStudents;
