# Frontend

This directory contains the Vue 3 + TypeScript frontend for the Restaurant Ordering System.

```powershell
npm ci
npm run dev
```

Vite serves the application on `http://localhost:8080` and proxies `/api` to `http://localhost:8081`, removing the `/api` prefix before forwarding. See the [root README](../README.md) for database and backend startup instructions.
