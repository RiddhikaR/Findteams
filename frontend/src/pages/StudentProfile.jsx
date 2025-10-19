import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/profile.css";

function StudentProfile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();
const viewResume = async () => {
  const token = sessionStorage.getItem("token");
  if (!token) {
    alert("You must be logged in to view the resume.");
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/resume", {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
      alert("Resume not found or an error occurred.");
      return;
    }

    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    window.open(url, "_blank");

    // Optional: revoke URL after some delay to free memory
    setTimeout(() => window.URL.revokeObjectURL(url), 10000);
  } catch (error) {
    console.error(error);
    alert("Failed to view resume.");
  }
};


  const goToSearch = () => {
    navigate("/searchStudents");
  };

  useEffect(() => {
    const token = sessionStorage.getItem("token");
    if (!token) {
      navigate("/"); // redirect if not logged in
      return;
    }

    fetch("http://localhost:8080/api/getProfile", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => setProfile(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [navigate]);

  if (loading) return <div className="profile-container"><div className="profile-card">Loading profile...</div></div>;
  if (error) return <div className="profile-container"><div className="profile-card">{error}</div></div>;

  return (
    <div className="profile-container">
      <div className="profile-card">
        {/* Avatar Placeholder */}
        <div className="profile-avatar">
          {profile.name ? profile.name.charAt(0).toUpperCase() : "U"}
        </div>

        <h2 className="profile-title">Student Profile</h2>

        <div className="profile-info">
          <p><strong>Name:</strong> {profile.name}</p>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Registration No:</strong> {profile.regNo}</p>
          <p><strong>Course:</strong> {profile.course}</p>
          <p><strong>CGPA:</strong> {profile.cgpa}</p>
          <p><strong>Hosteller/Dayscholar:</strong> {profile.hostellerOrDayscholar}</p>
          <p><strong>Preferences:</strong> {profile.preferences?.join(", ") || "None"}</p>
          <button onClick={viewResume} className="download-resume-btn">
  View Resume
</button>

        </div>

        <hr className="profile-divider" />

        
      </div>
    </div>
  );
}

export default StudentProfile;
