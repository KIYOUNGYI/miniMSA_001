package com.kiyoung.msa.gateway.APIGateway.utilities;//package com.kiyoung.msa.UserMicroService.utilities;
//
//import java.io.IOException;
//import java.util.Map;
//import net.minidev.json.JSONObject;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class JsonUtils {
//
//    /**
//     * Map을 json으로 변환한다.
//     *
//     * @param map Map<String, Object>.
//     * @return JSONObject.
//     */
//    public JSONObject getJsonStringFromMap( Map<String, Object> map )
//    {
//        JSONObject jsonObject = new JSONObject();
//        for( Map.Entry<String, Object> entry : map.entrySet() ) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            jsonObject.put(key, value);
//        }
//
//        return jsonObject;
//    }
//
//    /**
//     * JsonObject를 Map<String, String>으로 변환한다.
//     *
//     * @param jsonObj JSONObject.
//     * @return Map<String, Object>.
//     */
//    @SuppressWarnings("unchecked")
//    public static Map<String, Object> getMapFromJsonObject( JSONObject jsonObj )
//    {
//        Map<String, Object> map = null;
//
//        try {
//
//            map = new ObjectMapper().readValue(jsonObj.toJSONString(), Map.class) ;
//
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return map;
//    }
//}
