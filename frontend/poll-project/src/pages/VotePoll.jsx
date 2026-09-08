import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api, fetchPollResults, subscribeToPollVotes } from "../api";
import { getSession } from "../auth";
import { deriveStatus } from "../pollUtils";
import { recordVote } from "../votes";
import Brand from "../components/Brand";
import ResultsView from "../components/ResultsView";

export default function VotePoll() {
  const { pollId } = useParams();
  const [poll, setPoll] = useState(null);
  const [options, setOptions] = useState([]);
  const [text, setText] = useState("");
  const [optionId, setOptionId] = useState("");
  const [rating, setRating] = useState("");
  const [results, setResults] = useState(null);
  const [voted, setVoted] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const session = getSession();

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError("");
    api
      .getPoll(pollId)
      .then(async (data) => {
        if (cancelled) return;
        setPoll(data);
        if (data.type === "MCQ") {
          const opts = await api.getPollOptions(pollId);
          if (!cancelled) setOptions(opts);
        }
      })
      .catch((err) => {
        if (!cancelled) setError(err.message);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [pollId]);

  useEffect(() => {
    if (!poll || !voted) return;
    const source = subscribeToPollVotes(poll.id, setResults);
    return () => source.close();
  }, [poll, voted]);

  async function submitVote(e) {
    e.preventDefault();
    setError("");
    try {
      const userId = session?.userId ?? null;
      if (poll.type === "MCQ") {
        await api.voteMcq(poll.id, userId, Number(optionId));
      } else if (poll.type === "RATING") {
        await api.voteRating(poll.id, userId, Number(rating));
      } else {
        await api.voteWordCloud(poll.id, userId, text);
      }
      recordVote(poll.id);
      const data = await fetchPollResults(poll);
      setResults(data);
      setVoted(true);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="page-center">
    <div className="card">
      <Brand />

      {loading && <p className="muted">Loading poll…</p>}

      {error && <p className="error">{error}</p>}

      {poll && !voted && (
        <form onSubmit={submitVote}>
          <div>
            <h1>{poll.question}</h1>
            <span className={`badge badge-${deriveStatus(poll)}`}>{deriveStatus(poll)}</span>
          </div>

          {poll.type === "MCQ" ? (
            <div className="options-editor">
              {options.map((opt) => (
                <label key={opt.id} className="radio-row">
                  <input
                    type="radio"
                    name="option"
                    value={opt.id}
                    checked={optionId === String(opt.id)}
                    onChange={(e) => setOptionId(e.target.value)}
                    required
                  />
                  {opt.value}
                </label>
              ))}
            </div>
          ) : poll.type === "RATING" ? (
            <label>
              Your rating
              <input
                required
                type="number"
                value={rating}
                onChange={(e) => setRating(e.target.value)}
              />
            </label>
          ) : (
            <label>
              Your word
              <input required value={text} onChange={(e) => setText(e.target.value)} />
            </label>
          )}

          <div className="button-row">
            <button type="submit">Submit vote</button>
          </div>
        </form>
      )}

      {voted && results && (
        <>
          <h1>{poll.question}</h1>
          <h2>Results</h2>
          <ResultsView results={results} />
        </>
      )}

      <button className="link" onClick={() => navigate("/")}>
        ← Back home
      </button>
    </div>
    </div>
  );
}
