package com.zhicheng.wukongcharge.communicate.processor;

import java.util.Arrays;
import java.util.Scanner;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.zhicheng.wukongcharge.communicate.TomdaProcotolContent;
import com.zhicheng.wukongcharge.entity.Charger;
import com.zhicheng.wukongcharge.model.TomdaProtocol;
import com.zhicheng.wukongcharge.models.AttributeEnumTypeProto.attributeEnumType;
import com.zhicheng.wukongcharge.models.AuthorizeProto;
import com.zhicheng.wukongcharge.models.BeatHeartProto;
import com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartReq;
import com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartResp;
import com.zhicheng.wukongcharge.models.BootNotificationProto;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationReq;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationReq.Builder;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationReq.bootReasonEnumType;
import com.zhicheng.wukongcharge.models.BootNotificationProto.bootNotificationResp.registrationStatusEnumType;
import com.zhicheng.wukongcharge.models.ChargingDataReportProto;
import com.zhicheng.wukongcharge.models.ComponentTypeProto.componentType;
import com.zhicheng.wukongcharge.models.CostUpdatedProto;
import com.zhicheng.wukongcharge.models.DataTransferProto;
import com.zhicheng.wukongcharge.models.FirmwareStatusNotificationProto;
import com.zhicheng.wukongcharge.models.GetVariableDataTypeProto.getVariableDataType;
import com.zhicheng.wukongcharge.models.GetVariableProto;
import com.zhicheng.wukongcharge.models.IDTokenTypeProto;
import com.zhicheng.wukongcharge.models.IDTokenTypeProto.IDTokenType;
import com.zhicheng.wukongcharge.models.IDTokenTypeProto.IDTokenType.IDTokenEnumType;
import com.zhicheng.wukongcharge.models.NotifyEventProto;
import com.zhicheng.wukongcharge.models.SetVariableDataTypeProto.setVariableDataType;
import com.zhicheng.wukongcharge.models.SetVariablesProto;
import com.zhicheng.wukongcharge.models.StartTransactionProto;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionReq;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionReq.chargeModeEnumType;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionReq.chargingProfileType;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionResp;
import com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionResp.startTransactionStatusEnumType;
import com.zhicheng.wukongcharge.models.StatusNotificationProto;
import com.zhicheng.wukongcharge.models.StopTransactionProto;
import com.zhicheng.wukongcharge.models.TransactionEventProto;
import com.zhicheng.wukongcharge.models.TransactionEventProto.transactionEventReq.transactionEventEnumType;
import com.zhicheng.wukongcharge.models.TransactionEventProto.transactionEventResp;
import com.zhicheng.wukongcharge.models.TransactionEventProto.transactionEventResp.transactionStatusEnumType;
import com.zhicheng.wukongcharge.models.TriggerMessageProto;
import com.zhicheng.wukongcharge.models.TriggerMessageProto.triggerMessageReq.messageTriggerEnumType;
import com.zhicheng.wukongcharge.models.UpdateFirmwareEventProto;
import com.zhicheng.wukongcharge.models.UpdateFirmwareEventProto.updateFirmwareEventReq.updateEventTypeEnumType;
import com.zhicheng.wukongcharge.models.UpdateFirmwareEventProto.updateFirmwareEventReq.updateMethodEnumType;
import com.zhicheng.wukongcharge.models.UpdateFirmwareEventProto.updateFirmwareEventReq.updateParamType;
import com.zhicheng.wukongcharge.models.VariableCharacteristicsTypeProto.variableCharacteristicsType;
import com.zhicheng.wukongcharge.models.VariableCharacteristicsTypeProto.variableCharacteristicsType.DateEnumType;
import com.zhicheng.wukongcharge.models.VariableTypeProto.variableType;
import com.zhicheng.wukongcharge.tcp.KeyAttach;
import com.zhicheng.wukongcharge.tool.Tool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class TomdaChargeActionProcessor {
	public static final AttributeKey<KeyAttach> NETTY_CHANNEL_KEY = AttributeKey.valueOf("tomdanetty.channel");
	public static int i = 1;
	public static boolean flag = true;
	/**
	 * 登录应答 102
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
//		 未识别（未知的序列号） 的Socket， 全新的socket连接
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

		System.out.println(tomdaProtocol.organizeFrame(102, msgId, resp));
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
	 * 心跳应答 104
	 * 
	 * @param ctx
	 * @throws InvalidProtocolBufferException
	 */
	public void beatHeartResponse(ChannelHandlerContext ctx, int msgId) throws InvalidProtocolBufferException {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
			com.zhicheng.wukongcharge.models.BeatHeartProto.beatHeartResp.Builder builder = BeatHeartProto.beatHeartResp
					.newBuilder();
			builder.setTimestamp(Tool.getISO8601Timestamp());
			MessageLite resp = builder.build();
			byte[] byteArray = resp.toByteArray();
			TomdaProtocol tomdaProtocol = new TomdaProtocol();
			tomdaProtocol.loadModels();
			MessageLite beatBuf = BeatHeartProto.beatHeartResp.parseFrom(byteArray);
			System.out.println(Arrays.toString(byteArray));
			System.out.println(tomdaProtocol.organizeFrame(104, msgId, beatBuf));
			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(104, msgId, beatBuf));
		}
	}

	/**
	 * 数据帧: AA 55 01 00 1E 00 CC 00 05 4E 08 04 12 07 0A 03 39 33 34 10 01 1A 05 08
	 * 00 10 D0 05 58 35
	 * 
	 * 关键数据:
	 * 
	 * [startTransactionReq] connectorId: 4 IDToken { id: "934" type: eMAID }
	 * chargingProfile { chargeMode: ChargeByHour value: 720 }
	 * 
	 * 
	 *  用户开始充电 msgId:19973
	 * 
	 * 
	 */
	/**
	 * 充电授权 204
	 * 
	 * @param ctx
	 * @param connectId
	 * @throws InvalidProtocolBufferException
	 * 
	 */
	public void startChargingReq(ChannelHandlerContext ctx, int connectId, int msgId)
			throws InvalidProtocolBufferException {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
			com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionReq.Builder builder = StartTransactionProto.startTransactionReq
					.newBuilder();
			builder.setConnectorId(connectId);
			com.zhicheng.wukongcharge.models.IDTokenTypeProto.IDTokenType.Builder idBuilder = IDTokenTypeProto.IDTokenType
					.newBuilder();
			idBuilder.setId("123");
			idBuilder.setType(IDTokenEnumType.eMAID);
			IDTokenType idTokenType = idBuilder.build();
			builder.setIDToken(idTokenType);
			
			com.zhicheng.wukongcharge.models.StartTransactionProto.startTransactionReq.chargingProfileType.Builder chargingProfileTypeBuilder = StartTransactionProto.startTransactionReq.chargingProfileType
					.newBuilder();
			chargingProfileTypeBuilder.setChargeMode(chargeModeEnumType.ChargeByHour);
			chargingProfileTypeBuilder.setValue(180);
			chargingProfileTypeBuilder.setPowerLimit(200);
			
			builder.setChargingProfile(chargingProfileTypeBuilder);

			MessageLite resp = builder.build();

			byte[] byteArray = resp.toByteArray();

			MessageLite chargingBuf = StartTransactionProto.startTransactionReq.parseFrom(byteArray);
			TomdaProtocol tomdaProtocol = new TomdaProtocol();
			tomdaProtocol.loadModels();

			System.out.println(Arrays.toString(byteArray));
			System.out.println(tomdaProtocol.organizeFrame(204, msgId, chargingBuf));
			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(204, msgId, chargingBuf));
		}
	}

	/**
	 * 数据帧: AA 55 01 00 22 00 CE 00 C1 49 0A 14 32 30 31 39 30 38 31 35 31 37 34 33
	 * 35 32 30 30 30 34 37 30 18 78
	 * 
	 * 关键数据:
	 * 
	 * [stopTransactionReq] transactionId: "20190815174352000470" 用户停止充电 msgId:18881
	 * 
	 */
	/**
	 * 停止充电 206
	 * 
	 * @param ctx
	 * @param msgId
	 * @param transactionId
	 * @throws InvalidProtocolBufferException
	 * 20190815225740000004
	 * 20190816170007000010
	 * 20190816165847000009
	 * 20190815233926000006
	 */
	public void stopCharging(ChannelHandlerContext ctx, int msgId, String transactionId)
			throws InvalidProtocolBufferException {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
		}
		com.zhicheng.wukongcharge.models.StopTransactionProto.stopTransactionReq.Builder stopTransactionBuilder = StopTransactionProto.stopTransactionReq
				.newBuilder();
		
