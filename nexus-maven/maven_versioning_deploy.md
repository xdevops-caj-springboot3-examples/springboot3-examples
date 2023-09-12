# Maven versioning and deploy

## Configure snapshot and release repositories in pom.xml

Modify `pom.xml` under project folder:

```xml
	<distributionManagement>
		<repository>
			<id>nexus-releases</id>
			<name>Nexus Release Repository</name>
			<url>http://localhost:8081/repository/maven-releases/</url>
		</repository>
		<snapshotRepository>
			<id>nexus-snapshots</id>
			<name>Nexus Snapshot Repository</name>
			<url>http://localhost:8081/repository/maven-snapshots/</url>
		</snapshotRepository>
	</distributionManagement>
```

When run `mvn deploy`:
- The release versioned jar (without `-SNAPSHOT`) will be pushed into `maven-releases` repository in Nexus.
- The snapshto versioned jar (with `-SNAPSHOT`) will be pushed into `maven-snapshots` repository in Nexus.

## Configure reposistories credentials in settings.xml

Modify `~/.m2/settings.xml`:

```xml
  <servers>
    <server>
      <id>nexus-releases</id>
      <username>admin</username>
      <password>admin123</password>
    </server>
      <server>
      <id>nexus-snapshots</id>
      <username>admin</username>
      <password>admin123</password>
    </server>
  </servers>
```

The server id of `servers` in `settings.xml` is same as repository id of `distributionManagement` in `pom.xml`

## Deploy released jar to Nexus 

Change to released version:

```bash
# modify project version in pom.xml
# e.g. change version from 0.0.1-SNAPSHOT to 0.0.1
mvn versions:set -DnewVersion=0.0.1
```

It will backup the original `pom.xml` to `pom.xml.versionsBackup`.

You can add `pom.xml.versionsBackup` to `.gitignore` to avoid commiting this file to Git repository.

Deploy to nexus:
```bash
mvn deploy
```

## Change to snapshot version again

```bash
# modify project version in pom.xml
# e.g. change version from 0.0.1 to 0.0.2--SNAPSHOT
mvn versions:set -DnewVersion=0.0.2-SNAPSHOT
```

