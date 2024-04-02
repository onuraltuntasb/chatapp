package com.chatapp.chatapp.service;

import com.chatapp.chatapp.dao.FriendDao;
import com.chatapp.chatapp.dao.UserDao;
import com.chatapp.chatapp.entity.User;
import com.chatapp.chatapp.exception.ResourceNotFoundException;
import com.chatapp.chatapp.payload.request.AddFriendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendDao friendDao;
    private final UserDao userDao;

    public Boolean addFriend(AddFriendRequest addFriendRequest){

        Boolean response = false;
        String userEmail = addFriendRequest.getUserEmail();
        String friendEmail =addFriendRequest.getFriendEmail();


        User user = userDao.findByEmail(userEmail);

        if(user==null){
            throw new ResourceNotFoundException("User is not found with this email : "+userEmail);
        }

        User friendUser = userDao.findByEmail(friendEmail);

        if(friendUser == null){
            throw new ResourceNotFoundException("User's friend not found with this email : " + friendEmail);
        }else{
            //if any record is already in table friendDao.save throws sql exception
            response = friendDao.save(user.getId(),friendUser.getId());
        }

        return response;
    }
}
