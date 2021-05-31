---
title: "Database Access"
draft: false
disableBreadcrumb: true
weight: 6
---

The original MuShop application includes a lot of handwritten SQL logic that is greatly simplified by the use of [Micronaut Data](https://micronaut-projects.github.io/micronaut-data/latest/guide/).

In the Go version of the following code, SQL is appended together manually and executed in order to perform a count:

```golang
func (s *catalogueService) Count(categories []string) (int, error) {
	query := "SELECT COUNT(DISTINCT products.sku) FROM products JOIN product_category ON products.sku=product_category.sku JOIN categories ON product_category.category_id=categories.category_id"

	var args []interface{}

	for i, t := range categories {
		if i == 0 {
			query += " WHERE categories.name=:categoryname"
			args = append(args, t)
		} else {
			query += " OR categories.name=:categoryname"
			args = append(args, t)
		}
	}

	sel, err := s.db.Prepare(query)

	if err != nil {
		s.logger.Log("database error", err)
		return 0, ErrDBConnection
	}
	defer sel.Close()

	var count int
	err = sel.QueryRow(args...).Scan(&count)

	if err != nil {
		s.logger.Log("database error", err)
		return 0, ErrDBConnection
	}

	return count, nil
}

```

The above code is littered with error prone logic to produce the correct query and ensure database connetions are closed and error handling logic correctly applied.

The service logic still then needs to be wired into an exposed endpoing with `Go`:

```golang
func MakeEndpoints(s Service, tracer stdopentracing.Tracer) Endpoints {
	return Endpoints{
		CountEndpoint:      opentracing.TraceServer(tracer, "GET /catalogue/size")(MakeCountEndpoint(s)),
		...
	}
}

func MakeCountEndpoint(s Service) endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (response interface{}, err error) {
		req := request.(countRequest)
		n, err := s.Count(req.Categories)
		return countResponse{N: n, Err: err}, err
	}
}
```

All of this complexity is removed in the Micronaut version thanks to Micronaut Data into a simple repository declaration:

```java
@JdbcRepository(dialect = Dialect.ORACLE)
public interface ProductRepository extends PageableRepository<Product, String> {
	...

    int countDistinct();

    int countDistinctByCategoriesNameInList(List<String> categories);
}

```

The SQL queries are automatically implemented at compilation time based on the conventions in the repository interface using the [repository pattern](https://martinfowler.com/eaaCatalog/repository.html).

The endpoint can then just use the repository to easily expose the necessary data over HTTP:

```java
@Get("/catalogue/size{?categories}")
public CatalogueSizeDTO size(@QueryValue @Nullable List<String> categories) {
    if (categories == null || categories.isEmpty()) {
        return new CatalogueSizeDTO(productRepository.countDistinct());
    } else {
        return new CatalogueSizeDTO(productRepository.countDistinctByCategoriesNameInList(categories));
    }
}
```

The code is even more complex for listing records instead of performing a count, with Go manual pagination of records is implemented to combine with the already complex SQL concatenation logic:

```golang
func (s *catalogueService) List(categories []string, order string, pageNum, pageSize int) ([]Product, error) {
	var products []Product
	query := baseQuery

	var args []interface{}

	for i, t := range categories {
		if i == 0 {
			query += " WHERE categories.name=:categoryname"
			args = append(args, t)
		} else {
			query += " OR categories.name=:categoryname"
			args = append(args, t)
		}
	}

	if order != "" {
		query += " ORDER BY :orderby"
		args = append(args, order)
	}

	err := s.db.Select(&products, query, args...)
	if err != nil {
		s.logger.Log("database error", err)
		return []Product{}, ErrDBConnection
	}
	for i, s := range products {
		products[i].ImageURL = []string{s.ImageURL1, s.ImageURL2}
		products[i].Categories = strings.Split(s.CategoryString, ",")
	}


	products = cut(products, pageNum, pageSize)

	return products, nil
}

func cut(products []Product, pageNum, pageSize int) []Product {
	if pageNum == 0 || pageSize == 0 {
		return []Product{} // pageNum is 1-indexed
	}
	start := (pageNum * pageSize) - pageSize
	if start > len(products) {
		return []Product{}
	}
	end := (pageNum * pageSize)
	if end > len(products) {
		end = len(products)
	}
	return products[start:end]
}
``` 

All of this is unnecessary in the Micronaut version as Micronaut Data has the built in ability to bind pagination parameters from the request and perform pagination on data retrieved from the database:

```java
@Get("/catalogue{?categories}")
public List<CatalogueItemDTO> list(
        @Nullable List<String> categories,
        @Nullable Pageable pageable) {
    Stream<Product> productStream;
    if (pageable == null || pageable == Pageable.UNPAGED) {
        productStream = productRepository.findAll().stream();
    } else {
        productStream = productRepository.list(pageable).stream();
    }
    return productStream.map(this::toDTO)
            .collect(Collectors.toList());
}

private CatalogueItemDTO toDTO(Product product) {
    return new CatalogueItemDTO(
            product.getSku(),
            product.getBrand(),
            product.getTitle(),
            product.getDescription(),
            product.getWeight(),
            product.getProductSize(),
            product.getColors(),
            product.getQuantity(),
            product.getPrice(),
            getImageUrl(product),
            getCategories(product)
    );
}
```

The Micronaut Data version is in fact more flexible as you can customize the default pagination parameter names and sizes through simple configuration in `application.yml`:

```yaml
micronaut:
  data:
    pageable:
      default-page-size: -1
      sort-parameter-name: sort
      page-parameter-name: page
      size-parameter-name: size
```

For example changing the `page-parameter-sort` setting allows you to change the name of the HTTP request parameter used in the query string from client requests to alter how data is sorted.