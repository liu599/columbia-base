package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.ProductActivateRequest;
import base.ecs32.top.api.dto.ProductCourseTreeQueryRequest;
import base.ecs32.top.api.dto.ProductCourseTreeQueryResponse;
import base.ecs32.top.api.dto.ProductCourseTreeRequest;
import base.ecs32.top.api.dto.ProductCourseTreeResponse;
import base.ecs32.top.api.dto.ProductSaveRequest;
import base.ecs32.top.api.service.ActivationCodeService;
import base.ecs32.top.api.service.CourseService;
import base.ecs32.top.api.service.CreditService;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.api.vo.ProductCourseVO;
import base.ecs32.top.dao.CourseMapper;
import base.ecs32.top.entity.Course;
import base.ecs32.top.entity.Product;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ActivationCodeService activationCodeService;
    private final CourseMapper courseMapper;

    @PostMapping("/save")
    @AdminAudit(module = AuditModule.PRODUCT, action = AuditAction.SAVE_PRODUCT)
    public Map<String, Object> saveProduct(@RequestBody ProductSaveRequest request) {
        Product product;
        if (request.getId() != null) {
            product = productService.getById(request.getId());
            if (product != null) {
                AuditContext.setBeforeValue(product);
                AuditContext.setTargetId(product.getId().toString());
            } else {
                product = new Product();
            }
        } else {
            product = new Product();
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBaseCredits(request.getBaseCredits());
        product.setStatus(request.getStatus());

        productService.saveOrUpdate(product);
        AuditContext.setTargetId(product.getId().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("id", product.getId());
        return data;
    }

    @PostMapping("/activate")
    @AdminAudit(module = AuditModule.PRODUCT, action = AuditAction.MANUAL_ACTIVATE)
    public Map<String, Object> manualActivate(@RequestBody ProductActivateRequest request) {
        AuditContext.setTargetId(request.getTargetUserId().toString());
        
        // Manual activation logic: skip code, just give product benefits
        // This is a new feature, implementation depends on ActivationCodeService or a new service method
        activationCodeService.manualActivate(request.getTargetUserId(), request.getProductId(), request.getRemark());

        Product product = productService.getById(request.getProductId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getTargetUserId());
        data.put("product_name", product != null ? product.getName() : "Unknown");
        data.put("added_credits", product != null ? product.getBaseCredits() : 0);
        return data;
    }

    /**
     * 产品课程树形结构API - 创建或更新整个课程结构
     * 支持创建新产品、更新现有产品及其课程-章节-课时树形结构
     */
    @PostMapping("/tree")
    public ProductCourseTreeResponse saveProductCourseTree(
            @Valid @RequestBody ProductCourseTreeRequest request) {
        return productService.saveProductCourseTree(request);
    }

    /**
     * 根据产品ID查询关联的课程树形结构
     * 支持不同层级：TITLES_ONLY, BASIC, FULL
     */
    @PostMapping("/courses/query")
    public ProductCourseTreeQueryResponse getProductCourseTree(
            @RequestBody ProductCourseTreeQueryRequest request) {
        return productService.getProductCourseTree(request);
    }

    /**
     * 根据产品ID查询关联的课程列表（原接口，保留兼容性）
     */
    @GetMapping("/{product_id}/courses")
    @Deprecated
    public Map<String, Object> getCoursesByProductId(@PathVariable("product_id") Long productId) {
        // 验证产品是否存在
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND, "产品不存在");
        }

        // 查询关联的课程
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getProductId, productId)
        );

        List<ProductCourseVO> courseList = courses.stream()
                .map(course -> {
                    ProductCourseVO vo = new ProductCourseVO();
                    vo.setId(course.getId());
                    vo.setTitle(course.getTitle());
                    vo.setDescription(course.getDescription());
                    vo.setStatus(course.getStatus());
                    return vo;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("product_id", productId);
        data.put("product_name", product.getName());
        data.put("courses", courseList);
        return data;
    }
}
