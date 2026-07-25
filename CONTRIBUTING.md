# Contributing

Thanks for contributing to the Restaurant Ordering System.

## Before you start

- Read the root README and the relevant files under `开发文档/`.
- Keep backend paths, request DTOs, and response fields aligned with `开发文档/06-接口文档.md`.
- Do not commit real credentials, production database exports, user data, runtime logs, generated uploads, or IDE/agent configuration.

## Development checks

Run the relevant checks before opening a pull request:

```powershell
# backend/
mvn test

# front/
npm run build
```

If a change needs MySQL, document the schema and test data used without sharing private data.

## Pull requests

- Keep each pull request focused and explain the user-visible impact.
- Update code, API documentation, SQL migrations, and test cases together when the behavior changes.
- State the checks you ran and any checks that could not be run.
- Do not change the project license or add generated artifacts without prior discussion.
