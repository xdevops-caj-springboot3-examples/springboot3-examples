# Generate PDF Asynchronously with @Async

## Dependencies

- itext7 for pdf generating
- Spring Web for REST API
- H2 for in-memory database
- Spring Data JPA for database access

## Run the application

```bash
mvn spring-boot:run
```

When run the application, it will execute `resources/import.sql` to initialize the database.

## Enable and configure Async

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor pdfGenerateExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("pdf-");
        executor.initialize();
        return executor;
    }
}
```

Notes:
- Inject a bean named `pdfGenerateExecutor`

## Generate PDF Async

```java
    @Async("pdfGenerateExecutor")
    public CompletableFuture<ByteArrayOutputStream> generatePdf(java.util.List<City> cityList) {
        log.info("Generating PDF...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create a new PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));

        try (Document document = new Document(pdf, PageSize.A4)) {

            // Create a new list with the items
            List list = new List(ListNumberingType.DECIMAL);
            for (City city : cityList) {
                list.add(new ListItem(city.toString()));
            }

            document.add(list);
        }

        //TODO simulate slow PDF generation
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("PDF generated successfully");
        return CompletableFuture.completedFuture(baos);
    }
```

Notes:
- Indicate the method is async by annotating with `@Async("pdfGenerateExecutor")`
- Return a `CompletableFuture<ByteArrayOutputStream>` to allow the caller to wait for the result
- Sleep 10s to simulate slow PDF generation

## Call Async method

```java
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf() {
        List<City> cityList = cityRepository.findAll();
        CompletableFuture<ByteArrayOutputStream> baos = pdfGeneratingService.generatePdf(cityList);
        CompletableFuture.allOf(baos).join();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cities.pdf");

        try {
            return new ResponseEntity<>(baos.get().toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
```

Notes:
- Use `CompletableFuture.allOf(baos).join()` to wait for the result.

## Testing

Open two tabs in Browser, and open `http://localhost:8080/cities/pdf`.

It will generate and download the pdf file automatically.

Check logs:

```text
2023-05-03T12:08:31.017+08:00  INFO 22797 --- [          pdf-1] c.e.a.service.PdfGeneratingService       : Generating PDF...
2023-05-03T12:08:40.956+08:00  INFO 22797 --- [          pdf-1] c.e.a.service.PdfGeneratingService       : PDF generated successfully
2023-05-03T12:08:41.594+08:00  INFO 22797 --- [          pdf-2] c.e.a.service.PdfGeneratingService       : Generating PDF...
2023-05-03T12:08:51.607+08:00  INFO 22797 --- [          pdf-2] c.e.a.service.PdfGeneratingService       : PDF generated successfully
```

Notes:
- The thread name is `[pdf-1]` and `[pdf-2]`, which means the PDF generation is running in different threads.
- Takes about 10s to generate the PDF file.


## References

- https://spring.io/guides/gs/async-method/
- https://www.techgeeknext.com/asyncsp