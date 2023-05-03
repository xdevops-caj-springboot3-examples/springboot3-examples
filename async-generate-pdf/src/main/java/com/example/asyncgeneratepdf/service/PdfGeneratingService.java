package com.example.asyncgeneratepdf.service;

import com.example.asyncgeneratepdf.entity.City;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.property.ListNumberingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PdfGeneratingService {

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
}
