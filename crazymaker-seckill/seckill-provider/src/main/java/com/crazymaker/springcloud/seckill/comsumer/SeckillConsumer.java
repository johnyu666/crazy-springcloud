package com.crazymaker.springcloud.seckill.comsumer;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.message.annotation.MqSubscriber;
import com.crazymaker.springcloud.message.annotation.TopicConsumer;
import com.crazymaker.springcloud.seckill.api.dto.SimpleOrderDTO;
import com.crazymaker.springcloud.seckill.service.impl.SeckillServiceImpl;
import com.crazymaker.springcloud.standard.context.AppContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MqSubscriber(topic = "topic.seckill" )
public class SeckillConsumer
{

    //    @Autowired
    SeckillServiceImpl seckillService;

    @TopicConsumer(group = "group.seckill.order" )
    public boolean processGroupA(String content)
    {
//        log.info("MsgReceiverA  processGroupA接收处理队列A当中的消息： " + content);

        SimpleOrderDTO dto = JsonUtil.jsonToPojo(content, SimpleOrderDTO.class);

        if (seckillService == null)
        {
            seckillService = AppContextHolder.getBean(SeckillServiceImpl.class);
        }
        try
        {
            seckillService.executeSeckillV3(dto);
        } catch (Throwable throwable)
        {
            throwable.printStackTrace();
            if (throwable instanceof BusinessException)
            {
                log.error(((BusinessException) throwable).getErrMsg());
            } else
            {
                log.error(throwable.getMessage());
            }
//            return false;
        }
        return true;
    }


}