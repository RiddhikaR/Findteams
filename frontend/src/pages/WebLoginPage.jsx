import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/website.css";

function WebsiteLoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const response = await fetch("http://localhost:8080/api/findteams/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      const data = await response.json();
      if (data.success) {
        sessionStorage.setItem("token", data.token);
        sessionStorage.setItem("username", username);
        navigate("/Dashboard");
      } else {
        setMessage(data.message);
      }
    } catch (err) {
      setMessage("Error connecting to server.");
      console.error(err);
    }
  };

  return (
    <div className="website-login-page">
      <div className="website-login-box">
        <h2>Website Login</h2>
        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Login</button>
        </form>
        {message && <p>{message}</p>}
      </div>
    </div>
  );
}

export default WebsiteLoginPage;
