package com.example.dao.mapper;

import com.example.dao.model.Product;
import com.example.dao.model.generated.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductDto productToProductDto(Product product);

    List<ProductDto> productsToProductDtos(List<Product> products);
}
