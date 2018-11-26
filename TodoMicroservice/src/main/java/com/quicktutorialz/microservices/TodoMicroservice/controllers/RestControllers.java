package com.quicktutorialz.microservices.TodoMicroservice.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicktutorialz.microservices.TodoMicroservice.entities.ToDo;
import com.quicktutorialz.microservices.TodoMicroservice.entities.User;
import com.quicktutorialz.microservices.TodoMicroservice.proxy.UserProxy;
import com.quicktutorialz.microservices.TodoMicroservice.services.LoginService;
import com.quicktutorialz.microservices.TodoMicroservice.services.ToDoService;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.*;
import io.jsonwebtoken.ExpiredJwtException;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.bind.annotation.ResponseBody;

import org.json.simple.JSONObject;

@RestController
public class RestControllers {
    private static final Logger log = LoggerFactory.getLogger(RestControllers.class);

    @Autowired
    LoginService loginService;

    @Autowired
    ToDoService toDoService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserProxy userProxy;

    /**
     * IF YOU DONT WANNA USE @ResponseBody, use @RestController instead of @Controller
     *
     * @return
     */
    @RequestMapping("/alivecheck")
//  @ResponseBody
    public String hello() {
        return "hello todo service!";   //"hello"-> hello.jsp  HTML page(ViewResolver)
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
     * @param toDo
     * @return
     */
    @RequestMapping("/toDoInput1")
    public String toDoInput1(ToDo toDo) {
        return "ToDo description: " + toDo.getDescription() + ", ToDo priority: " + toDo.getPriority();
    }

    /**
     * @param toDo
     * @return
     */
    @RequestMapping("/toDoInput2")
    public String toDoInput2(@Valid ToDo toDo) {
        return "ToDo description: " + toDo.getDescription() + ", ToDo priority: " + toDo.getPriority();
    }

    // BindingResult -> Spring object which collect the validation errors
    @RequestMapping("/toDoInput3")
    public String toDoInput3(@Valid ToDo toDo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "I return the error in the format I like: " + bindingResult.toString();
        }
        return "ToDo description: " + toDo.getDescription() + ", ToDo priority: " + toDo.getPriority();
    }

    // BindingResult -> Spring object which collect the validation errors
    @RequestMapping("/toDoInput4")
    public String toDoInput4(ToDo toDo, BindingResult bindingResult) {
        ToDoValidator toDoValidator = new ToDoValidator();
        toDoValidator.validate(toDo, bindingResult);

        if (bindingResult.hasErrors()) {
            return "I return the error in the format I like: " + bindingResult.toString();
        }
        return "ToDo description: " + toDo.getDescription() + ", ToDo priority: " + toDo.getPriority();
    }

    // BindingResult -> Spring object which collect the validation errors
    @RequestMapping("/toDoInput5")
    public String toDoInput5(@Valid ToDo toDo, BindingResult bindingResult) {
        ToDoValidator toDoValidator = new ToDoValidator();
        toDoValidator.validate(toDo, bindingResult);

        if (bindingResult.hasErrors()) {
            return "I return the error in the format I like: " + bindingResult.toString();
        }
        return "ToDo description: " + toDo.getDescription() + ", ToDo priority: " + toDo.getPriority();
    }

    // HttpResponse -> mapped in Java by ResponseEntity

    // This object, representing the Http Response has two parts:

    // 1.header(the header of the HttpResponse in which the microservices sends cookies or JWT)

    // 2.body (the object of the response which will contain HTML page code or Json Message if we're using REST approach)

