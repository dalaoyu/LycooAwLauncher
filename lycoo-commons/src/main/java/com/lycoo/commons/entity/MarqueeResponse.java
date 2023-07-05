package com.lycoo.commons.entity;


import com.lycoo.commons.base.BaseResponse;

/**
 * Marquee response
 * 数据结构如下：
 * {
 * "statusCode": 1,
 * "message": "",
 * "data": {
 * "show": true,
 * "name": "廿年不忘 习近平心系香江之凝魂篇",
 * "count": 100,
 * "period": 5,
 * "data": "团结统一的中华民族是海内外中华儿女共同的根，博大精深的中华文化是海内外中华儿女共同的魂，实现中华民族伟大复兴是海内外中华儿女共同的梦。3333",
 * "updateTime": "2017-10-23 16:01:34"
 * }
 * }
 *
 * Created by lancy on 2018/1/2
 */
public class MarqueeResponse extends BaseResponse {

    private MarqueeInfo data;

    public MarqueeInfo getData() {
        return data;
    }

    public void setData(MarqueeInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MarqueeResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
