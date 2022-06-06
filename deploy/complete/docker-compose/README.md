# Micronaut MuShop Docker

The complete **Micronaut MuShop** application can be run using `docker-compose` locally.

Visit the [documentation](https://oracle-quickstart.github.io/oci-micronaut/quickstart/dockecompose/) for detailed instructions.

To export logs:

```shell
for service in api carts catalogue fulfillment orders payment user ; do docker-compose logs --no-color --no-log-prefix $service | ansi2txt > logs-$service.txt ; done 
```

