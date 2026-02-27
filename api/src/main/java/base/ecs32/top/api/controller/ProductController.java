package base.ecs32.top.api.controller;

import base.ecs32.top.api.advice.ResultVo;
import base.ecs32.top.api.dto.ProductListRequest;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.api.vo.PublicProductVO;
import base.ecs32.top.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

 private final ProductService productService;

 @PostMapping("/list")
 public PageResponse<Product> listProducts(@RequestBody SearchRequest request) {
 return productService.listProducts(request);
 }

 /**
  * 公开的产品列表接口
  * 返回 status=1 的产品，包含课程和章节信息，支持通过 id 数组进行过滤
  */
 @PostMapping("/public/list")
 public ResultVo<List<PublicProductVO>> publicListProducts(@RequestBody(required = false) ProductListRequest request) {
  List<PublicProductVO> products = productService.publicListProducts(request);
  return ResultVo.success(products);
 }
}
