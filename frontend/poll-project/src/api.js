const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const fieldMessages = data.fields ? Object.values(data.fields).join(", ") : "";
    const err = new Error(fieldMessages || data.error || `Request failed (${res.status})`);
    err.fields = data.fields;
    throw err;
  }
  return data;
}

export const api = {
  createUser: (email, password) =>
    request("/users/register", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),
  loginUser: (email, password) =>
    request("/users/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),

  createPoll: ({ ownerId, question, pollType, expiryDate, options, minRating, maxRating }) => {
    if (pollType === "MCQ") {
      return request("/polls/mcq", {
        method: "POST",
        body: JSON.stringify({ ownerId, question, expiryDate, allowMultipleAnswers: false, options }),
      });
    }
    if (pollType === "RATING") {
      return request("/polls/rating", {
        method: "POST",
        body: JSON.stringify({ ownerId, question, expiryDate, minRating, maxRating }),
      });
    }
    return request("/polls/wordcloud", {
      method: "POST",
      body: JSON.stringify({ ownerId, question, expiryDate, maxWords: 100 }),
    });
  },

  getPoll: (pollId) => request(`/polls/${pollId}`),
  getPollOptions: (pollId) => request(`/polls/${pollId}/options`),
  getPollResults: (pollId) => request(`/polls/${pollId}/results`),
  getUserPolls: (ownerId) => request(`/polls/owner/${ownerId}`),

  voteMcq: (pollId, userId, mcqOptionId) =>
    request(`/polls/${pollId}/responses/mcq`, {
      method: "POST",
      body: JSON.stringify({ userId, mcqOptionId }),
    }),
  voteRating: (pollId, userId, rating) =>
    request(`/polls/${pollId}/responses/rating`, {
      method: "POST",
      body: JSON.stringify({ userId, rating }),
    }),
  voteWordCloud: (pollId, userId, text) =>
    request(`/polls/${pollId}/responses/wordcloud`, {
      method: "POST",
      body: JSON.stringify({ userId, text }),
    }),

  listMcqResponses: (pollId) => request(`/polls/${pollId}/responses/mcq`),
  listRatingResponses: (pollId) => request(`/polls/${pollId}/responses/rating`),
  listWordCloudResponses: (pollId) => request(`/polls/${pollId}/responses/wordcloud`),
};

export function fetchPollResults(poll) {
  return api.getPollResults(poll.id);
}

export function subscribeToPollVotes(pollId, onResults) {
  const source = new EventSource(`${BASE_URL}/polls/${pollId}/stream`);
  source.addEventListener("vote", (event) => onResults(JSON.parse(event.data)));
  return source;
}
