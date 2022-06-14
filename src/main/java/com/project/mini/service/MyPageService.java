package com.project.mini.service;

import com.project.mini.dto.response.MyPageResponseDto;
import com.project.mini.models.Post;
import com.project.mini.models.User;
import com.project.mini.repository.UserRepository;
import com.project.mini.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;

    public MyPageResponseDto allInfo(UserDetailsImpl userDetails) {
//        User user = userDetails.getUser();
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        String nickname = user.getNickname();
        List<Post> posts = user.getPosts();

        int myRank = checkMyRank(user);
        int totalUser = checkTotalUser();

        return new MyPageResponseDto(nickname, posts, myRank, totalUser);
    }

    public int checkMyRank(User user) {
        Long targetId = user.getId();

        //타겟의 게시글 수 , 해피포인트
        User target = userRepository.findById(targetId).orElse(null);
        //게시글 작성 안했을 때 꼴지로 보냄
        if (target.getPosts().size() == 0) {
            return userRepository.findAll().size();
        }
        int targetCountPost = target.getPosts().size();
        int targetHappyPoint = target.getHappypoint();

        int targetAvePoint = targetHappyPoint / targetCountPost;

        //게시글이 있는 전체 유저리스트
        List<User> userList = userRepository.findDistinctAllByPostsIsNotNull();

        int myRank = 1;
        for (User value : userList) {
            //다른 유저의 게시글 수 , 해피포인트
            int countPost = value.getPosts().size();
            int eachHappyPoint = value.getHappypoint();

            int myAvePoint = eachHappyPoint / countPost;

            if (targetAvePoint < myAvePoint) {
                myRank += 1;
            }
        }
        return myRank;
    }

    public int checkTotalUser() {

        List<User> allUser = userRepository.findAll();
        //전체 유저리스트 갯수
        return allUser.size();
    }
}
