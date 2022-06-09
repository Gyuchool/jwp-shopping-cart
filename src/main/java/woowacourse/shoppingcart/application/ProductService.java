package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.application.exception.InvalidCartItemException;
import woowacourse.shoppingcart.application.exception.InvalidCustomerException;
import woowacourse.shoppingcart.dao.CartItemDao;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.dao.ProductDao;
import woowacourse.shoppingcart.domain.Cart;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.domain.Product;
import woowacourse.shoppingcart.dto.LookUpUser;
import woowacourse.shoppingcart.dto.ProductRequest;
import woowacourse.shoppingcart.dto.ProductResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductService {
    private final ProductDao productDao;
    private final CartItemDao cartItemDao;
    private final CustomerDao customerDao;

    public ProductService(final ProductDao productDao, final CartItemDao cartItemDao, final CustomerDao customerDao) {
        this.productDao = productDao;
        this.cartItemDao = cartItemDao;
        this.customerDao = customerDao;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findProducts(final LookUpUser user) {
        List<Product> products = productDao.findProducts();
        if (user.isNotLogin()) {
            return products.stream()
                    .map(ProductResponse::new)
                    .collect(Collectors.toList());
        }
        Customer customer = customerDao.findById(user.getId())
                .orElseThrow(InvalidCustomerException::new);

        List<Long> productIds = cartItemDao.findProductIdsByCustomerId(customer.getId());
        List<ProductResponse> productResponses = new ArrayList<>();
        for (final Product product : products) {
            addProduct(productIds, productResponses, product, customer);
        }
        return productResponses;
    }

    private void addProduct(final List<Long> productIds, final List<ProductResponse> productResponses,
                            final Product product, final Customer customer) {
        ProductResponse productResponse = new ProductResponse(product);
        addCartItem(productIds, product, productResponse, customer.getId());
        productResponses.add(productResponse);
    }

    private void addCartItem(final List<Long> productIds, final Product product,
                             final ProductResponse productResponse, final Long customerId) {
        if (productIds.contains(product.getId())) {
            Cart cart = cartItemDao.findIdAndQuantityByProductId(product.getId(), customerId)
                    .orElseThrow(InvalidCartItemException::new);
            productResponse.addCartQuantity(cart);
        }
    }

    public Long addProduct(final ProductRequest.AllProperties productRequest) {
        return productDao.save(productRequest.toEntity());
    }

    public ProductResponse findProductById(final Long productId) {
        return new ProductResponse(productDao.findProductById(productId));
    }

    public void deleteProductById(final Long productId) {
        productDao.delete(productId);
    }
}
