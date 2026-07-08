import { WaitlistForm } from "./waitlist-form";

export default function Home() {
  return (
    <div className="shell">
      <header className="nav">
        <a className="wordmark" href="#">
          ship<b>folio</b>
        </a>
        <a className="nav-link" href="#how-it-works">
          How it works
        </a>
      </header>

      <section className="hero">
        <p className="eyebrow">Early access</p>
        <h1>Your GitHub, turned into a portfolio that gets you hired.</h1>
        <p className="thesis">
          Shipfolio reads your repos and writes the case study for you —{" "}
          <strong>problem, stack, decisions, impact</strong> — then publishes
          it as a portfolio site in minutes, not a weekend.
        </p>
        <WaitlistForm />
      </section>

      <section className="section" id="how-it-works">
        <h2>How it works</h2>
        <ol className="flow">
          <li>
            <span className="step-n">1</span>
            <p>
              <strong>Sign in with GitHub.</strong> One click, no forms.
            </p>
          </li>
          <li>
            <span className="step-n">2</span>
            <p>
              <strong>Pick your projects.</strong> Shipfolio reads the
              README, dependencies, and commit history.
            </p>
          </li>
          <li>
            <span className="step-n">3</span>
            <p>
              <strong>AI drafts the case study.</strong> Problem, your role,
              stack, key decisions, result — ready to refine, not to write
              from scratch.
            </p>
          </li>
          <li>
            <span className="step-n">4</span>
            <p>
              <strong>Publish.</strong> Pick a theme, go live on a free
              subdomain or your own domain.
            </p>
          </li>
        </ol>
      </section>

      <section className="section">
        <h2>Why developers need this</h2>
        <ul className="pain-grid">
          <li>A README explains code to other developers, not your impact to a recruiter.</li>
          <li>Most portfolios were built once during a bootcamp and never touched again.</li>
          <li>Recruiters spend 30–45 seconds on a profile before moving on.</li>
          <li>Every portfolio builder makes you write the content yourself — none of them read your code for you.</li>
        </ul>
      </section>

      <section className="section">
        <h2>Pricing</h2>
        <div className="pricing-grid">
          <div className="price-card">
            <h3>Free</h3>
            <div className="price">€0</div>
            <ul>
              <li>1 published project</li>
              <li>shipfolio.dev subdomain</li>
              <li>Shipfolio badge</li>
            </ul>
          </div>
          <div className="price-card">
            <h3>Pro</h3>
            <div className="price">
              €9<span>/month</span>
            </div>
            <ul>
              <li>Unlimited projects</li>
              <li>Custom domain</li>
              <li>All themes, no badge</li>
              <li>Visitor analytics</li>
            </ul>
          </div>
        </div>
      </section>

      <footer className="footer">
        <p>Shipfolio — built in public. No spam, one email when access opens.</p>
      </footer>
    </div>
  );
}
