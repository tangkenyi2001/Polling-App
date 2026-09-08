import { Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Home from "./pages/Home";
import CreatePoll from "./pages/CreatePoll";
import VotePoll from "./pages/VotePoll";
import MyPolls from "./pages/MyPolls";
import PollResults from "./pages/PollResults";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Home />} />
      <Route path="/create" element={<CreatePoll />} />
      <Route path="/vote/:pollId" element={<VotePoll />} />
      <Route path="/my-polls" element={<MyPolls />} />
      <Route path="/poll/:pollId/results" element={<PollResults />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
