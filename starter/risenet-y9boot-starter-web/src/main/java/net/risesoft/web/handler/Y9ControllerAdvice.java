package net.risesoft.web.handler;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9PermissionException;

/**
 * 全局的异常处理器
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@ControllerAdvice(basePackages = {"net.risesoft"})
@Slf4j
public class Y9ControllerAdvice {

    /**
     * 参数校验失败 例如 {@link javax.validation.constraints.NotBlank}
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Y9Result<Object> processException(ConstraintViolationException e) {
        LOGGER.warn(e.getMessage(), e);
        return Y9Result.failure(GlobalErrorCodeEnum.INVALID_ARGUMENT, StringUtils.isNotBlank(e.getMessage())
            ? e.getMessage() : GlobalErrorCodeEnum.INVALID_ARGUMENT.getDescription());
    }

    /**
     * 缺少参数
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Y9Result<Object> processException(MissingServletRequestParameterException e) {
        LOGGER.warn(e.getMessage(), e);
        return Y9Result.failure(GlobalErrorCodeEnum.INVALID_ARGUMENT, StringUtils.isNotBlank(e.getMessage())
            ? e.getMessage() : GlobalErrorCodeEnum.INVALID_ARGUMENT.getDescription());
    }

    /**
     * 参数类型不匹配
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Y9Result<Object> processException(MethodArgumentTypeMismatchException e) {
        LOGGER.warn(e.getMessage(), e);
        String message = String.format("参数[%s]类型应为[%s]，当前参数值为[%s]", e.getParameter().getParameterName(),
            e.getRequiredType().getSimpleName(), e.getValue());
        return Y9Result.failure(GlobalErrorCodeEnum.INVALID_ARGUMENT, message);
    }

    /**
     * 缺少路径参数
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({MissingPathVariableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Y9Result<Object> processException(MissingPathVariableException e) {
        LOGGER.warn(e.getMessage(), e);
        return Y9Result.failure(GlobalErrorCodeEnum.INVALID_ARGUMENT, StringUtils.isNotBlank(e.getMessage())
            ? e.getMessage() : GlobalErrorCodeEnum.INVALID_ARGUMENT.getDescription());
    }

    /**
     * 参数绑定异常
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Y9Result<Object> processException(BindException e) {
        LOGGER.warn(e.getMessage(), e);
        // 拿到@NotNull,@NotBlank和 @NotEmpty等注解上的message值
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null && StringUtils.isNotBlank(fieldError.getDefaultMessage())) {
            return Y9Result.failure(GlobalErrorCodeEnum.INVALID_ARGUMENT,
                fieldError.getField() + fieldError.getDefaultMessage());
        }

        // 参数类型不匹配检验
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = e.getFieldErrors();
        fieldErrors.forEach((oe) -> msg.append(oe.getObjectName()).append(".").append(oe.getField()).append("=")
            .append(oe.getRejectedValue()).append("，与预期的字段类型不匹配"));
        return Y9Result.failure(GlobalErrorCodeEnum.INVALID_ARGUMENT, msg.toString());
    }

    /**
     * 业务异常 <br>
     * 统一返回 http 的状态码为 400
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({Y9BusinessException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Y9Result<Object> processException(Y9BusinessException e) {
        LOGGER.warn(e.getMessage(), e);
        return Y9Result.failure(e.getCode(),
            StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : GlobalErrorCodeEnum.FAILURE.getDescription());
    }

    /**
     * y9 权限异常
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({Y9PermissionException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Y9Result<Object> processException(Y9PermissionException e) {
        LOGGER.warn(e.getMessage(), e);
        return Y9Result.failure(e.getCode(), StringUtils.isNotBlank(e.getMessage()) ? e.getMessage()
            : GlobalErrorCodeEnum.PERMISSION_DENIED.getDescription());
    }

    /**
     * 通用的错误异常 <br>
     * 对异常处理得当的应用理论上不会走这里 而是走上方更具体的异常 如果走到了这里应用应处理掉能处理的异常
     *
     * @param e e
     * @return {@link Y9Result}<{@link Object}>
     */
    @ExceptionHandler({Throwable.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Y9Result<Object> processException(Throwable e) {
        LOGGER.warn(e.getMessage(), e);
        return Y9Result.failure(GlobalErrorCodeEnum.FAILURE.getDescription());
    }

}
