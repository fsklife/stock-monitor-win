package org.fsk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author fanshk
 * @date 2020/3/2 18:02
 */
@Slf4j
public class Base64Util {
    /**
     * Base64编码
     */
    public static String encryptBase64(byte[] key) throws UnsupportedEncodingException {
        return new String(Base64.getEncoder().encode(key), "utf-8");
    }

    /**
     * Base64解码
     *
     * @throws IOException
     */
    public static byte[] decryptBase64(String key) {
        return Base64.getDecoder().decode(key);
    }

    /**
     * base64解码
     *
     * @param pswd 密码
     * @return 解码密码
     */
    public static String decodeBase64(String pswd) {
        if (StringUtils.isEmpty(pswd)) {
            return pswd;
        }
        String ps = pswd;
        try {
            ps = new String(decryptBase64(pswd), "utf-8");
        } catch (Exception e) {
            log.error("数据库密码解码base64失败：", e);
        }
        return ps;
    }

    /**
     * base64编码
     *
     * @param pswd 密码
     * @return 编码密码
     */
    public static String encodeBase64(String pswd) {
        if (StringUtils.isEmpty(pswd)) {
            return pswd;
        }
        String ps = pswd;
        try {
            ps = encryptBase64(pswd.getBytes());
        } catch (UnsupportedEncodingException e) {
            log.error("数据库密码编码base64失败！");
        }
        return ps;
    }

    /**
     * ASCII64解码(修改使用反转再base64解码)
     *
     * @param pswd 密码
     * @return 解码密码
     */
    public static String decodeASCII(String pswd) {
        if (StringUtils.isEmpty(pswd)) {
            return pswd;
        }
        String reversePwd = new StringBuffer(pswd).reverse().toString();
        String pwd = decodeBase64(reversePwd);
        return pwd;
    }

    /**
     * ASCII编码(修改使用base64后再反转)
     *
     * @param pswd 密码
     * @return 编码密码
     */
    public static String encodeASCII(String pswd) {
        if (StringUtils.isEmpty(pswd)) {
            return pswd;
        }
        String base64Pwd = encodeBase64(pswd);

        return new StringBuffer(base64Pwd).reverse().toString();
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println(encodeASCII("fsk851222"));
    }
}
