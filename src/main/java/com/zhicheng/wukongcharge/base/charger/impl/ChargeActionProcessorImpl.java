package com.zhicheng.wukongcharge.base.charger.impl;

import com.zhicheng.wukongcharge.base.charger.IChargeActionProcessor;
import com.zhicheng.wukongcharge.communicate.Login;
import com.zhicheng.wukongcharge.communicate.TomdaProcotolContent;
import com.zhicheng.wukongcharge.communicate.processor.TomdaChargeActionProcessor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
/**
 * 电池活动控制器的实现类
 * @author 章家宝
 * IChargeActionProcessor
 *
 */
public class ChargeActionProcessorImpl implements IChargeActionProcessor {

	public void processModel(TomdaProcotolContent procotolContent, ChannelHandlerContext context) {
		
//		Login login = new Login();
//		login.login(context, procotolContent.getMsgId());
//		System.out.println(procotolContent.getFrame());
		TomdaChargeActionProcessor t = new TomdaChargeActionProcessor();
//		t.wukongChargeLogin(context);
		try {
//			t.loginRequest(context, procotolContent.getMsgId(), "2018873124");
			t.instruct3(context, procotolContent.getMsgId(), "2018873124", procotolContent.getDataType(), procotolContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		context.channel().writeAndFlush(procotolContent.getFrame());
	}

}
