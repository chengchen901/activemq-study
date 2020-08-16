package com.study.activemq.le1.helloworld.spring;

/**
 * @author Hash
 * @since 2020/8/16
 */
public class Email {
    /** 发送地址*/
    private String to;

    /** 消息内容*/
    private String body;

    public Email() {
    }

    public Email(String to, String body) {
        this.to = to;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Email{" +
                "to='" + to + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
