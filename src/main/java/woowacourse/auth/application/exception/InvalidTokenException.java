package woowacourse.auth.application.exception;

public final class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        this("유효하지 않은 토큰입니다.");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
