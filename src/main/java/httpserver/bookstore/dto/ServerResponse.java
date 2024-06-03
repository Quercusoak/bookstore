package httpserver.bookstore.dto;

public class ServerResponse<T> {

    private final T result;
    private final String errorMessage;

    public ServerResponse(T result, String errorMessage){
        this.result = result;
        this.errorMessage = errorMessage;
    }

    public T getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
