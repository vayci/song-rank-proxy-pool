package me.olook.songrank.proxypool.netease;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

/**
 * 网易云api加解密工具
 *
 *
 * @see {https://www.zhihu.com/question/36081767/answer/140287795}
 * @author zhaohw
 * @date 2018-03-06 18:42
 */
public class NetEaseEncryptUtil {

    /**
     * 获取加密后的参数Json
     * 包含params和encSecKey
     */
    public static String getUrlParams(String json){
        try {
            return "?params=" + URLEncoder.encode(NetEaseEncryptUtil.getParams(json), "UTF-8") + "&encSecKey=" + NetEaseEncryptUtil.getEncSecKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取加密参数params
     */
    private static String getParams(String text) throws Exception {
        String firstKey = "0CoJUm6Qyw8W8jud";
        String secondKey = "FFFFFFFFFFFFFFFF";
        // 第一次加密 对json原文参数加密
        String hEncText = NetEaseEncryptUtil.encrypt(text, firstKey);
        // 第二次加密 对第一次加密结果进行加密
        hEncText = NetEaseEncryptUtil.encrypt(hEncText, secondKey);
        return hEncText;
    }

    /**
     * encSecKey
     *      第一个参数 16位随机字符串（这里固定为FFFFFFFFFFFFFFFF）
     *      第二个参数 e（010001）
     *      第三个参数 f(00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7
     * RSA得出 所以此处为常量
     */
    private static String getEncSecKey() {
        return  "257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffca5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";
    }

    /**
     * 参数加密
     * 算法/模式/补码方式 AES/CBC/PKCS5Padding
     * 偏移向量iv 0102030405060708
     * 加密完成后BASE64转码
     */
    private static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return new BASE64Encoder().encode(encrypted);
    }

    private static String decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("0102030405060708"
                    .getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            //先用base64解密
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return new String(original);
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

}
