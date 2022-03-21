package com.coder.community;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LogTest {
    private static final Logger logger = LoggerFactory.getLogger("LogTest");

    @Test
    public void testLog(){
        System.out.println(logger.getName());
        logger.debug("debug.Log");
        logger.info("info Log");
        logger.warn("warn Log");
        logger.error("error Log");
    }
}
