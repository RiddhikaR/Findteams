import React, { useState } from "react";
import Select from "react-select";
import { useNavigate } from "react-router-dom";
import "../css/setPreferences.css"; // Import CSS file

function SetPreferences() {
  const [preferences, setPreferences] = useState([]);
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const options = [
    { value: "Hackathons", label: "Hackathons" },
    { value: "Research", label: "Research" },
    { value: "Technical Clubs", label: "Technical Clubs" },
    { value: "Non Technical Clubs", label: "Non Technical Clubs" },
    { value: "Projects", label: "Projects" },
    { value: "Internships", label: "Internships" },
  ];

  const handleSubmit = async () => {
    const token = sessionStorage.getItem("token");
    if (!token) {
      setMessage("⚠️ User not authenticated");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/findteams/setPreferences", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({ preferences: preferences.map((p) => p.value) }),
      });

      const data = await response.json();
      console.log("Backend response:", data);

      if (data.message === "preferences has been set successfully") {
        navigate("/Dashboard");
      } else {
        setMessage(data.message || "❌ Failed to save preferences");
      }
    } catch (err) {
      console.error(err);
      setMessage("⚠️ Error: " + err.message);
    }
  };

  return (
    <div className="preferences-container">
      <div className="preferences-box">
        <h2 className="preferences-title">Select Your Preferences</h2>
        <Select
          isMulti
          options={options}
          value={preferences}
          onChange={setPreferences}
          placeholder="Choose preferences..."
          className="preferences-select"
        />
        <button className="save-btn" onClick={handleSubmit}>
          Save Preferences
        </button>
        {message && <p className="preferences-message">{message}</p>}
      </div>
    </div>
  );
}

export default SetPreferences;
