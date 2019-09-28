package com.kiyoung.msa.UserMicroService.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.kiyoung.msa.UserMicroService.daos.UserDao;
import com.kiyoung.msa.UserMicroService.entities.User;
import com.kiyoung.msa.UserMicroService.services.LoginService;
import com.kiyoung.msa.UserMicroService.utilities.*;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.SignatureException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class RestControllers {
    @Autowired
    LoginService loginService;

    @Autowired
    UserDao userDao;

    @Autowired
    EncryptionUtils encryptionUtils;


    private static final Logger log = LoggerFactory.getLogger(RestControllers.class);

    @RequestMapping("/alivecheck")
//  @ResponseBody
    public String hello() {
        return "usermicroserive hello";   //"hello"-> hello.jsp  HTML page(ViewResolver)
        // RestController를 사용하므로 이제 jsp 파일로 생성되는 걱적 ㄴㄴ
    }

    /**
     * @return
     */
    @RequestMapping("/userInOutput") //jackson-Library -> Object.java into Json Message
    public User giveMeAUser() {
        return new User("mike@quicktutorialz.com", "Mike Anthony", "password");
    }

    /**
     * @return
     */
    @RequestMapping("/exampleUrl")
    public ResponseEntity<JsonResponseBody> returnMyStandardResponse(HttpServletRequest request) {
        try {
            Map<String, Object> temp = loginService.verifyJwtAndGetData(request);
            return ResponseEntity.status(HttpStatus.OK).header("jwt").body(new JsonResponseBody(HttpStatus.OK.value(),temp.toString()));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        }catch(SignatureException e4){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(),"signature fail"+e4.toString()));
        }catch(JsonParseException e5){
            log.info("[JsonParseException] exception:"+e5.toString());
            return ResponseEntity.status(HttpStatus.valueOf(499)).body(new JsonResponseBody(HttpStatus.valueOf(499).value(),"JsonParseException"+e5.toString()));
        }
    }

    /**
     * @param email
     * @param password
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> login(@RequestParam("email") String email, @RequestParam("password") String password) {

        log.info(">>>> login id : " + email + " , password : " + password);
//        String encryptedPwd;
//        password = encryptionUtils.encrypt(password);

        // 1) success : return a String with the login message + JWT in the Header of the HTTP Response
        // 2) return the error message
        try {
            Optional<User> userr = loginService.getUserFromDb(email, password);
            log.info(">>>>> userr : "+userr.toString());
            User user = userr.get();
            String jwt = loginService.createJwt(email, user.getName(), new Date());
            log.info(">>>> login success and jwt Token : " + jwt);
            // the only case in which the server sends the JWT to the client in the HEADER of the RESPONSE
            JsonJWTResponse js =  new JsonJWTResponse(HttpStatus.OK.value(),"Success!",jwt);
            log.info(">>>> js : "+js.toString());
            ResponseEntity rs = ResponseEntity.status(HttpStatus.OK).header("jwt",jwt).body(js);//// 여기 로그에는 header가 찍히기는 하는데,,, 이상하게 프런트에선 받지를 못한다...이유를  아직은 모르겠다.
            log.info(">>>> ResponseEntity I made: "+rs);
            return  rs;

//            HttpHeaders headersImade = new HttpHeaders();
//            headersImade.add("jwt",jwt);
//            ResponseEntity rs = ResponseEntity.status(HttpStatus.OK).headers(headersImade).body(new JsonResponseBody(HttpStatus.OK.value(), "Success! Logged in & jwt Token : " + jwt));
//            log.info(">>>> rs.getHeaders() :"+rs.getHeaders());
//            log.info(">>>> response.toString() :"+rs.toString());
//            return  rs;
//           return ResponseEntity.status(HttpStatus.OK).headers(headersImade).body(new JsonResponseBody(HttpStatus.OK.value(), "Success! Logged in & jwt Token : " + jwt));
//            return ResponseEntity.status(HttpStatus.OK).header("jwt", jwt).body(new JsonResponseBody(HttpStatus.OK.value(), "Success! Logged in & jwt Token : " + jwt));

        } catch (UserNotInDatabaseException e1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e2.toString()));
        }
    }
    @CrossOrigin
    @RequestMapping(value="/verifyJwtAndGetData",method=RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> verifyJwtAndGetData(HttpServletRequest request){
        try {
            Map<String, Object> temp = loginService.verifyJwtAndGetData(request);
            log.info("verifyJwtAndGetData return result:"+temp.toString());
            return ResponseEntity.status(HttpStatus.OK).header("jwt").body(new JsonResponseBody(HttpStatus.OK.value(),temp.toString()));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        } catch(SignatureException e4){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(),"signature fail"+e4.toString()));
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(),"fuck you "+e.toString()));
        }
    }

    @RequestMapping(value="/verifyJwtAndGetData3",method=RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> verifyJwtAndGetData3(HttpServletRequest request){
        try {
            log.info("[verifyJwtAndGetData3] :"+request.toString());
            Map<String, Object> temp = loginService.verifyJwtAndGetData(request);

            JSONObject json = new JSONObject();
            json.putAll(temp);
            log.info("[verifyJwtAndGetData3] json : "+json.toString());

            return ResponseEntity.status(HttpStatus.OK).header("email",temp.get("email").toString()).body(new JsonResponseBody(HttpStatus.OK.value(),json.toString()));
//            log.info();

//            return ResponseEntity.status(HttpStatus.OK).header("jwt").body(new JsonResponseBody(HttpStatus.OK.value(),json.toString()));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        } catch(SignatureException e4){
            log.info("[SignatureException] exception:"+e4.toString());
            log.info("HttpStatus Code:"+HttpStatus.FORBIDDEN.value());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(),"signature fail: "+e4.toString()));
        } catch(JsonParseException e5){
            log.info("[JsonParseException] exception:"+e5.toString());
            return ResponseEntity.status(HttpStatus.valueOf(499)).body(new JsonResponseBody(HttpStatus.valueOf(499).value(),"JsonParseException"+e5.toString()));
        }

    }


    /**
     * 설명: 사용자 가입 원시 버전
     * TODO: 중복 아이디 체크
     * @param email
     * @param name
     * @param password
     * @return
     * @since 2018.7.28
     *
     */
    @RequestMapping(value="/userSave",method=RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> userSave(@RequestParam("email") String email,@RequestParam("name") String name, @RequestParam("password") String password){
        try {
            String encryptedPwd;
            encryptedPwd = encryptionUtils.encrypt(password);
            User user = new User(email, name, encryptedPwd);
            log.info(">>>> userSave object : " + user.toString());
            Object x = userDao.save(user);
            log.info(">>>> x : " + x.toString());
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), user.toString()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(),"error:"+e.toString()));
        }
    }




}
