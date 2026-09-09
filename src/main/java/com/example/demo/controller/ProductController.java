package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "products/list";
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {

        Product product = new Product();

        product.setDiscountType("NONE");

        product.setDetail(new ProductDetail());

        product.getReviews().add(new Review());

        model.addAttribute("product", product);

        return "products/add";
    }

    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute Product product,
            RedirectAttributes redirectAttributes) {

        productService.saveProduct(product);

        redirectAttributes.addFlashAttribute(
                "message",
                "เพิ่มสินค้าเรียบร้อยแล้ว"
        );

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "product",
                productService.getProductById(id)
        );

        return "products/edit";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            RedirectAttributes redirectAttributes) {

        productService.updateProduct(id, product);

        redirectAttributes.addFlashAttribute(
                "message",
                "แก้ไขสินค้าเรียบร้อยแล้ว"
        );

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "product",
                productService.getProductById(id)
        );

        return "products/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        productService.deleteProduct(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "ลบสินค้าเรียบร้อยแล้ว"
        );

        return "redirect:/products";
    }
}