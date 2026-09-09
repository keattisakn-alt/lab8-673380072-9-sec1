package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductRepository;
import com.example.demo.strategy.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {

        List<Product> products = productRepository.findAll();

        for (Product product : products) {

            product.setDiscountedPrice(
                    calculateDiscount(product)
            );
        }

        return products;
    }

    public Product getProductById(Long id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Product not found: " + id
                        )
                );

        product.setDiscountedPrice(
                calculateDiscount(product)
        );

        return product;
    }

    public Product saveProduct(Product product) {

        // เชื่อม Product <-> ProductDetail
        if (product.getDetail() != null) {
            product.getDetail().setProduct(product);
        }

        // เชื่อม Product <-> Review
        List<Review> validReviews = new ArrayList<>();

        if (product.getReviews() != null) {

            for (Review review : product.getReviews()) {

                if (review == null) {
                    continue;
                }

                boolean hasReviewer =
                        review.getReviewer() != null &&
                        !review.getReviewer().isBlank();

                boolean hasComment =
                        review.getComment() != null &&
                        !review.getComment().isBlank();

                if (hasReviewer || hasComment) {

                    review.setProduct(product);

                    if (review.getReviewDate() == null) {
                        review.setReviewDate(LocalDate.now());
                    }

                    validReviews.add(review);
                }
            }
        }

        product.setReviews(validReviews);

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product newProduct) {

        Product product = getProductById(id);

        product.setName(newProduct.getName());
        product.setCategory(newProduct.getCategory());
        product.setBrand(newProduct.getBrand());
        product.setStock(newProduct.getStock());
        product.setPrice(newProduct.getPrice());
        product.setDiscountType(newProduct.getDiscountType());

        if (newProduct.getDetail() != null) {

            ProductDetail detail = product.getDetail();

            if (detail == null) {
                detail = new ProductDetail();
            }

            detail.setDescription(
                    newProduct.getDetail().getDescription()
            );

            detail.setWarranty(
                    newProduct.getDetail().getWarranty()
            );

            detail.setWeight(
                    newProduct.getDetail().getWeight()
            );

            detail.setDimensions(
                    newProduct.getDetail().getDimensions()
            );

            detail.setManufacturedCountry(
                    newProduct.getDetail().getManufacturedCountry()
            );

            detail.setProduct(product);

            product.setDetail(detail);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private double calculateDiscount(Product product) {

        if (product.getPrice() == null) {
            return 0;
        }

        DiscountStrategy strategy;

        String type = product.getDiscountType();

        if ("MEMBER".equals(type)) {

            strategy = new MemberDiscountStrategy();

        } else if ("SEASONAL".equals(type)) {

            strategy = new SeasonalSaleStrategy();

        } else {

            strategy = new NoDiscountStrategy();
        }

        DiscountContext context =
                new DiscountContext(strategy);

        return context.calculate(product.getPrice());
    }
}