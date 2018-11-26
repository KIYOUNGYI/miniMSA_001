package com.kiyoung.msa.UserMicroService.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.kiyoung.msa.UserMicroService.entities.User;
import com.kiyoung.msa.UserMicroService.utilities.UserNotInDatabaseException;
import com.kiyoung.msa.UserMicroService.utilities.UserNotLoggedException;
import io.jsonwebtoken.SignatureException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

public interface LoginService {
    Optional<User> getUserFromDb(String email, String pwd) throws UserNotInDatabaseException;

    String createJwt(String email, String name, Date date) throws UnsupportedEncodingException;

    Map<String, Object> verifyJwtAndGetData(HttpServletRequest request) throws UnsupportedEncodingException,UserNotLoggedException,SignatureException,JsonParseException;
}
