package com.mortal.wms.business.enums;

public enum ResultTypeEnum {

    /* 状态码 */
    SERVICE_SUCCESS(0, "操作成功"),
    SERVICE_FAILED(-1, "操作失败"),
    HTTP_ERROR_100(100, "1XX错误"),
    HTTP_ERROR_300(300, "3XX错误"),
    HTTP_ERROR_400(400, "参数解析错误"),
    USER_NOT_LOGGED_IN(401, "Unauthorized"),
    REQUEST_NOT_FOUND(404, "Not Found！"),
    METHOD_NOT_ALLOWED(405, "不支持当前请求"),
    REPEAT_REQUEST_NOT_ALLOWED(406, "请求重复提交"),
    SERVICE_ERROR(500, "服务器运行异常"),

    /* 系统错误：10001-19999 */
    PARAM_IS_INVALID(10001, "参数解析失败"),
    SYSTEM_INNER_ERROR(10002, "系统繁忙，请稍后重试"),
    ERROR_DO_SQL(10003,"操作异常"),
    PARAM_NOT_COMPLETE(10004,"请求参数不完整"),
    PARAM_CANNOT_EMPTY(10005,"参数不能为空"),

    /* 权限错误：20001-29999 */
    PERMISSION_TOKEN_EXPIRED(20001, "登录已过期，请重新登录"),
    PERMISSION_TOKEN_ERROR(20002,"认证参数有误"),
    PERMISSION_EXPIRE(20003,"登录状态过期！"),
    PERMISSION_LIMIT(20005, "访问次数受限制"),
    PERMISSION_TOKEN_INVALID(20006, "token无效，请重新登录"),
    PERMISSION_SIGNATURE_ERROR(20007, "签名失败"),

    /* 业务错误(通用功能)：30001-39999 */
    LOGIN_ERROR(30001, "账号或密码错误"),
    USER_FORBIDDEN_ERROR(30010, "账号已禁用"),
    NOT_PARAM_USER_OR_ERROR_PWD(30002, "用户名或密码为空"),
    QUERY_USERINFO_ERROR(30003, "用户信息查询失败"),
    UNAUTHO_ERROR(30004, "该用户无登录权限"),
    USER_PASSWORD_LENGTH_TO_LITTLE(30005,"密码长度不足八位"),
    USER_PASSWORD_NO_LETTER_OR_NUMBER(30006,"密码未包含数字和字母"),
    PASSWORD_FORMAT_ERROR(30007,"密码格式不符"),
    WECHART_NOT_BINDING(30008,"未绑定账号请先登录"),
    FAILED_TO_SEND_MESSAGE(30090,"发送消息失败"),
    CHARACTER_EXIST_ERROR(30013,"角色不存在"),
    TOKEN_NULL(30014, "无法获取登录状态，请重新登录"),
    USER_PASSWORD_ERROR(30015, "原密码错误！"),
    USER_PERMISSION_ERROR(30016, "无权限！"),

    FILE_UPLOAD_ERROR(40001,"文件上传失败"),

    FILE_UPLOAD_EMPTY(40002,"上传文件不能为空"),
    FILE_OPERATION_FAILED(40003,"文件操作失败"),
    FILE_NOT_EXIST(40004,"文件不存在"),
    NOT_FILE_UPLOAD_FILE(40006,"无上传文件id或无上传信息"),
    FILE_MISS_CHUNKS(40007, "文件部分模块上传错误"),
    /* 流程编码错误：50001-59999 */
    PROCESS_PARAMS_ERROR(50001, "文件部分模块上传错误"),
    NEXT_USER_MISS_ERROR(50002, "未指定下一审批人"),

    /* e通对接相关异常：60001-69999 */
    GET_ACCESS_TOKEN_ERROR(60001, "统一身份认证平台AccessToken获取失败，请重新登录！"),
    GET_LOGIN_NAME_ERROR(60002, "统一身份认证平台用户信息获取失败，请重新登录！"),
    ACCESS_TOKEN_FAILURE(60003, "统一身份认证平台AccessToken失效，请重新登录！"),
    ;

    private Integer code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ResultTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
