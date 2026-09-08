const MIN_FONT = 0.85;
const MAX_FONT = 2.6;

export default function ResultsView({ results }) {
  const maxVotes = results.options
    ? Math.max(1, ...results.options.map((o) => o.voteCount))
    : 1;

  const maxWordCount = results.words
    ? Math.max(1, ...results.words.map((w) => w.count))
    : 1;

  function wordSize(count) {
    return MIN_FONT + (count / maxWordCount) * (MAX_FONT - MIN_FONT);
  }

  return (
    <>
      <p className="muted">{results.responseCount} response(s)</p>
      {results.options ? (
        <ul className="results-list">
          {results.options.map((o) => (
            <li key={o.optionId} className="result-bar-row">
              <div className="result-bar-label">
                <span>{o.label}</span>
                <span>{o.voteCount}</span>
              </div>
              <div className="result-bar-track">
                <div
                  className="result-bar-fill"
                  style={{ width: `${(o.voteCount / maxVotes) * 100}%` }}
                />
              </div>
            </li>
          ))}
        </ul>
      ) : results.words ? (
        results.words.length ? (
          <div className="word-cloud">
            {results.words.map((w) => (
              <span
                key={w.word}
                className="word-cloud-item"
                style={{
                  fontSize: `${wordSize(w.count)}rem`,
                  opacity: 0.55 + (w.count / maxWordCount) * 0.45,
                }}
                title={`${w.count} mention(s)`}
              >
                {w.word}
              </span>
            ))}
          </div>
        ) : (
          <p className="muted">No words submitted yet.</p>
        )
      ) : (
        <ul className="results-list">
          {results.text.map((t, i) => (
            <li key={i} className="result-text">
              {t}
            </li>
          ))}
        </ul>
      )}
    </>
  );
}
