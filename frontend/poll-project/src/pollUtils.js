export function deriveStatus(poll) {
  if (!poll.expiryDate) return "open";
  return new Date(poll.expiryDate) > new Date() ? "open" : "closed";
}

export function toPollRow(poll) {
  return {
    pollId: poll.id,
    title: poll.question,
    pollType: poll.type,
    status: deriveStatus(poll),
    createdAt: poll.createdAt,
  };
}
