package com.gsta.ukeyesurfing.aidl;

import com.gsta.ukeyesurfing.aidl.GetCertOprResult;

interface IUkeyService {
	String getVersion();
	GetCertOprResult getCertificate(String spUserId);
}