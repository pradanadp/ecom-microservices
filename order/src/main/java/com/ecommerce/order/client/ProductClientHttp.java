package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductDTO;
import com.ecommerce.order.exception.ProductServiceUnavailableException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProductClientHttp implements ProductClient {

    private final RestClient restClient;

    public ProductClientHttp(@Value("${product.service.url}") String productServiceUrl) {
        this.restClient = RestClient.create(productServiceUrl);
    }

    @Override
    public ProductDTO fetchProduct(Long productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        } catch (RestClientException ex) {
            throw new ProductServiceUnavailableException(
                    "Product service is unavailable. Unable to fetch product: " + productId);
        }
    }
}
