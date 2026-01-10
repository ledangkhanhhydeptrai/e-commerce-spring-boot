package com.example.demo.service.Implement;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductResponsePublic;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductResponseMapper;
import com.example.demo.mapper.ProductStockResponse;
import com.example.demo.repository.ProductRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductResponseMapper productMapper;
    private final ProductStockResponse productStockResponse;
    private final CloudinaryServiceImpl cloudinaryService;

    public ProductServiceImpl(ProductRepository productRepository, CloudinaryServiceImpl cloudinaryService, ProductResponseMapper productMapper, ProductStockResponse productStockResponse) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productStockResponse = productStockResponse;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ApiResponse<List<ProductResponsePublic>> getAllProducts() {
        List<ProductResponsePublic> products = productRepository.findAll()
                .stream()
                .map(productStockResponse::toPublicResponse)
                .toList();
        return ApiResponse.<List<ProductResponsePublic>>builder()
                .status(200)
                .message("Get All Product Successfully")
                .data(products)
                .build();
    }

    @Override
    public ApiResponse<List<ProductResponse>> getAllProductForAdmin() {
        List<ProductResponse> products = productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
        return ApiResponse.<List<ProductResponse>>builder()
                .status(200)
                .message("Get Product For Admin Successfully")
                .data(products)
                .build();
    }

    @Override
    public ApiResponse<ProductResponse> createProduct(CreateProductRequest request, MultipartFile file) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();
        if (file != null && !file.isEmpty()) {
            try {
                String image = cloudinaryService.uploadFile(file);
                product.setFileUrl(image);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Upload file thất bại");
            }
        }
        Product savedProduct = productRepository.save(product);
        ProductResponse responses = ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .quantity(savedProduct.getQuantity())
                .image(savedProduct.getFileUrl())
                .build();
        return ApiResponse.<ProductResponse>builder()
                .status(200)
                .message("Tạo sản phẩm thành công")
                .data(responses)
                .build();
    }

    @Override
    public ApiResponse<ProductResponsePublic> getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có id của sản phẩm này"));
        ProductResponsePublic productResponsePublic = productStockResponse.toPublicResponse(product);
        return ApiResponse.<ProductResponsePublic>builder()
                .status(200)
                .message("Get the product successfully")
                .data(productResponsePublic)
                .build();
    }

    @Override
    public ApiResponse<ProductResponse> getProductAdminById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có id của sản phẩm này"));
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .image(product.getFileUrl())
                .build();
        return ApiResponse.<ProductResponse>builder()
                .status(200)
                .message("Get the product successfully")
                .data(productResponse)
                .build();
    }

    @Override
    public ApiResponse<ProductResponse> updateProductById(UUID id, CreateProductRequest request, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có id của sản phẩm này"));
        if (file != null && !file.isEmpty()) {
            try {
                String image = cloudinaryService.uploadFile(file);
                product.setFileUrl(image);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Upload file thất bại");
            }
        }
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        Product savedProduct = productRepository.save(product);
        ProductResponse productPublic = productMapper.toResponse(savedProduct);
        return ApiResponse.<ProductResponse>builder()
                .status(200)
                .message("Cập nhật sản phẩm thành công")
                .data(productPublic)
                .build();
    }

    @Override
    public ApiResponse<String> deleteProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có id của sản phẩm này"));
        productRepository.delete(product);
        return ApiResponse.<String>builder()
                .status(200)
                .message("Xóa sản phẩm thành công")
                .data(null)
                .build();
    }
}
