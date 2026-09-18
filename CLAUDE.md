# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A generated Java client for Zenodo's second-generation (InvenioRDM) API. The client code itself is **not
hand-written** — it is generated at build time from the upstream OpenAPI spec at
https://github.com/inveniosoftware/invenio-openapi/blob/master/docs/openapi.yaml via the
`swagger-codegen-maven-plugin`. This repo only contains the Maven build configuration plus a couple of
small example/scaffold files under `src/`.

Reference docs:
- Zenodo/InvenioRDM REST API list: https://inveniosoftware.github.io/invenio-openapi/
- REST API documentation: https://inveniordm.docs.cern.ch/reference/rest_api_index/

## Common commands

Always invoke the Maven wrapper (`./mvnw`), never a system-installed `mvn`, so builds use the project's
pinned Maven version.

```bash
./mvnw clean install        # full build: downloads spec, generates client, compiles, tests, installs
./mvnw clean install -DskipTests   # skip tests
./mvnw test -Dtest=ZenodoClientTest   # run a single test class
```

CI (`.github/workflows/mvn.yml`) runs `./mvnw -B -ntp clean install` on every push, on Ubuntu with JDK 17.

There is no separate lint/checkstyle gate for day-to-day work: checkstyle and spotbugs are configured but
set to `skip`/`failOnError=false` for the generated sources, since generated code is not expected to
conform to project style.

## Build / code generation architecture

The build pipeline (all driven from the single `pom.xml`, no parent POM) works as follows:

1. **`swagger-codegen-maven-plugin`** (v3.0.71, `generate` goal) fetches the OpenAPI spec directly from the
   `inveniosoftware/invenio-openapi` GitHub repo (`inputSpec` is a raw GitHub URL — network access is
   required to build) and generates a `jersey2`-based Java client into
   `target/generated-sources/swagger/src/main/java`.
   - Generated models go in `io.openapi.invenio.model`, generated API classes in `io.openapi.invenio.api`
     (configured via `configOptions.modelPackage` / `configOptions.apiPackage`).
   - Each Invenio resource/tag becomes its own `*Api` class (e.g. `RecordsApi`, `DraftsApi`, `CommunitiesApi`,
     `AccessApi`, `UsersApi`, `VocabulariesApi`, etc.) — there is no single "Zenodo client" facade class.
2. **`replacer` plugin** post-processes the generated sources to rewrite `javax.*` imports to `jakarta.*`
   (`javax.annotation` → `jakarta.annotation`, `javax.ws` → `jakarta.ws`, etc.), because the swagger-codegen
   templates still emit `javax` even though the project depends on the `jakarta.*` Jersey 3 stack.
3. **`build-helper-maven-plugin`** adds `target/generated-sources` (the codegen output) as a compiled source
   root, alongside the hand-written `src/main/java` and `src/test/java`.
4. **`flatten-maven-plugin`** writes a flattened POM to `generated/src/main/resources/pom.xml` (used for
   downstream publishing; not something to hand-edit).
5. HTTP transport is Jersey (`jersey-client`, `jersey-media-multipart`, `jersey-media-json-jackson`); JSON
   via Jackson (including `jackson-datatype-jsr310` for date/time and `jackson-dataformat-xml`).

Because the client is generated fresh from the upstream spec on every build, do not hand-edit anything
under `target/generated-sources/**` — those changes will be discarded. If the generated API surface needs
to change, either wait for an upstream spec update or adjust the `configOptions`/plugin config in `pom.xml`.

## Hand-written code

- `src/main/java/io/dockstore/EntryCreatorExample.java` — a worked example (currently entirely commented
  out, pending the generated classes being wired up) showing the intended usage pattern: create a
  `DepositsApi`/`FilesApi`/`ActionsApi` deposit-and-publish flow against Zenodo (`sandbox.zenodo.org` or
  `zenodo.org`), including setting metadata, creators, and related identifiers, then publishing and
  versioning a deposit.
- `src/test/java/io/dockstore/ZenodoClientTest.java` — corresponding test scaffold (also commented out).

Both files predate/anticipate the new Invenio-generated client (`io.openapi.invenio.*`) and still reference
older `io.swagger.zenodo.client.*` types from a previous codegen setup — treat them as a guide to intended
usage, not working code, until updated to the current generated package names.

## Dependency management

Dependency versions are managed via the `io.dockstore:bom-internal` BOM (imported in
`dependencyManagement`), pulled from the OICR Artifactory repo (`artifacts.oicr.on.ca`). When adding a new
dependency already covered by the BOM, omit the `<version>`.

When a need could be met more than one way, prefer, in order: (1) built-in Java features, (2) a
third-party library already pulled in via Maven elsewhere in the project, (3) a new third-party
dependency — only reach for a new one when neither of the above covers the need.

## Branching

Unlike some other Dockstore repos, this one does not use a `develop`/`main` Hubflow split in practice:
`main` is the default branch and PRs (including dependabot bumps) merge directly into it. A stale
`develop` branch exists but isn't the integration target — base new branches/PRs on `main`.

## Pull requests

When creating a PR, always create it in draft mode. A human developer must be the one to mark it ready
for review/move it out of draft state — Claude Code should not do this itself.

Keep the freeform "Description" and "Review Instructions" sections of `.github/PULL_REQUEST_TEMPLATE.md`
brief — one paragraph each, or two for a genuinely complicated fix. The "Security and Privacy" checklist
section is separate and must be copied into the PR description verbatim — never reword, reformat,
condense, or append explanatory text to a checklist item. Only toggle `[ ]` to `[x]` for an item, and only
after actually confirming that action was taken/verified for this PR; leave it unchecked otherwise.

## JIRA

When adding comments to JIRA tickets, clearly indicate that the comment was written by Claude (e.g. lead
with a line like "This comment was generated by Claude (Claude Code).").
