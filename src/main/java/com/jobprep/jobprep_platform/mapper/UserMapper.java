package com.jobprep.jobprep_platform.mapper;
import com.jobprep.jobprep_platform.model.dto.user.UserQueryParam;
import com.jobprep.jobprep_platform.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * UserMapper means: an interface that defines methods for accessing user data in the database,
 *  without implementing them.
 * DAO (Data Access Object) = layer responsible for database operations.
 * Isolates database logic from business logic 
 */
@Mapper
public interface UserMapper {

    /**
     * insert new user
     * @param user - new user used to be inserted, 
     * includes all user information
     */
    int insert(User user);

    /**
     * find user based on id
     * @param userId 
     * @return user, else null
     */
    User findById(@Param("userId") Long userId);
    /**
     *  find users in batch 
     * @param userIds
     * @return userList, else null list
     */
    List<User> findByIdBatch(@Param("userIds") List<Long> userIds);

    /**
     * find user based on account 
     * @param account
     * @return user, else null
     */
    User findByAccount(@Param("account") String account);

    /**
     * find user list based on user params 
     * @param queryParams 
     *      The user query parameter object encapsulates various filtering conditions for querying users.
     *  @return List of users matching the query conditions
     */
    List<User> findByQueryParam(@Param("queryParams") UserQueryParam queryParams,
                                @Param("limit") Integer limit,
                                @Param("offset") Integer offset);

    /**
     * Count the number of users based on the query parameters.
     * @param queryParams 
     * @return number of users who satisifies all 
     */
    int countByQueryParam(@Param("queryParams") UserQueryParam queryParams);

    /**
     * update user information 
     * @param user 
     */
    int update(User user);

    /**
     * update user last login time
     * @param userId
     */
    int updateLastLoginAt(@Param("userId") Long userId);

    /**
     * get today's login number
     * @return number
     */
    int getTodayLoginCount();

    /**
     * Today's registration count
     * @return number
     */
    int getTodayRegisterCount();

    /**
     * Total registration 
     * 
     */
    int getTotalRegisterCount();

    /**
     * find user based on email
     * @return user object ,else null
     */
    User findByEmail(@Param("email") String email);

    /**
     * search user
     * @param keyword
     * @param limit
     * @param offset
     * @return user list
     */
    List<User> searchUsers(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);




}
