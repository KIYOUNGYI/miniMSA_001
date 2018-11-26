package com.quicktutorialz.microservices.TodoMicroservice.services;

import com.quicktutorialz.microservices.TodoMicroservice.daos.UserDao;
import com.quicktutorialz.microservices.TodoMicroservice.entities.User;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.EncryptionUtils;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.JwtUtils;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.UserNotInDatabaseException;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.UserNotLoggedException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
    @Autowired
    UserDao userDao;

    @Autowired
    EncryptionUtils encryptionUtils;

    @Autowired
    JwtUtils jwtUtils;

    /**
     *
     * @param email
     * @param pwd
     * @return
     * @throws UserNotInDatabaseException
     */
    @Override
    public Optional<User> getUserFromDb(String email, String pwd) throws UserNotInDatabaseException {
        Optional<User> userr = userDao.findUserByEmail(email);
        if(userr.isPresent()){
            User user = userr.get();
            if(!encryptionUtils.decrypt(user.getPassword()).equals(pwd)){
                throw new UserNotInDatabaseException("Wrong Email or Password");
            }
        }else{
            throw new UserNotInDatabaseException("Wrong Email or Password");
        }

        return userr;
    }

    /**
     *
     * @param email
     * @param name
     * @param date
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String createJwt(String email, String name, Date date) throws UnsupportedEncodingException {
        date.setTime(date.getTime()+(300*1000));
        return jwtUtils.generateJwt(email,name,date);
    }


//    public String createJwt2(String email, String name, Date date){
//        date.setTime(date.getTime()+(300*1000));
//        try {
//            return jwtUtils.generateJwt(email, name, date);
//        }catch (UnsupportedEncodingException e) {
//            return "UnsupportedEncodingException";
//        }
//    }

    /**
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws UserNotLoggedException
     */
    @Override
    public Map<String, Object> verifyJwtAndGetData(HttpServletRequest request) throws UnsupportedEncodingException, UserNotLoggedException {
        String jwt = jwtUtils.getJwtFromHttpRequest(request);
        if(jwt==null){
            throw new UserNotLoggedException("User Not Logged in first.");
        }
        return jwtUtils.jwt2Map(jwt);
    }




}

