import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSession, clearSession } from "../auth";
import Brand from "../components/Brand";

export default function Home() {
  const session = getSession();
  const navigate = useNavigate();
  const [pollId, setPollId] = useState("");

  function logout() {
    clearSession();
    navigate("/login");
  }

  function joinPoll(e) {
    e.preventDefault();
    if (pollId.trim()) {
      navigate(`/vote/${pollId.trim()}`);
    }
  }

  return (
    <div className="page-full">
      <header className="top-bar">
        <Brand />
        {session ? (
          <div className="account-chip">
            <button className="secondary" onClick={() => navigate("/my-polls")}>
              My polls
            </button>
            <span className="muted">{session.email}</span>
            <button className="secondary" onClick={logout}>
              Log out
            </button>
          </div>
        ) : (
          <button className="secondary" onClick={() => navigate("/login")}>
            Log in / Sign up
          </button>
        )}
      </header>

      <main className="hero">
        <h1>Join a poll</h1>
        <p className="subtitle">Enter a poll ID to see it and cast your vote.</p>
        <form className="join-form" onSubmit={joinPoll}>
          <input
            required
            autoFocus
            value={pollId}
            onChange={(e) => setPollId(e.target.value)}
            placeholder="Paste a poll ID"
          />
          <button type="submit">Join</button>
        </form>
      </main>

      <button className="fab" onClick={() => navigate("/create")}>
        + Create a poll
      </button>
    </div>
  );
}
