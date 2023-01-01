//package com.coder.community;
//
//import com.coder.community.dao.DiscussPostMapper;
////import com.coder.community.dao.elasticsearch.DiscussPostRepository;
//import com.coder.community.entity.DiscussPost;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
//import org.springframework.data.elasticsearch.core.query.Query;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
//public class ElasticSearchTest {
//    @Autowired
//    DiscussPostMapper discussPostMapper;
//
////    @Autowired
////    DiscussPostRepository discussPostRepository;
//
//    @Autowired
//    RestHighLevelClient highLevelClient;
//
//    @Test
//    public void testInsert(){
//        discussPostRepository.save(discussPostMapper.selectById(241));
//        discussPostRepository.save(discussPostMapper.selectById(242));
//        discussPostRepository.save(discussPostMapper.selectById(243));
//    }
//
//    @Test
//    public void testInsertList(){
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
//
//    }
//
//    @Test
//    public void testUpdate(){
//        DiscussPost discussPost = discussPostMapper.selectById(231);
//        discussPost.setContent("我是新人，使劲灌水");
//        discussPostRepository.save(discussPost);
//    }
//    @Test
//    public void testDelete(){
//        discussPostRepository.deleteById(231);
//    }
//    @Test
//    public void testSearch(){
////        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
////                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
////                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
////                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
////                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
////                .withPageable(PageRequest.of(0,10))
////                .withHighlightFields(
////                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
////                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
////                ).build();
//        DiscussPost discussPost = new DiscussPost();
//        discussPost.setContent("互联网寒冬");
//        discussPost.setTitle("互联网寒冬");
//
//        Page<DiscussPost> discussPosts = discussPostRepository.searchSimilar(discussPost, new String[]{"title", "content"}, PageRequest.of(0, 10));
//        System.out.println(discussPosts.getTotalElements());
//    }
//}
