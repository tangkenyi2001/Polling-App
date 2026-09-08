const KEY = "votedPolls";

export function recordVote(pollId) {
  const votes = getVotes();
  if (votes.some((v) => v.pollId === pollId)) return;
  votes.push({ pollId, votedAt: new Date().toISOString() });
  localStorage.setItem(KEY, JSON.stringify(votes));
}

export function getVotes() {
  const raw = localStorage.getItem(KEY);
  return raw ? JSON.parse(raw) : [];
}