    // into the body we want the JSON message of the response: JACKSON Library maps ANY Java Object into a JSON message
    // So our BODY HTTP RESPONSE WILL BE A JAVA OBJECT CALLED JsonResponseBody and jackson Library will translate it into a Json Message.

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

        }
    }

    /**
     * @param email
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> login(@RequestParam("email") String email, @RequestParam("password") String password) {

        log.info(">>>> login id : " + email + " , password : " + password);
        // 1) success : return a String with the login message + JWT in the Header of the HTTP Response
        // 2) return the error message
        try {
            Optional<User> userr = loginService.getUserFromDb(email, password);
            User user = userr.get();
            String jwt = loginService.createJwt(email, user.getName(), new Date());
            log.info(">>>> login success and jwt Token : " + jwt);
            // the only case in which the server sends the JWT to the client in the HEADER of the RESPONSE
            return ResponseEntity.status(HttpStatus.OK).header("jwt", jwt).body(new JsonResponseBody(HttpStatus.OK.value(), "Success! Logged in & jwt Token : " + jwt));
        } catch (UserNotInDatabaseException e1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e2.toString()));
        }
    }


    /**
     * @param request
     * @return
     */
    @CrossOrigin
    @RequestMapping("/showToDos")
    public ResponseEntity<JsonResponseBody> showToDos(HttpServletRequest request) {
        // 1) arrayList of ToDos in the "response" attributes of the JsonResponse Body
        // 2) fail:error message
        try {
            // UserMicroservice에서 가져올 준비
            log.info(">>>> showToDos request : "+request.getHeader("jwt"));
            Map<String, Object> userData = loginService.verifyJwtAndGetData(request);
            log.info(">>>>userData  : " + userData.toString());
            List<ToDo> temp = toDoService.getToDos((String) userData.get("email"));
            log.info(">>>>temp : " + temp.toString());
            log.info(">>>>showToDos : " + toDoService.getToDos((String) userData.get("email")));
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), toDoService.getToDos((String) userData.get("email"))));
        } catch (UserNotLoggedException e1) {
            log.info("is it happened?");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));

        }

    }


    /**
     * @param request
     * @return
     */
    @CrossOrigin
    @RequestMapping("/showToDos2")
    public ResponseEntity<JsonResponseBody> showToDos2(HttpServletRequest request) throws ParseException{
        // 1) arrayList of ToDos in the "response" attributes of the JsonResponse Body
        // 2) fail:error message
        try {

            String email = jwtUtils.getUserEmailFromHttpRequest(request);
            log.info(">>>>[showToDos3] email : "+email);
            List<ToDo> toDoList = toDoService.getToDos(email);
            log.info(">>>>toDoList : " + toDoList.toString());

            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(),toDoList ));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));

        }

    }

    /**
     * TODO : userMicroservice로 통신할 때 urls 부분 수정 필요. / 메소드 추출
     * @param request
     * @return
     */
    @CrossOrigin
    @RequestMapping("/showToDos3")
    public ResponseEntity<JsonResponseBody> showToDos3(HttpServletRequest request) {
        // 1) arrayList of ToDos in the "response" attributes of the JsonResponse Body
        // 2) fail:error message
        String email = null;
        try {
            // UserMicroservice에서 가져올 준비
            email = userProxy.talkToUserMicroServiceToVerifyUserAndGetEmail(request);
            if(email==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(),"I talked to User-Microservice...but,it didn't work out."));
            }
            log.info(">>>> got email from user-Microservice Result : "+email);
            List<ToDo> temp = toDoService.getToDos(email);
            log.info(">>>>temp : " + temp.toString());
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), temp));
        } catch (UserNotLoggedException e1) {
            log.info("is it happened?");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        }

    }

    /**
     * @param request
     * @param toDo
     * @param result
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/newToDo", method = RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> newToDo(HttpServletRequest request, @Valid ToDo toDo, BindingResult result) {
        //1) success - toDoinserted into the response of JsonResponseBody
        //2) fail    - error message

        // Todo_validate
        ToDoValidator toDoValidator = new ToDoValidator();
        toDoValidator.validate(toDo, result);
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Data not valid." + result.toString()));
        }

        try {
            Map<String, Object> userData = loginService.verifyJwtAndGetData(request);
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), toDoService.addToDo(toDo)));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        }
    }


    /**
     * @param request
     * @param toDo
     * @param result
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/newToDo2", method = RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> newToDo2(HttpServletRequest request, @Valid ToDo toDo, BindingResult result) throws ParseException {
        //1) success - toDoinserted into the response of JsonResponseBody
        //2) fail    - error message

        // Todo_validate
        ToDoValidator toDoValidator = new ToDoValidator();
        toDoValidator.validate(toDo, result);
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Data not valid." + result.toString()));
        }

        try {
            String email = jwtUtils.getUserEmailFromHttpRequest(request);

            if(email.equals("JWT TOKEN IS INVALID") || email == "JWT TOKEN IS INVALID"){
                log.info("JWT 토큰이 유효한 토큰이 아니야~");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(),"JWT TOKEN IS INVALID:"));
            }
            log.info(">>>>토큰이 유효하다면 여길로 들어오겠지 ~ email:"+email);

            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), toDoService.addToDo(toDo)));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        }
    }

    /**
     * TODO -> UserMicroservice 로 가서 인증 받고 오게끔 해야지~
     * @param request
     * @param toDo
     * @param result
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/newToDo3", method = RequestMethod.POST)
    public ResponseEntity<JsonResponseBody> newToDo3(HttpServletRequest request, @Valid ToDo toDo, BindingResult result) throws ParseException {
        //1) success - toDoinserted into the response of JsonResponseBody
        //2) fail    - error message
        String email = null;
        // Todo_validate
        ToDoValidator toDoValidator = new ToDoValidator();
        toDoValidator.validate(toDo, result);
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Data not valid." + result.toString()));
        }

        try {
            // 원본 코드
//          String email = jwtUtils.getUserEmailFromHttpRequest(request);
            email = userProxy.talkToUserMicroServiceToVerifyUserAndGetEmail(request);
            if(email==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(),"I talked to User-Microservice...but,it didn't work out."));
            }
            if(email.equals("JWT TOKEN IS INVALID") || email == "JWT TOKEN IS INVALID"){
                log.info("JWT 토큰이 유효한 토큰이 아니야~");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(),"JWT TOKEN IS INVALID:"));
            }
            log.info(">>>>토큰이 유효하다면 여길로 들어오겠지 ~ email:"+email);

            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), toDoService.addToDo(toDo)));
        } catch (UserNotLoggedException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request:" + e1.toString()));
        } catch (UnsupportedEncodingException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden Request:" + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY Timeout:" + e3.toString()));
        }
    }


    /**
     * @param request
     * @param toDoId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/deleteToDo/{id}")
    public ResponseEntity<JsonResponseBody> deleteToDo(HttpServletRequest request, @PathVariable(name = "id") Integer toDoId) {

        // 1) success - message of success delete
        // 2) fail - error message
        try {
            Map<String, Object> temp = loginService.verifyJwtAndGetData(request);
            log.info(">>>>temp : " + temp.toString());
            toDoService.deleteToDo(toDoId);

            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), "ToDo correctly delete"));
        } catch (UnsupportedEncodingException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request: " + e1.toString()));
        } catch (UserNotLoggedException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden: " + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "Session Expired: " + e3.toString()));
        }
    }


    /**
     * @param request
     * @param toDoId
     * @return
     */
    @RequestMapping(value = "/deleteToDo2/{id}")
    public ResponseEntity<JsonResponseBody> deleteToDo2(HttpServletRequest request, @PathVariable(name = "id") Integer toDoId) throws ParseException{

        // 1) success - message of success delete
        // 2) fail - error message
        try {
            String email = jwtUtils.getUserEmailFromHttpRequest(request);

            if(email.equals("JWT TOKEN IS INVALID") || email == "JWT TOKEN IS INVALID"){
                log.info("JWT 토큰이 유효한 토큰이 아니야~");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(),"JWT TOKEN IS INVALID:"));
            }
            log.info(">>>>토큰이 유효하다면 여길로 들어오겠지 ~ email:"+email);

            toDoService.deleteToDo(toDoId);

            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), "ToDo correctly delete"));
        } catch (UnsupportedEncodingException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request: " + e1.toString()));
        } catch (UserNotLoggedException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden: " + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "Session Expired: " + e3.toString()));
        }
    }

    /**
     * @param request
     * @param toDoId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/deleteToDo3/{id}")
    public ResponseEntity<JsonResponseBody> deleteToDo3(HttpServletRequest request, @PathVariable(name = "id") Integer toDoId) throws ParseException{

        // 1) success - message of success delete
        // 2) fail - error message
        try {
            String email =  userProxy.talkToUserMicroServiceToVerifyUserAndGetEmail(request);
            if(email==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(),"I talked to User-Microservice...but,it didn't work out."));
            }
            if(email.equals("JWT TOKEN IS INVALID") || email == "JWT TOKEN IS INVALID"){
                log.info("JWT 토큰이 유효한 토큰이 아니야~");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(),"JWT TOKEN IS INVALID:"));
            }
            log.info(">>>>토큰이 유효하다면 여길로 들어오겠지 ~ email:"+email);

            toDoService.deleteToDo(toDoId);

            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), "ToDo correctly delete"));
        } catch (UnsupportedEncodingException e1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request: " + e1.toString()));
        } catch (UserNotLoggedException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Forbidden: " + e2.toString()));
        } catch (ExpiredJwtException e3) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "Session Expired: " + e3.toString()));
        }
    }


}


// Controller layer - (we want it to manage all Exceptions! )
//       |
//       |
//  Service Layer (throw all the present and "lower-layer" exceptions)
//  |         |
//  |         |
// utilities  Daos
//            |
//            |
//            Database



