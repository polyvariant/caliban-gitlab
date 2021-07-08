# caliban-gitlab

Caliban-generated client for Gitlab's GraphQL API, for Scala.

## Use

sbt dependency:

```scala
"com.kubukoz" %% "caliban-gitlab" % version
```

For calling queries/mutations, consult [Caliban's documentation](https://ghostdogpr.github.io/caliban).

## Development

`sbt codegen` - generates sources

`sbt githubWorkflowGenerate` - generates the github actions

## Publishing

If you really need to publish from localhost:

- `+publishSigned`
- `sonatypeBundleRelease`
