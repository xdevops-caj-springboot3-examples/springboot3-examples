# Run Nexus with Docker

```bash
cd work/data
mkdir -p nexus-data
chmod -R 777 nexus-data

docker run -d -p 8081:8081 --name nexus \
  -v /Users/william/work/data:/nexus-data \
  sonatype/nexus3
```

The default username is `admin`.

The default password as belows:

```bash
docker exec -it nexus cat /nexus-data/admin.password
```

Login <http://localhost:8081> in browser to open Nexus.

Loging with default username and password, and reset password.

Disabale anonymous access.
