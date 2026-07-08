# Shipfolio

Shipfolio reads a developer's GitHub repos and uses AI to write recruiter-ready
case studies — problem, stack, decisions, impact — then publishes them as a
portfolio site. Full product plan (market, pricing, roadmap, tech stack):
see the conversation this repo was built from, or `docs/plan.md` once added.

**Current milestone: landing page + waitlist**, the validation step before
building the real product (GitHub import, AI generation, publishing).

## Stack

Next.js 16 (App Router) · TypeScript · Tailwind CSS v4 · Supabase (waitlist storage)

## Getting started

```bash
npm install
cp .env.example .env.local   # fill in Supabase credentials, see below
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

### Waitlist setup (Supabase)

1. Create a project at [supabase.com](https://supabase.com).
2. Run the migration in `supabase/migrations/0001_waitlist.sql` (SQL editor
   or `supabase db push` if you use the CLI).
3. In **Project Settings → API**, copy the Project URL and the
   `service_role` secret key into `.env.local` as `SUPABASE_URL` and
   `SUPABASE_SERVICE_ROLE_KEY`.

Without these two variables the `/api/waitlist` route returns a clear
"not configured" error instead of the page crashing.

## Roadmap

- [x] Landing page + waitlist capture
- [ ] GitHub OAuth + repo import
- [ ] AI case-study generation pipeline (Claude API)
- [ ] Theme system + publishing on subdomains
- [ ] Stripe billing (Free / Pro)
- [ ] Analytics + weekly digest email

## Deploy

Built for [Vercel](https://vercel.com). Set the same environment variables
from `.env.example` in the project's Vercel settings before deploying.
