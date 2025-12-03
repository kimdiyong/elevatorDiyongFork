package com.elevatorVendingMachineSystem.service;

import com.elevatorVendingMachineSystem.domain.Product;
import com.elevatorVendingMachineSystem.dto.ProductDto;
import com.elevatorVendingMachineSystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 목록 조회 (SID-001, SID-019)
     * 사용자와 관리자 모두 사용합니다.
     * UT-101 시험 케이스 대응: DB 상태에 따라 콘솔에 텍스트 출력
     */
    public List<ProductDto.Response> getAllProducts() {
        try {
            // 1. DB 조회 시도
            List<Product> products = productRepository.findAll();

            // 2. 데이터 유무 확인 및 텍스트 출력
            if (!products.isEmpty()) {
                System.out.println("======================");
                System.out.println("Result: DB에 데이터 존재");  // UT-101-01
                System.out.println("======================");
            } else {
                System.out.println("======================");
                System.out.println("Result: DB에 데이터 없음");  // UT-101-02
                System.out.println("======================");
            }

            return products.stream()
                    .map(ProductDto.Response::new)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // 3. DB 연결 실패 등 예외 발생 시 텍스트 출력
            System.out.println("======================");
            System.out.println("Result: DB 연결 실패");      // UT-101-03
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("======================");

            // 컨트롤러가 에러를 인지할 수 있도록 예외를 다시 던짐
            throw e;
        }
    }

    /**
     * 상품 상세 조회 (SID-003, SID-020)
     */
    public ProductDto.Response getProduct(Long id) {
        try {
            // 1. DB 조회 시도
            Optional<Product> productOpt = productRepository.findById(id);

            // 2. 데이터 유무 확인 및 텍스트 출력
            if (productOpt.isPresent()) {
                System.out.println("======================");
                System.out.println("Result: DB에 데이터 존재"); // UT-101-01 (상세)
                System.out.println("======================");
                return new ProductDto.Response(productOpt.get());
            } else {
                System.out.println("======================");
                System.out.println("Result: DB에 데이터 없음"); // UT-101-02 (상세)
                System.out.println("======================");
                throw new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id);
            }

        } catch (IllegalArgumentException e) {
            // 데이터 없음(404) 상황은 위에서 로그를 찍었으므로 그대로 던짐
            throw e;
        } catch (Exception e) {
            // 3. 그 외 DB 연결 실패 등 예외 발생 시
            printDbConnectionError(e);
            throw e;
        }
    }

    /**
     * 신규 상품 등록 (SID-022)
     * 관리자 전용 기능입니다.
     */
    @Transactional
    public Long saveProduct(ProductDto.Request request) {
        // 위치 번호 중복 체크 로직 추가 가능
        return productRepository.save(request.toEntity()).getId();
    }

    /**
     * 상품 정보 수정 (SID-021)
     * 재고 보충 기능도 포함될 수 있습니다. (COM-06)
     */
    @Transactional
    public Long updateProduct(Long id, ProductDto.Request requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));

        // 정보 수정
        product.updateInfo(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getVolume(),
                requestDto.getCalories(),
                requestDto.getExpirationDate(),
                requestDto.getAllergyInfo()
        );

        // 재고 수정 로직 (재고가 변경된 경우)
        // 현재 재고보다 요청 재고가 많으면 보충(addStock), 적으면 감소 등으로 처리하거나
        // 간단하게 stock 자체를 덮어씌우는 방식도 가능합니다. 여기선 간단히 덮어씌우는 걸로 가정하지 않고
        // 별도 재고 관리 메서드를 두거나 엔티티에 setter를 두어 처리해야 하나,
        // 엔티티의 무결성을 위해 updateInfo에 stock을 포함하지 않았으므로 별도 처리합니다.
        // *편의상 여기서는 재고 수량을 직접 변경하는 메서드를 호출한다고 가정합니다.*
        // product.setStock(requestDto.getStock()); // 엔티티에 setStock이나 updateStock 메서드 필요
        product.updateStock(requestDto.getStock());

        return id;
    }

    /**
     * 상품 삭제 (SID-023)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));
        productRepository.delete(product);
    }

    // 공통 에러 출력 메서드
    private void printDbConnectionError(Exception e) {
        System.out.println("======================");
        System.out.println("Result: DB 연결 실패");      // UT-101-03
        System.out.println("Error Message: " + e.getMessage());
        System.out.println("======================");
    }
}