import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { getSession } from "../auth";
import Brand from "../components/Brand";

const POLL_TYPES = [
  { value: "MCQ", label: "Multiple choice" },
  { value: "RATING", label: "Rating" },
  { value: "WORDCLOUD", label: "Word cloud" },
];

const EXPIRY_PRESETS = [
  { value: "", label: "Never" },
  { value: "5m", label: "5 minutes from now" },
  { value: "10m", label: "10 minutes from now" },
  { value: "30m", label: "30 minutes from now" },
  { value: "1h", label: "1 hour from now" },
  { value: "1d", label: "1 day from now" },
  { value: "custom", label: "Custom date/time..." },
];

function presetToDate(preset) {
  const minutes = { "5m": 5, "10m": 10, "30m": 30, "1h": 60, "1d": 60 * 24 }[preset];
  if (!minutes) return null;
  return new Date(Date.now() + minutes * 60 * 1000);
}

export default function CreatePoll() {
  const [title, setTitle] = useState("");
  const [pollType, setPollType] = useState("MCQ");
  const [options, setOptions] = useState(["", ""]);
  const [minRating, setMinRating] = useState(1);
  const [maxRating, setMaxRating] = useState(5);
  const [expiryPreset, setExpiryPreset] = useState("");
  const [customExpiresAt, setCustomExpiresAt] = useState("");
  const [error, setError] = useState("");
  const [created, setCreated] = useState(null);
  const navigate = useNavigate();
  const session = getSession();

  function updateOption(index, value) {
    setOptions(options.map((opt, i) => (i === index ? value : opt)));
  }

  function addOption() {
    setOptions([...options, ""]);
  }

  function removeOption(index) {
    setOptions(options.filter((_, i) => i !== index));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const cleanOptions = options.map((o) => o.trim()).filter(Boolean);
      const expiryDate =
        expiryPreset === "custom"
          ? customExpiresAt
            ? new Date(customExpiresAt)
            : null
          : presetToDate(expiryPreset);
      const poll = await api.createPoll({
        ownerId: session.userId,
        question: title,
        pollType,
        expiryDate: expiryDate ? expiryDate.toISOString() : undefined,
        options: pollType === "MCQ" ? cleanOptions : undefined,
        minRating: pollType === "RATING" ? Number(minRating) : undefined,
        maxRating: pollType === "RATING" ? Number(maxRating) : undefined,
      });
      setCreated(poll.id);
    } catch (err) {
      setError(err.message);
    }
  }

  if (!session) {
    return (
      <div className="page-center">
      <div className="card">
        <Brand />
        <h1>Log in to create a poll</h1>
        <p className="subtitle">Voting stays anonymous-friendly, but creating a poll needs an account.</p>
        <div className="button-row">
          <button onClick={() => navigate("/login")}>Log in / Sign up</button>
          <button className="secondary" onClick={() => navigate("/")}>
            Back home
          </button>
        </div>
      </div>
      </div>
    );
  }

  if (created) {
    return (
      <div className="page-center">
      <div className="card">
        <Brand />
        <h1>Poll created</h1>
        <p className="subtitle">Share this poll ID with voters:</p>
        <code className="poll-id">{created}</code>
        <div className="button-row">
          <button onClick={() => navigate(`/vote/${created}`)}>Go vote on it</button>
          <button className="secondary" onClick={() => navigate("/")}>
            Back home
          </button>
        </div>
      </div>
      </div>
    );
  }

  return (
    <div className="page-center">
    <div className="card">
      <Brand />
      <h1>Create a poll</h1>
      <p className="subtitle">Pick a type and give it a title.</p>
      <form onSubmit={handleSubmit}>
        <label>
          Title
          <input
            required
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Do you like the new loot system?"
          />
        </label>
        <label>
          Poll type
          <div className="segmented">
            {POLL_TYPES.map((t) => (
              <button
                key={t.value}
                type="button"
                className={pollType === t.value ? "active" : ""}
                onClick={() => setPollType(t.value)}
              >
                {t.label}
              </button>
            ))}
          </div>
        </label>

        {pollType === "MCQ" && (
          <div className="options-editor">
            <p className="muted" style={{ margin: 0 }}>
              Options
            </p>
            {options.map((opt, i) => (
              <div key={i} className="option-row">
                <input
                  value={opt}
                  placeholder={`Option ${i + 1}`}
                  onChange={(e) => updateOption(i, e.target.value)}
                />
                {options.length > 2 && (
                  <button type="button" className="link" onClick={() => removeOption(i)}>
                    remove
                  </button>
                )}
              </div>
            ))}
            <button type="button" className="secondary" onClick={addOption}>
              + Add option
            </button>
          </div>
        )}

        {pollType === "RATING" && (
          <div className="option-row">
            <label>
              Min rating
              <input
                type="number"
                value={minRating}
                onChange={(e) => setMinRating(e.target.value)}
              />
            </label>
            <label>
              Max rating
              <input
                type="number"
                value={maxRating}
                onChange={(e) => setMaxRating(e.target.value)}
              />
            </label>
          </div>
        )}

        <label>
          Closes at
          <select
            value={expiryPreset}
            onChange={(e) => setExpiryPreset(e.target.value)}
          >
            {EXPIRY_PRESETS.map((p) => (
              <option key={p.value} value={p.value}>
                {p.label}
              </option>
            ))}
          </select>
        </label>

        {expiryPreset === "custom" && (
          <label>
            Custom close date/time
            <input
              type="datetime-local"
              value={customExpiresAt}
              onChange={(e) => setCustomExpiresAt(e.target.value)}
            />
          </label>
        )}

        {error && <p className="error">{error}</p>}
        <div className="button-row">
          <button type="submit">Create poll</button>
          <button type="button" className="secondary" onClick={() => navigate("/")}>
            Cancel
          </button>
        </div>
      </form>
    </div>
    </div>
  );
}
