package woowacourse.shoppingcart.application.exception;

public final class InvalidProductException extends ValidatedException {
    public InvalidProductException() {
        this("올바르지 않은 사용자 이름이거나 상품 아이디 입니다.");
    }

    public InvalidProductException(final String msg) {
        super(msg);
    }
}
