import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { getSession } from "../auth";
import { toPollRow } from "../pollUtils";
import { getVotes } from "../votes";
import Brand from "../components/Brand";

function PollRow({ poll, timestampLabel, timestamp, onClick }) {
  const [copied, setCopied] = useState(false);

  function copyId(e) {
    e.stopPropagation();
    navigator.clipboard.writeText(String(poll.pollId)).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    });
  }

  return (
    <li className="poll-row" onClick={onClick}>
      <div>
        <div className="poll-row-title">{poll.title}</div>
        <div className="muted">
          {poll.pollType.toLowerCase()} · {timestampLabel}{" "}
          {new Date(timestamp).toLocaleDateString()}
        </div>
        <div className="muted">
          ID: <code>{poll.pollId}</code>{" "}
          <button type="button" className="link" style={{ margin: 0 }} onClick={copyId}>
            {copied ? "Copied!" : "copy"}
          </button>
        </div>
      </div>
      <span className={`badge badge-${poll.status}`}>{poll.status}</span>
    </li>
  );
}

export default function MyPolls() {
  const session = getSession();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!session) return;

    const votes = getVotes();

    Promise.all([
      api.getUserPolls(session.userId).then((polls) => polls.map(toPollRow)),
      Promise.all(
        votes.map((v) =>
          api
            .getPoll(v.pollId)
            .then((poll) => ({ ...toPollRow(poll), votedAt: v.votedAt }))
            .catch(() => null)
        )
      ).then((rows) => rows.filter(Boolean)),
    ])
      .then(([created, voted]) => setData({ created, voted }))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [session]);

  if (!session) {
    return (
      <div className="page-center">
        <div className="card">
          <Brand />
          <h1>Log in to see your polls</h1>
          <p className="subtitle">Track polls you've created or voted on.</p>
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

  return (
    <div className="page-center">
      <div className="card wide">
        <Brand />
        <h1>My polls</h1>
        <p className="subtitle">Signed in as {session.email}</p>

        {loading && <p className="muted">Loading…</p>}
        {error && <p className="error">{error}</p>}

        {data && (
          <>
            <h2>Created by you</h2>
            {data.created.length ? (
              <ul className="poll-list">
                {data.created.map((poll) => (
                  <PollRow
                    key={poll.pollId}
                    poll={poll}
                    timestampLabel="created"
                    timestamp={poll.createdAt}
                    onClick={() => navigate(`/poll/${poll.pollId}/results`)}
                  />
                ))}
              </ul>
            ) : (
              <p className="muted">You haven't created any polls yet.</p>
            )}

            <hr className="divider" />

            <h2>Voted on</h2>
            <p className="muted" style={{ marginTop: 0 }}>
              Tracked on this device only.
            </p>
            {data.voted.length ? (
              <ul className="poll-list">
                {data.voted.map((poll) => (
                  <PollRow
                    key={poll.pollId}
                    poll={poll}
                    timestampLabel="voted"
                    timestamp={poll.votedAt}
                    onClick={() => navigate(`/poll/${poll.pollId}/results`)}
                  />
                ))}
              </ul>
            ) : (
              <p className="muted">You haven't voted on any polls yet.</p>
            )}
          </>
        )}

        <button className="link" onClick={() => navigate("/")}>
          ← Back home
        </button>
      </div>
    </div>
  );
}
