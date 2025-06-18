package com.bharath.springcloud.controllers;

import com.bharath.springcloud.dto.Coupon;
import com.bharath.springcloud.model.Product;
import com.bharath.springcloud.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/productapi")
public class ProductRestController {

    @Autowired
    ProductRepo repo;

    @Autowired
    private RestTemplate restTemplate;
    // RestTemplate è usato per effettuare chiamate HTTP verso servizi esterni
    // (ad esempio, per recuperare i dati di un coupon).
    // Assicurati che un bean RestTemplate sia definito nella configurazione Spring.

    @Value("${couponService.url}")
    private String couponServiceUrl;

    @PostMapping("/products")
    public Product create(@RequestBody Product product) {
       Coupon coupon = restTemplate.getForObject(couponServiceUrl + "/" + product.getCouponCode(), Coupon.class);
       product.setPrice(product.getPrice().subtract(coupon.getDiscount()));
        return repo.save(product);
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return repo.findById(id).orElse(null);
    }
}
