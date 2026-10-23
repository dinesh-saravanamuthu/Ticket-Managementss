# Resolve — Ticket Management System (PERN)

A browser-based ticket management application migrated from the supplied single-file Java console project. Built with **PostgreSQL, Express, React, and Node.js**.

## Included functionality

- Registration and login with bcrypt password hashing and JWT sessions
- Role-based access: `USER`, `STAFF`, and `ADMIN`
- Create, search, and filter tickets by status and priority
- Ticket details, threaded comments, status history, and feedback
- Admin ticket assignment, user role management, and reports
- In-app notifications for ticket assignments and status changes
- Responsive dashboard and ticket workflow UI
- Parameterized PostgreSQL queries and API error handling

## Requirements

- Node.js 20 or newer (Node 22 LTS recommended)
- PostgreSQL 14 or newer
- VS Code

## Run locally (Windows PowerShell)

Open this folder in VS Code. Use **two terminals** for client and server.

### 1. Create PostgreSQL database

Ensure PostgreSQL is running. Open `psql` or pgAdmin and run:

```sql
CREATE DATABASE ticket_management;
```

Then open the `ticket_management` database and execute the contents of `database/schema.sql` (in pgAdmin: Query Tool → open the file → Execute).

### 2. Configure backend

In the VS Code terminal:

```powershell
cd server
Copy-Item .env.example .env
```

Edit `server/.env` and set `DATABASE_URL` to your local PostgreSQL username/password. Example:

```env
PORT=5000
NODE_ENV=development
CLIENT_URL=http://localhost:5173
DATABASE_URL=postgresql://postgres:YOUR_POSTGRES_PASSWORD@localhost:5432/ticket_management
JWT_SECRET=replace-with-a-long-random-secret
PGSSL=false
```

Replace `YOUR_POSTGRES_PASSWORD`; do not add quotes unless required by your password parsing. Never commit `.env`.

Install packages and create the demo users:

```powershell
npm install
npm run seed
npm run dev
```

The API should run at `http://localhost:5000`. Check `http://localhost:5000/api/health` for the health response.

### 3. Configure frontend

Open a **second** VS Code terminal from the project root:

```powershell
cd client
Copy-Item .env.example .env
npm install
npm run dev
```

Open the Vite URL shown in the terminal, usually `http://localhost:5173`.

### 4. Demo logins

| Role | Email | Password |
|---|---|---|
| Admin | `admin@ticket.com` | `admin123` |
| Support staff | `staff@ticket.com` | `staff123` |
| User | `user@ticket.com` | `user123` |

These are local-development credentials only. Do not use them on a public deployment. The seed script only creates these accounts if the email does not already exist.

## Test the main workflow

1. Sign in as `user@ticket.com` and create a ticket (title 4+ characters, description 10+ characters).
2. Sign in as `admin@ticket.com` in a separate browser/incognito session.
3. Open the ticket, assign it to `Support Staff`, and update its status.
4. Sign in as `staff@ticket.com`; open the assigned ticket, add a comment, and change its status.
5. Sign back in as the user to review the ticket history, comments, notifications, and submit feedback once resolved.
6. As admin, check the Users and Reports pages.

## Project structure

```text
client/                 React + Vite UI
  src/App.jsx            Routes, pages, forms, and role-aware UI
  src/services/api.js    API client and JWT header
  src/styles.css         Responsive design system
server/                 Express REST API
  src/config/db.js       PostgreSQL connection pool
  src/controllers/       Authentication and ticket business logic
  src/middleware/auth.js JWT authentication and role checks
  src/routes/            API route definitions
  src/seed.js             Creates development demo users
  .env.example            Backend environment template
database/schema.sql      PostgreSQL tables, checks, foreign keys, indexes
TicketManagementSystem.java  Original supplied Java source for reference
```

## API overview

- `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me`
- `GET /api/dashboard`
- `GET/POST /api/tickets`
- `GET /api/tickets/:id`, `PATCH /api/tickets/:id`
- `PATCH /api/tickets/:id/assign`
- `POST /api/tickets/:id/comments`, `POST /api/tickets/:id/feedback`
- `GET /api/users`, `PATCH /api/users/:id/role` (admin)
- `GET /api/reports` (admin)
- `GET /api/notifications`, `PATCH /api/notifications/:id/read`

## Build checks

From `client/` run `npm run build` to create the production frontend in `client/dist/`.
From `server/` run `npm start` to start the API without nodemon.

## Deployment outline

1. Provision a managed PostgreSQL database and run `database/schema.sql` against it.
2. Deploy `server/` to a Node hosting provider. Set `DATABASE_URL`, `JWT_SECRET`, `CLIENT_URL`, `NODE_ENV=production`, and `PGSSL=true` if required by the database provider. Use the server directory as the service root; build/install command `npm install`, start command `npm start`.
3. Deploy `client/` as a static Vite site. Set `VITE_API_URL` to the deployed backend's API base, e.g. `https://your-api.example.com/api`. Build command `npm install && npm run build`, output directory `dist`.
4. Set backend `CLIENT_URL` to the exact deployed frontend origin. Configure SPA fallback to `index.html` on the frontend host.
5. Create/change admin credentials before public use; do not expose secrets in GitHub.
6. Test login, ticket creation, assignment, comments, status transitions, and feedback on the live URLs.

## Notes and limitations

- This is a functional MVP, not a security audit. Before public production use, add rate limiting, email verification/password reset, audit monitoring, and stronger admin-account provisioning.
- Ticket IDs are generated by PostgreSQL; use the IDs shown in the UI.
- PostgreSQL stores all application records persistently; unlike the original console application, data survives restarts.
- The demo seed accounts are development convenience only. Registration always creates a `USER`; users cannot grant themselves staff/admin privileges.
