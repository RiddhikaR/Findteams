import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import "../css/login.css";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [captcha, setCaptcha] = useState("");
  const [captchaRequired, setCaptchaRequired] = useState(false);
  const [captchaSrc, setCaptchaSrc] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [accountExists, setAccountExists] = useState(false);
useEffect(() => {
  const checkUserExists = async () => {
    if (username.trim().length === 0) {
      setAccountExists(false);
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/vtop/check-user?username=${username}`);
      const data = await res.json();
      setAccountExists(data.exists);
    } catch (err) {
      console.error("Error checking user existence", err);
    }
  };

  checkUserExists();
}, [username]);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    const payload = { username, password };
    if (captchaRequired) payload.captcha = captcha;

    try {
      const response = await fetch("http://localhost:8080/api/vtop/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const data = await response.json();

      if (data.captcha_required) {
        setCaptchaRequired(true);
        setCaptchaSrc(data.captcha_src);
        setMessage(data.message || "Please solve the CAPTCHA.");
      } else {
        setCaptchaRequired(false);
        setCaptchaSrc(null);
        setMessage(data.message);

        if (data.success) {
          sessionStorage.setItem("username", username);
          navigate("/setPassword");
        }
      }
    } catch (error) {
      setMessage("Error connecting to server.");
      console.error(error);
    }

    setLoading(false);
  };

  return (
    <div className="login-page">
      <div className="login-box">
        <h2>Create An Account</h2>

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Enter Your Reg No.(Eg:23BRS1101)"
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            disabled={loading}
          />
{accountExists && (
  <p className="message" style={{ color: "red" }}>
    Account already exists. Please log in.
  </p>
)}

          <input
            type="password"
            placeholder="Enter Your Vtop Password(One Time)"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={loading}
          />

          {captchaRequired && (
            <>
              <img src={captchaSrc} alt="CAPTCHA" />
              <input
                type="text"
                placeholder="Enter CAPTCHA"
                value={captcha}
                onChange={(e) => setCaptcha(e.target.value)}
                required
                disabled={loading}
              />
            </>
          )}

         <button type="submit" disabled={loading || accountExists}>
  {loading ? "Logging in..." : "Login"}
</button>
        </form>

        <p>
          Already Created An Account?{" "}
          <button onClick={() => navigate("/websiteLogin")}>Login To The Website</button>
        </p>
        

        {message && <p className="message">{message}</p>}
      </div>
    </div>
  );
}

export default LoginPage;
