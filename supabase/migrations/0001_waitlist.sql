create table if not exists waitlist_signups (
  id uuid primary key default gen_random_uuid(),
  email text not null unique,
  created_at timestamptz not null default now()
);

alter table waitlist_signups enable row level security;

-- Inserts happen only through the server route using the service role key,
-- so no client-facing policy is needed here.
