package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.ProductCourseTreeQueryRequest;
import base.ecs32.top.api.dto.ProductCourseTreeQueryResponse;
import base.ecs32.top.api.dto.ProductCourseTreeRequest;
import base.ecs32.top.api.dto.ProductCourseTreeResponse;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProductService extends IService<Product> {
    PageResponse<Product> listProducts(SearchRequest request);
    ProductCourseTreeResponse saveProductCourseTree(ProductCourseTreeRequest request);
    ProductCourseTreeQueryResponse getProductCourseTree(ProductCourseTreeQueryRequest request);
}
