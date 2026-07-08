"use client";

import { useState, type FormEvent } from "react";

type Status = "idle" | "loading" | "success" | "already" | "error";

export function WaitlistForm() {
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState<Status>("idle");
  const [message, setMessage] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus("loading");
    setMessage("");

    try {
      const res = await fetch("/api/waitlist", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });
      const data = await res.json();

      if (!res.ok) {
        setStatus("error");
        setMessage(data.error ?? "Something went wrong. Try again.");
        return;
      }

      if (data.alreadyOnList) {
        setStatus("already");
      } else {
        setStatus("success");
      }
      setEmail("");
    } catch {
      setStatus("error");
      setMessage("Couldn't reach the server. Try again.");
    }
  }

  if (status === "success" || status === "already") {
    return (
      <div className="waitlist-done">
        <span className="pill-good">
          {status === "success" ? "You're on the list" : "Already on the list"}
        </span>
        <p>We&apos;ll email you the moment early access opens.</p>
      </div>
    );
  }

  return (
    <form className="waitlist-form" onSubmit={handleSubmit}>
      <div className="waitlist-field">
        <input
          type="email"
          required
          placeholder="you@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          aria-label="Email address"
        />
        <button type="submit" disabled={status === "loading"}>
          {status === "loading" ? "Joining…" : "Get early access"}
        </button>
      </div>
      {status === "error" && <p className="waitlist-error">{message}</p>}
    </form>
  );
}
