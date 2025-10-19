import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/resumeUpload.css"; // Import CSS file

function ResumeUpload() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    const token = sessionStorage.getItem("token");
    if (!file || !token) {
      setMessage("No file or token found");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("http://localhost:8080/api/findteams/uploadResume", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
        body: formData,
      });

      const data = await response.json();
      if (data.success) {
        setMessage("✅ Resume uploaded successfully!");
        navigate("/preferences", { replace: true });
      } else {
        setMessage(data.message || "❌ Error uploading resume");
      }
    } catch (err) {
      setMessage("⚠️ Error: " + err.message);
    }
  };

  return (
    <div className="resume-container">
      <div className="resume-box">
        <h2 className="resume-title">Upload Resume</h2>
        <input type="file" className="file-input" onChange={handleFileChange} />
        <button className="upload-btn" onClick={handleUpload}>
          Upload
        </button>
        {message && <p className="resume-message">{message}</p>}
      </div>
    </div>
  );
}

export default ResumeUpload;
