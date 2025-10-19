import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/setPassword.css";

function SetPasswordPage() {
  const [password, setPassword] = useState("");
  const [confirmpassword, setConfirmpassword] = useState("");
  const [message, setMessage] = useState("");

  const username = sessionStorage.getItem("username");
  const navigate = useNavigate();

  // Password validation rules
  const rules = {
    length: password.length >= 8,
    number: /\d/.test(password),
    uppercase: /[A-Z]/.test(password),
    special: /[!@#$%^&*(),.?":{}|<>]/.test(password),
    match: password === confirmpassword && password.length > 0,
  };

  const isAllValid =
    rules.length && rules.number && rules.uppercase && rules.special && rules.match;

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        "http://localhost:8080/api/findteams/setPassword",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password }),
        }
      );
      const data = await response.json();
      if (data.success) {
        setMessage(data.message);
        sessionStorage.setItem("token", data.token);
        navigate("/resume", { replace: true });
      } else {
        setMessage("Error setting up password");
      }
    } catch (err) {
      setMessage("Server error. Please try again later.");
      console.error(err);
    }
  };

  return (
    <div className="set-password-page">
      <div className="set-password-box">
        <h2>Set Your Password</h2>
        <form onSubmit={handleSubmit}>
          <label>Enter new password</label>
          <input
            type="password"
            placeholder="Enter password"
            value={password}
            required
            onChange={(e) => setPassword(e.target.value)}
          />

          <label>Confirm password</label>
          <input
            type="password"
            placeholder="Confirm password"
            value={confirmpassword}
            required
            onChange={(e) => setConfirmpassword(e.target.value)}
          />

          {/* Password Rules */}
          <ul className="password-rules">
            <li className={rules.length ? "valid" : "invalid"}>
              {rules.length ? "✔" : "✖"} At least 8 characters
            </li>
            <li className={rules.number ? "valid" : "invalid"}>
              {rules.number ? "✔" : "✖"} Must include a number
            </li>
            <li className={rules.uppercase ? "valid" : "invalid"}>
              {rules.uppercase ? "✔" : "✖"} Must include an uppercase letter
            </li>
            <li className={rules.special ? "valid" : "invalid"}>
              {rules.special ? "✔" : "✖"} Must include a special character
            </li>
            <li className={rules.match ? "valid" : "invalid"}>
              {rules.match ? "✔" : "✖"} Passwords must match
            </li>
          </ul>

          <button type="submit" disabled={!isAllValid}>
            Set Password
          </button>
        </form>

        {message && <p className="message">{message}</p>}
      </div>
    </div>
  );
}

export default SetPasswordPage;
