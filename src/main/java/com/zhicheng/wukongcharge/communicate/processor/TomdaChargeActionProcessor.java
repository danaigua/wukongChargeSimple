package com.zhicheng.wukongcharge.communicate.processor;

import java.util.Arrays;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.zhicheng.wukongcharge.entity.Charger;
import com.zhicheng.wukongcharge.model.TomdaProtocol;
import com.zhicheng.wukongcharge.models.BeatHeartProto;
import com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartReq;
import com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartResp;
import com.zhicheng.wukongcharge.models.BootNotificationProto;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationReq;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationReq.Builder;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationReq.bootReasonEnumType;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationResp.registrationStatusEnumType;
import com.zhicheng.wukongcharge.models.StartTransactionProto;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionResp;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionResp.startTransactionStatusEnumType;
import com.zhicheng.wukongcharge.tcp.KeyAttach;
import com.zhicheng.wukongcharge.tool.Tool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class TomdaChargeActionProcessor {

//	private String routerSerialNum;         //网关序列号
//	 private String routerCompanyCode;        //网关所属公司编码
//	 private Long routerStationId;         //网关关联电站的ID
//	 private boolean login;           //当前网关是否已经登录
//	 private Protocol protocol;          //协议版本
//	 private boolean needResetAuthKey;        //是否需要重新设置网关登录Key
//	 private long loginTime;           //登陆时间
//	 private long syncTime;                //网关同步后10秒后才去设置网关的登录Key
//	 private long ioExceptionTime;         //首次报IO异常的时间，超过2分钟，则断开TCP连接
//	 private List<String> commands = new ArrayList<String>();
//	 private String protocolId;          //协议的ID
//	 private String remoteConnectInfo;        //远端连接判定参数
//	KeyAttach keyAttach = new KeyAttach();
	public static final AttributeKey<KeyAttach> NETTY_CHANNEL_KEY = AttributeKey.valueOf("tomdanetty.channel");

	/**
	 * 充电桩登录
	 */
	public boolean wukongChargeLogin(ChannelHandlerContext ctx) {
		Charger charger = new Charger();
		charger.setCompanyCode("智诚");
		charger.setStationId((long) 123);
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		// 未识别（未知的序列号） 的Socket， 全新的socket连接
		if (null == keyAttach) {
			keyAttach = new KeyAttach();
		}
		String chargerSerialNum = "2018873123";
		keyAttach.setRouterSerialNum(chargerSerialNum);
		keyAttach.setRouterCompanyCode(charger.getCompanyCode());
		keyAttach.setProtocolId(this.getProtocolID());
		attr.setIfAbsent(keyAttach);

		// 设置登录状态为false，等待回应以后就设置为true
		keyAttach.setRouterStationId(charger.getStationId());
		keyAttach.setLogin(true);

		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		tomdaProtocol.loadModels();

		Builder builder2 = BootNotificationProto.bootNotificationReq.newBuilder();
		builder2.setReason(bootReasonEnumType.ApplicationReset).setSerialNumber("2018873123").setKey("83466")
				.setPcbID(111).setVendorName("aaa").setFirmwareVersion("111");
		bootNotificationReq req = builder2.build();
		byte[] byteArray = req.toByteArray();
		System.out.println(Arrays.toString(byteArray));
		System.out.println(builder2);
		System.out.println(tomdaProtocol.organizeFrame(101, 10101, req));
		ctx.channel().write(tomdaProtocol.organizeFrame(101, 10101, req));
//    	tomdaProtocol.organizeFrame(101, 10101, req);

		return true;
	}

	/**
	 * 登录应答
	 * 
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public boolean loginRequest(ChannelHandlerContext ctx, int msgId, String chargerSerialNum) throws Exception {
		Charger charger = new Charger();
		charger.setCompanyCode("智诚");
		charger.setStationId((long) 123);
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		// 未识别（未知的序列号） 的Socket， 全新的socket连接
		if (null == keyAttach) {
			keyAttach = new KeyAttach();
		}
		keyAttach.setRouterSerialNum(chargerSerialNum);
		keyAttach.setRouterCompanyCode(charger.getCompanyCode());
		keyAttach.setProtocolId(this.getProtocolID());
		attr.setIfAbsent(keyAttach);

		// 设置登录状态为false，等待回应以后就设置为true
		keyAttach.setRouterStationId(charger.getStationId());
		keyAttach.setLogin(true);

		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		tomdaProtocol.loadModels();

		com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationResp.Builder builder = BootNotificationProto.bootNotificationResp
				.newBuilder();

		builder.setCurrentTime(Tool.getISO8601Timestamp());
		builder.setStatus(registrationStatusEnumType.Accepted);
		builder.setInterval(180);

		MessageLite resp = builder.build();

		System.out.println("返回去的" + resp);
		byte[] byteArray = resp.toByteArray();
		String str2 = new String(byteArray, "ISO-8859-1");
		System.out.println(Tool.showData(str2));

//        System.out.println(testBuf);

//		System.out.println(tomdaProtocol.organizeFrame(102, msgId, resp));
		MessageLite testBuf = BootNotificationProto.bootNotificationResp.parseFrom(byteArray);
		System.out.println(tomdaProtocol.organizeFrame(102, msgId, testBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(102, msgId, testBuf));
//		ctx.writeAndFlush(tomdaProtocol.organizeFrame(102, msgId, testBuf));
		return true;
	}

	private String getProtocolID() {

		return null;
	}

	/**
	 * 请求心跳
	 * 
	 * @return
	 */
	public boolean beatHeartRequest(ChannelHandlerContext ctx) {
		/**
		 * 确认是否登录
		 */
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
			/**
			 * 如果是登录则可以请求心跳
			 */
			com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartReq.Builder builder = BeatHeartProto.beatHeartReq
					.newBuilder();
			builder.setCsq(60);
			beatHeartReq req = builder.build();
			byte[] byteArray = req.toByteArray();
			TomdaProtocol tomdaProtocol = new TomdaProtocol();
			tomdaProtocol.loadModels();
			System.out.println(Arrays.toString(byteArray));
			System.out.println(tomdaProtocol.organizeFrame(103, 10101, req));
			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(103, 10101, req));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 心跳应答
	 * 
	 * @param ctx
	 */
	public void beatHeartResponse(ChannelHandlerContext ctx) {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
			com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartResp.Builder builder = BeatHeartProto.beatHeartResp
					.newBuilder();
			builder.setTimestamp(Tool.getCurrentTimeStr10());
			beatHeartResp resp = builder.build();
			byte[] byteArray = resp.toByteArray();
			TomdaProtocol tomdaProtocol = new TomdaProtocol();
			tomdaProtocol.loadModels();
			System.out.println(Arrays.toString(byteArray));
			System.out.println(tomdaProtocol.organizeFrame(104, 10101, resp));
			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(104, 10101, resp));
		}
	}

	/**
	 * 充电授权
	 * 
	 * @param ctx
	 * @param connectId
	 */
	public void startChargingReq(ChannelHandlerContext ctx, int connectId) {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
			com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionResp.Builder builder = StartTransactionProto.startTransactionResp
					.newBuilder();
			builder.setStatus(startTransactionStatusEnumType.Accepted);
			startTransactionResp resp = builder.build();
			byte[] byteArray = resp.toByteArray();
			TomdaProtocol tomdaProtocol = new TomdaProtocol();
			tomdaProtocol.loadModels();
			System.out.println(Arrays.toString(byteArray));
			System.out.println(tomdaProtocol.organizeFrame(104, 10101, resp));
			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(104, 10101, resp));
		}
	}

	public void stopCharging(ChannelHandlerContext ctx) {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {

		}
	}

	public static void main(String[] args) {
	}
}
