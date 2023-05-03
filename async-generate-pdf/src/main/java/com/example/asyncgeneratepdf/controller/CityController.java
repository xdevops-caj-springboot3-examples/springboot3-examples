package com.example.asyncgeneratepdf.controller;

import com.example.asyncgeneratepdf.entity.City;
import com.example.asyncgeneratepdf.repository.CityRepository;
import com.example.asyncgeneratepdf.service.PdfGeneratingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityRepository cityRepository;

    private final PdfGeneratingService pdfGeneratingService;

    public CityController(CityRepository cityRepository, PdfGeneratingService pdfGeneratingService) {
        this.cityRepository = cityRepository;
        this.pdfGeneratingService = pdfGeneratingService;
    }

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
}
