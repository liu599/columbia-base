package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.api.util.QueryWrapperUtils;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.dao.ProductMapper;
import base.ecs32.top.entity.Product;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public PageResponse<Product> listProducts(SearchRequest request) {
        Page<Product> page = new Page<>(request.getCurrent(), request.getPageSize());
        QueryWrapper<Product> wrapper = QueryWrapperUtils.buildWrapper(request, Arrays.asList("name", "description"));
        
        productMapper.selectPage(page, wrapper);
        
        return PageResponse.of(page.getRecords(), page.getTotal(), (int)page.getCurrent(), (int)page.getSize());
    }
}
