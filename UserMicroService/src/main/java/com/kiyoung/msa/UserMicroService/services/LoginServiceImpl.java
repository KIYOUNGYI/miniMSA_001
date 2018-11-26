package com.kiyoung.msa.UserMicroService.services;

import com.kiyoung.msa.UserMicroService.daos.UserDao;
import com.kiyoung.msa.UserMicroService.entities.User;
import com.kiyoung.msa.UserMicroService.utilities.*;
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

    @Autowired
    UserDao userDao;

    @Autowired
    EncryptionUtils encryptionUtils;

    @Autowired
    JwtUtils jwtUtils;

    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
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
        date.setTime(date.getTime()+(300*1000*1000));
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
            log.info("[verifyJwtAndGetData] jwt is null, User Not Logged in.");
            throw new UserNotLoggedException("User Not Logged in first.");
        }
        return jwtUtils.jwt2Map(jwt);
    }
}

