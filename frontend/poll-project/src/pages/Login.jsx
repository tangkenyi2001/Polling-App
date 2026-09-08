import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { setSession } from "../auth";
import Brand from "../components/Brand";

export default function Login() {
  const [mode, setMode] = useState("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const isLogin = mode === "login";

  function switchMode(next) {
    setMode(next);
    setError("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const { id } = isLogin
        ? await api.loginUser(email, password)
        : await api.createUser(email, password);
      setSession({ userId: id, email });
      navigate("/");
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="page-center">
    <div className="card">
      <Brand />
      <h1>{isLogin ? "Welcome back" : "Create an account"}</h1>
      <p className="subtitle">
        {isLogin ? "Log in to manage your polls." : "Sign up to start creating polls."}
      </p>
      <form onSubmit={handleSubmit}>
        <label>
          Email
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
          />
        </label>
        <label>
          Password
          <input
            type="password"
            required
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />
        </label>
        {error && <p className="error">{error}</p>}
        <div className="button-row">
          <button type="submit">{isLogin ? "Log in" : "Sign up"}</button>
        </div>
      </form>
      <button
        className="link"
        onClick={() => switchMode(isLogin ? "signup" : "login")}
      >
        {isLogin ? "New here? Sign up →" : "Already have an account? Log in →"}
      </button>
    </div>
    </div>
  );
}
