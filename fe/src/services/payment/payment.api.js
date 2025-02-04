import { jsonRequest } from '@/services/api';

export const paymentInfo = async (param) => {
	const query = '?' + new URLSearchParams(param).toString();

	const response = await jsonRequest(`/payment${query}`, 'GET');

	// 응답 데이터가 유효한지 확인
	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const insertStaging = async (data) => {
	await jsonRequest(`/payment`, 'POST', data);
	
	return { success: true, message: "결제를 요청했습니다." };
};

export const paymentSuccess = async (data) => {
	await jsonRequest(`/payment/success`, 'POST', data);

	return { success: true, message: "결제를 성공했습니다." };
};

export const paymentFail = async (data) => {
	await jsonRequest(`/payment/fail`, 'DELETE', data);

	return { success: true, message: "결제를 취소했습니다." };
}