package com.zhicheng.wukongcharge.communicate;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.zhicheng.wukongcharge.models.BeatHeartProto;
import com.zhicheng.wukongcharge.models.BootNotificationProto;
import com.zhicheng.wukongcharge.models.ChargingDataReportProto;
import com.zhicheng.wukongcharge.tool.Tool;

public class Test {

	public static void main(String[] args) throws Exception {

		bootNotificationResp();
	}

	public static void bootNotificationResp() throws Exception {
		BootNotificationProto.bootNotificationResp.Builder builder = BootNotificationProto.bootNotificationResp
				.newBuilder();
		builder.setStatus(BootNotificationProto.bootNotificationResp.registrationStatusEnumType.Accepted);
		builder.setCurrentTime(Tool.getISO8601Timestamp());
		builder.setInterval(180);
		MessageLite messageLite = builder.build();
		byte[] bytes = messageLite.toByteArray();
		String str2 = new String(bytes, "ISO-8859-1");
		System.out.println(Tool.showData(str2));

		MessageLite testBuf = BootNotificationProto.bootNotificationResp.parseFrom(bytes);
		System.out.println("1111:" + testBuf.toString());
	}

	public static void bootNotificationResp1() throws Exception {
		byte[] bytes1 = { (byte) 0xAA, 0x55, 0x01, 0x00, 0x2C, 0x00, 0x66, 0x00, 0x01, 0x00, 0x5B, 0x42, 0x40, 0x34,
				0x66, 0x32, 0x66, 0x61, 0x38, 0x38, 0x35, 0x79, 0x04 };
		byte[] bytes2 = { 0x3C, 0x42, 0x79, 0x74, 0x65, 0x53, 0x74, 0x72, 0x69, 0x6E, 0x67, 0x40, 0x33, 0x35, 0x36,
				0x34, 0x64, 0x33, 0x63, 0x63, 0x20, 0x73, 0x69, 0x7A, 0x65, 0x3D, 0x33, 0x32, 0x3E };

		byte[] bytes = { 0x08, 0x00, 0x12, 0x19, 0x32, 0x30, 0x31, 0x38, 0x2D, 0x31, 0x30, 0x2D, 0x31, 0x30, 0x54, 0x32,
				0x31, 0x3A, 0x31, 0x32, 0x3A, 0x30, 0x36, 0x2B, 0x30, 0x38, 0x3A, 0x30, 0x30, 0x18, (byte) 0xB4, 0x01 };
		MessageLite testBuf = BootNotificationProto.bootNotificationResp.parseFrom(bytes);
		System.out.println(testBuf.toString());
	}

	public static void authorizeReq() throws Exception {
		byte[] bytes = { (byte) 0xAA, 0x55, 0x01, 0x00, 0x0C, 0x00, (byte) 0xC9, 0x00, 0x09, 0x00, (byte) 0xA5, 0x6D, };
		MessageLite testBuf = BootNotificationProto.bootNotificationResp.parseFrom(bytes);
		System.out.println(testBuf.toString());
	}

	public static void getData(byte[] result) {
		try {
			BeatHeartProto.beatHeartReq testBuf = BeatHeartProto.beatHeartReq.parseFrom(result);
			System.out.println(testBuf);
//            System.out.println(BeatHeartProto.beatHeartReq.);//这里使用枚举
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public static void chargingDataReportReq() throws Exception {
		// AA 55 01 00 35 00 2F 01 0E 01 0A 0C 08 01 18 ED 01 20 D0 02 28 B0 FC 02 12 19
		// 32 30 31 38 2D 31 30 2D 31 30 54 32 33 3A 32 31 3A 35 38 2B 30 38 3A 30 30 14
		// E0
		byte[] bytes = { 0x0A, 0X0C, 0x08, 0x01, 0x18, (byte) 0xEE, 0x01, 0x20, (byte) 0xD7, 0x02, 0x28, (byte) 0xF7,
				(byte) 0xFE, 0x02, 0x12, 0x19, 0x32, 0x30, 0x31, 0x38, 0x2D, 0x31, 0x30, 0x2D, 0x31, 0x30, 0x54, 0x32,
				0x33, 0x3A, 0x33, 0x31, 0x3A, 0x35, 0x38, 0x2B, 0x30, 0x38, 0x3A, 0x30, 0x30 };

//        MessageLite testBuf = ChargingDataReportProto.chargingDataReportReq.parseFrom(bytes);
		MessageLite testBuf1 = ChargingDataReportProto.chargingDataReportReq.getDefaultInstance().getParserForType()
				.parseFrom(bytes);

		System.out.println(testBuf1.toString());
	}

}
