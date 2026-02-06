package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.entity.Product;

public interface ProductService {
    PageResponse<Product> listProducts(SearchRequest request);
}
