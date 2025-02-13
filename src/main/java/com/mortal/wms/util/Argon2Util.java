package com.mortal.wms.util;

public class Argon2Util {
    private static final CustomArgon2PasswordEncoder argon2PasswordEncoder = createCustomArgon2PasswordEncoder();

    private static CustomArgon2PasswordEncoder createCustomArgon2PasswordEncoder() {
        int saltLength = 16; // 自定义盐长度
        int hashLength = 32; // 自定义哈希长度
        int parallelism = 1; // 自定义并行性（线程数）
        int memory = 1 << 16; // 自定义内存限制（以字节为单位）
        int iterations = 10; // 自定义加密迭代次数

        return new CustomArgon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    public static String hashPassword(String password) {
        return argon2PasswordEncoder.encode(password);
    }

    public static boolean isValidPassword(String inputPassword, String storedPassword) {
        return argon2PasswordEncoder.matches(inputPassword,storedPassword);
    }

    public static void main(String args[]) {
        // 原密码
        String password = "000000";
        System.out.println("明文(原生)密码：" + password);
        // 加密后的密码
        String argon2Password = hashPassword(password);
        System.out.println("加密后的密码：" + argon2Password);
        System.out.println("加密后的密码和原生密码是否是同一字符串:" + isValidPassword(password, argon2Password));
    }
}