package com.group.mapper;

import com.group.pojo.Employer;
import com.group.pojo.Message;
import com.group.pojo.User;
import com.group.pojo.UserRelation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/11/14:31
 * @Description:
 */

@Mapper
public interface MessageMapper {
    @Select("select * from user_relation where (uid = #{uid} and eid = #{eid})")
    UserRelation getRelation(Long uid, Long eid);

    @Select("SELECT id, AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       duty, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       company,character_type,image\n" +
            "FROM employer WHERE id in (select eid from user_relation where uid = #{id});")
    List<Employer> selectEmployerByUid(Long id);

    @Select("SELECT id,AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       nickname, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(address, 'address_secret_key', 'AES/CBC/PKCS5Padding') AS address,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       birth, min_expected_salary, max_expected_salary, ideal_city, education, type, school, major,resume,image\n" +
            "FROM user WHERE id in (select uid from user_relation where eid = #{id});")
    List<User> selectUserByEid(Long id);

    @Insert("insert into user_relation values (#{uid},#{eid})")
    void insertFriend(Long uid, Long eid);

    @Insert("insert into message values (#{l},#{toImage},#{l1},#{fromImage},#{nowtype},#{textMessage},#{saveTime},#{readed})")
    void saveMessage(long l, String toImage, long l1, String fromImage, String nowtype, String textMessage,LocalDateTime saveTime, boolean readed);

    @Select("select * from message where ((to_user = #{toUserId} and from_user = #{fromUserId}) " +
            "or " +
            "(to_user = #{fromUserId} and from_user = #{toUserId}))")
    List<Message> selectMessage(Long toUserId, Long fromUserId);

    @Update("update message set readed=1 where to_user = #{toUserId} and from_user = #{fromUserId} and readed = 0 ")
    void updateReaded(Long toUserId, Long fromUserId);

    @Select("select count(*) from message where (to_user = #{toId} and from_user = #{fromId} and readed=0)")
    Integer selectUnreadedById(Long toId, Long fromId);
}
