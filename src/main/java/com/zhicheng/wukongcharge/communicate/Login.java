package com.zhicheng.wukongcharge.communicate;

import com.zhicheng.wukongcharge.entity.Charger;
import com.zhicheng.wukongcharge.model.TomdaProtocol;
import com.zhicheng.wukongcharge.models.BootNotificationProto;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationResp.registrationStatusEnumType;
import com.zhicheng.wukongcharge.tcp.KeyAttach;
import com.zhicheng.wukongcharge.tool.PTool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class Login {
    public static final AttributeKey<KeyAttach> NETTY_CHANNEL_KEY = AttributeKey.valueOf("tomdanetty.channel");
    
    
	private String protocolID;
	public void login(ChannelHandlerContext ctx, int msgId) {
    	Charger charger = new Charger();
    	charger.setCompanyCode("TOMDA");
    	charger.setStationId((long) 102);
    	Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
    	KeyAttach keyAttach = attr.get();
    	//未识别（未知的序列号） 的Socket， 全新的socket连接
    	if(null == keyAttach) {
    		keyAttach = new KeyAttach();
    	}
    	String chargerSerialNum = "2018873123";
		keyAttach.setRouterSerialNum(chargerSerialNum);
    	keyAttach.setRouterCompanyCode(charger.getCompanyCode());
    	keyAttach.setProtocolId(this.getProtocolID());
    	attr.setIfAbsent(keyAttach);
    	
    	//设置登录状态为false，等待回应以后就设置为true
    	keyAttach.setRouterStationId(charger.getStationId());
    	keyAttach.setLogin(true);
    	
    	TomdaProtocol tomdaProtocol = new TomdaProtocol();
    	tomdaProtocol.loadModels();
    	BootNotificationProto.bootNotificationResp.Builder builder = BootNotificationProto.bootNotificationResp.newBuilder();
    	builder.setStatus(registrationStatusEnumType.Accepted).setCurrentTime(PTool.getCurrentTimeStr10()).setInterval(60);
	}
	public String getProtocolID() {
		return protocolID;
	}
	public void setProtocolID(String protocolID) {
		this.protocolID = protocolID;
	}

}