//		Scanner inputtransactionId = new Scanner(System.in);
//		System.out.println("请输入订单号：");
//		String inputId = inputtransactionId.nextLine();
		stopTransactionBuilder.setTransactionId(transactionId);
		
		
		MessageLite stopTransaction = stopTransactionBuilder.build();
		byte[] byteArray = stopTransaction.toByteArray();
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		MessageLite stopTransactionResp = StopTransactionProto.stopTransactionReq.parseFrom(byteArray);
//		System.out.println(tomdaProtocol.organizeFrame(206, msgId, stopTransactionResp));
		System.out.println(Tool.showData(tomdaProtocol.organizeFrame(206, msgId, stopTransactionResp)));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(206, msgId, stopTransactionResp));
	}

	/**
	 * 208 更新充电金额/时间请求
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void costUpdatedRequest(ChannelHandlerContext ctx, int msgId) throws Exception {
		Attribute<KeyAttach> attr = ctx.channel().attr(NETTY_CHANNEL_KEY);
		KeyAttach keyAttach = attr.get();
		if (keyAttach != null && keyAttach.isLogin()) {
			CostUpdatedProto.costUpdatedReq.Builder costBuilder = CostUpdatedProto.costUpdatedReq.newBuilder();
			// 设置订单id
			costBuilder.setTransactionId("123456");
			MessageLite costMessageLite = costBuilder.build();
			System.out.println("costMessageLite:" + costMessageLite);
			byte[] byteArrays = costMessageLite.toByteArray();
			System.out.println("byteArrays:" + byteArrays.toString());
			MessageLite costUpdateBuf = CostUpdatedProto.costUpdatedReq.parseFrom(byteArrays);
			System.out.println("costUpdateBuf:" + costUpdateBuf);
			TomdaProtocol tomdaProtocol = new TomdaProtocol();
			tomdaProtocol.loadModels();
			System.out.println("完整帧：" + tomdaProtocol.organizeFrame(208, msgId, costUpdateBuf));
			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(208, msgId, costUpdateBuf));
		}
	}

	/**
	 * 创建订单 响应用户开始充电成功 302
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void transactionEventResponse(ChannelHandlerContext ctx, int msgId, TomdaProcotolContent t)
			throws Exception {
		TransactionEventProto.transactionEventReq transactionEventRequest = (TransactionEventProto.transactionEventReq) getMessage(
				t);
		/**
		 * 连接超时 关闭充电桩
		 */
		if (transactionEventRequest.getTransaction().getStoppedReason().Timeout.equals(transactionEventRequest.getTransaction().getStoppedReason())) {
//			stopCharging(ctx, msgId, transactionEventRequest.getTransaction().getTransactionId());
		}
		if (transactionEventRequest.getTransaction().getStoppedReason().Remote.equals(transactionEventRequest.getTransaction().getStoppedReason())) {
//			
//			TransactionEventProto.transactionEventResp.Builder transactionEventResponseBuilder = TransactionEventProto.transactionEventResp
//					.newBuilder();
//			transactionEventResponseBuilder.setStatus(transactionStatusEnumType.Accepted);
//			MessageLite messageLite = transactionEventResponseBuilder.build();
//			byte[] byteArrays = messageLite.toByteArray();
//			System.out.println(byteArrays.toString());
//			MessageLite transactionBuf = TransactionEventProto.transactionEventResp.parseFrom(byteArrays);
//			TomdaProtocol tomdaProtocol = new TomdaProtocol();
//			System.out.println(tomdaProtocol.organizeFrame(302, msgId, transactionBuf));
//			ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(302, msgId, transactionBuf));
		}
		TransactionEventProto.transactionEventResp.Builder transactionEventResponseBuilder = TransactionEventProto.transactionEventResp
				.newBuilder();
		transactionEventResponseBuilder.setStatus(transactionStatusEnumType.Accepted);
		
		transactionEventResponseBuilder.setBalance(10000);
		transactionEventResponseBuilder.setTransactionId(transactionEventRequest.getTransaction().getTransactionId());
		
		
		MessageLite messageLite = transactionEventResponseBuilder.build();
		byte[] byteArrays = messageLite.toByteArray();
		System.out.println(byteArrays.toString());
		MessageLite transactionBuf = TransactionEventProto.transactionEventResp.parseFrom(byteArrays);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println(tomdaProtocol.organizeFrame(302, msgId, transactionBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(302, msgId, transactionBuf));
		/**
		 * 在这里应该把充电状态存在数据库里面
		 */
//		System.out.println(transactionEventRequest.getTransaction().getStoppedReason());
//		System.out.println(
//				"充电插口：" + transactionEventRequest.getConnectorId() + "状态" + transactionEventRequest.getEventType());
////		System.out.println("这里应该要创建订单");
////		for (; i < 11; i++) {
////			startChargingReq(ctx, i, msgId);
////		}
//		System.out.println("响应用户开始充电");
//		
//		TransactionEventProto.transactionEventResp.Builder transactionEventResponseBuilder = TransactionEventProto.transactionEventResp
//				.newBuilder();
//		transactionEventResponseBuilder.setStatus(transactionStatusEnumType.Accepted);
//		MessageLite messageLite = transactionEventResponseBuilder.build();
//		byte[] byteArrays = messageLite.toByteArray();
//		System.out.println(byteArrays.toString());
//		MessageLite transactionBuf = TransactionEventProto.transactionEventResp.parseFrom(byteArrays);
//		TomdaProtocol tomdaProtocol = new TomdaProtocol();
//		System.out.println(tomdaProtocol.organizeFrame(402, msgId, transactionBuf));
//		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(402, msgId, transactionBuf));
	}

	/**
	 * 工作数据上报应答 304
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void workDataResponse(ChannelHandlerContext ctx, int msgId) throws Exception {
		ChargingDataReportProto.chargingDataReportResp.Builder chargingReportRespBuilder = ChargingDataReportProto.chargingDataReportResp
				.newBuilder();
		MessageLite chargingReportRespMessageLite = chargingReportRespBuilder.build();
		byte[] byteArray = chargingReportRespMessageLite.toByteArray();
		MessageLite chargingReportRespBuf = ChargingDataReportProto.chargingDataReportResp.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("chargingReportRespBuf:" + tomdaProtocol.organizeFrame(304, msgId, chargingReportRespBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(304, msgId, chargingReportRespBuf));
	}

	/**
	 * 事件上报应答 306
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void notifyEventResponse(ChannelHandlerContext ctx, int msgId) throws Exception {
		NotifyEventProto.notifyEventResp.Builder notifyEventBuilder = NotifyEventProto.notifyEventResp.newBuilder();
		MessageLite notifyEventMessageLite = notifyEventBuilder.build();
		byte[] byteArray = notifyEventMessageLite.toByteArray();
		MessageLite notifyEventBuf = NotifyEventProto.notifyEventResp.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("notifyEventBuf:" + tomdaProtocol.organizeFrame(306, msgId, notifyEventBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(306, msgId, notifyEventBuf));
	}

	/**
	 * 插头状态上报答应 308
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void statusNotificationResponse(ChannelHandlerContext ctx, int msgId) throws Exception {
		StatusNotificationProto.statusNotificationResp.Builder statusNotificationBuilder = StatusNotificationProto.statusNotificationResp
				.newBuilder();
		MessageLite statusNotificationMessageLite = statusNotificationBuilder.build();
		byte[] byteArray = statusNotificationMessageLite.toByteArray();
		MessageLite statusNotificationBuf = StatusNotificationProto.statusNotificationResp.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		tomdaProtocol.loadModels();
		System.out.println(Arrays.toString(byteArray));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(308, msgId, statusNotificationBuf));
	}

	/**
	 * 数据帧: AA 55 01 00 D0 00 92 01 ED E9 0A 22 08 01 12 08 30 2D 36 30 30 3A 36 30
	 * 1A 08 0A 06 54 61 72 69 66 66 22 0A 0A 08 53 65 67 6D 65 6E 74 31 0A 1D 08 01
	 * 12 03 31 38 30 1A 08 0A 06 54 61 72 69 66 66 22 0A 0A 08 46 72 65 65 54 69 6D
	 * 65 0A 1F 08 01 12 05 30 2D 30 3A 30 1A 08 0A 06 54 61 72 69 66 66 22 0A 0A 08
	 * 53 65 67 6D 65 6E 74 32 0A 1F 08 01 12 05 30 2D 30 3A 30 1A 08 0A 06 54 61 72
	 * 69 66 66 22 0A 0A 08 53 65 67 6D 65 6E 74 33 0A 1F 08 01 12 05 30 2D 30 3A 30
	 * 1A 08 0A 06 54 61 72 69 66 66 22 0A 0A 08 53 65 67 6D 65 6E 74 34 0A 1C 08 01
	 * 12 04 74 72 75 65 1A 08 0A 06 54 61 72 69 66 66 22 08 0A 06 41 63 74 69 76 65
	 * 62 6A [setVariablesReq]
	 * 
	 * setVariableData { 
	 * 		attributeType: Actual 
	 * 		attributeValue: "0-600:60" 
	 * component{ name: "Tariff"} 
	 * variable { name:"Segment1"} 
	 * }
	 * 
	 * setVariableData { attributeType: Actual attributeValue: "180" component {
	 * name: "Tariff"} variable { name: "FreeTime"} }
	 * 
	 * setVariableData { attributeType: Actual attributeValue: "0-0:0" component {
	 * name: "Tariff"} variable { name: "Segment2"} }
	 * 
	 * setVariableData { attributeType: Actual attributeValue: "0-0:0" component {
	 * name: "Tariff"} variable { name:"Segment3"} }
	 * 
	 * setVariableData { attributeType: Actual attributeValue: "0-0:0" component {
	 * name: "Tariff"} variable { name: "Segment4"} }
	 * 
	 * setVariableData { attributeType: Actual attributeValue: "true" component {
	 * name: "Tariff"} variable { name: "Active"} }
	 */
	/**
	 * AA 55 01 00 D1 00 92 01 2F 48 0A 22 08 01 12 08 30 2D 36 30 30 3A 36 30 1A 08
	 * 0A 06 54 61 72 69 66 66 22 0A 0A 08 53 65 67 6D 65 6E 74 31 0A 1D 08 01 12 03
	 * 31 38 30 1A 08 0A 06 54 61 72 69 66 66 22 0A 0A 08 46 72 65 65 54 69 6D 65 0A
	 * 1F 08 01 12 05 30 2D 30 3A 30 1A 08 0A 06 54 61 72 69 66 66 22 0A 0A 08 53 65
	 * 67 6D 65 6E 74 32 0A 1F 08 01 12 05 30 2D 30 3A 30 1A 08 0A 06 54 61 72 69 66
	 * 66 22 0A 0A 08 53 65 67 6D 65 6E 74 33 0A 1F 08 01 12 05 30 2D 30 3A 30 1A 08
	 * 0A 06 54 61 72 69 66 66 22 0A 0A 08 53 65 67 6D 65 6E 74 34 0A 1D 08 01 12 05
	 * 30 2D 30 3A 30 1A 08 0A 06 54 61 72 69 66 66 22 08 0A 06 41 63 74 69 76 65 B7
	 * 4A
	 */
	/**
	 * 设置费率 402
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws InvalidProtocolBufferException setVariableData
	 */
	public void setVariableRequest(ChannelHandlerContext ctx, int msgId) throws InvalidProtocolBufferException {
		// 第0个
		/**
		 * setVariableData { attributeType: Actual attributeValue: "0-600:60" component
		 * { name: "Tariff"} variable { name:"Segment1"} }
		 */
		SetVariablesProto.setVariablesReq.Builder variablesProtoBuilder = SetVariablesProto.setVariablesReq
				.newBuilder();
		setVariableDataType.Builder setVariableDataTypeBuilder = setVariableDataType.newBuilder();
		setVariableDataTypeBuilder.setAttributeType(attributeEnumType.Actual);
		setVariableDataTypeBuilder.setAttributeValue("0-600:60");

		componentType.Builder componentTypeBuilder = componentType.newBuilder();
		componentTypeBuilder.setName("Tariff");
		setVariableDataTypeBuilder.setComponent(componentTypeBuilder);
		variableType.Builder variableTypeBuilder = variableType.newBuilder();
		variableTypeBuilder.setName("Segment1");
		setVariableDataTypeBuilder.setVariable(variableTypeBuilder);
		variablesProtoBuilder.addSetVariableData(setVariableDataTypeBuilder);
		/**
		 * setVariableData { attributeType: Actual attributeValue: "180" component {
		 * name: "Tariff"} variable { name: "FreeTime"} }
		 */
		// 第一个
		setVariableDataType.Builder setVariableDataTypeBuilder1 = setVariableDataType.newBuilder();
		setVariableDataTypeBuilder1.setAttributeType(attributeEnumType.Actual);
		setVariableDataTypeBuilder1.setAttributeValue("180");

		componentType.Builder component1 = componentType.newBuilder();
		component1.setName("Tariff");
		setVariableDataTypeBuilder1.setComponent(component1);
		variableType.Builder variableTypeBuilder1 = variableType.newBuilder();
		variableTypeBuilder1.setName("FreeTime");
		setVariableDataTypeBuilder1.setVariable(variableTypeBuilder1);

		variablesProtoBuilder.addSetVariableData(setVariableDataTypeBuilder1);
		/**
		 * * setVariableData { attributeType: Actual attributeValue: "0-0:0" component {
		 * name: "Tariff"} variable { name: "Segment2"} }
		 */
		// 第二个
		setVariableDataType.Builder setVariableDataTypeBuilder2 = setVariableDataType.newBuilder();
		setVariableDataTypeBuilder2.setAttributeType(attributeEnumType.Actual);
		setVariableDataTypeBuilder2.setAttributeValue("0-0:0");

		componentType.Builder component2 = componentType.newBuilder();
		component2.setName("Tariff");
		setVariableDataTypeBuilder2.setComponent(component2);
		variableType.Builder variableTypeBuilder2 = variableType.newBuilder();
		variableTypeBuilder2.setName("Segment2");
		setVariableDataTypeBuilder2.setVariable(variableTypeBuilder2);
		variablesProtoBuilder.addSetVariableData(setVariableDataTypeBuilder2);
		/**
		 * setVariableData { attributeType: Actual attributeValue: "0-0:0" component {
		 * name: "Tariff"} variable { name:"Segment3"} }
		 */
		// 第三个
		setVariableDataType.Builder setVariableDataTypeBuilder3 = setVariableDataType.newBuilder();
		setVariableDataTypeBuilder3.setAttributeType(attributeEnumType.Actual);
		setVariableDataTypeBuilder3.setAttributeValue("0-0:0");

		componentType.Builder component3 = componentType.newBuilder();
		component3.setName("Tariff");
		setVariableDataTypeBuilder3.setComponent(component3);
		variableType.Builder variableTypeBuilder3 = variableType.newBuilder();
		variableTypeBuilder3.setName("Segment3");
		setVariableDataTypeBuilder3.setVariable(variableTypeBuilder3);
		variablesProtoBuilder.addSetVariableData(setVariableDataTypeBuilder3);

		/**
		 * setVariableData { attributeType: Actual attributeValue: "0-0:0" component {
		 * name: "Tariff"} variable { name: "Segment4"} }
		 */
		// 第四个
		setVariableDataType.Builder setVariableDataTypeBuilder4 = setVariableDataType.newBuilder();
		setVariableDataTypeBuilder4.setAttributeType(attributeEnumType.Actual);
		setVariableDataTypeBuilder4.setAttributeValue("0-0:0");

		componentType.Builder component4 = componentType.newBuilder();
		component4.setName("Tariff");
		setVariableDataTypeBuilder4.setComponent(component4);
		variableType.Builder variableTypeBuilder4 = variableType.newBuilder();
		variableTypeBuilder4.setName("Segment4");
		setVariableDataTypeBuilder4.setVariable(variableTypeBuilder4);
		variablesProtoBuilder.addSetVariableData(setVariableDataTypeBuilder4);

		// 第五个
		/**
		 * setVariableData { attributeType: Actual attributeValue: "true" component {
		 * name: "Tariff"} variable { name: "Active"} }
		 */
		setVariableDataType.Builder setVariableDataTypeBuilder5 = setVariableDataType.newBuilder();
		setVariableDataTypeBuilder5.setAttributeType(attributeEnumType.Actual);
		setVariableDataTypeBuilder5.setAttributeValue("true");

		componentType.Builder component5 = componentType.newBuilder();
		component5.setName("Tariff");
		setVariableDataTypeBuilder5.setComponent(component5);
		variableType.Builder variableTypeBuilder5 = variableType.newBuilder();
		variableTypeBuilder5.setName("Active");
		setVariableDataTypeBuilder5.setVariable(variableTypeBuilder5);
		variablesProtoBuilder.addSetVariableData(setVariableDataTypeBuilder5);

		MessageLite setVariableMessageLite = variablesProtoBuilder.build();
		byte[] byteArray = setVariableMessageLite.toByteArray();
		MessageLite setVariableBuf = SetVariablesProto.setVariablesReq.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		tomdaProtocol.loadModels();
		System.out.println(Arrays.toString(byteArray));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(402, msgId, setVariableBuf));
	}

	/**
	 * 获取组件变量 404
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void getVariableResponse(ChannelHandlerContext ctx, int msgId) throws Exception {
		GetVariableProto.getVariableReq.Builder getVariableBuilder = GetVariableProto.getVariableReq.newBuilder();
		getVariableDataType.Builder getVariableDataTypeBuilder = getVariableDataType.newBuilder();
		getVariableBuilder.setGetVariablesData(0, getVariableDataTypeBuilder);
		MessageLite getVariableMessageLite = getVariableBuilder.build();
		byte[] byteArray = getVariableMessageLite.toByteArray();
		MessageLite getVariableBuf = GetVariableProto.getVariableReq.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("getVariableBuf:" + tomdaProtocol.organizeFrame(404, msgId, getVariableBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(404, msgId, getVariableBuf));
	}

	/**
	 * 固件更新请求 502
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void updateFirewareEventRequest(ChannelHandlerContext ctx, int msgId) throws Exception {
		UpdateFirmwareEventProto.updateFirmwareEventReq.Builder updateFirmwareEventBuilder = UpdateFirmwareEventProto.updateFirmwareEventReq
				.newBuilder();
		updateFirmwareEventBuilder.setEventType(updateEventTypeEnumType.Started);// 开始
//		updateFirmwareEventBuilder.setEventType(updateEventTypeEnumType.Canceled);//取消
//		updateFirmwareEventBuilder.setEventType(updateEventTypeEnumType.Paused);//暂停
		updateParamType.Builder updateParamTypeBuilder = updateParamType.newBuilder();
		updateParamTypeBuilder.setUpdateMethod(updateMethodEnumType.UpdateByCSMS);
		updateFirmwareEventBuilder.setUpdateParam(updateParamTypeBuilder);
		MessageLite updateFirewareEventMessageLite = updateFirmwareEventBuilder.build();
		byte[] byteArray = updateFirewareEventMessageLite.toByteArray();
		MessageLite updateFirewareEventBuf = UpdateFirmwareEventProto.updateFirmwareEventReq.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("updateFirewareEventBuf:" + tomdaProtocol.organizeFrame(502, msgId, updateFirewareEventBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(502, msgId, updateFirewareEventBuf));
	}

	/**
	 * 固件更新状态上报应答 504
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void firewareStatusNotificationResponse(ChannelHandlerContext ctx, int msgId) throws Exception {
		FirmwareStatusNotificationProto.firmwareStatusNotificationResp.Builder firewareStatusNoticationBuilder = FirmwareStatusNotificationProto.firmwareStatusNotificationResp
				.newBuilder();
		MessageLite firewareStatusNoticationMessageLite = firewareStatusNoticationBuilder.build();
		byte[] byteArray = firewareStatusNoticationMessageLite.toByteArray();
		MessageLite firewareStatusNoticationBuf = FirmwareStatusNotificationProto.firmwareStatusNotificationResp
				.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("notifyEventBuf:" + tomdaProtocol.organizeFrame(504, msgId, firewareStatusNoticationBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(504, msgId, firewareStatusNoticationBuf));
	}

	/**
	 * 固件数据传输请求 506
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void dataTransferRequest(ChannelHandlerContext ctx, int msgId, String messageId) throws Exception {
		DataTransferProto.dataTransferReq.Builder dataTransferReqBuilder = DataTransferProto.dataTransferReq
				.newBuilder();
		dataTransferReqBuilder.setMessageID(messageId);
		MessageLite dataTransferMessageLite = dataTransferReqBuilder.build();
		byte[] byteArray = dataTransferMessageLite.toByteArray();
		MessageLite dataTransferBuf = DataTransferProto.dataTransferReq.parseFrom(byteArray);
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("triggerMessageBuf:" + tomdaProtocol.organizeFrame(506, msgId, dataTransferBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(506, msgId, dataTransferBuf));
	}

	/**
	 * 触发消息请求 602
	 * 
	 * @param ctx
	 * @param msgId
	 * @throws Exception
	 */
	public void triggerMessageRequest(ChannelHandlerContext ctx, int msgId) throws Exception {
		TriggerMessageProto.triggerMessageReq.Builder triggerMessageReqBuilder = TriggerMessageProto.triggerMessageReq
				.newBuilder();
		System.out.println("请选择要触发的消息类型：");
		System.out.println("1 ------------登录指令");
		System.out.println("2 ------------固件升级状态");
		System.out.println("3 ------------心跳");
		System.out.println("4 ------------状态信息");
		Scanner input = new Scanner(System.in);
		int i = input.nextInt();
		if (i == 1) {

			triggerMessageReqBuilder.setRequestedMessage(messageTriggerEnumType.BootNotification);

		} else if (i == 2) {

			triggerMessageReqBuilder.setRequestedMessage(messageTriggerEnumType.FirmwareStatusNotification);

		} else if (i == 3) {

			triggerMessageReqBuilder.setRequestedMessage(messageTriggerEnumType.Heartbeat);

		} else if (i == 4) {

			triggerMessageReqBuilder.setRequestedMessage(messageTriggerEnumType.StatusNotification);

		}
		MessageLite triggerMessageLite = triggerMessageReqBuilder.build();
		byte[] byteArray = triggerMessageLite.toByteArray();
		MessageLite triggerMessageBuf = TriggerMessageProto.triggerMessageReq.parseFrom(byteArray);
		input.close();
		TomdaProtocol tomdaProtocol = new TomdaProtocol();
		System.out.println("triggerMessageBuf:" + tomdaProtocol.organizeFrame(602, msgId, triggerMessageBuf));
		ctx.channel().writeAndFlush(tomdaProtocol.organizeFrame(602, msgId, triggerMessageBuf));
	}

	private MessageLite getMessage(TomdaProcotolContent t) {
		switch (t.getDataType()) {
		case 101:
			return (BootNotificationProto.bootNotificationReq) t.getMessageLite();
		case 103:
			return (BeatHeartProto.beatHeartReq) t.getMessageLite();
		case 201:
			return (AuthorizeProto.authorizeReq) t.getMessageLite();
		case 203:
			return (StartTransactionProto.startTransactionResp) t.getMessageLite();
		case 205:
			return (StopTransactionProto.stopTransactionResp) t.getMessageLite();
		case 207:
			return (CostUpdatedProto.costUpdatedResp) t.getMessageLite();
		case 301:
			return (TransactionEventProto.transactionEventReq) t.getMessageLite();
		case 303:
			return (ChargingDataReportProto.chargingDataReportReq) t.getMessageLite();
		case 305:
			return (NotifyEventProto.notifyEventReq) t.getMessageLite();
		case 307:
			return (StatusNotificationProto.statusNotificationReq) t.getMessageLite();
		case 401:
			return (SetVariablesProto.setVariablesResp) t.getMessageLite();
		case 403:
			return (GetVariableProto.getVariableResp) t.getMessageLite();
		case 501:
			return (UpdateFirmwareEventProto.updateFirmwareEventResp) t.getMessageLite();
		case 503:
			return (FirmwareStatusNotificationProto.firmwareStatusNotificationReq) t.getMessageLite();
		case 505:
			return (DataTransferProto.dataTransferResp) t.getMessageLite();
		case 601:
			return (TriggerMessageProto.triggerMessageReq) t.getMessageLite();
		}
		return null;
	}

	/**
	 * 智能指令控制类
	 * 
	 * @param ctx
	 * @param msgId
	 * @param chargerSerialNum
	 * @param actionId
	 * @param t
	 */
	public void instruct2(ChannelHandlerContext ctx, int msgId, String chargerSerialNum, int actionId,
			TomdaProcotolContent t) throws Exception {
		if(actionId == 101 || actionId == 103 || actionId == 201 || actionId == 307) {
			actionId += 1;
			switch(actionId) {
			case 102:
				// EVSE登录应答
				loginRequest(ctx, msgId, chargerSerialNum);
				System.out.println("登录应答发送成功！");
				break;
			case 104:
				// Server心跳应答
				beatHeartResponse(ctx, msgId);
				System.out.println("心跳应答成功");
				break;
			case 202:
				// EVSE本地授权应答
				break;
			case 308:
				// 插头状态上报应答
				statusNotificationResponse(ctx, msgId);
				System.out.println("插头状态上报应答成功");
				break;
			}
		}else {
			
		}
	}
	/**
	 * 指令控制类
	 * 
	 * @param ctx
	 * @param msgId
	 * @param chargerSerialNum
	 * @throws InvalidProtocolBufferException 
	 */
	public void instruct(ChannelHandlerContext ctx, int msgId, String chargerSerialNum, int actionId,
			TomdaProcotolContent t) throws InvalidProtocolBufferException {
		t.getMessageLite();
		if(flag) {
			setVariableRequest(ctx, msgId);
			flag = false;
		}
		actionId += 1;
//		while (true) {
		System.out.println("请输入指令：");
		System.out.println("102：登录应答");
		System.out.println("104: 心跳应答");
		System.out.println("202: 本地授权应答(没有编写代码)");
		System.out.println("204: 远程授权充电请求");
		System.out.println("206: 后台停止充电请求");
		System.out.println("208: 更新充电金额/时间请求");
		System.out.println("302: 事务事件应答");
		System.out.println("304: 工作数据上报应答");
		System.out.println("306: 事件上报应答");
		System.out.println("308: 插头状态上报应答");
		System.out.println("402：设置费率");
		System.out.println("404：获取组件变量");
		System.out.println("502：固件更新请求");
		System.out.println("504：固件更新状态上报应答");
		System.out.println("506：固件数据传输请求");
		System.out.println("602：触发消息请求");
		Scanner input = new Scanner(System.in);
//		int i = input.nextInt();
		try {
			switch (actionId) {
			case 102:
				// EVSE登录应答
				loginRequest(ctx, msgId, chargerSerialNum);
				System.out.println("登录应答发送成功！");
				break;
			case 104:
				// Server心跳应答
				beatHeartResponse(ctx, msgId);
				System.out.println("心跳应答成功");
				break;
			case 202:
				// EVSE本地授权应答
				break;
			case 204:
				System.out.println("请输入要授权充电的插座口:");
//				int connectId = input.nextInt();
//				startChargingReq(ctx, 1, msgId);
				// 远程授权充电请求
				break;
			case 206:
				// 后台停止充电请求
				stopCharging(ctx, msgId, "20190816170007000010");
				System.out.println("创建完成！");
				break;
			case 208:
				costUpdatedRequest(ctx, msgId);
				// 更新充电金额/时间请求
				System.out.println("更新充电金额/时间请求成功");
				break;
			case 302:
				// 事务事件应答----订单
				transactionEventResponse(ctx, msgId, t);
				System.out.println("事务事件应答----订单成功");
				break;
			case 304:
				// 工作数据上报应答
				workDataResponse(ctx, msgId);
				System.out.println("工作数据上报应答");
				break;
			case 306:
				// 事件上报应答
//				transactionEvents(ctx, msgId);
				System.out.println("事件上报应答");
				break;
			case 308:
				// 插头状态上报应答
				statusNotificationResponse(ctx, msgId);
				System.out.println("插头状态上报应答成功");
				break;
			case 402:
				// 设置组件变量
				setVariableRequest(ctx, msgId);
				System.out.println("设置组件变量成功");
				break;
			case 404:
				// 获取组件变量
				getVariableResponse(ctx, msgId);
				System.out.println("获取组件变量成功");
				break;
			case 502:
				// 固件更新请求
				updateFirewareEventRequest(ctx, msgId);
				System.out.println("固件更新请求成功");
				break;
			case 504:
				// 固件更新状态上报应答
				firewareStatusNotificationResponse(ctx, msgId);
				System.out.println("固件更新状态上报应答成功");
				break;
			case 506:
				// 固件数据传输请求
				System.out.println("请设置消息ID：");
				String messageId = input.nextLine();
				dataTransferRequest(ctx, msgId, messageId);
				System.out.println("固件数据传输请求");
				break;
			case 602:
				// 触发消息请求
				triggerMessageRequest(ctx, msgId);
				System.out.println("触发消息请求成功");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
	}
	/**
	 * 第三个指令版本
	 * @param ctx
	 * @param msgId
	 * @param chargerSerialNum
	 * @param actionId
	 * @param t
	 * @throws InvalidProtocolBufferException
	 */
	public void instruct3(ChannelHandlerContext ctx, int msgId, String chargerSerialNum, int actionId,
			TomdaProcotolContent t) throws Exception {
		//设置费率
		switch(actionId) {
		case 101:
			loginRequest(ctx, msgId, chargerSerialNum);
			if(flag) {
				setVariableRequest(ctx, Integer.parseInt(Tool.getRandomInt(3)));
				flag = false;
			}
			if(i == 1) {
				startChargingReq(ctx, 5, msgId);
				i++;
			}
			break;
		case 103:
			beatHeartResponse(ctx, msgId);
			break;
		case 203:
			break;
		case 205:
			break;
		case 207:
			costUpdatedRequest(ctx, msgId);
			break;
		case 301:
			transactionEventResponse(ctx, msgId, t);
			break;
		case 303:
			workDataResponse(ctx, msgId);
			break;
		case 307:
			statusNotificationResponse(ctx, msgId);
			break;
		case 401:
			break;
		case 403:
			getVariableResponse(ctx, msgId);
			break;
		}
	}
//	public void instruct4   
}
