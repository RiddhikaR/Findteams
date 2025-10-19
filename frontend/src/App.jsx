import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SetPasswordPage from "./pages/SetPasswordPage";
import ResumeUpload from "./pages/ResumeUpload";
import Preferences from "./pages/Preferences";
import StudentProfile from "./pages/StudentProfile";
import SearchStudents from "./pages/SearchStudents";
import WebLoginPage from "./pages/WebLoginPage";
import Dashboard from "./pages/Dashboard"; // new dashboard page
import NotificationsPage from "./pages/NotificationsPage";
import GroupsPage from "./pages/GroupsPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route
          path="/setPassword"
          element={
            <ProtectedRoute>
              <SetPasswordPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/resume"
          element={
            <TokenProtected>
              <ResumeUpload />
            </TokenProtected>
          }
        />
        <Route
          path="/preferences"
          element={
            <TokenProtected>
              <Preferences />
            </TokenProtected>
          }
        />
        <Route
          path="/profile"
          element={
            <TokenProtected>
              <StudentProfile />
            </TokenProtected>
          }
        />
        <Route
          path="/searchStudents"
          element={
            <TokenProtected>
              <SearchStudents />
            </TokenProtected>
          }
        />
        <Route
          path="/dashboard"
          element={
            <TokenProtected>
              <Dashboard />
            </TokenProtected>
          }
        />
        <Route
          path="/NotificationsPage"
          element={
            <TokenProtected>
              <NotificationsPage />
            </TokenProtected>
          }
        />
        <Route
          path="/GroupsPage"
          element={
            <TokenProtected>
              <GroupsPage />
            </TokenProtected>
          }
        />
        <Route path="/websiteLogin" element={<WebLoginPage />} />
      </Routes>
    </Router>
  );
}

function ProtectedRoute({ children }) {
  const username = sessionStorage.getItem("username");
  return username ? children : <Navigate to="/" replace />;
}

function TokenProtected({ children }) {
  const token = sessionStorage.getItem("token");
  return token ? children : <Navigate to="/" replace />;
}

export default App;
