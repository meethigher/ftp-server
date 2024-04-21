package top.meethigher.ftp.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JWTUtils {
    private static String SECRET = "meethigher";

    /**
     * 传入Payload生成jwt
     *
     * @param map
     * @return
     */
    public static String getToken(Map<String, String> map) {
        Calendar calendar = Calendar.getInstance();
        //7天过期
        calendar.add(Calendar.HOUR, 1);

        //第一种存payload方式：遍历map存入
//        JWTCreator.Builder builder = JWT.create();
//        map.forEach(builder::withClaim);
        //第二种存payload方式：直接放map,底层采用的也是第一种方式
        return JWT.create()
                .withPayload(map)
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(SECRET));

    }

    /**
     * 校验签名是否正确，并返回token信息
     *
     * @param token
     * @return
     */
    public static DecodedJWT getTokenInfo(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token);
    }
}