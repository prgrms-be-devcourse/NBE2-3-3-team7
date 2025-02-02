// , multipartRequest
import { jsonRequest, multipartRequest } from '@/services/api';

export const signinEmail = async (data) => {

	const response = await jsonRequest(`/auth/login`, 'POST', data);

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const signout = async () => {
	await jsonRequest(`/auth/logout`, 'POST');

	return { success: true, message: "로그아웃 되었습니다." };
}

export const signupEmail = async (data) => {
	await multipartRequest(`/signup`, 'POST', data);

	return { success: true, message: "회원가입을 성공했습니다." };
}

export const signupSocial = async (data) => {
	await multipartRequest(`/auth/oauth2/signup`, 'POST', data);

	return { success: true, message: "회원가입을 성공했습니다." };
}

