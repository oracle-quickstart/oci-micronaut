# Load / Integration Tests

These tests simulate actual end user usage of the application. They are used to
validate the overall functionality and can also be used to put simulated load on
the system. The tests are written using [locust.io](http://locust.io)

## Parameters

* `[host]` - The hostname (and port if applicable) where the application is exposed. (Required)
* `[number of clients]` - The nuber of concurrent end users to simulate. (Optional: Default is 2)
* `[total run time]` - The total time to run before terminating the tests. (Optional: Default is 10)

## Deploy to K8S

1. Start the locust pods

```text
kubectl apply -f load-dep.yaml
```

2. Watch pods/hpa

```text
kubectl get hpa --watch
```

3. Teardown

```text
kubectl delete -f load-dep.yaml
```

## Running locally

### Requirements

* locust `pip install locustio`

`./runLocust.sh -h [host] -c [number of clients] -r [total run time]`

## Running in Docker Container

The image has already been pushed to `iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/load:latest`. For example:

```shell
docker run --net=host iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/load:latest -h localhost:81 -c 10 -r 30
```

If you want to build your own image:

* Build `docker build -t mushop/load .`
* Run `docker run mushop/load -h [host] -c [number of clients] -r [total run time]`

In case you want to test MuShop that is running locally:
- Mac OS users use as host `host.docker.internal:[port]`, for MuShop from docker-compose use `host.docker.internal:81`
- Linux users, start the mushop/load docker image with `--net=host` 