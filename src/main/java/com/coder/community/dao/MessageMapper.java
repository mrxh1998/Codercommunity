package com.coder.community.dao;

import com.coder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询会话列表
    List<Message> selectConversations(int userId,int offset,int limit);

    //查询当前会话数量
    int selectConversationCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectLetterUnreadCount(int userId,String conversationId);

    //添加私信
    int insertLetter(Message message);

    //修改私信状态
    int updateStatus(List<Integer> ids,int status);

    //查询某主题中最新的一条通知
    Message selectLatestNotice(int userId,String topic);
    //某个主题包含的通知数量
    int selectNoticeCount(int userId,String topic);
    //某个主题的未读通知数量
    int selectUnreadNoticeCount(int userId,String topic);

    //查询某个主题的所有通知
    List<Message> selectNotices(int userId,String topic,int offset,int limit);
}
