import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api, fetchPollResults, subscribeToPollVotes } from "../api";
import { deriveStatus } from "../pollUtils";
import Brand from "../components/Brand";
import ResultsView from "../components/ResultsView";

export default function PollResults() {
  const { pollId } = useParams();
  const [poll, setPoll] = useState(null);
  const [results, setResults] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError("");
    api
      .getPoll(pollId)
      .then(async (pollData) => {
        const resultsData = await fetchPollResults(pollData);
        if (!cancelled) {
          setPoll(pollData);
          setResults(resultsData);
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
    if (!poll) return;
    const source = subscribeToPollVotes(poll.id, setResults);
    return () => source.close();
  }, [poll]);

  return (
    <div className="page-center">
      <div className="card">
        <Brand />

        {loading && <p className="muted">Loading results…</p>}
        {error && <p className="error">{error}</p>}

        {poll && results && (
          <>
            <div>
              <h1>{poll.question}</h1>
              <span className={`badge badge-${deriveStatus(poll)}`}>{deriveStatus(poll)}</span>
            </div>
            <ResultsView results={results} />
          </>
        )}

        <button className="link" onClick={() => navigate("/my-polls")}>
          ← Back to my polls
        </button>
      </div>
    </div>
  );
}
