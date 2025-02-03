import { jsonRequest } from '@/services/api';

export const paymentRefund = async (id) => {
	await jsonRequest(`/receipt/${id}`, 'PATCH')

	return { success: true, message: "결제가 취소되었습니다." };
}

export const reservationByLand = async (id, param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/reservation/${id}${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const receiptByUser = async (param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/receipt${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}