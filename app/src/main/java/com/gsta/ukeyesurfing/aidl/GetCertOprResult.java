package com.gsta.ukeyesurfing.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class GetCertOprResult implements Parcelable {

	private int oprResultCode;
	private String certHexStr;
//	private byte[] certBytes;
	
//	public GetCertOprResult(int oprResultCode, byte certBytes){
//		
//	}
	
	public GetCertOprResult(){}
	
	public GetCertOprResult(Parcel in){
		readFromParcel(in);
	}
	
	public GetCertOprResult(int oprResultCode, String certHexStr){
		this.oprResultCode = oprResultCode;
		this.certHexStr = certHexStr;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeInt(oprResultCode);
//		dest.writeByteArray(certBytes);
		dest.writeString(certHexStr);
	}
	
	public void readFromParcel(Parcel in){
	    this.oprResultCode = in.readInt();
	    this.certHexStr = in.readString();
//	    this.photo = new byte[in.readInt()];
//	    in.readByteArray(this.photo);
	}
	
	public static final Creator<GetCertOprResult> CREATOR = new Creator<GetCertOprResult>() {
		
		@Override
		public GetCertOprResult[] newArray(int size) {
			return new GetCertOprResult[size];
		}
		
		@Override
		public GetCertOprResult createFromParcel(Parcel source) {
			return new GetCertOprResult(source);
		}
	};
	
	
	public int getOprResultCode() {
		return oprResultCode;
	}
	public void setOprResultCode(int oprResultCode) {
		this.oprResultCode = oprResultCode;
	}

	public String getCertHexStr() {
		return certHexStr;
	}

	public void setCertHexStr(String certHexStr) {
		this.certHexStr = certHexStr;
	}
	
//	public byte[] getCertBytes() {
//		return certBytes;
//	}
//	public void setCertBytes(byte[] certBytes) {
//		this.certBytes = certBytes;
//	}


}
