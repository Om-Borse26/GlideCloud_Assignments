# User Management — React Client

![React UI](Images/React_UI.png)

A minimal React front‑end for managing users with create, read, update, and delete (CRUD) operations.

## Features
- Add users with name, email, and age
- View users with a running count
- Edit and delete existing users
- Simple form validation and clean dark UI

## Tech
- React (Vite or CRA)
- Fetch/Axios via `client/src/services/api.js`
- CSS framework or custom styles

## Prerequisites
- Node.js 18+ and npm

## Setup
1. `cd client`
2. Install deps: `npm install`
3. Configure API base URL:
    - Create `.env` in `client/`
    - Use one of:
        - `VITE_API_URL=http://localhost:5000/api`
        - `REACT_APP_API_URL=http://localhost:5000/api`
    - Ensure `client/src/services/api.js` reads the same variable.
4. Start dev server:
    - Vite: `npm run dev`
    - CRA: `npm start`

## Scripts
- Dev: `npm run dev` or `npm start`
- Build: `npm run build`
- Test: `npm test`

## Expected API
- `GET /users` — list users
- `POST /users` — add user
- `PUT /users/:id` — update user
- `DELETE /users/:id` — remove user

## Project Structure (client)
- `src/components/` — UI components
- `src/services/api.js` — API client
- `src/App.jsx` — app shell
- `src/index.jsx` — entry point

## Notes
- Place `react_ui.png` alongside this file or adjust the image path above.
- Update CORS on the server if accessing from a different origin.
