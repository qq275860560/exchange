package querydsldemo;

import com.ghf.exchange.Application;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class QuerydslTest {

    @Lazy
    @Resource
    private JPAQueryFactory jpaQueryFactory;

    @Test
    public void test() {
        Assert.assertTrue(1 == 1);
    }

}
