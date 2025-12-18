package org.uneev.charityboard.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.Converter;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.uneev.charityboard.dto.CommentResponseDto;
import org.uneev.charityboard.dto.PostResponseDto;
import org.uneev.charityboard.dto.ReviewResponseDto;
import org.uneev.charityboard.dto.UserResponseDto;
import org.uneev.charityboard.entity.Comment;
import org.uneev.charityboard.entity.DepositStatus;
import org.uneev.charityboard.entity.Post;
import org.uneev.charityboard.entity.PostStatus;
import org.uneev.charityboard.entity.Review;
import org.uneev.charityboard.entity.User;
import org.uneev.charityboard.entity.UserProfile;

@Configuration
public class UtilsConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	    @Bean
	    public ModelMapper modelMapper() {
	        ModelMapper modelMapper = new ModelMapper();
	        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

	        Converter<PostStatus, String> postStatusToString = ctx ->
	                ctx.getSource() == null ? null : ctx.getSource().name();
	        Converter<DepositStatus, String> depositStatusToString = ctx ->
	                ctx.getSource() == null ? null : ctx.getSource().name();
	        Converter<UserProfile, String> executorUsername = ctx -> {
	            UserProfile executor = ctx.getSource();
	            if (executor == null || executor.getUser() == null) return null;
	            return executor.getUser().getUsername();
	        };
	        Converter<UserProfile, String> profileFirstName = ctx -> {
	            UserProfile profile = ctx.getSource();
	            return profile == null ? null : profile.getFirstName();
	        };
	        Converter<UserProfile, String> profileSecondName = ctx -> {
	            UserProfile profile = ctx.getSource();
	            return profile == null ? null : profile.getSecondName();
	        };
	        Converter<UserProfile, String> profileCity = ctx -> {
	            UserProfile profile = ctx.getSource();
	            return profile != null && profile.getCity() != null ? profile.getCity().getName() : null;
	        };
	        Converter<UserProfile, String> executorFirstName = ctx -> {
	            UserProfile executor = ctx.getSource();
	            return executor == null ? null : executor.getFirstName();
	        };
	        Converter<UserProfile, String> executorSecondName = ctx -> {
	            UserProfile executor = ctx.getSource();
	            return executor == null ? null : executor.getSecondName();
	        };

        TypeMap<User, UserResponseDto> userMapper = modelMapper.createTypeMap(
                User.class,
                UserResponseDto.class
        );
        userMapper.addMappings(mapper -> {
            mapper.map(src -> src.getProfile().getFirstName(), UserResponseDto::setFirstName);
            mapper.map(src -> src.getProfile().getSecondName(), UserResponseDto::setSecondName);
            mapper.map(src -> src.getProfile().getCity().getName(), UserResponseDto::setCity);
        });

	        TypeMap<Post, PostResponseDto> postMapper = modelMapper.createTypeMap(
	                Post.class,
	                PostResponseDto.class
	        );
	        postMapper.addMappings(mapper -> {
	            mapper.map(src -> src.getCategory().getName(), PostResponseDto::setCategory);
	            mapper.using(postStatusToString).map(Post::getStatus, PostResponseDto::setStatus);
	            mapper.using(depositStatusToString).map(Post::getDepositStatus, PostResponseDto::setDepositStatus);
	            mapper.map(Post::getDepositAmount, PostResponseDto::setDepositAmount);
	            mapper.using(executorUsername).map(Post::getAuthor, PostResponseDto::setAuthorUsername);
	            mapper.using(profileFirstName).map(Post::getAuthor, PostResponseDto::setAuthorFirstName);
	            mapper.using(profileSecondName).map(Post::getAuthor, PostResponseDto::setAuthorSecondName);
	            mapper.using(profileCity).map(Post::getAuthor, PostResponseDto::setAuthorCity);
	            mapper.using(executorUsername).map(Post::getExecutor, PostResponseDto::setExecutorUsername);
	            mapper.using(executorFirstName).map(Post::getExecutor, PostResponseDto::setExecutorFirstName);
	            mapper.using(executorSecondName).map(Post::getExecutor, PostResponseDto::setExecutorSecondName);
	        });


        TypeMap<Comment, CommentResponseDto> commentMapper = modelMapper.createTypeMap(
                Comment.class,
                CommentResponseDto.class
        );
        commentMapper.addMappings(mapper -> {
            mapper.map(Comment::getId, CommentResponseDto::setId);
            mapper.map(src -> src.getAuthor().getUser().getUsername(), CommentResponseDto::setUsername);
            mapper.map(src -> src.getAuthor().getFirstName(), CommentResponseDto::setFirstName);
            mapper.map(src -> src.getAuthor().getSecondName(), CommentResponseDto::setSecondName);
        });

        TypeMap<Review, ReviewResponseDto> reviewMapper = modelMapper.createTypeMap(
                Review.class,
                ReviewResponseDto.class
        );
        reviewMapper.addMappings(mapper -> {
            mapper.map(src -> src.getPost().getId(), ReviewResponseDto::setPostId);
            mapper.map(src -> src.getReviewer().getUser().getUsername(), ReviewResponseDto::setReviewerUsername);
            mapper.map(src -> src.getReviewer().getFirstName(), ReviewResponseDto::setReviewerFirstName);
            mapper.map(src -> src.getReviewer().getSecondName(), ReviewResponseDto::setReviewerSecondName);
        });

        return modelMapper;
    }
}
