package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/list")
    public PageResponse<Product> listProducts(@RequestBody SearchRequest request) {
        return productService.listProducts(request);
    }
}
